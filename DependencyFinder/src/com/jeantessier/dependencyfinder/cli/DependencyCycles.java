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

import com.jeantessier.commandline.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.dependency.*;

public class DependencyCycles {
    public static final String DEFAULT_INCLUDES        = "//";
    public static final String DEFAULT_SCOPE_INCLUDES  = "//";
    public static final String DEFAULT_LOGFILE         = "System.out";

    public static void showError(CommandLineUsage clu, String msg) {
        System.err.println(msg);
        DependencyCycles.showError(clu);
    }

    public static void showError(CommandLineUsage clu) {
        System.err.println(clu);
        System.err.println();
        System.err.println("Default is text output to the console.");
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
        CommandLine commandLine = new CommandLine(new AtLeastParameterStrategy(1));
        commandLine.addMultipleValuesSwitch("scope-includes",          DependencyCycles.DEFAULT_SCOPE_INCLUDES);
        commandLine.addMultipleValuesSwitch("scope-excludes");
        commandLine.addToggleSwitch("package-scope");
        commandLine.addMultipleValuesSwitch("package-scope-includes");
        commandLine.addMultipleValuesSwitch("package-scope-excludes");
        commandLine.addToggleSwitch("class-scope");
        commandLine.addMultipleValuesSwitch("class-scope-includes");
        commandLine.addMultipleValuesSwitch("class-scope-excludes");
        commandLine.addToggleSwitch("feature-scope");
        commandLine.addMultipleValuesSwitch("feature-scope-includes");
        commandLine.addMultipleValuesSwitch("feature-scope-excludes");

        commandLine.addMultipleValuesSwitch("scope-includes-list");
        commandLine.addMultipleValuesSwitch("scope-excludes-list");

        commandLine.addSingleValueSwitch("maximum-cycle-length");

        commandLine.addToggleSwitch("xml");
        commandLine.addToggleSwitch("validate");
        commandLine.addSingleValueSwitch("encoding",                   XMLPrinter.DEFAULT_ENCODING);
        commandLine.addSingleValueSwitch("dtd-prefix",                 XMLPrinter.DEFAULT_DTD_PREFIX);
        commandLine.addSingleValueSwitch("indent-text");
        commandLine.addToggleSwitch("time");
        commandLine.addSingleValueSwitch("out");
        commandLine.addToggleSwitch("help");
        commandLine.addOptionalValueSwitch("verbose",                  DependencyCycles.DEFAULT_LOGFILE);
        commandLine.addToggleSwitch("version");

        CommandLineUsage usage = new CommandLineUsage("DependencyReporter");
        commandLine.accept(usage);

        try {
            commandLine.parse(args);
        } catch (IllegalArgumentException ex) {
            DependencyCycles.showError(usage, ex.toString());
            System.exit(1);
        } catch (CommandLineException ex) {
            DependencyCycles.showError(usage, ex.toString());
            System.exit(1);
        }

        if (commandLine.getToggleSwitch("help")) {
            DependencyCycles.showError(usage);
        }

        if (commandLine.getToggleSwitch("version")) {
            DependencyCycles.showVersion();
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

        if (DependencyCycles.hasScopeRegularExpressionSwitches(commandLine) && DependencyCycles.hasScopeListSwitches(commandLine)) {
            DependencyCycles.showError(usage, "You can use switches for regular expressions or lists for scope, but not at the same time");
        }

        /*
        *  Beginning of main processing
        */

        Date start = new Date();

        NodeFactory factory = new NodeFactory();

        for (String filename : commandLine.getParameters()) {
            Logger.getLogger(DependencyMetrics.class).info("Reading " + filename);
            verboseListener.print("Reading " + filename);

            if (filename.endsWith(".xml")) {
                NodeLoader loader = new NodeLoader(factory, commandLine.getToggleSwitch("validate"));
                loader.addDependencyListener(verboseListener);
                loader.load(filename);
            }

            Logger.getLogger(DependencyMetrics.class).info("Read \"" + filename + "\".");
        }

        SelectionCriteria scopeCriteria = new ComprehensiveSelectionCriteria();

        if (DependencyCycles.hasScopeRegularExpressionSwitches(commandLine)) {
            RegularExpressionSelectionCriteria regularExpressionScopeCriteria = new RegularExpressionSelectionCriteria();

            if (commandLine.isPresent("package-scope") || commandLine.isPresent("class-scope") || commandLine.isPresent("feature-scope")) {
                regularExpressionScopeCriteria.setMatchingPackages(commandLine.getToggleSwitch("package-scope"));
                regularExpressionScopeCriteria.setMatchingClasses(commandLine.getToggleSwitch("class-scope"));
                regularExpressionScopeCriteria.setMatchingFeatures(commandLine.getToggleSwitch("feature-scope"));
            }

            if (commandLine.isPresent("scope-includes") || (!commandLine.isPresent("package-scope-includes") && !commandLine.isPresent("class-scope-includes") && !commandLine.isPresent("feature-scope-includes"))) {
                // Only use the default if nothing else has been specified.
                regularExpressionScopeCriteria.setGlobalIncludes(commandLine.getMultipleSwitch("scope-includes"));
            }
            regularExpressionScopeCriteria.setGlobalExcludes(commandLine.getMultipleSwitch("scope-excludes"));
            regularExpressionScopeCriteria.setPackageIncludes(commandLine.getMultipleSwitch("package-scope-includes"));
            regularExpressionScopeCriteria.setPackageExcludes(commandLine.getMultipleSwitch("package-scope-excludes"));
            regularExpressionScopeCriteria.setClassIncludes(commandLine.getMultipleSwitch("class-scope-includes"));
            regularExpressionScopeCriteria.setClassExcludes(commandLine.getMultipleSwitch("class-scope-excludes"));
            regularExpressionScopeCriteria.setFeatureIncludes(commandLine.getMultipleSwitch("feature-scope-includes"));
            regularExpressionScopeCriteria.setFeatureExcludes(commandLine.getMultipleSwitch("feature-scope-excludes"));

            scopeCriteria = regularExpressionScopeCriteria;
        } else if (DependencyCycles.hasScopeListSwitches(commandLine)) {
            scopeCriteria = DependencyCycles.createCollectionSelectionCriteria(commandLine.getMultipleSwitch("scope-includes-list"), commandLine.getMultipleSwitch("scope-excludes-list"));
        }

        CycleDetector detector = new CycleDetector(scopeCriteria);

        if (commandLine.isPresent("maximum-cycle-length")) {
            detector.setMaximumCycleLength(Integer.parseInt(commandLine.getSingleSwitch("maximum-cycle-length")));
        }

        detector.traverseNodes(factory.getPackages().values());

        verboseListener.print("Printing the graph ...");

        PrintWriter out;
        if (commandLine.isPresent("out")) {
            out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
        } else {
            out = new PrintWriter(System.out);
        }

        CyclePrinter printer;
        if (commandLine.isPresent("xml")) {
            printer = new XMLCyclePrinter(out, commandLine.getSingleSwitch("encoding"), commandLine.getSingleSwitch("dtd-prefix"));
        } else {
            printer = new TextCyclePrinter(out);
        }

        if (commandLine.isPresent("indent-text")) {
            printer.setIndentText(commandLine.getSingleSwitch("indent-text"));
        }

        printer.visitCycles(detector.getCycles());

        out.close();

        Date end = new Date();

        if (commandLine.getToggleSwitch("time")) {
            System.err.println(DependencyCycles.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
        }

        verboseListener.close();
    }

    private static boolean hasScopeRegularExpressionSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("scope-includes") ||
            switches.contains("scope-excludes") ||
            switches.contains("package-scope") ||
            switches.contains("package-scope-includes") ||
            switches.contains("package-scope-excludes") ||
            switches.contains("class-scope") ||
            switches.contains("class-scope-includes") ||
            switches.contains("class-scope-excludes") ||
            switches.contains("feature-scope") ||
            switches.contains("feature-scope-includes") ||
            switches.contains("feature-scope-excludes");
    }

    private static boolean hasScopeListSwitches(CommandLine commandLine) {
        Collection<String> switches = commandLine.getPresentSwitches();

        return
            switches.contains("scope-includes-list") ||
            switches.contains("scope-excludes-list");
    }

    private static CollectionSelectionCriteria createCollectionSelectionCriteria(Collection<String> includes, Collection<String> excludes) {
        return new CollectionSelectionCriteria(DependencyCycles.loadCollection(includes), DependencyCycles.loadCollection(excludes));
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
                    Logger.getLogger(DependencyCycles.class).error("Couldn't read file " + filename, ex);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DependencyCycles.class).error("Couldn't close file " + filename, ex);
                    }
                }
            }
        }

        return result;
    }
}
