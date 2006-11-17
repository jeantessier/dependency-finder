/*
 *  Copyright (c) 2001-2006, Jean Tessier
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
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;

public class DependencyExtractor {
    public static final String DEFAULT_FILTER_INCLUDES = "//";
    public static final String DEFAULT_LOGFILE = "System.out";

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

    public static void showVersion() {
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
        commandLine.addMultipleValuesSwitch("filter-includes",         DEFAULT_FILTER_INCLUDES);
        commandLine.addMultipleValuesSwitch("filter-excludes");
        commandLine.addToggleSwitch("package-filter");
        commandLine.addMultipleValuesSwitch("package-filter-includes");
        commandLine.addMultipleValuesSwitch("package-filter-excludes");
        commandLine.addToggleSwitch("class-filter");
        commandLine.addMultipleValuesSwitch("class-filter-includes");
        commandLine.addMultipleValuesSwitch("class-filter-excludes");
        commandLine.addToggleSwitch("feature-filter");
        commandLine.addMultipleValuesSwitch("feature-filter-includes");
        commandLine.addMultipleValuesSwitch("feature-filter-excludes");

        commandLine.addMultipleValuesSwitch("filter-includes-list");
        commandLine.addMultipleValuesSwitch("filter-excludes-list");

        commandLine.addToggleSwitch("xml");
        commandLine.addToggleSwitch("maximize");
        commandLine.addToggleSwitch("minimize");
        commandLine.addSingleValueSwitch("encoding",    com.jeantessier.dependency.XMLPrinter.DEFAULT_ENCODING);
        commandLine.addSingleValueSwitch("dtd-prefix",  com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX);
        commandLine.addSingleValueSwitch("indent-text");
        commandLine.addToggleSwitch("time");
        commandLine.addSingleValueSwitch("out");
        commandLine.addToggleSwitch("help");
        commandLine.addOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
        commandLine.addToggleSwitch("version");

        CommandLineUsage usage = new CommandLineUsage("DependencyExtractor");
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

        SelectionCriteria filterCriteria = new ComprehensiveSelectionCriteria();

        if (hasFilterRegularExpressionSwitches(commandLine)) {
            RegularExpressionSelectionCriteria regularExpressionFilterCriteria = new RegularExpressionSelectionCriteria();

            if (commandLine.isPresent("package-filter") || commandLine.isPresent("class-filter") || commandLine.isPresent("feature-filter")) {
                regularExpressionFilterCriteria.setMatchingPackages(commandLine.getToggleSwitch("package-filter"));
                regularExpressionFilterCriteria.setMatchingClasses(commandLine.getToggleSwitch("class-filter"));
                regularExpressionFilterCriteria.setMatchingFeatures(commandLine.getToggleSwitch("feature-filter"));
            }

            if (commandLine.isPresent("filter-includes") || (!commandLine.isPresent("package-filter-includes") && !commandLine.isPresent("class-filter-includes") && !commandLine.isPresent("feature-filter-includes"))) {
                // Only use the default if nothing else has been specified.
                regularExpressionFilterCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("filter-includes"));
            }
            regularExpressionFilterCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("filter-excludes"));
            regularExpressionFilterCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-filter-includes"));
            regularExpressionFilterCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-filter-excludes"));
            regularExpressionFilterCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-filter-includes"));
            regularExpressionFilterCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-filter-excludes"));
            regularExpressionFilterCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-filter-includes"));
            regularExpressionFilterCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-filter-excludes"));

            filterCriteria = regularExpressionFilterCriteria;
        } else if (hasFilterListSwitches(commandLine)) {
            filterCriteria = createCollectionSelectionCriteria(commandLine.getMultipleSwitch("filter-includes-list"), commandLine.getMultipleSwitch("filter-excludes-list"));
        }

        List<String> parameters = commandLine.getParameters();
        if (parameters.size() == 0) {
            parameters.add(".");
        }

        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory, filterCriteria);
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(collector));
        loader.addLoadListener(verboseListener);
        loader.load(parameters);

        if (commandLine.isPresent("minimize")) {
            LinkMinimizer minimizer = new LinkMinimizer();
            minimizer.traverseNodes(factory.getPackages().values());
        } else if (commandLine.isPresent("maximize")) {
            LinkMaximizer maximizer = new LinkMaximizer();
            maximizer.traverseNodes(factory.getPackages().values());
        }

        verboseListener.print("Printing the graph ...");

        PrintWriter out;
        if (commandLine.isPresent("out")) {
            out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
        } else {
            out = new PrintWriter(System.out);
        }
            
        com.jeantessier.dependency.Printer printer;
        if (commandLine.getToggleSwitch("xml")) {
            printer = new com.jeantessier.dependency.XMLPrinter(out, commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
        } else {
            printer = new com.jeantessier.dependency.TextPrinter(out);
        }
            
        if (commandLine.isPresent("indent-text")) {
            printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
        }

        printer.traverseNodes(factory.getPackages().values());

        out.close();

        Date end = new Date();

        if (commandLine.getToggleSwitch("time")) {
            System.err.println(DependencyExtractor.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
        }

        verboseListener.close();
    }

    private static boolean hasFilterRegularExpressionSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("filter-includes") ||
            switches.contains("filter-excludes") ||
            switches.contains("package-filter") ||
            switches.contains("package-filter-includes") ||
            switches.contains("package-filter-excludes") ||
            switches.contains("class-filter") ||
            switches.contains("class-filter-includes") ||
            switches.contains("class-filter-excludes") ||
            switches.contains("feature-filter") ||
            switches.contains("feature-filter-includes") ||
            switches.contains("feature-filter-excludes");
    }

    private static boolean hasFilterListSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("filter-includes-list") ||
            switches.contains("filter-excludes-list");
    }

    private static CollectionSelectionCriteria createCollectionSelectionCriteria(Collection<String> includes, Collection<String> excludes) {
        return new CollectionSelectionCriteria(loadCollection(includes), loadCollection(excludes));
    }

    private static Collection<String> loadCollection(Collection<String> filenames) {
        Collection<String> result = null;

        if (!filenames.isEmpty()) {
            result = new HashSet<String>();

            for (String filename : filenames) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(filename));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.add(line);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(DependencyExtractor.class).error("Couldn't read file " + filename, ex);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DependencyExtractor.class).error("Couldn't close file " + filename, ex);
                    }
                }
            }
        }

        return result;
    }
}
