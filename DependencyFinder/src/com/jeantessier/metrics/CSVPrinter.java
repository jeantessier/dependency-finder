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

public class CSVPrinter extends Printer {
	private boolean is_first = true;
	
	public CSVPrinter() {
		super();
	}

	public CSVPrinter(String indent_text) {
		super(indent_text);
	}
			
	public void VisitMetrics(Metrics metrics) {
		if (is_first) {
			VisitFirstMetrics(metrics);
		}
		
		Append("\"").Append(metrics.Name()).Append("\", ");
			
		Iterator names = metrics.MetricNames().iterator();
		while (names.hasNext()) {
			metrics.Metric((String) names.next()).Accept(this);

			if (names.hasNext()) {
				Append(", ");
			}
		}

		Append("\n");
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		Append(measurement.Minimum()).Append(", ");
		Append(measurement.Median()).Append(", ");
		Append(measurement.Average()).Append(", ");
		Append(measurement.Maximum()).Append(", ");
		Append(measurement.Sum()).Append(", ");
		Append(measurement.NbDataPoints());
	}
	
	protected void VisitNumericalMeasurement(NumericalMeasurement measurement) {
		Append(measurement.Value());
	}

	private void VisitFirstMetrics(Metrics metrics) {
		Append("\"name\", ");
		
		Iterator names = metrics.MetricNames().iterator();
		while (names.hasNext()) {
			String      name    = (String) names.next();
			Measurement measure = metrics.Metric(name);
			if (measure instanceof StatisticalMeasurement) {
				Append("\"").Append(name).Append("(minimum)\", ");
				Append("\"").Append(name).Append("(median)\", ");
				Append("\"").Append(name).Append("(average)\", ");
				Append("\"").Append(name).Append("(maximum)\", ");
				Append("\"").Append(name).Append("(sum)\", ");
				Append("\"").Append(name).Append("(nb)\"");
			} else {
				Append("\"").Append(name).Append("\"");
			}
			
			if (names.hasNext()) {
				Append(", ");
			}
		}
		
		Append("\n");
		
		is_first = false;
	}
}
