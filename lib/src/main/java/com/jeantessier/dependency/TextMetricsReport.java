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

package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

public class TextMetricsReport extends MetricsReport {
    public TextMetricsReport(PrintWriter out) {
        super(out);
    }

    public void process(MetricsGatherer metrics) {
        int nbPackages = metrics.getPackages().size();
        print(nbPackages + " package(s)");
        if (nbPackages > 0) {
            var nbConfirmedPackages = countConfirmedNodes(metrics.getPackages());
            print(" (" + nbConfirmedPackages + " confirmed, " + (nbConfirmedPackages / (double) nbPackages) + ")");
        }
        println();
        if (isListingElements()) {
            metrics.getPackages().forEach(node -> println("    " + node));
        }

        int nbClasses = metrics.getClasses().size();
        print(nbClasses + " class(es)");
        if (nbClasses > 0) {
            var nbConfirmedClasses = countConfirmedNodes(metrics.getClasses());
            print(" (" + nbConfirmedClasses + " confirmed, " + (nbConfirmedClasses / (double) nbClasses) + ")");
        }
        println();
        if (isListingElements()) {
            metrics.getClasses().forEach(node -> println("    " + node));
        }

        int nbFeatures = metrics.getFeatures().size();
        print(nbFeatures + " feature(s)");
        if (nbFeatures > 0) {
            var nbConfirmedFeatures = countConfirmedNodes(metrics.getFeatures());
            print(" (" + nbConfirmedFeatures + " confirmed, " + (nbConfirmedFeatures / (double) nbFeatures) + ")");
        }
        println();
        if (isListingElements()) {
            metrics.getFeatures().forEach(node -> println("    " + node));
        }

        println();

        println(metrics.getNbOutbound() + " outbound link(s)");

        long nbOutboundPackages = metrics.getNbOutboundPackages();
        print("    " + nbOutboundPackages + " from package(s)");
        if (nbOutboundPackages > 0 && nbPackages > 0) {
            print(" (on average " + (nbOutboundPackages / (double) nbPackages) + " per package)");
        }
        println();

        long nbOutboundClasses = metrics.getNbOutboundClasses();
        print("    " + nbOutboundClasses + " from class(es)");
        if (nbOutboundClasses > 0 && nbClasses > 0) {
            print(" (on average " + (nbOutboundClasses / (double) nbClasses) + " per class)");
        }
        println();

        long nbOutboundFeatures = metrics.getNbOutboundFeatures();
        print("    " + nbOutboundFeatures + " from feature(s)");
        if (nbOutboundFeatures > 0 && nbFeatures > 0) {
            print(" (on average " + (nbOutboundFeatures / (double) nbFeatures) + " per feature)");
        }
        println();

        println(metrics.getNbInbound() + " inbound link(s)");

        long nbInboundPackages = metrics.getNbInboundPackages();
        print("    " + nbInboundPackages + " to package(s)");
        if (nbInboundPackages > 0 && nbPackages > 0) {
            print(" (on average " + (nbInboundPackages / (double) nbPackages) + " per package)");
        }
        println();

        long nbInboundClasses = metrics.getNbInboundClasses();
        print("    " + nbInboundClasses + " to class(es)");
        if (nbInboundClasses > 0 && nbClasses > 0) {
            print(" (on average " + (nbInboundClasses / (double) nbClasses) + " per class)");
        }
        println();

        long nbInboundFeatures = metrics.getNbInboundFeatures();
        print("    " + nbInboundFeatures + " to feature(s)");
        if (nbInboundFeatures > 0 && nbFeatures > 0) {
            print(" (on average " + (nbInboundFeatures / (double) nbFeatures) + " per feature)");
        }
        println();

        if (isChartingClassesPerPackage() ||
            isChartingFeaturesPerClass() ||
            isChartingInboundsPerPackage() ||
            isChartingOutboundsPerPackage() ||
            isChartingInboundsPerClass() ||
            isChartingOutboundsPerClass() ||
            isChartingInboundsPerFeature() ||
            isChartingOutboundsPerFeature()) {

            println();

            print("n");
            if (isChartingClassesPerPackage()) {
                print(", \"classes per package\"");
            }
            if (isChartingFeaturesPerClass()) {
                print(", \"features per class\"");
            }
            if (isChartingInboundsPerPackage()) {
                print(", \"inbounds per package\"");
            }
            if (isChartingOutboundsPerPackage()) {
                print(", \"outbounds per package\"");
            }
            if (isChartingInboundsPerClass()) {
                print(", \"inbounds per class\"");
            }
            if (isChartingOutboundsPerClass()) {
                print(", \"outbounds per class\"");
            }
            if (isChartingInboundsPerFeature()) {
                print(", \"inbounds per feature\"");
            }
            if (isChartingOutboundsPerFeature()) {
                print(", \"outbounds per feature\"");
            }
            println();

            for (int k=0; k<=metrics.getChartMaximum(); k++) {
                long[] dataPoint = metrics.getChartData(k);

                print(k);
                if (isChartingClassesPerPackage()) {
                    print(", " + dataPoint[MetricsGatherer.CLASSES_PER_PACKAGE]);
                }
                if (isChartingFeaturesPerClass()) {
                    print(", " + dataPoint[MetricsGatherer.FEATURES_PER_CLASS]);
                }
                if (isChartingInboundsPerPackage()) {
                    print(", " + dataPoint[MetricsGatherer.INBOUNDS_PER_PACKAGE]);
                }
                if (isChartingOutboundsPerPackage()) {
                    print(", " + dataPoint[MetricsGatherer.OUTBOUNDS_PER_PACKAGE]);
                }
                if (isChartingInboundsPerClass()) {
                    print(", " + dataPoint[MetricsGatherer.INBOUNDS_PER_CLASS]);
                }
                if (isChartingOutboundsPerClass()) {
                    print(", " + dataPoint[MetricsGatherer.OUTBOUNDS_PER_CLASS]);
                }
                if (isChartingInboundsPerFeature()) {
                    print(", " + dataPoint[MetricsGatherer.INBOUNDS_PER_FEATURE]);
                }
                if (isChartingOutboundsPerFeature()) {
                    print(", " + dataPoint[MetricsGatherer.OUTBOUNDS_PER_FEATURE]);
                }
                println();
            }
        }
    }

    private long countConfirmedNodes(Collection<? extends Node> nodes) {
        return nodes.parallelStream().filter(Node::isConfirmed).count();
    }
}
