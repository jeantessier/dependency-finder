/*
 *  Copyright (c) 2001-2002, Jean Tessier
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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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
