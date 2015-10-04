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

package com.jeantessier.metrics;

import java.io.*;
import java.util.*;

public class XMLPrinter extends Printer {
    public static final String DEFAULT_ENCODING   = "utf-8";
    public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

    private MetricsConfiguration configuration;
    
    public XMLPrinter(PrintWriter out, MetricsConfiguration configuration) {
        this(out, configuration, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
    }
    
    public XMLPrinter(PrintWriter out, MetricsConfiguration configuration, String encoding, String dtdPrefix) {
        super(out);
        
        this.configuration = configuration;

        appendHeader(encoding, dtdPrefix);
    }

    private void appendHeader(String encoding, String dtdPrefix) {
        append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
        eol();
        append("<!DOCTYPE metrics SYSTEM \"").append(dtdPrefix).append("/metrics.dtd\">").eol();
        eol();
    }

    public void visitMetrics(Metrics metrics) {
        indent().append("<metrics>").eol();
        raiseIndent();
        
        visitProjectMetrics(metrics);
                
        lowerIndent();
        indent().append("</metrics>").eol();
    }

    private void visitProjectMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            indent().append("<project>").eol();
            raiseIndent();
            indent().append("<name>").append(metrics.getName()).append("</name>").eol();
            
            visitMeasurements(metrics, configuration.getProjectMeasurements());

            for (Metrics subMetrics : metrics.getSubMetrics()) {
                visitGroupMetrics(subMetrics);
            }
            
            lowerIndent();
            indent().append("</project>").eol();
        }
    }

    private void visitGroupMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            indent().append("<group>").eol();
            raiseIndent();
            indent().append("<name>").append(metrics.getName()).append("</name>").eol();
            
            visitMeasurements(metrics, configuration.getGroupMeasurements());

            for (Metrics subMetrics : metrics.getSubMetrics()) {
                visitClassMetrics(subMetrics);
            }
            
            lowerIndent();
            indent().append("</group>").eol();
        }
    }

    private void visitClassMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            indent().append("<class>").eol();
            raiseIndent();
            indent().append("<name>").append(metrics.getName()).append("</name>").eol();
            
            visitMeasurements(metrics, configuration.getClassMeasurements());

            for (Metrics subMetrics : metrics.getSubMetrics()) {
                visitMethodMetrics(subMetrics);
            }
            
            lowerIndent();
            indent().append("</class>").eol();
        }
    }

    private void visitMethodMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            indent().append("<method>").eol();
            raiseIndent();
            indent().append("<name>").append(metrics.getName()).append("</name>").eol();
            
            visitMeasurements(metrics, configuration.getMethodMeasurements());
            
            lowerIndent();
            indent().append("</method>").eol();
        }
    }

    private void visitMeasurements(Metrics metrics, List<MeasurementDescriptor> descriptors) {
        for (MeasurementDescriptor descriptor : descriptors) {
            if (isShowHiddenMeasurements() || descriptor.isVisible()) {
                metrics.getMeasurement(descriptor.getShortName()).accept(this);
            }
        }
    }

    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        indent().append("<measurement>").eol();
        raiseIndent();
        indent().append("<short-name>").append(measurement.getShortName()).append("</short-name>").eol();
        indent().append("<long-name>").append(measurement.getLongName()).append("</long-name>").eol();
        indent().append("<value>").append(measurement.getValue()).append("</value>").eol();
        indent().append("<minimum>").append(measurement.getMinimum()).append("</minimum>").eol();
        indent().append("<median>").append(measurement.getMedian()).append("</median>").eol();
        indent().append("<average>").append(measurement.getAverage()).append("</average>").eol();
        indent().append("<standard-deviation>").append(measurement.getStandardDeviation()).append("</standard-deviation>").eol();
        indent().append("<maximum>").append(measurement.getMaximum()).append("</maximum>").eol();
        indent().append("<sum>").append(measurement.getSum()).append("</sum>").eol();
        indent().append("<nb-data-points>").append(measurement.getNbDataPoints()).append("</nb-data-points>").eol();
        lowerIndent();
        indent().append("</measurement>").eol();
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
        indent().append("<measurement>").eol();
        raiseIndent();
        indent().append("<short-name>").append(measurement.getShortName()).append("</short-name>").eol();
        indent().append("<long-name>").append(measurement.getLongName()).append("</long-name>").eol();
        indent().append("<value>").append(measurement.getValue()).append("</value>").eol();
        indent().append("<members>").eol();
        raiseIndent();
        for (Object member : measurement.getValues()) {
            indent().append("<member>").append(member).append("</member>").eol();
        }
        lowerIndent();
        indent().append("</members>").eol();
        lowerIndent();
        indent().append("</measurement>").eol();
    }
    
    protected void visitMeasurement(Measurement measurement) {
        indent().append("<measurement>").eol();
        raiseIndent();
        indent().append("<short-name>").append(measurement.getShortName()).append("</short-name>").eol();
        indent().append("<long-name>").append(measurement.getLongName()).append("</long-name>").eol();
        indent().append("<value>").append(measurement.getValue()).append("</value>").eol();
        lowerIndent();
        indent().append("</measurement>").eol();
    }
}
