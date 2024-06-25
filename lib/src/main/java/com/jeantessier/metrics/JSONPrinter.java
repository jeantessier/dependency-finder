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
import java.util.*;

import static java.util.stream.Collectors.joining;

public class JSONPrinter extends Printer {
    private final MetricsConfiguration configuration;

    public JSONPrinter(PrintWriter out, MetricsConfiguration configuration) {
        super(out);
        
        this.configuration = configuration;
    }

    public void visitMetrics(Collection<Metrics> metrics) {
        visitProjectMetrics(metrics);
    }

    public void visitMetrics(Metrics metrics) {
        visitProjectMetrics(metrics);
    }

    private void visitProjectMetrics(Collection<Metrics> metrics) {
        if (hasVisibleMetrics(metrics)) {
            append("[").eol();
            raiseIndent();

            indent().append(metrics.stream()
                    .filter(this::isVisibleMetrics)
                    .map(projectMetrics -> {
                        StringWriter out = new StringWriter();
                        clonePrinter(out).visitProjectMetrics(projectMetrics);
                        return out.toString();
                    })
                    .collect(joining(", "))).eol();

            lowerIndent();
            indent().append("]");
        } else {
            append("[]");
        }
    }

    private void visitProjectMetrics(Metrics metrics) {
        append("{").eol();
        raiseIndent();
        indent().append("\"name\": \"").append(metrics.getName()).append("\",").eol();

        indent().append("\"measurements\": ");
        visitMeasurements(metrics, configuration.getProjectMeasurements());
        append(",").eol();

        indent().append("\"groups\": ");
        visitGroupMetrics(metrics.getSubMetrics());
        eol();

        lowerIndent();
        indent().append("}");
    }

    private void visitGroupMetrics(Collection<Metrics> metrics) {
        if (hasVisibleMetrics(metrics)) {
            append("[").eol();
            raiseIndent();

            indent().append(metrics.stream()
                    .filter(this::isVisibleMetrics)
                    .map(groupMetrics -> {
                        StringWriter out = new StringWriter();
                        clonePrinter(out).visitGroupMetrics(groupMetrics);
                        return out.toString();
                    })
                    .collect(joining(", "))).eol();

            lowerIndent();
            indent().append("]");
        } else {
            append("[]");
        }
    }

    private void visitGroupMetrics(Metrics metrics) {
        append("{").eol();
        raiseIndent();
        indent().append("\"name\": \"").append(metrics.getName()).append("\",").eol();

        indent().append("\"measurements\": ");
        visitMeasurements(metrics, configuration.getGroupMeasurements());
        append(",").eol();

        indent().append("\"classes\": ");
        visitClassMetrics(metrics.getSubMetrics());
        eol();

        lowerIndent();
        indent().append("}");
    }

    private void visitClassMetrics(Collection<Metrics> metrics) {
        if (hasVisibleMetrics(metrics)) {
            append("[").eol();
            raiseIndent();

            indent().append(metrics.stream()
                    .filter(this::isVisibleMetrics)
                    .map(classMetrics -> {
                        StringWriter out = new StringWriter();
                        clonePrinter(out).visitClassMetrics(classMetrics);
                        return out.toString();
                    })
                    .collect(joining(", "))).eol();

            lowerIndent();
            indent().append("]");
        } else {
            append("[]");
        }
    }

    private void visitClassMetrics(Metrics metrics) {
        append("{").eol();
        raiseIndent();
        indent().append("\"name\": \"").append(metrics.getName()).append("\",").eol();

        indent().append("\"measurements\": ");
        visitMeasurements(metrics, configuration.getClassMeasurements());
        append(",").eol();

        indent().append("\"methods\": ");
        visitMethodMetrics(metrics.getSubMetrics());
        eol();

        lowerIndent();
        indent().append("}");
    }

    private void visitMethodMetrics(Collection<Metrics> metrics) {
        if (hasVisibleMetrics(metrics)) {
            append("[").eol();
            raiseIndent();

            indent().append(metrics.stream()
                    .filter(this::isVisibleMetrics)
                    .map(methodMetrics -> {
                        StringWriter out = new StringWriter();
                        clonePrinter(out).visitMethodMetrics(methodMetrics);
                        return out.toString();
                    })
                    .collect(joining(", "))).eol();

            lowerIndent();
            indent().append("]");
        } else {
            append("[]");
        }
    }

    private void visitMethodMetrics(Metrics metrics) {
        append("{").eol();
        raiseIndent();
        indent().append("\"name\": \"").append(metrics.getName()).append("\",").eol();

        indent().append("\"measurements\": ");
        visitMeasurements(metrics, configuration.getMethodMeasurements());
        eol();

        lowerIndent();
        indent().append("}");
    }

    protected void visitMeasurements(Metrics metrics, List<MeasurementDescriptor> descriptors) {
        if (hasVisibleMeasurements(descriptors)) {
            append("[").eol();
            raiseIndent();

            indent().append(descriptors.stream()
                    .filter(descriptor -> isShowHiddenMeasurements() || descriptor.isVisible())
                    .map(descriptor -> metrics.getMeasurement(descriptor.getShortName()))
                    .map(measurement -> {
                        StringWriter out = new StringWriter();
                        measurement.accept(clonePrinter(out));
                        return out.toString();
                    })
                    .collect(joining(", "))).eol();

            lowerIndent();
            indent().append("]");
        } else {
            append("[]");
        }
    }

    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        append("{").eol();
        raiseIndent();

        indent().append("\"short-name\": \"").append(measurement.getShortName()).append("\",").eol();
        indent().append("\"long-name\": \"").append(measurement.getLongName()).append("\",").eol();
        indent().append("\"value\": ").append(measurement.getValue()).append(",").eol();
        indent().append("\"minimum\": ").append(measurement.getMinimum()).append(",").eol();
        indent().append("\"median\": ").append(measurement.getMedian()).append(",").eol();
        indent().append("\"average\": ").append(measurement.getAverage()).append(",").eol();
        indent().append("\"standard-deviation\": ").append(measurement.getStandardDeviation()).append(",").eol();
        indent().append("\"maximum\": ").append(measurement.getMaximum()).append(",").eol();
        indent().append("\"sum\": ").append(measurement.getSum()).append(",").eol();

        var requestedPercentiles = measurement.getRequestedPercentiles();
        if (requestedPercentiles.isEmpty()) {
            indent().append("\"nb-data-points\": ").append(measurement.getNbDataPoints()).eol();
        } else {
            indent().append("\"nb-data-points\": ").append(measurement.getNbDataPoints()).append(",").eol();
            indent().append("\"percentiles\": {").eol();
            raiseIndent();

            var i = requestedPercentiles.iterator();
            while (i.hasNext()) {
                var percentile = i.next();
                indent();
                append("\"p").append(percentile).append("\": ").append(measurement.getPercentile(percentile));
                if (i.hasNext()) {
                    append(",");
                }
                eol();
            }

            lowerIndent();
            indent().append("}").eol();
        }

        lowerIndent();
        indent().append("}");
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
        append("{").eol();
        raiseIndent();
        indent().append("\"short-name\": \"").append(measurement.getShortName()).append("\",").eol();
        indent().append("\"long-name\": \"").append(measurement.getLongName()).append("\",").eol();

        if (isExpandCollectionMeasurements()) {
            indent().append("\"value\": ").append(measurement.getValue()).append(",").eol();
            indent().append("\"members\": [").append(measurement.getValues().stream().sorted().map(value -> "\"" + value + "\"").collect(joining(", "))).append("]").eol();
        } else {
            indent().append("\"value\": ").append(measurement.getValue()).eol();
        }

        lowerIndent();
        indent().append("}");
    }
    
    protected void visitMeasurement(Measurement measurement) {
        append("{").eol();
        raiseIndent();
        indent().append("\"short-name\": \"").append(measurement.getShortName()).append("\",").eol();
        indent().append("\"long-name\": \"").append(measurement.getLongName()).append("\",").eol();
        indent().append("\"value\": ").append(measurement.getValue()).eol();
        lowerIndent();
        indent().append("}");
    }

    private JSONPrinter clonePrinter(StringWriter out) {
        JSONPrinter result = new JSONPrinter(new PrintWriter(out), configuration);

        result.setIndentLevel(getIndentLevel());
        result.setIndentText(getIndentText());
        result.setShowEmptyMetrics(isShowEmptyMetrics());
        result.setShowEmptyMetrics(isShowEmptyMetrics());
        result.setExpandCollectionMeasurements(isExpandCollectionMeasurements());

        return result;
    }
}
