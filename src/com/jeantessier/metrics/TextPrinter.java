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
import java.text.*;
import java.util.*;

public class TextPrinter extends Printer {
    private static final NumberFormat valueFormat = new DecimalFormat("#.##");
    private static final NumberFormat ratioFormat = new DecimalFormat("#%");

    private List<MeasurementDescriptor> descriptors;

    private boolean expandCollectionMeasurements;
    
    private Metrics currentMetrics = null;
    
    public TextPrinter(PrintWriter out, List<MeasurementDescriptor> descriptors) {
        super(out);
        
        this.descriptors = descriptors;
    }

    public boolean isExpandCollectionMeasurements() {
        return expandCollectionMeasurements;
    }

    public void setExpandCollectionMeasurements(boolean expandCollectionMeasurements) {
        this.expandCollectionMeasurements = expandCollectionMeasurements;
    }
    
    public void visitMetrics(Metrics metrics) {
        if (isShowEmptyMetrics() || isShowHiddenMeasurements() || !metrics.isEmpty()) {
            currentMetrics = metrics;
            
            indent().append(metrics.getName()).eol();
            raiseIndent();

            for (MeasurementDescriptor descriptor : descriptors) {
                if (isShowHiddenMeasurements() || descriptor.isVisible()) {
                    metrics.getMeasurement(descriptor.getShortName()).accept(this);
                }
            }
            
            lowerIndent();
            
            eol();
        }
    }

    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        indent().append(measurement.getLongName()).append(" (").append(measurement.getShortName()).append("): ").append(valueFormat.format(measurement.getValue()));

        try {
            RatioMeasurement ratio = (RatioMeasurement) currentMetrics.getMeasurement(measurement.getShortName() + "R");
            append(" (").append(ratioFormat.format(ratio.getValue())).append(")");
        } catch (ClassCastException ex) {
            // Do nothing, no ratio for this measurement
        }

        append(" ").append(measurement);
        
        eol();
    }
    
    public void visitRatioMeasurement(RatioMeasurement measurement) {
        if (!measurement.getShortName().endsWith("R")) {
            super.visitRatioMeasurement(measurement);
        }
    }
    
    public void visitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
        super.visitContextAccumulatorMeasurement(measurement);

        visitCollectionMeasurement(measurement);
    }
    
    public void visitNameListMeasurement(NameListMeasurement measurement) {
        super.visitNameListMeasurement(measurement);

        visitCollectionMeasurement(measurement);
    }
    
    public void visitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
        super.visitSubMetricsAccumulatorMeasurement(measurement);

        visitCollectionMeasurement(measurement);
    }
    
    protected void visitCollectionMeasurement(CollectionMeasurement measurement) {
        if (isExpandCollectionMeasurements()) {
            raiseIndent();
            for (String value : measurement.getValues()) {
                indent().append(value).eol();
            }
            lowerIndent();
        }
    }
    
    protected void visitMeasurement(Measurement measurement) {
        indent().append(measurement.getLongName()).append(" (").append(measurement.getShortName()).append("): ").append(valueFormat.format(measurement.getValue()));

        try {
            RatioMeasurement ratio = (RatioMeasurement) currentMetrics.getMeasurement(measurement.getShortName() + "R");
            append(" (").append(ratioFormat.format(ratio.getValue())).append(")");
        } catch (ClassCastException ex) {
            // Do nothing, no ratio for this measurement
        }

        eol();
    }
}
