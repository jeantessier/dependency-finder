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

    private CommandLine commandLine = new CommandLine();

    public DependencyExtractor() {
        getCommandLine().addMultipleValuesSwitch("filter-includes",         DEFAULT_FILTER_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("filter-excludes");
        getCommandLine().addToggleSwitch("package-filter");
        getCommandLine().addMultipleValuesSwitch("package-filter-includes");
        getCommandLine().addMultipleValuesSwitch("package-filter-excludes");
        getCommandLine().addToggleSwitch("class-filter");
        getCommandLine().addMultipleValuesSwitch("class-filter-includes");
        getCommandLine().addMultipleValuesSwitch("class-filter-excludes");
        getCommandLine().addToggleSwitch("feature-filter");
        getCommandLine().addMultipleValuesSwitch("feature-filter-includes");
        getCommandLine().addMultipleValuesSwitch("feature-filter-excludes");

        getCommandLine().addMultipleValuesSwitch("filter-includes-list");
        getCommandLine().addMultipleValuesSwitch("filter-excludes-list");

        getCommandLine().addToggleSwitch("xml");
        getCommandLine().addToggleSwitch("maximize");
        getCommandLine().addToggleSwitch("minimize");
        getCommandLine().addSingleValueSwitch("encoding",    com.jeantessier.dependency.XMLPrinter.DEFAULT_ENCODING);
        getCommandLine().addSingleValueSwitch("dtd-prefix",  com.jeantessier.dependency.XMLPrinter.DEFAULT_DTD_PREFIX);
        getCommandLine().addSingleValueSwitch("indent-text");
        getCommandLine().addToggleSwitch("time");
        getCommandLine().addSingleValueSwitch("out");
        getCommandLine().addToggleSwitch("help");
        getCommandLine().addOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
        getCommandLine().addToggleSwitch("version");
    }

    public void showError(CommandLineUsage clu, String msg, PrintStream out) {
        out.println(msg);
        showError(clu, out);
    }

    public void showError(CommandLineUsage clu, PrintStream out) {
        out.println(clu);
        out.println();
        out.println("If no files are specified, it processes the current directory.");
        out.println();
        out.println("If file is a directory, it is recusively scanned for files");
        out.println("ending in \".class\".");
        out.println();
        out.println("Defaults is text output to the console.");
        out.println();
    }

    public void showVersion(PrintStream out) {
        Version version = new Version();
        
        out.print(version.getImplementationTitle());
        out.print(" ");
        out.print(version.getImplementationVersion());
        out.print(" (c) ");
        out.print(version.getCopyrightDate());
        out.print(" ");
        out.print(version.getCopyrightHolder());
        out.println();
        
        out.print(version.getImplementationURL());
        out.println();
        
        out.print("Compiled on ");
        out.print(version.getImplementationDate());
        out.println();
    }

    public static void main(String[] args) throws Exception {
        DependencyExtractor command = new DependencyExtractor();

        // Parsing the command line
        CommandLine commandLine = command.getCommandLine();

        CommandLineUsage usage = new CommandLineUsage("DependencyExtractor");
        commandLine.accept(usage);

        try {
            commandLine.parse(args);
        } catch (IllegalArgumentException ex) {
            command.showError(usage, ex.toString(), System.err);
            System.exit(1);
        } catch (CommandLineException ex) {
            command.showError(usage, ex.toString(), System.err);
            System.exit(1);
        }

        if (commandLine.getToggleSwitch("help")) {
            command.showError(usage, System.err);
        }
        
        if (commandLine.getToggleSwitch("version")) {
            command.showVersion(System.err);
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

    public CommandLine getCommandLine() {
        return commandLine;
    }
}
