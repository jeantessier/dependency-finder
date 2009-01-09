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

public abstract class Printer implements MeasurementVisitor {
    public static final String DEFAULT_INDENT_TEXT = "    ";

    private PrintWriter out;

    private boolean showEmptyMetrics = false;
    private boolean showHiddenMeasurements = false;
    private String indentText = DEFAULT_INDENT_TEXT;
    private int indentLevel = 0;

    public Printer(PrintWriter out) {
        this.out = out;
    }
    
    public String getIndentText() {
        return indentText;
    }

    public void setIndentText(String indentText) {
        this.indentText = indentText;
    }

    public boolean isShowEmptyMetrics() {
        return showEmptyMetrics;
    }

    public void setShowEmptyMetrics(boolean showEmptyMetrics) {
        this.showEmptyMetrics = showEmptyMetrics;
    }

    public boolean isShowHiddenMeasurements() {
        return showHiddenMeasurements;
    }

    public void setShowHiddenMeasurements(boolean showHiddenMeasurements) {
        this.showHiddenMeasurements = showHiddenMeasurements;
    }
    
    protected Printer append(boolean b) {
        out.print(b);
        return this;
    }

    protected Printer append(char c) {
        out.print(c);
        return this;
    }

    protected Printer append(char[] s) {
        out.print(s);
        return this;
    }

    protected Printer append(double d) {
        out.print(d);
        return this;
    }

    protected Printer append(float f) {
        out.print(f);
        return this;
    }

    protected Printer append(int i) {
        out.print(i);
        return this;
    }

    protected Printer append(long l) {
        out.print(l);
        return this;
    }

    protected Printer append(Object obj) {
        out.print(obj);
        return this;
    }

    protected Printer append(String s) {
        out.print(s);
        return this;
    }

    protected Printer indent() {
        for (int i=0; i<indentLevel; i++) {
            append(getIndentText());
        }

        return this;
    }

    protected Printer eol() {
        out.println();
        return this;
    }
    
    protected void raiseIndent() {
        indentLevel++;
    }

    protected void lowerIndent() {
        indentLevel--;
    }

    public void visitMetrics(Collection<Metrics> metrics) {
        for (Metrics metric : metrics) {
            visitMetrics(metric);
        }
    }
    
    public abstract void visitMetrics(Metrics metrics);
    
    public void visitRatioMeasurement(RatioMeasurement measurement) {
        visitMeasurement(measurement);
    }
    
    public void visitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
        visitMeasurement(measurement);
    }
    
    public void visitCounterMeasurement(CounterMeasurement measurement) {
        visitMeasurement(measurement);
    }
    
    public void visitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
        visitMeasurement(measurement);
    }
        
    public void visitNameListMeasurement(NameListMeasurement measurement) {
        visitMeasurement(measurement);
    }
    
    public void visitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
        visitMeasurement(measurement);
    }

    public void visitSumMeasurement(SumMeasurement measurement) {
        visitMeasurement(measurement);
    }

    protected abstract void visitMeasurement(Measurement measurement);
}
