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

import java.text.*;
import java.util.*;

public class PrettyPrinter extends Printer {
	private static final NumberFormat value_format = new DecimalFormat("#.##");
	private static final NumberFormat ratio_format = new DecimalFormat("#%");

	private Metrics current_metrics = null;
	
	public PrettyPrinter() {
		super();
	}

	public PrettyPrinter(String indent_text) {
		super(indent_text);
	}

	public void VisitMetrics(Metrics metrics) {
		current_metrics = metrics;
		
		Indent().Append(metrics.Name()).Append("\n");
		RaiseIndent();
			
		Iterator names = metrics.MetricNames().iterator();
		while (names.hasNext()) {
			metrics.Metric((String) names.next()).Accept(this);
		}

		LowerIndent();

		Append("\n");
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		Indent();
		Append(measurement.Name());
		Append(": (").Append(value_format.format(measurement.NbDataPoints())).Append(")");
		Append("\t").Append(value_format.format(measurement.Minimum()));
		Append(" ").Append(value_format.format(measurement.Median()));
		Append("/").Append(value_format.format(measurement.Average()));
		Append(" ").Append(value_format.format(measurement.Maximum()));
		Append(" (").Append(value_format.format(measurement.Sum())).Append(")");
		Append("\n");
	}
	
	public void VisitRatioMeasurement(RatioMeasurement measurement) {
		// Do nothing
	}
	
	protected void VisitNumericalMeasurement(NumericalMeasurement measurement) {
		Indent().Append(measurement.Name()).Append(":\t").Append(value_format.format(measurement.Value()));
		
		RatioMeasurement ratio = (RatioMeasurement) current_metrics.Metric(measurement.Name() + " ratio");
		if (ratio != null) {
			Append(" (").Append(ratio_format.format(ratio.Value())).Append(")");
		}
		
		Append("\n");
	}
}
