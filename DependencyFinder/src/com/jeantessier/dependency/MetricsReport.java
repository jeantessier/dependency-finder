/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

public class MetricsReport {
	private StringWriter buffer = new StringWriter();
	private PrintWriter  out    = new PrintWriter(buffer);

	boolean list_elements               = false;
	boolean classes_per_package_chart   = false;
	boolean features_per_class_chart    = false;
	boolean inbounds_per_package_chart  = false;
	boolean outbounds_per_package_chart = false;
	boolean inbounds_per_class_chart    = false;
	boolean outbounds_per_class_chart   = false;
	boolean inbounds_per_feature_chart  = false;
	boolean outbounds_per_feature_chart = false;

	public boolean ListElements() {
		return list_elements;
	}

	public void ListElements(boolean list_elements) {
		this.list_elements = list_elements;
	}
	
	public boolean ClassesPerPackageChart() {
		return classes_per_package_chart;
	}

	public void ClassesPerPackageChart(boolean classes_per_package_chart) {
		this.classes_per_package_chart = classes_per_package_chart;
	}
	
	public boolean FeaturesPerClassChart() {
		return features_per_class_chart;
	}

	public void FeaturesPerClassChart(boolean features_per_class_chart) {
		this.features_per_class_chart = features_per_class_chart;
	}
	
	public boolean InboundsPerPackageChart() {
		return inbounds_per_package_chart;
	}

	public void InboundsPerPackageChart(boolean inbounds_per_package_chart) {
		this.inbounds_per_package_chart = inbounds_per_package_chart;
	}
	
	public boolean OutboundsPerPackageChart() {
		return outbounds_per_package_chart;
	}

	public void OutboundsPerPackageChart(boolean outbounds_per_package_chart) {
		this.outbounds_per_package_chart = outbounds_per_package_chart;
	}
	
	public boolean InboundsPerClassChart() {
		return inbounds_per_class_chart;
	}

	public void InboundsPerClassChart(boolean inbounds_per_class_chart) {
		this.inbounds_per_class_chart = inbounds_per_class_chart;
	}
	
	public boolean OutboundsPerClassChart() {
		return outbounds_per_class_chart;
	}

	public void OutboundsPerClassChart(boolean outbounds_per_class_chart) {
		this.outbounds_per_class_chart = outbounds_per_class_chart;
	}
	
	public boolean InboundsPerFeatureChart() {
		return inbounds_per_feature_chart;
	}

	public void InboundsPerFeatureChart(boolean inbounds_per_feature_chart) {
		this.inbounds_per_feature_chart = inbounds_per_feature_chart;
	}
	
	public boolean OutboundsPerFeatureChart() {
		return outbounds_per_feature_chart;
	}

	public void OutboundsPerFeatureChart(boolean outbounds_per_feature_chart) {
		this.outbounds_per_feature_chart = outbounds_per_feature_chart;
	}
	
	public void Process(MetricsGatherer metrics) {
		Iterator j;
		
		out.println(metrics.Packages().size() + " package(s)");
		if (ListElements()) {
			j = metrics.Packages().iterator();
			while (j.hasNext()) {
				out.println("    " + j.next());
			}
		}
		
		out.println(metrics.Classes().size() + " class(es)");
		if (ListElements()) {
			j = metrics.Classes().iterator();
			while (j.hasNext()) {
				out.println("    " + j.next());
			}
		}

		out.println(metrics.Features().size() + " feature(s)");
		if (ListElements()) {
			j = metrics.Features().iterator();
			while (j.hasNext()) {
				out.println("    " + j.next());
			}
		}

		out.println();

		out.println(metrics.NbOutbound() + " outbound link(s)");
		out.println("    " + metrics.NbOutboundPackages() + " from package(s) (average " + (metrics.NbOutboundPackages() / (double) metrics.Packages().size()) + " per package)");
		out.println("    " + metrics.NbOutboundClasses() + " from class(es) (average " + (metrics.NbOutboundClasses() / (double) metrics.Classes().size()) + " per class)");
		out.println("    " + metrics.NbOutboundFeatures() + " from feature(s) (average " + (metrics.NbOutboundFeatures() / (double) metrics.Features().size()) + " per feature)");

		out.println(metrics.NbInbound() + " inbound link(s)");
		out.println("    " + metrics.NbInboundPackages() + " to package(s) (average " + (metrics.NbInboundPackages() / (double) metrics.Packages().size()) + " per package)");
		out.println("    " + metrics.NbInboundClasses() + " to class(es) (average " + (metrics.NbInboundClasses() / (double) metrics.Classes().size()) + " per class)");
		out.println("    " + metrics.NbInboundFeatures() + " to feature(s) (average " + (metrics.NbInboundFeatures() / (double) metrics.Features().size()) + " per feature)");

		if (ClassesPerPackageChart()   ||
			FeaturesPerClassChart()    ||
			InboundsPerPackageChart()  ||
			OutboundsPerPackageChart() ||
			InboundsPerClassChart()    ||
			OutboundsPerClassChart()   ||
			InboundsPerFeatureChart()  ||
			OutboundsPerFeatureChart()) {

			out.println();

			out.print("n");
			if (ClassesPerPackageChart()) {
				out.print(", \"classes per package\"");
			}
			if (FeaturesPerClassChart()) {
				out.print(", \"features per class\"");
			}
			if (InboundsPerPackageChart()) {
				out.print(", \"inbounds per package\"");
			}
			if (OutboundsPerPackageChart()) {
				out.print(", \"outbounds per package\"");
			}
			if (InboundsPerClassChart()) {
				out.print(", \"inbounds per class\"");
			}
			if (OutboundsPerClassChart()) {
				out.print(", \"outbounds per class\"");
			}
			if (InboundsPerFeatureChart()) {
				out.print(", \"inbounds per feature\"");
			}
			if (OutboundsPerFeatureChart()) {
				out.print(", \"outbounds per feature\"");
			}
			out.println();

			for (int k=0; k<=metrics.ChartSize(); k++) {
				long[] data_point = metrics.ChartData(k);
				
				out.print(k);
				if (ClassesPerPackageChart()) {
					out.print(", " + data_point[MetricsGatherer.CLASSES_PER_PACKAGE]);
				}
				if (FeaturesPerClassChart()) {
					out.print(", " + data_point[MetricsGatherer.FEATURES_PER_CLASS]);
				}
				if (InboundsPerPackageChart()) {
					out.print(", " + data_point[MetricsGatherer.INBOUNDS_PER_PACKAGE]);
				}
				if (OutboundsPerPackageChart()) {
					out.print(", " + data_point[MetricsGatherer.OUTBOUNDS_PER_PACKAGE]);
				}
				if (InboundsPerClassChart()) {
					out.print(", " + data_point[MetricsGatherer.INBOUNDS_PER_CLASS]);
				}
				if (OutboundsPerClassChart()) {
					out.print(", " + data_point[MetricsGatherer.OUTBOUNDS_PER_CLASS]);
				}
				if (InboundsPerFeatureChart()) {
					out.print(", " + data_point[MetricsGatherer.INBOUNDS_PER_FEATURE]);
				}
				if (OutboundsPerFeatureChart()) {
					out.print(", " + data_point[MetricsGatherer.OUTBOUNDS_PER_FEATURE]);
				}
				out.println();
			}
		}

		out.flush();
		out.close();
	}

	public String toString() {
		return buffer.toString();
	}
}
