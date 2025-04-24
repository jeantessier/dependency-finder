/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import java.util.*;

import com.jeantessier.commandline.*;
import com.jeantessier.dependency.*;

public class DependencyMetrics extends DependencyGraphCommand {
    protected void populateCommandLineSwitches()  {
        super.populateCommandLineSwitches();

        populateCommandLineSwitchesForScoping();
        populateCommandLineSwitchesForFiltering();

        getCommandLine().addAliasSwitch("p2p", "package-scope", "package-filter");
        getCommandLine().addAliasSwitch("c2p", "class-scope", "package-filter");
        getCommandLine().addAliasSwitch("c2c", "class-scope", "class-filter");
        getCommandLine().addAliasSwitch("f2f", "feature-scope", "feature-filter");
        getCommandLine().addAliasSwitch("includes", "scope-includes", "filter-includes");
        getCommandLine().addAliasSwitch("excludes", "scope-excludes", "filter-excludes");

        getCommandLine().addToggleSwitch("list");

        getCommandLine().addToggleSwitch("chart-classes-per-package");
        getCommandLine().addToggleSwitch("chart-features-per-class");
        getCommandLine().addToggleSwitch("chart-inbounds-per-package");
        getCommandLine().addToggleSwitch("chart-outbounds-per-package");
        getCommandLine().addToggleSwitch("chart-inbounds-per-class");
        getCommandLine().addToggleSwitch("chart-outbounds-per-class");
        getCommandLine().addToggleSwitch("chart-inbounds-per-feature");
        getCommandLine().addToggleSwitch("chart-outbounds-per-feature");

        getCommandLine().addAliasSwitch("chart-inbounds", "chart-inbounds-per-package", "chart-inbounds-per-class", "chart-inbounds-per-feature");
        getCommandLine().addAliasSwitch("chart-outbounds", "chart-outbounds-per-package", "chart-outbounds-per-class", "chart-outbounds-per-feature");
        getCommandLine().addAliasSwitch("chart-packages", "chart-classes-per-package", "chart-inbounds-per-package", "chart-outbounds-per-package");
        getCommandLine().addAliasSwitch("chart-classes", "chart-features-per-class", "chart-inbounds-per-class", "chart-outbounds-per-class");
        getCommandLine().addAliasSwitch("chart-features", "chart-inbounds-per-feature", "chart-outbounds-per-feature");
        getCommandLine().addAliasSwitch("chart-all", "chart-classes-per-package", "chart-features-per-class", "chart-inbounds-per-package", "chart-outbounds-per-package", "chart-inbounds-per-class", "chart-outbounds-per-class", "chart-inbounds-per-feature", "chart-outbounds-per-feature");

        getCommandLine().addToggleSwitch("histogram-classes-per-package");
        getCommandLine().addToggleSwitch("histogram-features-per-class");
        getCommandLine().addToggleSwitch("histogram-inbounds-per-package");
        getCommandLine().addToggleSwitch("histogram-outbounds-per-package");
        getCommandLine().addToggleSwitch("histogram-inbounds-per-class");
        getCommandLine().addToggleSwitch("histogram-outbounds-per-class");
        getCommandLine().addToggleSwitch("histogram-inbounds-per-feature");
        getCommandLine().addToggleSwitch("histogram-outbounds-per-feature");

        getCommandLine().addAliasSwitch("histogram-inbounds", "histogram-inbounds-per-package", "histogram-inbounds-per-class", "histogram-inbounds-per-feature");
        getCommandLine().addAliasSwitch("histogram-outbounds", "histogram-outbounds-per-package", "histogram-outbounds-per-class", "histogram-outbounds-per-feature");
        getCommandLine().addAliasSwitch("histogram-packages", "histogram-classes-per-package", "histogram-inbounds-per-package", "histogram-outbounds-per-package");
        getCommandLine().addAliasSwitch("histogram-classes", "histogram-features-per-class", "histogram-inbounds-per-class", "histogram-outbounds-per-class");
        getCommandLine().addAliasSwitch("histogram-features", "histogram-inbounds-per-feature", "histogram-outbounds-per-feature");
        getCommandLine().addAliasSwitch("histogram-all", "histogram-classes-per-package", "histogram-features-per-class", "histogram-inbounds-per-package", "histogram-outbounds-per-package", "histogram-inbounds-per-class", "histogram-outbounds-per-class", "histogram-inbounds-per-feature", "histogram-outbounds-per-feature");

        getCommandLine().addToggleSwitch("json");
    }

    protected Collection<CommandLineException> parseCommandLine(String[] args) {
        Collection<CommandLineException> exceptions = super.parseCommandLine(args);

        exceptions.addAll(validateCommandLineForScoping());
        exceptions.addAll(validateCommandLineForFiltering());

        return exceptions;
    }

    public void doProcessing() throws Exception {
        MetricsReport reporter;
        if (getCommandLine().getToggleSwitch("json")) {
            reporter = new JSONMetricsReport(getOut());
        } else {
            reporter = new TextMetricsReport(getOut());
        }

        reporter.setListingElements(getCommandLine().getToggleSwitch("list"));
        reporter.setShowingClassesPerPackageChart(getCommandLine().getToggleSwitch("chart-classes-per-package"));
        reporter.setShowingFeaturesPerClassChart(getCommandLine().getToggleSwitch("chart-features-per-class"));
        reporter.setShowingInboundsPerPackageChart(getCommandLine().getToggleSwitch("chart-inbounds-per-package"));
        reporter.setShowingOutboundsPerPackageChart(getCommandLine().getToggleSwitch("chart-outbounds-per-package"));
        reporter.setShowingInboundsPerClassChart(getCommandLine().getToggleSwitch("chart-inbounds-per-class"));
        reporter.setShowingOutboundsPerClassChart(getCommandLine().getToggleSwitch("chart-outbounds-per-class"));
        reporter.setShowingInboundsPerFeatureChart(getCommandLine().getToggleSwitch("chart-inbounds-per-feature"));
        reporter.setShowingOutboundsPerFeatureChart(getCommandLine().getToggleSwitch("chart-outbounds-per-feature"));
        reporter.setShowingClassesPerPackageHistogram(getCommandLine().getToggleSwitch("histogram-classes-per-package"));
        reporter.setShowingFeaturesPerClassHistogram(getCommandLine().getToggleSwitch("histogram-features-per-class"));
        reporter.setShowingInboundsPerPackageHistogram(getCommandLine().getToggleSwitch("histogram-inbounds-per-package"));
        reporter.setShowingOutboundsPerPackageHistogram(getCommandLine().getToggleSwitch("histogram-outbounds-per-package"));
        reporter.setShowingInboundsPerClassHistogram(getCommandLine().getToggleSwitch("histogram-inbounds-per-class"));
        reporter.setShowingOutboundsPerClassHistogram(getCommandLine().getToggleSwitch("histogram-outbounds-per-class"));
        reporter.setShowingInboundsPerFeatureHistogram(getCommandLine().getToggleSwitch("histogram-inbounds-per-feature"));
        reporter.setShowingOutboundsPerFeatureHistogram(getCommandLine().getToggleSwitch("histogram-outbounds-per-feature"));

        SelectionCriteria scopeCriteria = getScopeCriteria();
        SelectionCriteria filterCriteria = getFilterCriteria();

        getVerboseListener().print("Generating report ...");

        MetricsGatherer metrics = new MetricsGatherer(new SelectiveTraversalStrategy(scopeCriteria, filterCriteria));
        metrics.traverseNodes(loadGraph().getPackages().values());
        reporter.process(metrics);
    }

    public static void main(String[] args) throws Exception {
        new DependencyMetrics().run(args);
    }
}
