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

package com.jeantessier.dependency;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class MetricsGatherer extends VisitorBase {
    private Collection<PackageNode> packages = new LinkedList<PackageNode>();
    private Collection<ClassNode> classes  = new LinkedList<ClassNode>();
    private Collection<FeatureNode> features = new LinkedList<FeatureNode>();

    private long nbOutbound = 0;
    private long nbInbound = 0;
    private long nbOutboundPackages = 0;
    private long nbInboundPackages = 0;
    private long nbOutboundClasses = 0;
    private long nbInboundClasses = 0;
    private long nbOutboundFeatures = 0;
    private long nbInboundFeatures = 0;

    private Map<Integer, long[]> chartData = new TreeMap<Integer, long[]>();
    private int chartMaximum = 0;
    public static final int CHART_INDEX           = 0;
    public static final int CLASSES_PER_PACKAGE   = 1;
    public static final int FEATURES_PER_CLASS    = 2;
    public static final int INBOUNDS_PER_PACKAGE  = 3;
    public static final int OUTBOUNDS_PER_PACKAGE = 4;
    public static final int INBOUNDS_PER_CLASS    = 5;
    public static final int OUTBOUNDS_PER_CLASS   = 6;
    public static final int INBOUNDS_PER_FEATURE  = 7;
    public static final int OUTBOUNDS_PER_FEATURE = 8;
    public static final int NB_CHARTS             = 9;

    private static final String[] CHART_NAMES = {"n",
                                                 "Classes per Package",
                                                 "Feafures per Class",
                                                 "Inbounds per Package",
                                                 "Outbounds per Package",
                                                 "Inbounds per Class",
                                                 "Outbounds per Class",
                                                 "Inbounds per Feature",
                                                 "Outbounds per Feature"};

    public static int getNbCharts() {
        return NB_CHARTS;
    }
    
    public static String getChartName(int i) {
        return CHART_NAMES[i];
    }
    
    public MetricsGatherer() {
        super();
    }

    public MetricsGatherer(TraversalStrategy strategy) {
        super(strategy);
    }

    public long[] getChartData(int i) {
        long[] result = chartData.get(i);

        if (result == null) {
            result = new long[NB_CHARTS];
            result[CHART_INDEX] = i;
            chartData.put(i, result);

            if (chartMaximum < i) {
                chartMaximum = i;
            }
        }

        return result;
    }

    public int getChartMaximum() {
        return chartMaximum;
    }
    
    public Collection<PackageNode> getPackages() {
        return packages;
    }

    public Collection<ClassNode> getClasses() {
        return classes;
    }

    public Collection<FeatureNode> getFeatures() {
        return features;
    }

    public long getNbOutbound() {
        return nbOutbound;
    }

    public long getNbInbound() {
        return nbInbound;
    }

    public long getNbOutboundPackages() {
        return nbOutboundPackages;
    }

    public long getNbInboundPackages() {
        return nbInboundPackages;
    }

    public long getNbOutboundClasses() {
        return nbOutboundClasses;
    }

    public long getNbInboundClasses() {
        return nbInboundClasses;
    }

    public long getNbOutboundFeatures() {
        return nbOutboundFeatures;
    }

    public long getNbInboundFeatures() {
        return nbInboundFeatures;
    }
    
    public void preprocessPackageNode(PackageNode node) {
        super.preprocessPackageNode(node);

        packages.add(node);

        getChartData(node.getClasses().size())[CLASSES_PER_PACKAGE]++;
        getChartData(node.getInboundDependencies().size())[INBOUNDS_PER_PACKAGE]++;
        getChartData(node.getOutboundDependencies().size())[OUTBOUNDS_PER_PACKAGE]++;
    }

    /**
     *  PackageNode --&gt; CurrentNode()
     */
    public void visitInboundPackageNode(PackageNode node) {
        if (getStrategy().isInFilter(node)) {
            nbInbound++;
            nbOutboundPackages++;
        }
    }

    /**
     *  CurrentNode() --&gt; PackageNode
     */
    public void visitOutboundPackageNode(PackageNode node) {
        if (getStrategy().isInFilter(node)) {
            nbOutbound++;
            nbInboundPackages++;
        }
    }

    public void preprocessClassNode(ClassNode node) {
        super.preprocessClassNode(node);

        classes.add(node);

        getChartData(node.getFeatures().size())[FEATURES_PER_CLASS]++;
        getChartData(node.getInboundDependencies().size())[INBOUNDS_PER_CLASS]++;
        getChartData(node.getOutboundDependencies().size())[OUTBOUNDS_PER_CLASS]++;
    }

    /**
     *  ClassNode --&gt; CurrentNode()
     */
    public void visitInboundClassNode(ClassNode node) {
        if (getStrategy().isInFilter(node)) {
            nbInbound++;
            nbOutboundClasses++;
        }
    }

    /**
     *  CurrentNode() --&gt; ClassNode
     */
    public void visitOutboundClassNode(ClassNode node) {
        if (getStrategy().isInFilter(node)) {
            nbOutbound++;
            nbInboundClasses++;
        }
    }

    public void preprocessFeatureNode(FeatureNode node) {
        super.preprocessFeatureNode(node);

        features.add(node);

        getChartData(node.getInboundDependencies().size())[INBOUNDS_PER_FEATURE]++;
        getChartData(node.getOutboundDependencies().size())[OUTBOUNDS_PER_FEATURE]++;
    }

    /**
     *  FeatureNode --&gt; CurrentNode()
     */
    public void visitInboundFeatureNode(FeatureNode node) {
        if (getStrategy().isInFilter(node)) {
            nbInbound++;
            nbOutboundFeatures++;
        }
    }

    /**
     *  CurrentNode() --&gt; FeatureNode
     */
    public void visitOutboundFeatureNode(FeatureNode node) {
        if (getStrategy().isInFilter(node)) {
            nbOutbound++;
            nbInboundFeatures++;
        }
    }
}
