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

package com.jeantessier.metrics;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

public class CSVPrinter extends Printer {
    private final List<MeasurementDescriptor> descriptors;
    
    public CSVPrinter(PrintWriter out, List<MeasurementDescriptor> descriptors) {
        super(out);
        
        this.descriptors = descriptors;
    }

    public void visitMetrics(Collection<Metrics> metrics) {
        appendHeader();
        super.visitMetrics(metrics);
    }

    public void visitMetrics(Metrics metrics) {
        if (isVisibleMetrics(metrics)) {
            append("\"").append(metrics.getName()).append("\", ");
            
            Iterator<MeasurementDescriptor> i = descriptors.iterator();
            while (i.hasNext()) {
                MeasurementDescriptor descriptor = i.next();
                
                if (isVisibleMeasurement(descriptor)) {
                    Measurement measurement = metrics.getMeasurement(descriptor.getShortName());
                    
                    measurement.accept(this);
                    
                    if (i.hasNext()) {
                        append(", ");
                    }
                }
            }
            
            eol();
        }
    }

    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        append(
                Stream.concat(
                        Stream.of(
                                measurement.getMinimum(),
                                measurement.getMedian(),
                                measurement.getAverage(),
                                measurement.getStandardDeviation(),
                                measurement.getMaximum(),
                                measurement.getSum(),
                                measurement.getNbDataPoints()
                        ),
                        measurement.getRequestedPercentiles().stream().map(measurement::getPercentile)
                )
                .map(String::valueOf)
                .collect(joining(", ")));
    }
    
    protected void visitMeasurement(Measurement measurement) {
        append(measurement.getValue());
    }

    private void appendHeader() {
        appendLongNames();
        appendShortNames();
        appendStatSubNames();
    }

    private void appendLongNames() {
        append(
                Stream.concat(
                                Stream.of("name"),
                                descriptors.stream()
                                        .filter(this::isVisibleMeasurement)
                                        .flatMap(descriptor -> {
                                            if (descriptor.getClassFor().equals(StatisticalMeasurement.class)) {
                                                return IntStream.rangeClosed(1, StatisticalMeasurement.countValues(descriptor.getInitText())).mapToObj(n -> descriptor.getLongName());
                                            } else {
                                                return Stream.of(descriptor.getLongName());
                                            }
                                        })
                        )
                        .map(name -> name.isEmpty() ? "" : "\"" + name + "\"")
                        .collect(joining(", ")));
        eol();
    }

    private void appendShortNames() {
        append(
                Stream.concat(
                                Stream.of(""),
                                descriptors.stream()
                                        .filter(this::isVisibleMeasurement)
                                        .flatMap(descriptor -> {
                                            if (descriptor.getClassFor().equals(StatisticalMeasurement.class)) {
                                                return IntStream.rangeClosed(1, StatisticalMeasurement.countValues(descriptor.getInitText())).mapToObj(n -> descriptor.getShortName());
                                            } else {
                                                return Stream.of(descriptor.getShortName());
                                            }
                                        })
                        )
                        .map(name -> name.isEmpty() ? "" : "\"" + name + "\"")
                        .collect(joining(", ")));
        eol();
    }

    private void appendStatSubNames() {
        append(
                Stream.concat(
                                Stream.of(""),
                                descriptors.stream()
                                        .filter(this::isVisibleMeasurement)
                                        .flatMap(descriptor -> {
                                            if (descriptor.getClassFor().equals(StatisticalMeasurement.class)) {
                                                try {
                                                    return Stream.concat(
                                                            Stream.of("minimum", "median", "average", "std dev", "maximum", "sum", "nb"),
                                                            StatisticalMeasurement.parseRequestedPercentiles(descriptor.getInitText()).stream().map(percentile -> "p" + percentile)
                                                    );
                                                } catch (IOException e) {
                                                    return Stream.of("minimum", "median", "average", "std dev", "maximum", "sum", "nb");
                                                }
                                            } else {
                                                return Stream.of("");
                                            }
                                        })
                        )
                        .map(name -> name.isEmpty() ? "" : "\"" + name + "\"")
                        .collect(joining(", ")));
        eol();
    }
}
