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

import com.jeantessier.dependency.*;
import com.jeantessier.commandline.*;

import org.apache.log4j.*;

public class DependencyMetrics extends Command {
    public DependencyMetrics() {
        super("DependencyMetrics");
    }

    protected void showSpecificUsage(PrintStream out) {
        out.println();
        out.println("-p2p shorthand for the combination:");
        out.println("    -package-scope");
        out.println("    -package-filter");
        out.println();
        out.println("-c2p shorthand for the combination:");
        out.println("    -class-scope");
        out.println("    -package-filter");
        out.println();
        out.println("-c2c shorthand for the combination:");
        out.println("    -class-scope");
        out.println("    -class-filter");
        out.println();
        out.println("-f2f shorthand for the combination:");
        out.println("    -feature-scope");
        out.println("    -feature-filter");
        out.println();
        out.println("-includes \"str\" shorthand for the combination:");
        out.println("    -scope-includes \"str\"");
        out.println("    -filter-includes \"str\"");
        out.println();
        out.println("-excludes \"str\" shorthand for the combination:");
        out.println("    -scope-excludes \"str\"");
        out.println("    -filter-excludes \"str\"");
        out.println();
        out.println("-chart-all shorthand for the combination:");
        out.println("    -chart-classes-per-package");
        out.println("    -chart-features-per-class");
        out.println("    -chart-inbounds-per-package");
        out.println("    -chart-outbounds-per-package");
        out.println("    -chart-inbounds-per-class");
        out.println("    -chart-outbounds-per-class");
        out.println("    -chart-inbounds-per-feature");
        out.println("    -chart-outbounds-per-feature");
        out.println();
        out.println("-chart-inbounds shorthand for the combination:");
        out.println("    -chart-inbounds-per-package");
        out.println("    -chart-inbounds-per-class");
        out.println("    -chart-inbounds-per-feature");
        out.println();
        out.println("-chart-outbounds shorthand for the combination:");
        out.println("    -chart-outbounds-per-package");
        out.println("    -chart-outbounds-per-class");
        out.println("    -chart-outbounds-per-feature");
        out.println();
        out.println("-chart-packages shorthand for the combination:");
        out.println("    -chart-classes-per-package");
        out.println("    -chart-inbounds-per-package");
        out.println("    -chart-outbounds-per-package");
        out.println();
        out.println("-chart-classes shorthand for the combination:");
        out.println("    -chart-features-per-class");
        out.println("    -chart-inbounds-per-class");
        out.println("    -chart-outbounds-per-class");
        out.println();
        out.println("-chart-features shorthand for the combination:");
        out.println("    -chart-inbounds-per-feature");
        out.println("    -chart-outbounds-per-feature");
        out.println();
        out.println("If no files are specified, it processes the current directory.");
        out.println();
        out.println("If file is a directory, it is recusively scanned for files");
        out.println("ending in \".class\".");
        out.println();
        out.println("Default is text output to the console.");
        out.println();
    }

    protected void populateCommandLineSwitches() {
        super.populateCommandLineSwitches();

        populateCommandLineSwitchesForScoping();
        populateCommandLineSwitchesForFiltering();

        getCommandLine().addToggleSwitch("p2p");
        getCommandLine().addToggleSwitch("c2p");
        getCommandLine().addToggleSwitch("c2c");
        getCommandLine().addToggleSwitch("f2f");
        getCommandLine().addMultipleValuesSwitch("includes", DEFAULT_INCLUDES);
        getCommandLine().addMultipleValuesSwitch("excludes");

        getCommandLine().addToggleSwitch("list");
        getCommandLine().addToggleSwitch("chart-classes-per-package");
        getCommandLine().addToggleSwitch("chart-features-per-class");
        getCommandLine().addToggleSwitch("chart-inbounds-per-package");
        getCommandLine().addToggleSwitch("chart-outbounds-per-package");
        getCommandLine().addToggleSwitch("chart-inbounds-per-class");
        getCommandLine().addToggleSwitch("chart-outbounds-per-class");
        getCommandLine().addToggleSwitch("chart-inbounds-per-feature");
        getCommandLine().addToggleSwitch("chart-outbounds-per-feature");
        getCommandLine().addToggleSwitch("chart-inbounds");
        getCommandLine().addToggleSwitch("chart-outbounds");
        getCommandLine().addToggleSwitch("chart-packages");
        getCommandLine().addToggleSwitch("chart-classes");
        getCommandLine().addToggleSwitch("chart-features");
        getCommandLine().addToggleSwitch("chart-all");

        getCommandLine().addToggleSwitch("validate");
    }

    protected boolean validateCommandLine(PrintStream out) throws IOException, CommandLineException {
        boolean result = super.validateCommandLine(out);

        result &= validateCommandLineForScoping(out);
        result &= validateCommandLineForFiltering(out);

        if (getCommandLine().getToggleSwitch("p2p")) {
            getCommandLine().getSwitch("package-scope").setValue(true);
            getCommandLine().getSwitch("package-filter").setValue(true);
        }

        if (getCommandLine().getToggleSwitch("c2p")) {
            getCommandLine().getSwitch("class-scope").setValue(true);
            getCommandLine().getSwitch("package-filter").setValue(true);
        }

        if (getCommandLine().getToggleSwitch("c2c")) {
            getCommandLine().getSwitch("class-scope").setValue(true);
            getCommandLine().getSwitch("class-filter").setValue(true);
        }

        if (getCommandLine().getToggleSwitch("f2f")) {
            getCommandLine().getSwitch("feature-scope").setValue(true);
            getCommandLine().getSwitch("feature-filter").setValue(true);
        }

        if (getCommandLine().isPresent("includes")) {
            for (String value : getCommandLine().getMultipleSwitch("includes")) {
                getCommandLine().getSwitch("scope-includes").setValue(value);
                getCommandLine().getSwitch("filter-includes").setValue(value);
            }
        }

        if (getCommandLine().isPresent("excludes")) {
            for (String value : getCommandLine().getMultipleSwitch("excludes")) {
                getCommandLine().getSwitch("scope-excludes").setValue(value);
                getCommandLine().getSwitch("filter-excludes").setValue(value);
            }
        }

        return result;
    }

    public void doProcessing() throws Exception {
        NodeFactory factory = new NodeFactory();

        for (String filename : getCommandLine().getParameters()) {
            Logger.getLogger(getClass()).info("Reading " + filename);
            getVerboseListener().print("Reading " + filename);

            if (filename.endsWith(".xml")) {
                NodeLoader loader = new NodeLoader(factory, getCommandLine().getToggleSwitch("validate"));
                loader.addDependencyListener(getVerboseListener());
                loader.load(filename);
            }

            Logger.getLogger(getClass()).info("Read \"" + filename + "\".");
        }

        MetricsReport reporter = new MetricsReport(out);

        reporter.setListingElements(getCommandLine().getToggleSwitch("list"));
        reporter.setChartingClassesPerPackage(getCommandLine().getToggleSwitch("chart-classes-per-package"));
        reporter.setChartingFeaturesPerClass(getCommandLine().getToggleSwitch("chart-features-per-class"));
        reporter.setChartingInboundsPerPackage(getCommandLine().getToggleSwitch("chart-inbounds-per-package"));
        reporter.setChartingOutboundsPerPackage(getCommandLine().getToggleSwitch("chart-outbounds-per-package"));
        reporter.setChartingInboundsPerClass(getCommandLine().getToggleSwitch("chart-inbounds-per-class"));
        reporter.setChartingOutboundsPerClass(getCommandLine().getToggleSwitch("chart-outbounds-per-class"));
        reporter.setChartingInboundsPerFeature(getCommandLine().getToggleSwitch("chart-inbounds-per-feature"));
        reporter.setChartingOutboundsPerFeature(getCommandLine().getToggleSwitch("chart-outbounds-per-feature"));

        if (getCommandLine().getToggleSwitch("chart-all")) {
            reporter.setChartingClassesPerPackage(true);
            reporter.setChartingFeaturesPerClass(true);
            reporter.setChartingInboundsPerPackage(true);
            reporter.setChartingOutboundsPerPackage(true);
            reporter.setChartingInboundsPerClass(true);
            reporter.setChartingOutboundsPerClass(true);
            reporter.setChartingInboundsPerFeature(true);
            reporter.setChartingOutboundsPerFeature(true);
        }

        if (getCommandLine().getToggleSwitch("chart-inbounds")) {
            reporter.setChartingInboundsPerPackage(true);
            reporter.setChartingInboundsPerClass(true);
            reporter.setChartingInboundsPerFeature(true);
        }

        if (getCommandLine().getToggleSwitch("chart-outbounds")) {
            reporter.setChartingOutboundsPerPackage(true);
            reporter.setChartingOutboundsPerClass(true);
            reporter.setChartingOutboundsPerFeature(true);
        }

        if (getCommandLine().getToggleSwitch("chart-packages")) {
            reporter.setChartingClassesPerPackage(true);
            reporter.setChartingInboundsPerPackage(true);
            reporter.setChartingOutboundsPerPackage(true);
        }

        if (getCommandLine().getToggleSwitch("chart-classes")) {
            reporter.setChartingFeaturesPerClass(true);
            reporter.setChartingInboundsPerClass(true);
            reporter.setChartingOutboundsPerClass(true);
        }

        if (getCommandLine().getToggleSwitch("chart-features")) {
            reporter.setChartingInboundsPerFeature(true);
            reporter.setChartingOutboundsPerFeature(true);
        }

        SelectionCriteria scopeCriteria = getScopeCriteria();
        SelectionCriteria filterCriteria = getFilterCriteria();

        Logger.getLogger(getClass()).info("Reporting on " + factory.getPackages().size() + " package(s) ...");
        getVerboseListener().print("Reporting on " + factory.getPackages().size() + " package(s) ...");

        MetricsGatherer metrics = new MetricsGatherer(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
        metrics.traverseNodes(factory.getPackages().values());
        reporter.process(metrics);
    }

    public static void main(String[] args) throws Exception {
        new DependencyMetrics().run(args);
    }
}
