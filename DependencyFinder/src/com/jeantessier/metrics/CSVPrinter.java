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
