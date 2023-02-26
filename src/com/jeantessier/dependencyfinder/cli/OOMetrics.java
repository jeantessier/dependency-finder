/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

import com.jeantessier.classreader.AggregatingClassfileLoader;
import com.jeantessier.classreader.ClassfileLoader;
import com.jeantessier.classreader.LoadListenerVisitorAdapter;
import com.jeantessier.classreader.TransientClassfileLoader;
import com.jeantessier.commandline.CommandLineException;
import com.jeantessier.metrics.Metrics;
import com.jeantessier.metrics.MetricsComparator;
import com.jeantessier.metrics.MetricsConfigurationLoader;
import com.jeantessier.metrics.MetricsFactory;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class OOMetrics extends DirectoryExplorerCommand {
    public static final String DEFAULT_PROJECT_NAME = "Project";
    public static final String DEFAULT_SORT = "name";

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();
        populateCommandLineSwitchesForXMLOutput(com.jeantessier.metrics.XMLPrinter.DEFAULT_ENCODING, com.jeantessier.metrics.XMLPrinter.DEFAULT_DTD_PREFIX, com.jeantessier.metrics.XMLPrinter.DEFAULT_INDENT_TEXT);

        getCommandLine().addSingleValueSwitch("project-name", DEFAULT_PROJECT_NAME);
        getCommandLine().addSingleValueSwitch("default-configuration", true);
        getCommandLine().addSingleValueSwitch("configuration");
        getCommandLine().addToggleSwitch("csv");
        getCommandLine().addToggleSwitch("txt");
        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("validate");
        getCommandLine().addToggleSwitch("project");
        getCommandLine().addToggleSwitch("groups");
        getCommandLine().addToggleSwitch("classes");
        getCommandLine().addToggleSwitch("methods");
        getCommandLine().addMultipleValuesSwitch("scope-includes-list");
        getCommandLine().addMultipleValuesSwitch("scope-excludes-list");
        getCommandLine().addMultipleValuesSwitch("filter-includes-list");
        getCommandLine().addMultipleValuesSwitch("filter-excludes-list");
        getCommandLine().addToggleSwitch("show-all-metrics");
        getCommandLine().addToggleSwitch("show-empty-metrics");
        getCommandLine().addToggleSwitch("show-hidden-measurements");
        getCommandLine().addSingleValueSwitch("sort", DEFAULT_SORT);
        getCommandLine().addToggleSwitch("expand");
        getCommandLine().addToggleSwitch("reverse");
        getCommandLine().addToggleSwitch("enable-cross-class-measurements");
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        if (!getCommandLine().isPresent("project") && !getCommandLine().isPresent("groups") && !getCommandLine().isPresent("classes") && !getCommandLine().isPresent("methods")) {
            getCommandLine().getSwitch("project").setValue(true);
            getCommandLine().getSwitch("groups").setValue(true);
            getCommandLine().getSwitch("classes").setValue(true);
            getCommandLine().getSwitch("methods").setValue(true);
        }

        int modeSwitch = 0;

        if (getCommandLine().getToggleSwitch("csv")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("txt")) {
            modeSwitch++;
        }
        if (getCommandLine().getToggleSwitch("xml")) {
            modeSwitch++;
        }
        if (modeSwitch != 1) {
            exceptions.add(new CommandLineException("Must have one and only one of -csv, -txt, or -xml"));
        }

        return exceptions;
    }

    protected void doProcessing() throws Exception {
        Logger.getLogger(OOMetrics.class).debug("Reading configuration ...");
        getVerboseListener().print("Reading configuration ...");

        String projectName = getCommandLine().getSingleSwitch("project-name");

        MetricsFactory factory;
        if (getCommandLine().isPresent("configuration")) {
            factory = new MetricsFactory(projectName, new MetricsConfigurationLoader(getCommandLine().getToggleSwitch("validate")).load(getCommandLine().getSingleSwitch("configuration")));
        } else {
            factory = new MetricsFactory(projectName, new MetricsConfigurationLoader(getCommandLine().getToggleSwitch("validate")).load(getCommandLine().getSingleSwitch("default-configuration")));
        }

        com.jeantessier.metrics.MetricsGatherer gatherer = new com.jeantessier.metrics.MetricsGatherer(factory);
        if (getCommandLine().isPresent("scope-includes-list") || getCommandLine().isPresent("scope-excludes-list")) {
            gatherer.setScopeIncludes(createCollection(getCommandLine().getMultipleSwitch("scope-includes-list"), getCommandLine().getMultipleSwitch("scope-excludes-list")));
        }
        if (getCommandLine().isPresent("filter-includes-list") || getCommandLine().isPresent("filter-excludes-list")) {
            gatherer.setFilterIncludes(createCollection(getCommandLine().getMultipleSwitch("filter-includes-list"), getCommandLine().getMultipleSwitch("filter-excludes-list")));
        }
        gatherer.addMetricsListener(getVerboseListener());

        if (getCommandLine().isPresent("enable-cross-class-measurements")) {
            Logger.getLogger(OOMetrics.class).debug("Reading in all classes ...");
            getVerboseListener().print("Reading in all classes ...");
            ClassfileLoader loader = new AggregatingClassfileLoader();
            loader.addLoadListener(getVerboseListener());
            loader.load(getCommandLine().getParameters());

            Logger.getLogger(OOMetrics.class).debug("Computing metrics ...");
            getVerboseListener().print("Computing metrics ...");
            gatherer.visitClassfiles(loader.getAllClassfiles());
        } else {
            ClassfileLoader loader = new TransientClassfileLoader();
            loader.addLoadListener(getVerboseListener());
            loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));

            Logger.getLogger(OOMetrics.class).debug("Reading classes and computing metrics as we go ...");
            getVerboseListener().print("Reading classes and computing metrics as we go ...");
            loader.load(getCommandLine().getParameters());
        }

        if (getCommandLine().isPresent("show-all-metrics")) {
            gatherer.getMetricsFactory().getAllClassMetrics().forEach(metrics ->
                    gatherer.getMetricsFactory().includeClassMetrics(metrics)
            );

            gatherer.getMetricsFactory().getAllMethodMetrics().forEach(metrics ->
                    gatherer.getMetricsFactory().includeMethodMetrics(metrics)
            );
        }

        Logger.getLogger(OOMetrics.class).debug("Printing results ...");
        getVerboseListener().print("Printing results ...");

        if (getCommandLine().isPresent("csv")) {
            printCSVFiles(gatherer.getMetricsFactory());
        } else if (getCommandLine().isPresent("txt")) {
            printTextFile(gatherer.getMetricsFactory());
        } else if (getCommandLine().isPresent("xml")) {
            printXMLFile(gatherer.getMetricsFactory());
        }

        Logger.getLogger(OOMetrics.class).debug("Done.");
    }

    private static Collection<String> createCollection(Collection<String> includes, Collection<String> excludes) throws IOException {
        Collection<String> result = new HashSet<>();

        for (String include : includes) {
            result.addAll(Files.readAllLines(Paths.get(include)));
        }

        for (String exclude : excludes) {
            result.removeAll(Files.readAllLines(Paths.get(exclude)));
        }

        return result;
    }

    private void printCSVFiles(MetricsFactory factory) throws IOException {
        MetricsComparator comparator = new MetricsComparator(getCommandLine().getSingleSwitch("sort"));
        if (getCommandLine().getToggleSwitch("reverse")) {
            comparator.reverse();
        }

        if (getCommandLine().getToggleSwitch("project")) {
            if (getCommandLine().isPresent("out")) {
                setOut(new PrintWriter(new FileWriter(getCommandLine().getSingleSwitch("out") + "_project.csv")));
            } else {
                getOut().println("Project:");
            }

            List<Metrics> metrics = new ArrayList<>(factory.getProjectMetrics());
            metrics.sort(comparator);

            com.jeantessier.metrics.Printer printer = new com.jeantessier.metrics.CSVPrinter(getOut(), factory.getConfiguration().getProjectMeasurements());
            printer.setShowEmptyMetrics(getCommandLine().isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(getCommandLine().isPresent("show-hidden-measurements"));
            if (getCommandLine().isPresent("indent-text")) {
                printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            if (getCommandLine().isPresent("out")) {
                getOut().close();
            } else {
                getOut().println();
            }
        }

        if (getCommandLine().getToggleSwitch("groups")) {
            if (getCommandLine().isPresent("out")) {
                setOut(new PrintWriter(new FileWriter(getCommandLine().getSingleSwitch("out") + "_groups.csv")));
            } else {
                getOut().println("Packages:");
            }

            List<Metrics> metrics = new ArrayList<>(factory.getGroupMetrics());
            metrics.sort(comparator);

            com.jeantessier.metrics.Printer printer = new com.jeantessier.metrics.CSVPrinter(getOut(), factory.getConfiguration().getGroupMeasurements());
            printer.setShowEmptyMetrics(getCommandLine().isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(getCommandLine().isPresent("show-hidden-measurements"));
            if (getCommandLine().isPresent("indent-text")) {
                printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            if (getCommandLine().isPresent("out")) {
                getOut().close();
            } else {
                getOut().println();
            }
        }

        if (getCommandLine().getToggleSwitch("classes")) {
            if (getCommandLine().isPresent("out")) {
                setOut(new PrintWriter(new FileWriter(getCommandLine().getSingleSwitch("out") + "_classes.csv")));
            } else {
                getOut().println("Classes:");
            }

            List<Metrics> metrics = new ArrayList<>(factory.getClassMetrics());
            metrics.sort(comparator);

            com.jeantessier.metrics.Printer printer = new com.jeantessier.metrics.CSVPrinter(getOut(), factory.getConfiguration().getClassMeasurements());
            printer.setShowEmptyMetrics(getCommandLine().isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(getCommandLine().isPresent("show-hidden-measurements"));
            if (getCommandLine().isPresent("indent-text")) {
                printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            if (getCommandLine().isPresent("out")) {
                getOut().close();
            } else {
                getOut().println();
            }
        }

        if (getCommandLine().getToggleSwitch("methods")) {
            if (getCommandLine().isPresent("out")) {
                setOut(new PrintWriter(new FileWriter(getCommandLine().getSingleSwitch("out") + "_methods.csv")));
            } else {
                getOut().println("Methods:");
            }

            List<Metrics> metrics = new ArrayList<>(factory.getMethodMetrics());
            metrics.sort(comparator);

            com.jeantessier.metrics.Printer printer = new com.jeantessier.metrics.CSVPrinter(getOut(), factory.getConfiguration().getMethodMeasurements());
            printer.setShowEmptyMetrics(getCommandLine().isPresent("show-empty-metrics"));
            printer.setShowHiddenMeasurements(getCommandLine().isPresent("show-hidden-measurements"));
            if (getCommandLine().isPresent("indent-text")) {
                printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
            }

            printer.visitMetrics(metrics);

            if (getCommandLine().isPresent("out")) {
                getOut().close();
            }
        }
    }

    private void printTextFile(MetricsFactory factory) throws IOException {
        MetricsComparator comparator = new MetricsComparator(getCommandLine().getSingleSwitch("sort"));
        if (getCommandLine().getToggleSwitch("reverse")) {
            comparator.reverse();
        }

        com.jeantessier.metrics.TextPrinter printer = new com.jeantessier.metrics.TextPrinter(getOut(), factory.getConfiguration().getProjectMeasurements());
        printer.setExpandCollectionMeasurements(getCommandLine().getToggleSwitch("expand"));
        printer.setShowEmptyMetrics(getCommandLine().isPresent("show-empty-metrics"));
        printer.setShowHiddenMeasurements(getCommandLine().isPresent("show-hidden-measurements"));
        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        if (getCommandLine().getToggleSwitch("project")) {
            getOut().println("Project metrics");
            getOut().println("---------------");

            List<Metrics> metrics = new ArrayList<>(factory.getProjectMetrics());
            metrics.sort(comparator);
            printer.visitMetrics(metrics);

            getOut().println();
        }

        if (getCommandLine().getToggleSwitch("groups")) {
            getOut().println("Group metrics");
            getOut().println("-------------");

            List<Metrics> metrics = new ArrayList<>(factory.getGroupMetrics());
            metrics.sort(comparator);
            printer.visitMetrics(metrics);

            getOut().println();
        }

        if (getCommandLine().getToggleSwitch("classes")) {
            getOut().println("Class metrics");
            getOut().println("-------------");

            List<Metrics> metrics = new ArrayList<>(factory.getClassMetrics());
            metrics.sort(comparator);
            printer.visitMetrics(metrics);

            getOut().println();
        }

        if (getCommandLine().getToggleSwitch("methods")) {
            getOut().println("Method metrics");
            getOut().println("--------------");

            List<Metrics> metrics = new ArrayList<>(factory.getMethodMetrics());
            metrics.sort(comparator);
            printer.visitMetrics(metrics);

            getOut().println();
        }

        getOut().close();
    }

    private void printXMLFile(MetricsFactory factory) throws IOException {
        MetricsComparator comparator = new MetricsComparator(getCommandLine().getSingleSwitch("sort"));
        if (getCommandLine().getToggleSwitch("reverse")) {
            comparator.reverse();
        }

        List<Metrics> metrics = new ArrayList<>(factory.getProjectMetrics());
        metrics.sort(comparator);

        com.jeantessier.metrics.Printer printer = new com.jeantessier.metrics.XMLPrinter(getOut(), factory.getConfiguration(), getCommandLine().getSingleSwitch("encoding"), getCommandLine().getSingleSwitch("dtd-prefix"));
        printer.setShowEmptyMetrics(getCommandLine().isPresent("show-empty-metrics"));
        printer.setShowHiddenMeasurements(getCommandLine().isPresent("show-hidden-measurements"));
        if (getCommandLine().isPresent("indent-text")) {
            printer.setIndentText(getCommandLine().getSingleSwitch("indent-text"));
        }

        printer.visitMetrics(metrics);

        getOut().close();
    }

    public static void main(String[] args) throws Exception {
        new OOMetrics().run(args);
    }
}
