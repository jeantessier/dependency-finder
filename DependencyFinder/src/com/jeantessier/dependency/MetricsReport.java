/*
 *  Copyright (c) 2001-2004, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

	boolean listingElements             = false;
	boolean chartingClassesPerPackage   = false;
	boolean chartingFeaturesPerClass    = false;
	boolean chartingInboundsPerPackage  = false;
	boolean chartingOutboundsPerPackage = false;
	boolean chartingInboundsPerClass    = false;
	boolean chartingOutboundsPerClass   = false;
	boolean chartingInboundsPerFeature  = false;
	boolean chartingOutboundsPerFeature = false;

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
		Iterator j;
		
		out.println(metrics.getPackages().size() + " package(s)");
		if (isListingElements()) {
			j = metrics.getPackages().iterator();
			while (j.hasNext()) {
				out.println("    " + j.next());
			}
		}
		
		out.println(metrics.getClasses().size() + " class(es)");
		if (isListingElements()) {
			j = metrics.getClasses().iterator();
			while (j.hasNext()) {
				out.println("    " + j.next());
			}
		}

		out.println(metrics.getFeatures().size() + " feature(s)");
		if (isListingElements()) {
			j = metrics.getFeatures().iterator();
			while (j.hasNext()) {
				out.println("    " + j.next());
			}
		}

		out.println();

		out.println(metrics.getNbOutbound() + " outbound link(s)");
		out.println("    " + metrics.getNbOutboundPackages() + " from package(s) (average " + (metrics.getNbOutboundPackages() / (double) metrics.getPackages().size()) + " per package)");
		out.println("    " + metrics.getNbOutboundClasses() + " from class(es) (average " + (metrics.getNbOutboundClasses() / (double) metrics.getClasses().size()) + " per class)");
		out.println("    " + metrics.getNbOutboundFeatures() + " from feature(s) (average " + (metrics.getNbOutboundFeatures() / (double) metrics.getFeatures().size()) + " per feature)");

		out.println(metrics.getNbInbound() + " inbound link(s)");
		out.println("    " + metrics.getNbInboundPackages() + " to package(s) (average " + (metrics.getNbInboundPackages() / (double) metrics.getPackages().size()) + " per package)");
		out.println("    " + metrics.getNbInboundClasses() + " to class(es) (average " + (metrics.getNbInboundClasses() / (double) metrics.getClasses().size()) + " per class)");
		out.println("    " + metrics.getNbInboundFeatures() + " to feature(s) (average " + (metrics.getNbInboundFeatures() / (double) metrics.getFeatures().size()) + " per feature)");

		if (isChartingClassesPerPackage()   ||
			isChartingFeaturesPerClass()    ||
			isChartingInboundsPerPackage()  ||
			isChartingOutboundsPerPackage() ||
			isChartingInboundsPerClass()    ||
			isChartingOutboundsPerClass()   ||
			isChartingInboundsPerFeature()  ||
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
}
