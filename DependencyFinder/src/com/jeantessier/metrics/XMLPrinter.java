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

		AppendHeader();
	}

	public XMLPrinter(String indent_text) {
		super(indent_text);

		AppendHeader();
	}

	private void AppendHeader() {
		Append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>").EOL();
		EOL();
		Append("<!DOCTYPE metrics SYSTEM \"http://depfind.sourceforge.net/dtd/metrics.dtd\">").EOL();
		EOL();
	}

	public void VisitMetrics(Metrics metrics) {
		Indent().Append("<metrics>").EOL();
		RaiseIndent();
		
		VisitProjectMetrics(metrics);
				
		LowerIndent();
		Indent().Append("</metrics>").EOL();
	}

	private void VisitProjectMetrics(Metrics metrics) {
		Indent().Append("<project>").EOL();
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();

		VisitMeasurements(metrics);

		Iterator i = metrics.SubMetrics().iterator();
		while (i.hasNext()) {
			VisitGroupMetrics((Metrics) i.next());
		}
				
		LowerIndent();
		Indent().Append("</project>").EOL();
	}

	private void VisitGroupMetrics(Metrics metrics) {
		Indent().Append("<group>").EOL();
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();

		VisitMeasurements(metrics);

		Iterator i = metrics.SubMetrics().iterator();
		while (i.hasNext()) {
			VisitClassMetrics((Metrics) i.next());
		}
				
		LowerIndent();
		Indent().Append("</group>").EOL();
	}

	private void VisitClassMetrics(Metrics metrics) {
		Indent().Append("<class>").EOL();
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();

		VisitMeasurements(metrics);

		Iterator i = metrics.SubMetrics().iterator();
		while (i.hasNext()) {
			VisitMethodMetrics((Metrics) i.next());
		}
				
		LowerIndent();
		Indent().Append("</class>").EOL();
	}

	private void VisitMethodMetrics(Metrics metrics) {
		Indent().Append("<method>").EOL();
		RaiseIndent();
		Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();

		VisitMeasurements(metrics);
				
		LowerIndent();
		Indent().Append("</method>").EOL();
	}

	private void VisitMeasurements(Metrics metrics) {
		Iterator names = metrics.MeasurementNames().iterator();
		while (names.hasNext()) {
			String      name        = (String) names.next();
			Measurement measurement = metrics.Measurement(name);

			measurement.Accept(this);
		}
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		Indent().Append("<measurement>").EOL();
		RaiseIndent();
		Indent().Append("<short-name>").Append(measurement.ShortName()).Append("</short-name>").EOL();
		Indent().Append("<long-name>").Append(measurement.LongName()).Append("</long-name>").EOL();
		Indent().Append("<minimum>").Append(measurement.Minimum()).Append("</minimum>").EOL();
		Indent().Append("<median>").Append(measurement.Median()).Append("</median>").EOL();
		Indent().Append("<average>").Append(measurement.Average()).Append("</average>").EOL();
		Indent().Append("<maximum>").Append(measurement.Maximum()).Append("</maximum>").EOL();
		Indent().Append("<sum>").Append(measurement.Sum()).Append("</sum>").EOL();
		Indent().Append("<nb-data-points>").Append(measurement.NbDataPoints()).Append("</nb-data-points>").EOL();
		LowerIndent();
		Indent().Append("</measurement>").EOL();
	}
	
	protected void VisitMeasurement(Measurement measurement) {
		Indent().Append("<measurement>").EOL();
		RaiseIndent();
		Indent().Append("<short-name>").Append(measurement.ShortName()).Append("</short-name>").EOL();
		Indent().Append("<long-name>").Append(measurement.LongName()).Append("</long-name>").EOL();
		Indent().Append("<value>").Append(measurement.Value()).Append("</value>").EOL();
		LowerIndent();
		Indent().Append("</measurement>").EOL();
	}
}
