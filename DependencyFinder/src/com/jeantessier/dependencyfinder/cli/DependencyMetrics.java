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
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;

public class DependencyMetrics {
    public static final String DEFAULT_INCLUDES        = "//";
    public static final String DEFAULT_SCOPE_INCLUDES  = "//";
    public static final String DEFAULT_FILTER_INCLUDES = "//";
    public static final String DEFAULT_LOGFILE         = "System.out";

    public static void showError(CommandLineUsage clu, String msg) {
        System.err.println(msg);
        showError(clu);
    }

    public static void showError(CommandLineUsage clu) {
        System.err.println(clu);
        System.err.println();
        System.err.println("-p2p shorthand for the combination:");
        System.err.println("    -package-scope");
        System.err.println("    -package-filter");
        System.err.println();
        System.err.println("-c2p shorthand for the combination:");
        System.err.println("    -class-scope");
        System.err.println("    -package-filter");
        System.err.println();
        System.err.println("-c2c shorthand for the combination:");
        System.err.println("    -class-scope");
        System.err.println("    -class-filter");
        System.err.println();
        System.err.println("-f2f shorthand for the combination:");
        System.err.println("    -feature-scope");
        System.err.println("    -feature-filter");
        System.err.println();
        System.err.println("-includes \"str\" shorthand for the combination:");
        System.err.println("    -scope-includes \"str\"");
        System.err.println("    -filter-includes \"str\"");
        System.err.println();
        System.err.println("-excludes \"str\" shorthand for the combination:");
        System.err.println("    -scope-excludes \"str\"");
        System.err.println("    -filter-excludes \"str\"");
        System.err.println();
        System.err.println("-chart-all shorthand for the combination:");
        System.err.println("    -chart-classes-per-package");
        System.err.println("    -chart-features-per-class");
        System.err.println("    -chart-inbounds-per-package");
        System.err.println("    -chart-outbounds-per-package");
        System.err.println("    -chart-inbounds-per-class");
        System.err.println("    -chart-outbounds-per-class");
        System.err.println("    -chart-inbounds-per-feature");
        System.err.println("    -chart-outbounds-per-feature");
        System.err.println();
        System.err.println("-chart-inbounds shorthand for the combination:");
        System.err.println("    -chart-inbounds-per-package");
        System.err.println("    -chart-inbounds-per-class");
        System.err.println("    -chart-inbounds-per-feature");
        System.err.println();
        System.err.println("-chart-outbounds shorthand for the combination:");
        System.err.println("    -chart-outbounds-per-package");
        System.err.println("    -chart-outbounds-per-class");
        System.err.println("    -chart-outbounds-per-feature");
        System.err.println();
        System.err.println("-chart-packages shorthand for the combination:");
        System.err.println("    -chart-classes-per-package");
        System.err.println("    -chart-inbounds-per-package");
        System.err.println("    -chart-outbounds-per-package");
        System.err.println();
        System.err.println("-chart-classes shorthand for the combination:");
        System.err.println("    -chart-features-per-class");
        System.err.println("    -chart-inbounds-per-class");
        System.err.println("    -chart-outbounds-per-class");
        System.err.println();
        System.err.println("-chart-features shorthand for the combination:");
        System.err.println("    -chart-inbounds-per-feature");
        System.err.println("    -chart-outbounds-per-feature");
        System.err.println();
        System.err.println("If no files are specified, it processes the current directory.");
        System.err.println();
        System.err.println("If file is a directory, it is recusively scanned for files");
        System.err.println("ending in \".class\".");
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
        commandLine.addMultipleValuesSwitch("scope-includes",          DEFAULT_SCOPE_INCLUDES);
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

        commandLine.addToggleSwitch("p2p");
        commandLine.addToggleSwitch("c2p");
        commandLine.addToggleSwitch("c2c");
        commandLine.addToggleSwitch("f2f");
        commandLine.addMultipleValuesSwitch("includes",                DEFAULT_INCLUDES);
        commandLine.addMultipleValuesSwitch("excludes");

        commandLine.addMultipleValuesSwitch("scope-includes-list");
        commandLine.addMultipleValuesSwitch("scope-excludes-list");
        commandLine.addMultipleValuesSwitch("filter-includes-list");
        commandLine.addMultipleValuesSwitch("filter-excludes-list");

        commandLine.addToggleSwitch("list");
        commandLine.addToggleSwitch("chart-classes-per-package");
        commandLine.addToggleSwitch("chart-features-per-class");
        commandLine.addToggleSwitch("chart-inbounds-per-package");
        commandLine.addToggleSwitch("chart-outbounds-per-package");
        commandLine.addToggleSwitch("chart-inbounds-per-class");
        commandLine.addToggleSwitch("chart-outbounds-per-class");
        commandLine.addToggleSwitch("chart-inbounds-per-feature");
        commandLine.addToggleSwitch("chart-outbounds-per-feature");
        commandLine.addToggleSwitch("chart-inbounds");
        commandLine.addToggleSwitch("chart-outbounds");
        commandLine.addToggleSwitch("chart-packages");
        commandLine.addToggleSwitch("chart-classes");
        commandLine.addToggleSwitch("chart-features");
        commandLine.addToggleSwitch("chart-all");
        commandLine.addToggleSwitch("time");
        commandLine.addToggleSwitch("validate");
        commandLine.addSingleValueSwitch("out");
        commandLine.addToggleSwitch("help");
        commandLine.addOptionalValueSwitch("verbose",   DEFAULT_LOGFILE);
        commandLine.addToggleSwitch("version");

        CommandLineUsage usage = new CommandLineUsage("DependencyMetrics");
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

        if (commandLine.getToggleSwitch("p2p")) {
            commandLine.getSwitch("package-scope").setValue(true);
            commandLine.getSwitch("package-filter").setValue(true);
        }

        if (commandLine.getToggleSwitch("c2p")) {
            commandLine.getSwitch("class-scope").setValue(true);
            commandLine.getSwitch("package-filter").setValue(true);
        }

        if (commandLine.getToggleSwitch("c2c")) {
            commandLine.getSwitch("class-scope").setValue(true);
            commandLine.getSwitch("class-filter").setValue(true);
        }

        if (commandLine.getToggleSwitch("f2f")) {
            commandLine.getSwitch("feature-scope").setValue(true);
            commandLine.getSwitch("feature-filter").setValue(true);
        }

        if (commandLine.isPresent("includes")) {
            for (String value : commandLine.getMultipleSwitch("includes")) {
                commandLine.getSwitch("scope-includes").setValue(value);
                commandLine.getSwitch("filter-includes").setValue(value);
            }
        }

        if (commandLine.isPresent("excludes")) {
            for (String value : commandLine.getMultipleSwitch("excludes")) {
                commandLine.getSwitch("scope-excludes").setValue(value);
                commandLine.getSwitch("filter-excludes").setValue(value);
            }
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
        
        PrintWriter out;
        if (commandLine.isPresent("out")) {
            out = new PrintWriter(new FileWriter(commandLine.getSingleSwitch("out")));
        } else {
            out = new PrintWriter(new OutputStreamWriter(System.out));
        }

        MetricsReport reporter = new MetricsReport(out);
        
        reporter.setListingElements(commandLine.getToggleSwitch("list"));
        reporter.setChartingClassesPerPackage(commandLine.getToggleSwitch("chart-classes-per-package"));
        reporter.setChartingFeaturesPerClass(commandLine.getToggleSwitch("chart-features-per-class"));
        reporter.setChartingInboundsPerPackage(commandLine.getToggleSwitch("chart-inbounds-per-package"));
        reporter.setChartingOutboundsPerPackage(commandLine.getToggleSwitch("chart-outbounds-per-package"));
        reporter.setChartingInboundsPerClass(commandLine.getToggleSwitch("chart-inbounds-per-class"));
        reporter.setChartingOutboundsPerClass(commandLine.getToggleSwitch("chart-outbounds-per-class"));
        reporter.setChartingInboundsPerFeature(commandLine.getToggleSwitch("chart-inbounds-per-feature"));
        reporter.setChartingOutboundsPerFeature(commandLine.getToggleSwitch("chart-outbounds-per-feature"));

        if (commandLine.getToggleSwitch("chart-all")) {
            reporter.setChartingClassesPerPackage(true);
            reporter.setChartingFeaturesPerClass(true);
            reporter.setChartingInboundsPerPackage(true);
            reporter.setChartingOutboundsPerPackage(true);
            reporter.setChartingInboundsPerClass(true);
            reporter.setChartingOutboundsPerClass(true);
            reporter.setChartingInboundsPerFeature(true);
            reporter.setChartingOutboundsPerFeature(true);
        }
        
        if (commandLine.getToggleSwitch("chart-inbounds")) {
            reporter.setChartingInboundsPerPackage(true);
            reporter.setChartingInboundsPerClass(true);
            reporter.setChartingInboundsPerFeature(true);
        }
        
        if (commandLine.getToggleSwitch("chart-outbounds")) {
            reporter.setChartingOutboundsPerPackage(true);
            reporter.setChartingOutboundsPerClass(true);
            reporter.setChartingOutboundsPerFeature(true);
        }
        
        if (commandLine.getToggleSwitch("chart-packages")) {
            reporter.setChartingClassesPerPackage(true);
            reporter.setChartingInboundsPerPackage(true);
            reporter.setChartingOutboundsPerPackage(true);
        }
        
        if (commandLine.getToggleSwitch("chart-classes")) {
            reporter.setChartingFeaturesPerClass(true);
            reporter.setChartingInboundsPerClass(true);
            reporter.setChartingOutboundsPerClass(true);
        }
        
        if (commandLine.getToggleSwitch("chart-features")) {
            reporter.setChartingInboundsPerFeature(true);
            reporter.setChartingOutboundsPerFeature(true);
        }

        SelectionCriteria scopeCriteria = new ComprehensiveSelectionCriteria();

        if (hasScopeRegularExpressionSwitches(commandLine)) {
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
        } else if (hasScopeListSwitches(commandLine)) {
            scopeCriteria = createCollectionSelectionCriteria(commandLine.getMultipleSwitch("scope-includes-list"), commandLine.getMultipleSwitch("scope-excludes-list"));
        }

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

        Logger.getLogger(DependencyMetrics.class).info("Reporting on " + factory.getPackages().size() + " package(s) ...");
        verboseListener.print("Reporting on " + factory.getPackages().size() + " package(s) ...");

        MetricsGatherer metrics = new MetricsGatherer(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
        metrics.traverseNodes(factory.getPackages().values());
        reporter.process(metrics);

        out.close();
        
        Date end = new Date();

        if (commandLine.getToggleSwitch("time")) {
            System.err.println(DependencyMetrics.class.getName() + ": " + ((end.getTime() - (double) start.getTime()) / 1000) + " secs.");
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
                    Logger.getLogger(DependencyReporter.class).error("Couldn't read file " + filename, ex);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DependencyReporter.class).error("Couldn't close file " + filename, ex);
                    }
                }
            }
        }

        return result;
    }
}
