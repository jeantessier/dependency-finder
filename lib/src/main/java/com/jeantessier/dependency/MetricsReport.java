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

public abstract class MetricsReport {
    private final PrintWriter out;

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

    public abstract void process(MetricsGatherer metrics);

    protected void print(int i) {
        out.print(i);
    }

    protected void print(String s) {
        out.print(s);
    }

    protected void println() {
        out.println();
    }

    protected void println(String s) {
        out.println(s);
    }
}
