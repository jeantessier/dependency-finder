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
		Append(Preamble());
		Indent().Append("<metrics>").Append("\n");
		RaiseIndent();
		
		VisitProjectMetrics(metrics);
				
		LowerIndent();
		Indent().Append("</metrics>").Append("\n");
	}

	private String Preamble() {
		StringBuffer result = new StringBuffer();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n");
		result.append("\n");
		result.append("<!DOCTYPE metrics SYSTEM \"http://depfind.sourceforge.net/dtd/metrics.dtd\">\n");
		result.append("\n");

		return result.toString();
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
		Iterator names = metrics.MeasurementNames().iterator();
		while (names.hasNext()) {
			String      name        = (String) names.next();
			Measurement measurement = metrics.Measurement(name);

			Indent().Append("<metric>").Append("\n");
			RaiseIndent();
			Indent().Append("<short-name>").Append(measurement.ShortName()).Append("</short-name>\n");
			Indent().Append("<long-name>").Append(measurement.LongName()).Append("</long-name>\n");

			measurement.Accept(this);
				
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
	
	protected void VisitMeasurement(Measurement measurement) {
		Indent().Append("<value>").Append(measurement.Value()).Append("</value>\n");
	}
}
