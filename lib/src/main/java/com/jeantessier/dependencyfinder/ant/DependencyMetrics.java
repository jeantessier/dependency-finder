/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

package com.jeantessier.dependencyfinder.ant;

import java.io.*;

import javax.xml.parsers.*;

import org.apache.tools.ant.*;

import org.xml.sax.*;

import com.jeantessier.dependency.*;

public class DependencyMetrics extends GraphTask {
    private String  scopeIncludes = "//";
    private String  scopeExcludes = "";
    private boolean packageScope;
    private String  packageScopeIncludes = "";
    private String  packageScopeExcludes = "";
    private boolean classScope;
    private String  classScopeIncludes = "";
    private String  classScopeExcludes = "";
    private boolean featureScope;
    private String  featureScopeIncludes = "";
    private String  featureScopeExcludes = "";
    private String  filterIncludes = "//";
    private String  filterExcludes = "";
    private boolean packageFilter;
    private String  packageFilterIncludes = "";
    private String  packageFilterExcludes = "";
    private boolean classFilter;
    private String  classFilterIncludes = "";
    private String  classFilterExcludes = "";
    private boolean featureFilter;
    private String  featureFilterIncludes = "";
    private String  featureFilterExcludes = "";

    private boolean list                     = false;

    private boolean chartClassesPerPackage   = false;
    private boolean chartFeaturesPerClass    = false;
    private boolean chartInboundsPerPackage  = false;
    private boolean chartOutboundsPerPackage = false;
    private boolean chartInboundsPerClass    = false;
    private boolean chartOutboundsPerClass   = false;
    private boolean chartInboundsPerFeature  = false;
    private boolean chartOutboundsPerFeature = false;

    private boolean histogramClassesPerPackage   = false;
    private boolean histogramFeaturesPerClass    = false;
    private boolean histogramInboundsPerPackage  = false;
    private boolean histogramOutboundsPerPackage = false;
    private boolean histogramInboundsPerClass    = false;
    private boolean histogramOutboundsPerClass   = false;
    private boolean histogramInboundsPerFeature  = false;
    private boolean histogramOutboundsPerFeature = false;

    public String getScopeincludes() {
        return scopeIncludes;
    }

    public void setScopeincludes(String scopeIncludes) {
        this.scopeIncludes = scopeIncludes;
    }
    
    public String getScopeexcludes() {
        return scopeExcludes;
    }

    public void setScopeexcludes(String scopeExcludes) {
        this.scopeExcludes = scopeExcludes;
    }

    public boolean getPackagescope() {
        return packageScope;
    }

    public void setPackagescope(boolean packageScope) {
        this.packageScope = packageScope;
    }
    
    public String getPackagescopeincludes() {
        return packageScopeIncludes;
    }

    public void setPackagescopeincludes(String packageScopeIncludes) {
        this.packageScopeIncludes = packageScopeIncludes;
    }
    
    public String getPackagescopeexcludes() {
        return packageScopeExcludes;
    }

    public void setPackagescopeexcludes(String packageScopeExcludes) {
        this.packageScopeExcludes = packageScopeExcludes;
    }

    public boolean getClassscope() {
        return classScope;
    }

    public void setClassscope(boolean classScope) {
        this.classScope = classScope;
    }
    
    public String getClassscopeincludes() {
        return classScopeIncludes;
    }

    public void setClassscopeincludes(String classScopeIncludes) {
        this.classScopeIncludes = classScopeIncludes;
    }
    
    public String getClassscopeexcludes() {
        return classScopeExcludes;
    }

    public void setClassscopeexcludes(String classScopeExcludes) {
        this.classScopeExcludes = classScopeExcludes;
    }

    public boolean getFeaturescope() {
        return featureScope;
    }

    public void setFeaturescope(boolean featureScope) {
        this.featureScope = featureScope;
    }
    
    public String getFeaturescopeincludes() {
        return featureScopeIncludes;
    }

    public void setFeaturescopeincludes(String featureScopeIncludes) {
        this.featureScopeIncludes = featureScopeIncludes;
    }
    
    public String getFeaturescopeexcludes() {
        return featureScopeExcludes;
    }

    public void setFeaturescopeexcludes(String featureScopeExcludes) {
        this.featureScopeExcludes = featureScopeExcludes;
    }

    public String getFilterincludes() {
        return filterIncludes;
    }

    public void setFilterincludes(String filterIncludes) {
        this.filterIncludes = filterIncludes;
    }
    
    public String getFilterexcludes() {
        return filterExcludes;
    }

    public void setFilterexcludes(String filterExcludes) {
        this.filterExcludes = filterExcludes;
    }

    public boolean getPackagefilter() {
        return packageFilter;
    }

    public void setPackagefilter(boolean packageFilter) {
        this.packageFilter = packageFilter;
    }
    
    public String getPackagefilterincludes() {
        return packageFilterIncludes;
    }

    public void setPackagefilterincludes(String packageFilterIncludes) {
        this.packageFilterIncludes = packageFilterIncludes;
    }
    
    public String getPackagefilterexcludes() {
        return packageFilterExcludes;
    }

    public void setPackagefilterexcludes(String packageFilterExcludes) {
        this.packageFilterExcludes = packageFilterExcludes;
    }

    public boolean getClassfilter() {
        return classFilter;
    }

    public void setClassfilter(boolean classFilter) {
        this.classFilter = classFilter;
    }
    
    public String getClassfilterincludes() {
        return classFilterIncludes;
    }

    public void setClassfilterincludes(String classFilterIncludes) {
        this.classFilterIncludes = classFilterIncludes;
    }
    
    public String getClassfilterexcludes() {
        return classFilterExcludes;
    }

    public void setClassfilterexcludes(String classFilterExcludes) {
        this.classFilterExcludes = classFilterExcludes;
    }

    public boolean getFeaturefilter() {
        return featureFilter;
    }

    public void setFeaturefilter(boolean featureFilter) {
        this.featureFilter = featureFilter;
    }
    
    public String getFeaturefilterincludes() {
        return featureFilterIncludes;
    }

    public void setFeaturefilterincludes(String featureFilterIncludes) {
        this.featureFilterIncludes = featureFilterIncludes;
    }
    
    public String getFeaturefilterexcludes() {
        return featureFilterExcludes;
    }

    public void setFeaturefilterexcludes(String featureFilterExcludes) {
        this.featureFilterExcludes = featureFilterExcludes;
    }

    public void setP2p(boolean value) {
        setPackagescope(value);
        setPackagefilter(value);
    }
    
    public void setC2p(boolean value) {
        setClassscope(value);
        setPackagefilter(value);
    }

    public void setC2c(boolean value) {
        setClassscope(value);
        setClassfilter(value);
    }

    public void setF2f(boolean value) {
        setFeaturescope(value);
        setFeaturefilter(value);
    }

    public void setIncludes(String value) {
        setScopeincludes(value);
        setFilterincludes(value);
    }

    public void setExcludes(String value) {
        setScopeexcludes(value);
        setFilterexcludes(value);
    }

    public boolean getList() {
        return list;
    }
    
    public void setList(boolean list) {
        this.list = list;
    }

    public boolean getChartclassesperpackage() {
        return chartClassesPerPackage;
    }
    
    public void setChartclassesperpackage(boolean chartClassesPerPackage) {
        this.chartClassesPerPackage = chartClassesPerPackage;
    }

    public boolean getChartfeaturesperclass() {
        return chartFeaturesPerClass;
    }
    
    public void setChartfeaturesperclass(boolean chartFeaturesPerClass) {
        this.chartFeaturesPerClass = chartFeaturesPerClass;
    }

    public boolean getChartinboundsperpackage() {
        return chartInboundsPerPackage;
    }
    
    public void setChartinboundsperpackage(boolean chartInboundsPerPackage) {
        this.chartInboundsPerPackage = chartInboundsPerPackage;
    }

    public boolean getChartoutboundsperpackage() {
        return chartOutboundsPerPackage;
    }
    
    public void setChartoutboundsperpackage(boolean chartOutboundsPerPackage) {
        this.chartOutboundsPerPackage = chartOutboundsPerPackage;
    }

    public boolean getChartinboundsperclass() {
        return chartInboundsPerClass;
    }
    
    public void setChartinboundsperclass(boolean chartInboundsPerClass) {
        this.chartInboundsPerClass = chartInboundsPerClass;
    }

    public boolean getChartoutboundsperclass() {
        return chartOutboundsPerClass;
    }
    
    public void setChartoutboundsperclass(boolean chartOutboundsPerClass) {
        this.chartOutboundsPerClass = chartOutboundsPerClass;
    }
    
    public boolean getChartinboundsperfeature() {
        return chartInboundsPerFeature;
    }
    
    public void setChartinboundsperfeature(boolean chartInboundsPerFeature) {
        this.chartInboundsPerFeature = chartInboundsPerFeature;
    }

    public boolean getChartoutboundsperfeature() {
        return chartOutboundsPerFeature;
    }
    
    public void setChartoutboundsperfeature(boolean chartOutboundsPerFeature) {
        this.chartOutboundsPerFeature = chartOutboundsPerFeature;
    }

    public void setChartinbounds(boolean chartInbounds) {
        setChartinboundsperpackage(chartInbounds);
        setChartinboundsperclass(chartInbounds);
        setChartinboundsperfeature(chartInbounds);
    }

    public void setChartoutbounds(boolean chartOutbounds) {
        setChartoutboundsperpackage(chartOutbounds);
        setChartoutboundsperclass(chartOutbounds);
        setChartoutboundsperfeature(chartOutbounds);
    }

    public void setChartpackages(boolean chartPackages) {
        setChartclassesperpackage(chartPackages);
        setChartinboundsperpackage(chartPackages);
        setChartoutboundsperpackage(chartPackages);
    }

    public void setChartclasses(boolean chartClasses) {
        setChartfeaturesperclass(chartClasses);
        setChartinboundsperclass(chartClasses);
        setChartoutboundsperclass(chartClasses);
    }

    public void setChartfeatures(boolean chartFeatures) {
        setChartinboundsperfeature(chartFeatures);
        setChartoutboundsperfeature(chartFeatures);
    }

    public void setChartall(boolean chartAll) {
        setChartclassesperpackage(chartAll);
        setChartfeaturesperclass(chartAll);
        setChartinboundsperpackage(chartAll);
        setChartoutboundsperpackage(chartAll);
        setChartinboundsperclass(chartAll);
        setChartoutboundsperclass(chartAll);
        setChartinboundsperfeature(chartAll);
        setChartoutboundsperfeature(chartAll);
    }

    public boolean getHistogramclassesperpackage() {
        return histogramClassesPerPackage;
    }

    public void setHistogramclassesperpackage(boolean histogramClassesPerPackage) {
        this.histogramClassesPerPackage = histogramClassesPerPackage;
    }

    public boolean getHistogramfeaturesperclass() {
        return histogramFeaturesPerClass;
    }

    public void setHistogramfeaturesperclass(boolean histogramFeaturesPerClass) {
        this.histogramFeaturesPerClass = histogramFeaturesPerClass;
    }

    public boolean getHistograminboundsperpackage() {
        return histogramInboundsPerPackage;
    }

    public void setHistograminboundsperpackage(boolean histogramInboundsPerPackage) {
        this.histogramInboundsPerPackage = histogramInboundsPerPackage;
    }

    public boolean getHistogramoutboundsperpackage() {
        return histogramOutboundsPerPackage;
    }

    public void setHistogramoutboundsperpackage(boolean histogramOutboundsPerPackage) {
        this.histogramOutboundsPerPackage = histogramOutboundsPerPackage;
    }

    public boolean getHistograminboundsperclass() {
        return histogramInboundsPerClass;
    }

    public void setHistograminboundsperclass(boolean histogramInboundsPerClass) {
        this.histogramInboundsPerClass = histogramInboundsPerClass;
    }

    public boolean getHistogramoutboundsperclass() {
        return histogramOutboundsPerClass;
    }

    public void setHistogramoutboundsperclass(boolean histogramOutboundsPerClass) {
        this.histogramOutboundsPerClass = histogramOutboundsPerClass;
    }

    public boolean getHistograminboundsperfeature() {
        return histogramInboundsPerFeature;
    }

    public void setHistograminboundsperfeature(boolean histogramInboundsPerFeature) {
        this.histogramInboundsPerFeature = histogramInboundsPerFeature;
    }

    public boolean getHistogramoutboundsperfeature() {
        return histogramOutboundsPerFeature;
    }

    public void setHistogramoutboundsperfeature(boolean histogramOutboundsPerFeature) {
        this.histogramOutboundsPerFeature = histogramOutboundsPerFeature;
    }

    public void setHistograminbounds(boolean histogramInbounds) {
        setHistograminboundsperpackage(histogramInbounds);
        setHistograminboundsperclass(histogramInbounds);
        setHistograminboundsperfeature(histogramInbounds);
    }

    public void setHistogramoutbounds(boolean histogramOutbounds) {
        setHistogramoutboundsperpackage(histogramOutbounds);
        setHistogramoutboundsperclass(histogramOutbounds);
        setHistogramoutboundsperfeature(histogramOutbounds);
    }

    public void setHistogrampackages(boolean histogramPackages) {
        setHistogramclassesperpackage(histogramPackages);
        setHistograminboundsperpackage(histogramPackages);
        setHistogramoutboundsperpackage(histogramPackages);
    }

    public void setHistogramclasses(boolean histogramClasses) {
        setHistogramfeaturesperclass(histogramClasses);
        setHistograminboundsperclass(histogramClasses);
        setHistogramoutboundsperclass(histogramClasses);
    }

    public void setHistogramfeatures(boolean histogramFeatures) {
        setHistograminboundsperfeature(histogramFeatures);
        setHistogramoutboundsperfeature(histogramFeatures);
    }

    public void setHistogramall(boolean histogramAll) {
        setHistogramclassesperpackage(histogramAll);
        setHistogramfeaturesperclass(histogramAll);
        setHistograminboundsperpackage(histogramAll);
        setHistogramoutboundsperpackage(histogramAll);
        setHistograminboundsperclass(histogramAll);
        setHistogramoutboundsperclass(histogramAll);
        setHistograminboundsperfeature(histogramAll);
        setHistogramoutboundsperfeature(histogramAll);
    }

    public void execute() throws BuildException {
        // first off, make sure that we've got what we need
        validateParameters();

        VerboseListener verboseListener = new VerboseListener(this);

        try {
            NodeFactory factory = new NodeFactory();

            for (String filename : getSrc().list()) {
                log("Reading graph from " + filename);

                if (filename.endsWith(".xml")) {
                    NodeLoader loader = new NodeLoader(factory, getValidate());
                    loader.addDependencyListener(verboseListener);
                    loader.load(filename);
                }
            }

            log("Saving metrics report to " + getDestfile().getAbsolutePath());

            PrintWriter out = new PrintWriter(new FileWriter(getDestfile()));

            MetricsReport reporter = new TextMetricsReport(out);

            reporter.setListingElements(getList());

            reporter.setShowingClassesPerPackageChart(getChartclassesperpackage());
            reporter.setShowingFeaturesPerClassChart(getChartfeaturesperclass());
            reporter.setShowingInboundsPerPackageChart(getChartinboundsperpackage());
            reporter.setShowingOutboundsPerPackageChart(getChartoutboundsperpackage());
            reporter.setShowingInboundsPerClassChart(getChartinboundsperclass());
            reporter.setShowingOutboundsPerClassChart(getChartoutboundsperclass());
            reporter.setShowingInboundsPerFeatureChart(getChartinboundsperfeature());
            reporter.setShowingOutboundsPerFeatureChart(getChartoutboundsperfeature());

            reporter.setShowingClassesPerPackageHistogram(getHistogramclassesperpackage());
            reporter.setShowingFeaturesPerClassHistogram(getHistogramfeaturesperclass());
            reporter.setShowingInboundsPerPackageHistogram(getHistograminboundsperpackage());
            reporter.setShowingOutboundsPerPackageHistogram(getHistogramoutboundsperpackage());
            reporter.setShowingInboundsPerClassHistogram(getHistograminboundsperclass());
            reporter.setShowingOutboundsPerClassHistogram(getHistogramoutboundsperclass());
            reporter.setShowingInboundsPerFeatureHistogram(getHistograminboundsperfeature());
            reporter.setShowingOutboundsPerFeatureHistogram(getHistogramoutboundsperfeature());

            MetricsGatherer metrics = new MetricsGatherer(getStrategy());
            metrics.traverseNodes(factory.getPackages().values());
            reporter.process(metrics);

            out.close();
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            throw new BuildException(ex);
        }
    }

    private SelectionCriteria getScopeCriteria() throws BuildException {
        RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

        if (getPackagescope() || getClassscope() || getFeaturescope()) {
            result.setMatchingPackages(getPackagescope());
            result.setMatchingClasses(getClassscope());
            result.setMatchingFeatures(getFeaturescope());
        }

        result.setGlobalIncludes(getScopeincludes());
        result.setGlobalExcludes(getScopeexcludes());
        result.setPackageIncludes(getPackagescopeincludes());
        result.setPackageExcludes(getPackagescopeexcludes());
        result.setClassIncludes(getClassscopeincludes());
        result.setClassExcludes(getClassscopeexcludes());
        result.setFeatureIncludes(getFeaturescopeincludes());
        result.setFeatureExcludes(getFeaturescopeexcludes());

        return result;
    }

    private SelectionCriteria getFilterCriteria() throws BuildException {
        RegularExpressionSelectionCriteria result = new RegularExpressionSelectionCriteria();

        if (getPackagefilter() || getClassfilter() || getFeaturefilter()) {
            result.setMatchingPackages(getPackagefilter());
            result.setMatchingClasses(getClassfilter());
            result.setMatchingFeatures(getFeaturefilter());
        }

        result.setGlobalIncludes(getFilterincludes());
        result.setGlobalExcludes(getFilterexcludes());
        result.setPackageIncludes(getPackagefilterincludes());
        result.setPackageExcludes(getPackagefilterexcludes());
        result.setClassIncludes(getClassfilterincludes());
        result.setClassExcludes(getClassfilterexcludes());
        result.setFeatureIncludes(getFeaturefilterincludes());
        result.setFeatureExcludes(getFeaturefilterexcludes());

        return result;
    }
    
    private TraversalStrategy getStrategy() throws BuildException {
        return new SelectiveTraversalStrategy(getScopeCriteria(), getFilterCriteria());
    }
}
