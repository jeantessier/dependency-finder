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

package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.*;

public class JSONMetricsReport extends MetricsReport {
    public JSONMetricsReport(PrintWriter out) {
        super(out);
    }

    public void process(MetricsGatherer metrics) {
        print("{");

        printProgrammingElementStats(metrics);
        print(",");
        printDependencyStats(metrics);

        if (isShowingClassesPerPackageHistogram() ||
                isShowingFeaturesPerClassHistogram() ||
                isShowingInboundsPerPackageHistogram() ||
                isShowingOutboundsPerPackageHistogram() ||
                isShowingInboundsPerClassHistogram() ||
                isShowingOutboundsPerClassHistogram() ||
                isShowingInboundsPerFeatureHistogram() ||
                isShowingOutboundsPerFeatureHistogram()) {
            print(",");
            printHistograms(metrics);
        }

        if (isShowingClassesPerPackageChart() ||
                isShowingFeaturesPerClassChart() ||
                isShowingInboundsPerPackageChart() ||
                isShowingOutboundsPerPackageChart() ||
                isShowingInboundsPerClassChart() ||
                isShowingOutboundsPerClassChart() ||
                isShowingInboundsPerFeatureChart() ||
                isShowingOutboundsPerFeatureChart()) {
            print(",");
            printChart(metrics);
        }

        print("}");
        println();
    }

    private void printProgrammingElementStats(MetricsGatherer metrics) {
        printProgrammingElementStats("packages", metrics.getPackages());

        print(",");

        printProgrammingElementStats("classes", metrics.getClasses());

        print(",");

        printProgrammingElementStats("features", metrics.getFeatures());
    }

    private void printProgrammingElementStats(String label, Collection<? extends Node> nodes) {
        var nbElements = nodes.size();
        var nbConfirmedElements = countConfirmedNodes(nodes);
        var ratio = nbConfirmedElements / (double) nbElements;

        print("\"" + label + "\":{");
        print("\"count\":" + nbElements);
        print(",");
        print("\"confirmed\":" + nbConfirmedElements);
        print(",");
        print("\"ratio\":" + formatValue(ratio));
        if (isListingElements()) {
            print(",");
            print("\"elements\": [");
            print(renderNodes(nodes));
            print("]");
        }
        print("}");
    }

    private String renderNodes(Collection<? extends Node> nodes) {
        return nodes.stream()
                .sorted()
                .map(node -> "{\"name\":\"" + node.getName() + "\",\"simpleName\":\"" + node.getSimpleName() + "\",\"confirmed\":" + node.isConfirmed() + "}")
                .collect(joining(","));
    }

    private void printDependencyStats(MetricsGatherer metrics) {
        print("\"outbounds\": {");
        print("\"packages\":" + metrics.getNbOutboundPackages() + ",");
        print("\"packageRatio\":" + formatValue(metrics.getNbOutboundPackages() / (double) metrics.getPackages().size()) + ",");
        print("\"classes\":" + metrics.getNbOutboundClasses() + ",");
        print("\"classRatio\":" + formatValue(metrics.getNbOutboundClasses() / (double) metrics.getClasses().size()) + ",");
        print("\"features\":" + metrics.getNbOutboundFeatures() + ",");
        print("\"featureRatio\":" + formatValue(metrics.getNbOutboundFeatures() / (double) metrics.getFeatures().size()) + ",");
        print("\"total\":" + metrics.getNbOutbound());
        print("}");

        print(",");

        print("\"inbounds\": {");
        print("\"packages\":" + metrics.getNbInboundPackages() + ",");
        print("\"packageRatio\":" + formatValue(metrics.getNbInboundPackages() / (double) metrics.getPackages().size()) + ",");
        print("\"classes\":" + metrics.getNbInboundClasses() + ",");
        print("\"classRatio\":" + formatValue(metrics.getNbInboundClasses() / (double) metrics.getClasses().size()) + ",");
        print("\"features\":" + metrics.getNbInboundFeatures() + ",");
        print("\"featureRatio\":" + formatValue(metrics.getNbInboundFeatures() / (double) metrics.getFeatures().size()) + ",");
        print("\"total\":" + metrics.getNbInbound());
        print("}");
    }

    private void printHistograms(MetricsGatherer metrics) {
        var histograms = new HashMap<String, Map<Long, Long>>();

        if (isShowingClassesPerPackageHistogram()) {
            histograms.put("classesPerPackage", metrics.getHistogram(MetricsGatherer.CLASSES_PER_PACKAGE));
        }

        if (isShowingFeaturesPerClassHistogram()) {
            histograms.put("featuresPerClass", metrics.getHistogram(MetricsGatherer.FEATURES_PER_CLASS));
        }

        if (isShowingInboundsPerPackageHistogram()) {
            histograms.put("inboundsPerPackage", metrics.getHistogram(MetricsGatherer.INBOUNDS_PER_PACKAGE));
        }

        if (isShowingOutboundsPerPackageHistogram()) {
            histograms.put("outboundsPerPackage", metrics.getHistogram(MetricsGatherer.OUTBOUNDS_PER_PACKAGE));
        }

        if (isShowingInboundsPerClassHistogram()) {
            histograms.put("inboundsPerClass", metrics.getHistogram(MetricsGatherer.OUTBOUNDS_PER_CLASS));
        }

        if (isShowingOutboundsPerClassHistogram()) {
            histograms.put("outboundsPerClass", metrics.getHistogram(MetricsGatherer.OUTBOUNDS_PER_CLASS));
        }

        if (isShowingInboundsPerFeatureHistogram()) {
            histograms.put("inboundsPerFeature", metrics.getHistogram(MetricsGatherer.OUTBOUNDS_PER_FEATURE));
        }

        if (isShowingOutboundsPerFeatureHistogram()) {
            histograms.put("outboundsPerFeature", metrics.getHistogram(MetricsGatherer.OUTBOUNDS_PER_FEATURE));
        }

        print("\"histograms\":{");
        print(
                histograms.entrySet().stream()
                        .map(entry ->
                                "\"" + entry.getKey() + "\":[" + new TreeMap<>(entry.getValue()).entrySet().stream().map(pair -> "[" + pair.getKey() + "," + pair.getValue() + "]").collect(joining(",")) + "]")
                        .collect(joining(","))
        );
        print("}");
    }

    private void printChart(MetricsGatherer metrics) {
        print("\"chart\":[");

        // Headings
        print("[");
        print("\"n\"");
        if (isShowingClassesPerPackageChart()) {
            print(",\"classes per package\"");
        }
        if (isShowingFeaturesPerClassChart()) {
            print(",\"features per class\"");
        }
        if (isShowingInboundsPerPackageChart()) {
            print(",\"inbounds per package\"");
        }
        if (isShowingOutboundsPerPackageChart()) {
            print(",\"outbounds per package\"");
        }
        if (isShowingInboundsPerClassChart()) {
            print(",\"inbounds per class\"");
        }
        if (isShowingOutboundsPerClassChart()) {
            print(",\"outbounds per class\"");
        }
        if (isShowingInboundsPerFeatureChart()) {
            print(",\"inbounds per feature\"");
        }
        if (isShowingOutboundsPerFeatureChart()) {
            print(",\"outbounds per feature\"");
        }
        print("]");

        // Data
        for (int k=0; k<=metrics.getChartMaximum(); k++) {
            long[] dataPoint = metrics.getChartData(k);

            print(",[");
            print(k);
            if (isShowingClassesPerPackageChart()) {
                print("," + dataPoint[MetricsGatherer.CLASSES_PER_PACKAGE]);
            }
            if (isShowingFeaturesPerClassChart()) {
                print("," + dataPoint[MetricsGatherer.FEATURES_PER_CLASS]);
            }
            if (isShowingInboundsPerPackageChart()) {
                print("," + dataPoint[MetricsGatherer.INBOUNDS_PER_PACKAGE]);
            }
            if (isShowingOutboundsPerPackageChart()) {
                print("," + dataPoint[MetricsGatherer.OUTBOUNDS_PER_PACKAGE]);
            }
            if (isShowingInboundsPerClassChart()) {
                print("," + dataPoint[MetricsGatherer.INBOUNDS_PER_CLASS]);
            }
            if (isShowingOutboundsPerClassChart()) {
                print("," + dataPoint[MetricsGatherer.OUTBOUNDS_PER_CLASS]);
            }
            if (isShowingInboundsPerFeatureChart()) {
                print("," + dataPoint[MetricsGatherer.INBOUNDS_PER_FEATURE]);
            }
            if (isShowingOutboundsPerFeatureChart()) {
                print("," + dataPoint[MetricsGatherer.OUTBOUNDS_PER_FEATURE]);
            }
            print("]");
        }

        print("]");
    }

    private String formatValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "null";
        }

        return String.valueOf(value);
    }
}
