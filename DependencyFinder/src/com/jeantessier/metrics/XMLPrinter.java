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

package com.jeantessier.metrics;

import java.util.*;

public class XMLPrinter extends Printer {
	public XMLPrinter() {
		super();
	}

	public XMLPrinter(String indent_text) {
		super(indent_text);
	}

	public void VisitMetrics(Metrics metrics) {
		Indent().Append("<metrics>").Append("\n");
		RaiseIndent();
		
		VisitProjectMetrics(metrics);
				
		LowerIndent();
		Indent().Append("</metrics>").Append("\n");
	}

	private void VisitProjectMetrics(Metrics metrics) {
		Indent().Append("<project>").Append("\n");
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>\n");

		VisitMeasurements(metrics);

		Iterator i = metrics.SubMetrics().iterator();
		while (i.hasNext()) {
			VisitGroupMetrics((Metrics) i.next());
		}
				
		LowerIndent();
		Indent().Append("</project>").Append("\n");
	}

	private void VisitGroupMetrics(Metrics metrics) {
		Indent().Append("<group>").Append("\n");
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>\n");

		VisitMeasurements(metrics);

		Iterator i = metrics.SubMetrics().iterator();
		while (i.hasNext()) {
			VisitClassMetrics((Metrics) i.next());
		}
				
		LowerIndent();
		Indent().Append("</group>").Append("\n");
	}

	private void VisitClassMetrics(Metrics metrics) {
		Indent().Append("<class>").Append("\n");
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>\n");

		VisitMeasurements(metrics);

		Iterator i = metrics.SubMetrics().iterator();
		while (i.hasNext()) {
			VisitMethodMetrics((Metrics) i.next());
		}
				
		LowerIndent();
		Indent().Append("</class>").Append("\n");
	}

	private void VisitMethodMetrics(Metrics metrics) {
		Indent().Append("<method>").Append("\n");
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>\n");

		VisitMeasurements(metrics);
				
		LowerIndent();
		Indent().Append("</method>").Append("\n");
	}

	private void VisitMeasurements(Metrics metrics) {
		Iterator names = metrics.MetricNames().iterator();
		while (names.hasNext()) {
			String      name    = (String) names.next();
			Measurement measure = metrics.Metric(name);

			Indent().Append("<metric>").Append("\n");
			RaiseIndent();
			Indent().Append("<name>").Append(name).Append("</name>\n");

			measure.Accept(this);
				
			LowerIndent();
			Indent().Append("</metric>").Append("\n");
		}
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		Indent().Append("<minimum>").Append(measurement.Minimum()).Append("</minimum>\n");
		Indent().Append("<median>").Append(measurement.Median()).Append("</median>\n");
		Indent().Append("<average>").Append(measurement.Average()).Append("</average>\n");
		Indent().Append("<maximum>").Append(measurement.Maximum()).Append("</maximum>\n");
		Indent().Append("<sum>").Append(measurement.Sum()).Append("</sum>\n");
		Indent().Append("<nb-data-points>").Append(measurement.NbDataPoints()).Append("</nb-data-points>\n");
	}
	
	protected void VisitNumericalMeasurement(NumericalMeasurement measurement) {
		Indent().Append("<value>").Append(measurement.Value()).Append("</value>\n");
	}
}
