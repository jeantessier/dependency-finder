/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

package com.jeantessier.metrics;

import java.io.*;
import java.util.Collection;
import java.util.function.Consumer;

public class YAMLPrinter extends Printer {
    private final MetricsConfiguration configuration;

    public YAMLPrinter(PrintWriter out, MetricsConfiguration configuration) {
        super(out);
        
        this.configuration = configuration;
    }

    public void visitMetrics(Metrics metrics) {
        indent().append("metrics:").eol();
        raiseIndent();
        visitProjectMetrics(metrics);
        lowerIndent();
    }

    private void visitProjectMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            indent().append("-").eol();
            raiseIndent();
            indent().append("name: ").append(formatText(metrics.getName())).eol();

            printMeasurements(metrics, configuration.getProjectMeasurements());
            printMetrics("groups", metrics.getSubMetrics(), this::visitGroupMetrics);

            lowerIndent();
        }
    }

    private void visitGroupMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            indent().append("-").eol();
            raiseIndent();
            indent().append("name: ").append(formatText(metrics.getName())).eol();

            printMeasurements(metrics, configuration.getGroupMeasurements());
            printMetrics("classes", metrics.getSubMetrics(), this::visitClassMetrics);

            lowerIndent();
        }
    }

    private void visitClassMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            indent().append("-").eol();
            raiseIndent();
            indent().append("name: ").append(formatText(metrics.getName())).eol();

            printMeasurements(metrics, configuration.getClassMeasurements());
            printMetrics("methods", metrics.getSubMetrics(), this::visitMethodMetrics);

            lowerIndent();
        }
    }

    private void visitMethodMetrics(Metrics metrics) {
        indent().append("-").eol();
        raiseIndent();
        indent().append("name: ").append(formatText(metrics.getName())).eol();

        printMeasurements(metrics, configuration.getMethodMeasurements());

        lowerIndent();
    }

    private void printMeasurements(Metrics metrics, Collection<MeasurementDescriptor> descriptors) {
        if (hasVisibleMeasurements(descriptors)) {
            indent().append("measurements:").eol();
            raiseIndent();
            visitMeasurements(metrics, descriptors);
            lowerIndent();
        } else {
            indent().append("measurements: []").eol();
        }
    }

    private void printMetrics(String label, Collection<Metrics> metrics, Consumer<Metrics> consumer) {
        if (hasVisibleMetrics(metrics)) {
            indent().append(label).append(":").eol();
            raiseIndent();
            metrics.stream()
                    .filter(this::isVisibleMetrics)
                    .forEach(consumer);
            lowerIndent();
        } else {
            indent().append(label).append(": []").eol();
        }
    }

    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        indent().append("-").eol();
        raiseIndent();
        indent().append("short-name: ").append(measurement.getShortName()).eol();
        indent().append("long-name: ").append(measurement.getLongName()).eol();
        indent().append("value: ").append(measurement.getValue()).eol();
        indent().append("minimum: ").append(measurement.getMinimum()).eol();
        indent().append("median: ").append(measurement.getMedian()).eol();
        indent().append("average: ").append(measurement.getAverage()).eol();
        indent().append("standard-deviation: ").append(measurement.getStandardDeviation()).eol();
        indent().append("maximum: ").append(measurement.getMaximum()).eol();
        indent().append("sum: ").append(measurement.getSum()).eol();
        indent().append("nb-data-points: ").append(measurement.getNbDataPoints()).eol();

        var requestedPercentiles = measurement.getRequestedPercentiles();
        if (!requestedPercentiles.isEmpty()) {
            indent().append("percentiles:").eol();
            raiseIndent();

            requestedPercentiles.forEach(percentile -> indent().append("p").append(percentile).append(": ").append(measurement.getPercentile(percentile)).eol());

            lowerIndent();
        }

        lowerIndent();
    }
    
    public void visitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
        visitCollectionMeasurement(measurement);
    }
        
    public void visitNameListMeasurement(NameListMeasurement measurement) {
        visitCollectionMeasurement(measurement);
    }
    
    public void visitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
        visitCollectionMeasurement(measurement);
    }
    
    protected void visitCollectionMeasurement(CollectionMeasurement measurement) {
        indent().append("-").eol();
        raiseIndent();
        indent().append("short-name: ").append(measurement.getShortName()).eol();
        indent().append("long-name: ").append(measurement.getLongName()).eol();
        indent().append("value: ").append(measurement.getValue()).eol();

        if (isExpandCollectionMeasurements()) {
            if (measurement.isEmpty()) {
                indent().append("members: []").eol();
            } else {
                indent().append("members:").eol();
                raiseIndent();
                measurement.getValues().stream()
                        .sorted()
                        .forEach(member -> indent().append("- ").append(formatText(member)).eol());
                lowerIndent();
            }
        }

        lowerIndent();
    }
    
    protected void visitMeasurement(Measurement measurement) {
        indent().append("-").eol();
        raiseIndent();
        indent().append("short-name: ").append(measurement.getShortName()).eol();
        indent().append("long-name: ").append(measurement.getLongName()).eol();
        indent().append("value: ").append(measurement.getValue()).eol();
        lowerIndent();
    }

    private String formatText(String name) {
        if (name.isEmpty()) {
            return "\"\"";
        }

        if (name.contains(": ")) {
            return "\"" + name + "\"";
        }

        return name;
    }
}
