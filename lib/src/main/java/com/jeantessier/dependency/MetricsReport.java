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

    private boolean showingClassesPerPackageChart = false;
    private boolean showingFeaturesPerClassChart = false;
    private boolean showingInboundsPerPackageChart = false;
    private boolean showingOutboundsPerPackageChart = false;
    private boolean showingInboundsPerClassChart = false;
    private boolean showingOutboundsPerClassChart = false;
    private boolean showingInboundsPerFeatureChart = false;
    private boolean showingOutboundsPerFeatureChart = false;

    private boolean showingClassesPerPackageHistogram = false;
    private boolean showingFeaturesPerClassHistogram = false;
    private boolean showingInboundsPerPackageHistogram = false;
    private boolean showingOutboundsPerPackageHistogram = false;
    private boolean showingInboundsPerClassHistogram = false;
    private boolean showingOutboundsPerClassHistogram = false;
    private boolean showingInboundsPerFeatureHistogram = false;
    private boolean showingOutboundsPerFeatureHistogram = false;

    public MetricsReport(PrintWriter out) {
        this.out = out;
    }

    public boolean isListingElements() {
        return listingElements;
    }

    public void setListingElements(boolean listingElements) {
        this.listingElements = listingElements;
    }

    public boolean isShowingClassesPerPackageChart() {
        return showingClassesPerPackageChart;
    }

    public void setShowingClassesPerPackageChart(boolean showingClassesPerPackageChart) {
        this.showingClassesPerPackageChart = showingClassesPerPackageChart;
    }

    public boolean isShowingFeaturesPerClassChart() {
        return showingFeaturesPerClassChart;
    }

    public void setShowingFeaturesPerClassChart(boolean showingFeaturesPerClassChart) {
        this.showingFeaturesPerClassChart = showingFeaturesPerClassChart;
    }

    public boolean isShowingInboundsPerPackageChart() {
        return showingInboundsPerPackageChart;
    }

    public void setShowingInboundsPerPackageChart(boolean showingInboundsPerPackageChart) {
        this.showingInboundsPerPackageChart = showingInboundsPerPackageChart;
    }

    public boolean isShowingOutboundsPerPackageChart() {
        return showingOutboundsPerPackageChart;
    }

    public void setShowingOutboundsPerPackageChart(boolean showingOutboundsPerPackageChart) {
        this.showingOutboundsPerPackageChart = showingOutboundsPerPackageChart;
    }

    public boolean isShowingInboundsPerClassChart() {
        return showingInboundsPerClassChart;
    }

    public void setShowingInboundsPerClassChart(boolean showingInboundsPerClassChart) {
        this.showingInboundsPerClassChart = showingInboundsPerClassChart;
    }

    public boolean isShowingOutboundsPerClassChart() {
        return showingOutboundsPerClassChart;
    }

    public void setShowingOutboundsPerClassChart(boolean showingOutboundsPerClassChart) {
        this.showingOutboundsPerClassChart = showingOutboundsPerClassChart;
    }

    public boolean isShowingInboundsPerFeatureChart() {
        return showingInboundsPerFeatureChart;
    }

    public void setShowingInboundsPerFeatureChart(boolean showingInboundsPerFeatureChart) {
        this.showingInboundsPerFeatureChart = showingInboundsPerFeatureChart;
    }

    public boolean isShowingOutboundsPerFeatureChart() {
        return showingOutboundsPerFeatureChart;
    }

    public void setShowingOutboundsPerFeatureChart(boolean showingOutboundsPerFeatureChart) {
        this.showingOutboundsPerFeatureChart = showingOutboundsPerFeatureChart;
    }

    public boolean isShowingClassesPerPackageHistogram() {
        return showingClassesPerPackageHistogram;
    }

    public void setShowingClassesPerPackageHistogram(boolean showingClassesPerPackageHistogram) {
        this.showingClassesPerPackageHistogram = showingClassesPerPackageHistogram;
    }

    public boolean isShowingFeaturesPerClassHistogram() {
        return showingFeaturesPerClassHistogram;
    }

    public void setShowingFeaturesPerClassHistogram(boolean showingFeaturesPerClassHistogram) {
        this.showingFeaturesPerClassHistogram = showingFeaturesPerClassHistogram;
    }

    public boolean isShowingInboundsPerPackageHistogram() {
        return showingInboundsPerPackageHistogram;
    }

    public void setShowingInboundsPerPackageHistogram(boolean showingInboundsPerPackageHistogram) {
        this.showingInboundsPerPackageHistogram = showingInboundsPerPackageHistogram;
    }

    public boolean isShowingOutboundsPerPackageHistogram() {
        return showingOutboundsPerPackageHistogram;
    }

    public void setShowingOutboundsPerPackageHistogram(boolean showingOutboundsPerPackageHistogram) {
        this.showingOutboundsPerPackageHistogram = showingOutboundsPerPackageHistogram;
    }

    public boolean isShowingInboundsPerClassHistogram() {
        return showingInboundsPerClassHistogram;
    }

    public void setShowingInboundsPerClassHistogram(boolean showingInboundsPerClassHistogram) {
        this.showingInboundsPerClassHistogram = showingInboundsPerClassHistogram;
    }

    public boolean isShowingOutboundsPerClassHistogram() {
        return showingOutboundsPerClassHistogram;
    }

    public void setShowingOutboundsPerClassHistogram(boolean showingOutboundsPerClassHistogram) {
        this.showingOutboundsPerClassHistogram = showingOutboundsPerClassHistogram;
    }

    public boolean isShowingInboundsPerFeatureHistogram() {
        return showingInboundsPerFeatureHistogram;
    }

    public void setShowingInboundsPerFeatureHistogram(boolean showingInboundsPerFeatureHistogram) {
        this.showingInboundsPerFeatureHistogram = showingInboundsPerFeatureHistogram;
    }

    public boolean isShowingOutboundsPerFeatureHistogram() {
        return showingOutboundsPerFeatureHistogram;
    }

    public void setShowingOutboundsPerFeatureHistogram(boolean showingOutboundsPerFeatureHistogram) {
        this.showingOutboundsPerFeatureHistogram = showingOutboundsPerFeatureHistogram;
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
