/*
 *  Copyright (c) 2001-2005, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.commandline.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.metrics.*;

public class OOMetrics {
    public static final String DEFAULT_PROJECT_NAME = "Project";
    public static final String DEFAULT_SORT         = "name";
    public static final String DEFAULT_LOGFILE      = "System.out";

    public static void showError(CommandLineUsage clu, String msg) {
        System.err.println(msg);
        showError(clu);
    }

    public static void showError(CommandLineUsage clu) {
        System.err.println(clu);
        System.err.println();
        System.err.println("If no files are specified, it processes the current directory.");
        System.err.println();
        System.err.println("If file is a directory, it is recusively scanned for files");
        System.err.println("ending in \".class\".");
        System.err.println();
        System.err.println("Defaults is text output to the console.");
        System.err.println();
    }

    public static void showVersion() throws IOException {
        Version version = new Version();
        
        System.err.print(version.getImplementationTitle());
        System.err.print(" ");
        System.err.print(version.getImplementationVersion());
        System.err.print(" (c) ");
        System.err.print(version.getCopyrightDate());
        System.err.print(" ");
        System.err.print(version.getCopyrightHolder());
        System.err.println();
        
        System.err.print(version.getImplementationURL());
        System.err.println();
        
        System.err.print("Compiled on ");
        System.err.print(version.getImplementationDate());
        System.err.println();
    }

    public static void main(String[] args) throws Exception {
        // Parsing the command line
        CommandLine commandLine = new CommandLine();
        commandLine.addSingleValueSwitch("project-name",           DEFAULT_PROJECT_NAME);
        commandLine.addSingleValueSwitch("default-configuration", true);
        commandLine.addSingleValueSwitch("configuration");
        commandLine.addToggleSwitch("csv");
        commandLine.addToggleSwitch("txt");
        commandLine.addToggleSwitch("xml");
        commandLine.addToggleSwitch("validate");
        commandLine.addSingleValueSwitch("encoding",               com.jeantessier.metrics.XMLPrinter.DEFAULT_ENCODING);
        commandLine.addSingleValueSwitch("dtd-prefix",             com.jeantessier.metrics.XMLPrinter.DEFAULT_DTD_PREFIX);
        commandLine.addSingleValueSwitch("indent-text");
        commandLine.addToggleSwitch("all");
        commandLine.addToggleSwitch("project");
        commandLine.addToggleSwitch("groups");
        commandLine.addToggleSwitch("classes");
        commandLine.addToggleSwitch("methods");
        commandLine.addMultipleValuesSwitch("scope-includes-list");
        commandLine.addMultipleValuesSwitch("scope-excludes-list");
        commandLine.addMultipleValuesSwitch("filter-includes-list");
        commandLine.addMultipleValuesSwitch("filter-excludes-list");
        commandLine.addToggleSwitch("show-all-metrics");
        commandLine.addToggleSwitch("show-empty-metrics");
        commandLine.addToggleSwitch("show-hidden-measurements");
        commandLine.addSingleValueSwitch("sort",                   DEFAULT_SORT);
        commandLine.addToggleSwitch("expand");
        commandLine.addToggleSwitch("reverse");
        commandLine.addToggleSwitch("time");
        commandLine.addSingleValueSwitch("out");
        commandLine.addToggleSwitch("help");
        commandLine.addOptionalValueSwitch("verbose",              DEFAULT_LOGFILE);
        commandLine.addToggleSwitch("version");

        CommandLineUsage usage = new CommandLineUsage("OOMetrics");
        commandLine.accept(usage);

        try {
            commandLine.parse(args);
        } catch (IllegalArgumentException ex) {
            showError(usage, ex.toString());
            System.exit(1);
        } catch (CommandLineException ex) {
            showError(usage, ex.toString());
            System.exit(1);
        }

        if (commandLine.getToggleSwitch("help")) {
            showError(usage);
        }
        
        if (commandLine.getToggleSwitch("version")) {
            showVersion();
        }

        if (commandLine.getToggleSwitch("help") || commandLine.getToggleSwitch("version")) {
            System.exit(1);
        }

        if (!commandLine.getToggleSwitch("all") && !commandLine.getToggleSwitch("project") && !commandLine.getToggleSwitch("groups") && !commandLine.getToggleSwitch("classes") && !commandLine.getToggleSwitch("methods")) {
            showError(usage, "Must have at least one of -all, -project, -groups, -classes, or -methods");
            System.exit(1);
        }

        int modeSwitch = 0;
        
        if (commandLine.getToggleSwitch("csv")) {
            modeSwitch++;
        }
        if (commandLine.getToggleSwitch("txt")) {
            modeSwitch++;
        }
        if (commandLine.getToggleSwitch("xml")) {
            modeSwitch++;
        }
        if (modeSwitch != 1) {
            showError(usage, "Must have one and only one of -csv, -txt, or -xml");
            System.exit(1);
        }

        VerboseListener verboseListener = new VerboseListener();
        if (commandLine.isPresent("verbose")) {
            if ("System.out".equals(commandLine.getOptionalSwitch("verbose"))) {
                verboseListener.setWriter(System.out);
            } else {
                verboseListener.setWriter(new FileWriter(commandLine.getOptionalSwitch("verbose")));
            }
        }

        /*
         *  Beginning of main processing
         */

        Date start = new Date();

        Logger.getLogger(OOMetrics.class).debug("Reading sources ...");

        List parameters = commandLine.getParameters();
        if (parameters.size() == 0) {
            parameters.add(".");
        }

        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.addLoadListener(verboseListener);
        loader.load(parameters);

        Logger.getLogger(OOMetrics.class).debug("Reading configuration ...");

        String projectName = commandLine.getSingleSwitch("project-name");
        
        MetricsFactory factory;
        
        if (commandLine.isPresent("configuration")) {
            factory = new MetricsFactory(projectName, new MetricsConfigurationLoader(commandLine.getToggleSwitch("validate")).load(commandLine.getSingleSwitch("configuration")));
        } else {
            factory = new MetricsFactory(projectName, new MetricsConfigurationLoader(commandLine.getToggleSwitch("validate")).load(commandLine.getSingleSwitch("default-configuration")));
        }

        Logger.getLogger(OOMetrics.class).debug("Computing metrics ...");

        com.jeantessier.metrics.MetricsGatherer gatherer = new com.jeantessier.metrics.MetricsGatherer(projectName, factory);
        if (commandLine.isPresent("scope-includes-list") || commandLine.isPresent("scope-excludes-list")) {
            gatherer.setScopeIncludes(createCollection(commandLine.getMultipleSwitch("scope-includes-list"), commandLine.getMultipleSwitch("scope-excludes-list")));
        }
        if (commandLine.isPresent("filter-includes-list") || commandLine.isPresent("filter-excludes-list")) {
            gatherer.setFilterIncludes(createCollection(commandLine.getMultipleSwitch("filter-includes-list"), commandLine.getMultipleSwitch("filter-excludes-list")));
        }
        gatherer.addMetricsListener(verboseListener);
        gatherer.visitClassfiles(loader.getAllClassfiles());
        
        if (commandLine.isPresent("show-all-metrics")) {
            Iterator i;

            i = gatherer.getMetricsFactory().getAllClassMetrics().iterator();
            while (i.hasNext()) {
                gatherer.getMetricsFactory().includeClassMetrics((Metrics) i.next());
            }

            i = gatherer.getMetricsFactory().getAllMethodMetrics().iterator();
            while (i.hasNext()) {
                gatherer.getMetricsFactory().includeMethodMetrics((Metrics) i.next());
            }
        }

        Logger.getLogger(OOMetrics.class).debug("Printing results ...");
        verboseListener.print("Printing results ...");
        
        if (commandLine.isPresent("csv")) {
            printCSVFiles(start, commandLine, gatherer.getMetricsFactory());
        } else if (commandLine.isPresent("txt")) {
            printTextFile(start, commandLine, gatherer.getMetricsFactory());
        } else if (commandLine.isPresent("xml")) {
            printXMLFile(start, commandLine, gatherer.getMetricsFactory());
        }

        Logger.getLogger(OOMetrics.class).debug("Done.");

        Date end = new Date();

        if (commandLine.getToggleSwitch("time")) {
            System.err.println(OOMetrics.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
        }

        verboseListener.close();
    }

    private static Collection createCollection(Collection includes, Collection excludes) throws IOException {
        Collection result = new HashSet();
        Iterator   i;
            
        i = includes.iterator();
        while (i.hasNext()) {
            BufferedReader reader = new BufferedReader(new FileReader(i.next().toString()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            reader.close();
        }
        
        i = excludes.iterator();
        while (i.hasNext()) {
            BufferedReader reader = new BufferedReader(new FileReader(i.next().toString()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.remove(line);
            }
            reader.close();
        }
        
        return result;
    }

    private static void printCSVFiles(Date start, CommandLine commandLine, MetricsFactory factory) throws IOException {
        MetricsComparator comparator = new MetricsComparator(commandLine.getSingleSwitch("sort"));
        if (commandLine.getToggleSwitch("reverse")) {
            comparator.reverse();
        }

        List               metrics;
        Iterator           i;
        com.jeantessier.metrics.Printer printer;
        PrintWriter        out = new PrintWriter(new OutputStreamWriter(System.out));

        if (commandLine.getToggleSwitch("project") || commandLine.getToggleSwitch("all")) {
            if (commandLine.isPresent("out")) {
                out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out") + "_project.csv"));
            } else {
                out.println("Project:");
            }
            
            metrics = new ArrayList(factory.getProjectMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getProjectMeasurements());
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);
            
            if (commandLine.isPresent("out")) {
                out.close();
            } else {
                out.println();
            }
        }

        if (commandLine.getToggleSwitch("groups") || commandLine.getToggleSwitch("all")) {
            if (commandLine.isPresent("out")) {
                out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out") + "_groups.csv"));
            } else {
                out.println("Packages:");
            }

            metrics = new ArrayList(factory.getGroupMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getGroupMeasurements());
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);
            
            if (commandLine.isPresent("out")) {
                out.close();
            } else {
                out.println();
            }
        }

        if (commandLine.getToggleSwitch("classes") || commandLine.getToggleSwitch("all")) {
            if (commandLine.isPresent("out")) {
                out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out") + "_classes.csv"));
            } else {
                out.println("Classes:");
            }

            metrics = new ArrayList(factory.getClassMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getClassMeasurements());
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);
            
            if (commandLine.isPresent("out")) {
                out.close();
            } else {
                out.println();
            }
        }

        if (commandLine.getToggleSwitch("methods") || commandLine.getToggleSwitch("all")) {
            if (commandLine.isPresent("out")) {
                out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out") + "_methods.csv"));
            } else {
                out.println("Methods:");
            }

            metrics = new ArrayList(factory.getMethodMetrics());
            Collections.sort(metrics, comparator);
            printer = new com.jeantessier.metrics.CSVPrinter(out, factory.getConfiguration().getMethodMeasurements());
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);
        
            if (commandLine.isPresent("out")) {
                out.close();
            }
        }
    }

    private static void printTextFile(Date start, CommandLine commandLine, MetricsFactory factory) throws IOException {
        PrintWriter out;
        if (commandLine.isPresent("out")) {
            out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out") + ".txt"));
        } else {
            out = new PrintWriter(new OutputStreamWriter(System.out));
        }

        MetricsComparator comparator = new MetricsComparator(commandLine.getSingleSwitch("sort"));
        if (commandLine.getToggleSwitch("reverse")) {
            comparator.reverse();
        }

        List               metrics;
        Iterator           i;

        if (commandLine.getToggleSwitch("project") || commandLine.getToggleSwitch("all")) {
            out.println("Project metrics");
            out.println("---------------");
            metrics = new ArrayList(factory.getProjectMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getProjectMeasurements());
            printer.setExpandCollectionMeasurements(commandLine.getToggleSwitch("expand"));
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            out.println();
        }

        if (commandLine.getToggleSwitch("groups") || commandLine.getToggleSwitch("all")) {
            out.println("Group metrics");
            out.println("-------------");
            metrics = new ArrayList(factory.getGroupMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getGroupMeasurements());
            printer.setExpandCollectionMeasurements(commandLine.getToggleSwitch("expand"));
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            out.println();
        }

        if (commandLine.getToggleSwitch("classes") || commandLine.getToggleSwitch("all")) {
            out.println("Class metrics");
            out.println("-------------");
            metrics = new ArrayList(factory.getClassMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getClassMeasurements());
            printer.setExpandCollectionMeasurements(commandLine.getToggleSwitch("expand"));
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            out.println();
        }
        
        if (commandLine.getToggleSwitch("methods") || commandLine.getToggleSwitch("all")) {
            out.println("Method metrics");
            out.println("--------------");
            metrics = new ArrayList(factory.getMethodMetrics());
            Collections.sort(metrics, comparator);
            com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(out, factory.getConfiguration().getMethodMeasurements());
            printer.setExpandCollectionMeasurements(commandLine.getToggleSwitch("expand"));
            printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
            if (commandLine.isPresent("indent-text")) {
                printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            out.println();
        }
        
        out.close();
    }

    private static void printXMLFile(Date start, CommandLine commandLine, MetricsFactory factory) throws IOException {
        PrintWriter out;
        if (commandLine.isPresent("out")) {
            out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out") + ".xml"));
        } else {
            out = new PrintWriter(System.out);
        }

        MetricsComparator comparator = new MetricsComparator(commandLine.getSingleSwitch("sort"));
        if (commandLine.getToggleSwitch("reverse")) {
            comparator.reverse();
        }

        List               metrics;
        Iterator           i;
        com.jeantessier.metrics.Printer printer;

        metrics = new ArrayList(factory.getProjectMetrics());
        Collections.sort(metrics, comparator);
        printer = new com.jeantessier.metrics.XMLPrinter(out, factory.getConfiguration(), commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
        printer.setShowEmptyMetrics(commandLine.isPresent("show-empty-metrics"));
        printer.setShowHiddenMeasurements(commandLine.isPresent("show-hidden-measurements"));
        if (commandLine.isPresent("indent-text")) {
            printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
        }

        printer.visitMetrics(metrics);

        out.close();
    }
}
