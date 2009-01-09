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

import java.io.*;
import java.util.*;

public class MetricsReport {
    private PrintWriter out;

    private boolean listingElements = false;
    private boolean chartingClassesPerPackage = false;
    private boolean chartingFeaturesPerClass = false;
    private boolean chartingInboundsPerPackage = false;
    private boolean chartingOutboundsPerPackage = false;
    private boolean chartingInboundsPerClass = false;
    private boolean chartingOutboundsPerClass = false;
    private boolean chartingInboundsPerFeature = false;
    private boolean chartingOutboundsPerFeature = false;

    public MetricsReport(PrintWriter out) {
        this.out = out;
    }

    public boolean isListingElements() {
        return listingElements;
    }

    public void setListingElements(boolean listingElements) {
        this.listingElements = listingElements;
    }

    public boolean isChartingClassesPerPackage() {
        return chartingClassesPerPackage;
    }

    public void setChartingClassesPerPackage(boolean chartingClassesPerPackage) {
        this.chartingClassesPerPackage = chartingClassesPerPackage;
    }

    public boolean isChartingFeaturesPerClass() {
        return chartingFeaturesPerClass;
    }

    public void setChartingFeaturesPerClass(boolean chartingFeaturesPerClass) {
        this.chartingFeaturesPerClass = chartingFeaturesPerClass;
    }

    public boolean isChartingInboundsPerPackage() {
        return chartingInboundsPerPackage;
    }

    public void setChartingInboundsPerPackage(boolean chartingInboundsPerPackage) {
        this.chartingInboundsPerPackage = chartingInboundsPerPackage;
    }

    public boolean isChartingOutboundsPerPackage() {
        return chartingOutboundsPerPackage;
    }

    public void setChartingOutboundsPerPackage(boolean chartingOutboundsPerPackage) {
        this.chartingOutboundsPerPackage = chartingOutboundsPerPackage;
    }

    public boolean isChartingInboundsPerClass() {
        return chartingInboundsPerClass;
    }

    public void setChartingInboundsPerClass(boolean chartingInboundsPerClass) {
        this.chartingInboundsPerClass = chartingInboundsPerClass;
    }

    public boolean isChartingOutboundsPerClass() {
        return chartingOutboundsPerClass;
    }

    public void setChartingOutboundsPerClass(boolean chartingOutboundsPerClass) {
        this.chartingOutboundsPerClass = chartingOutboundsPerClass;
    }

    public boolean isChartingInboundsPerFeature() {
        return chartingInboundsPerFeature;
    }

    public void setChartingInboundsPerFeature(boolean chartingInboundsPerFeature) {
        this.chartingInboundsPerFeature = chartingInboundsPerFeature;
    }

    public boolean isChartingOutboundsPerFeature() {
        return chartingOutboundsPerFeature;
    }

    public void setChartingOutboundsPerFeature(boolean chartingOutboundsPerFeature) {
        this.chartingOutboundsPerFeature = chartingOutboundsPerFeature;
    }

    public void process(MetricsGatherer metrics) {
        int nbPackages = metrics.getPackages().size();
        out.print(nbPackages + " package(s)");
        if (nbPackages > 0) {
            int nbConfirmedPackages = countConfirmedNodes(metrics.getPackages());
            out.print(" (" + nbConfirmedPackages + " confirmed, " + (nbConfirmedPackages / (double) nbPackages) + ")");
        }
        out.println();
        if (isListingElements()) {
            for (PackageNode packageNode : metrics.getPackages()) {
                out.println("    " + packageNode);
            }
        }

        int nbClasses = metrics.getClasses().size();
        out.print(nbClasses + " class(es)");
        if (nbClasses > 0) {
            int nbConfirmedClasses = countConfirmedNodes(metrics.getClasses());
            out.print(" (" + nbConfirmedClasses + " confirmed, " + (nbConfirmedClasses / (double) nbClasses) + ")");
        }
        out.println();
        if (isListingElements()) {
            for (ClassNode classNode : metrics.getClasses()) {
                out.println("    " + classNode);
            }
        }

        int nbFeatures = metrics.getFeatures().size();
        out.print(nbFeatures + " feature(s)");
        if (nbFeatures > 0) {
            int nbConfirmedFeatures = countConfirmedNodes(metrics.getFeatures());
            out.print(" (" + nbConfirmedFeatures + " confirmed, " + (nbConfirmedFeatures / (double) nbFeatures) + ")");
        }
        out.println();
        if (isListingElements()) {
            for (FeatureNode featureNode : metrics.getFeatures()) {
                out.println("    " + featureNode);
            }
        }

        out.println();

        out.println(metrics.getNbOutbound() + " outbound link(s)");

        long nbOutboundPackages = metrics.getNbOutboundPackages();
        out.print("    " + nbOutboundPackages + " from package(s)");
        if (nbOutboundPackages > 0 && nbPackages > 0) {
            out.print(" (on average " + (nbOutboundPackages / (double) nbPackages) + " per package)");
        }
        out.println();

        long nbOutboundClasses = metrics.getNbOutboundClasses();
        out.print("    " + nbOutboundClasses + " from class(es)");
        if (nbOutboundClasses > 0 && nbClasses > 0) {
            out.print(" (on average " + (nbOutboundClasses / (double) nbClasses) + " per class)");
        }
        out.println();

        long nbOutboundFeatures = metrics.getNbOutboundFeatures();
        out.print("    " + nbOutboundFeatures + " from feature(s)");
        if (nbOutboundFeatures > 0 && nbFeatures > 0) {
            out.print(" (on average " + (nbOutboundFeatures / (double) nbFeatures) + " per feature)");
        }
        out.println();

        out.println(metrics.getNbInbound() + " inbound link(s)");

        long nbInboundPackages = metrics.getNbInboundPackages();
        out.print("    " + nbInboundPackages + " to package(s)");
        if (nbInboundPackages > 0 && nbPackages > 0) {
            out.print(" (on average " + (nbInboundPackages / (double) nbPackages) + " per package)");
        }
        out.println();

        long nbInboundClasses = metrics.getNbInboundClasses();
        out.print("    " + nbInboundClasses + " to class(es)");
        if (nbInboundClasses > 0 && nbClasses > 0) {
            out.print(" (on average " + (nbInboundClasses / (double) nbClasses) + " per class)");
        }
        out.println();

        long nbInboundFeatures = metrics.getNbInboundFeatures();
        out.print("    " + nbInboundFeatures + " to feature(s)");
        if (nbInboundFeatures > 0 && nbFeatures > 0) {
            out.print(" (on average " + (nbInboundFeatures / (double) nbFeatures) + " per feature)");
        }
        out.println();

        if (isChartingClassesPerPackage() ||
            isChartingFeaturesPerClass() ||
            isChartingInboundsPerPackage() ||
            isChartingOutboundsPerPackage() ||
            isChartingInboundsPerClass() ||
            isChartingOutboundsPerClass() ||
            isChartingInboundsPerFeature() ||
            isChartingOutboundsPerFeature()) {

            out.println();

            out.print("n");
            if (isChartingClassesPerPackage()) {
                out.print(", \"classes per package\"");
            }
            if (isChartingFeaturesPerClass()) {
                out.print(", \"features per class\"");
            }
            if (isChartingInboundsPerPackage()) {
                out.print(", \"inbounds per package\"");
            }
            if (isChartingOutboundsPerPackage()) {
                out.print(", \"outbounds per package\"");
            }
            if (isChartingInboundsPerClass()) {
                out.print(", \"inbounds per class\"");
            }
            if (isChartingOutboundsPerClass()) {
                out.print(", \"outbounds per class\"");
            }
            if (isChartingInboundsPerFeature()) {
                out.print(", \"inbounds per feature\"");
            }
            if (isChartingOutboundsPerFeature()) {
                out.print(", \"outbounds per feature\"");
            }
            out.println();

            for (int k=0; k<=metrics.getChartMaximum(); k++) {
                long[] dataPoint = metrics.getChartData(k);

                out.print(k);
                if (isChartingClassesPerPackage()) {
                    out.print(", " + dataPoint[MetricsGatherer.CLASSES_PER_PACKAGE]);
                }
                if (isChartingFeaturesPerClass()) {
                    out.print(", " + dataPoint[MetricsGatherer.FEATURES_PER_CLASS]);
                }
                if (isChartingInboundsPerPackage()) {
                    out.print(", " + dataPoint[MetricsGatherer.INBOUNDS_PER_PACKAGE]);
                }
                if (isChartingOutboundsPerPackage()) {
                    out.print(", " + dataPoint[MetricsGatherer.OUTBOUNDS_PER_PACKAGE]);
                }
                if (isChartingInboundsPerClass()) {
                    out.print(", " + dataPoint[MetricsGatherer.INBOUNDS_PER_CLASS]);
                }
                if (isChartingOutboundsPerClass()) {
                    out.print(", " + dataPoint[MetricsGatherer.OUTBOUNDS_PER_CLASS]);
                }
                if (isChartingInboundsPerFeature()) {
                    out.print(", " + dataPoint[MetricsGatherer.INBOUNDS_PER_FEATURE]);
                }
                if (isChartingOutboundsPerFeature()) {
                    out.print(", " + dataPoint[MetricsGatherer.OUTBOUNDS_PER_FEATURE]);
                }
                out.println();
            }
        }
    }

    private int countConfirmedNodes(Collection<? extends Node> nodes) {
        int result = 0;

        for (Node node : nodes) {
            if (node.isConfirmed()) {
                result++;
            }
        }

        return result;
    }
}
