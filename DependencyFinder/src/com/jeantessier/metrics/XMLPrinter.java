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

package com.jeantessier.metrics;

import java.io.*;
import java.util.*;

public class XMLPrinter extends Printer {
	public static final String DEFAULT_ENCODING   = "utf-8";
	public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

	private MetricsConfiguration configuration;
	
	public XMLPrinter(PrintWriter out, MetricsConfiguration configuration) {
		this(out, configuration, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
	}
	
	public XMLPrinter(PrintWriter out, MetricsConfiguration configuration, String encoding, String dtd_prefix) {
		super(out);
		
		this.configuration = configuration;

		AppendHeader(encoding, dtd_prefix);
	}

	private void AppendHeader(String encoding, String dtd_prefix) {
		Append("<?xml version=\"1.0\" encoding=\"").Append(encoding).Append("\" ?>").EOL();
		EOL();
		Append("<!DOCTYPE metrics SYSTEM \"").Append(dtd_prefix).Append("/metrics.dtd\">").EOL();
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
		if (ShowEmptyMetrics() || ShowHiddenMeasurements() || !metrics.Empty()) {
			Indent().Append("<project>").EOL();
			RaiseIndent();
			Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();
			
			VisitMeasurements(metrics, configuration.ProjectMeasurements());
			
			Iterator i = metrics.SubMetrics().iterator();
			while (i.hasNext()) {
				VisitGroupMetrics((Metrics) i.next());
			}
			
			LowerIndent();
			Indent().Append("</project>").EOL();
		}
	}

	private void VisitGroupMetrics(Metrics metrics) {
		if (ShowEmptyMetrics() || ShowHiddenMeasurements() || !metrics.Empty()) {
			Indent().Append("<group>").EOL();
			RaiseIndent();
			Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();
			
			VisitMeasurements(metrics, configuration.GroupMeasurements());
			
			Iterator i = metrics.SubMetrics().iterator();
			while (i.hasNext()) {
				VisitClassMetrics((Metrics) i.next());
			}
			
			LowerIndent();
			Indent().Append("</group>").EOL();
		}
	}

	private void VisitClassMetrics(Metrics metrics) {
		if (ShowEmptyMetrics() || ShowHiddenMeasurements() || !metrics.Empty()) {
			Indent().Append("<class>").EOL();
			RaiseIndent();
			Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();
			
			VisitMeasurements(metrics, configuration.ClassMeasurements());
			
			Iterator i = metrics.SubMetrics().iterator();
			while (i.hasNext()) {
				VisitMethodMetrics((Metrics) i.next());
			}
			
			LowerIndent();
			Indent().Append("</class>").EOL();
		}
	}

	private void VisitMethodMetrics(Metrics metrics) {
		if (ShowEmptyMetrics() || ShowHiddenMeasurements() || !metrics.Empty()) {
			Indent().Append("<method>").EOL();
			RaiseIndent();
			Indent().Append("<name>").Append(metrics.Name()).Append("</name>").EOL();
			
			VisitMeasurements(metrics, configuration.MethodMeasurements());
			
			LowerIndent();
			Indent().Append("</method>").EOL();
		}
	}

	private void VisitMeasurements(Metrics metrics, List descriptors) {
		Iterator i = descriptors.iterator();
		while (i.hasNext()) {
			MeasurementDescriptor descriptor = (MeasurementDescriptor) i.next();

			if (ShowHiddenMeasurements() || descriptor.Visible()) {
				metrics.Measurement(descriptor.ShortName()).Accept(this);
			}
		}
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		Indent().Append("<measurement>").EOL();
		RaiseIndent();
		Indent().Append("<short-name>").Append(measurement.ShortName()).Append("</short-name>").EOL();
		Indent().Append("<long-name>").Append(measurement.LongName()).Append("</long-name>").EOL();
		Indent().Append("<value>").Append(measurement.doubleValue()).Append("</value>").EOL();
		Indent().Append("<minimum>").Append(measurement.Minimum()).Append("</minimum>").EOL();
		Indent().Append("<median>").Append(measurement.Median()).Append("</median>").EOL();
		Indent().Append("<average>").Append(measurement.Average()).Append("</average>").EOL();
		Indent().Append("<standard-deviation>").Append(measurement.StandardDeviation()).Append("</standard-deviation>").EOL();
		Indent().Append("<maximum>").Append(measurement.Maximum()).Append("</maximum>").EOL();
		Indent().Append("<sum>").Append(measurement.Sum()).Append("</sum>").EOL();
		Indent().Append("<nb-data-points>").Append(measurement.NbDataPoints()).Append("</nb-data-points>").EOL();
		LowerIndent();
		Indent().Append("</measurement>").EOL();
	}
	
	public void VisitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
		VisitCollectionMeasurement(measurement);
	}
		
	public void VisitNameListMeasurement(NameListMeasurement measurement) {
		VisitCollectionMeasurement(measurement);
	}
	
	public void VisitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
		VisitCollectionMeasurement(measurement);
	}
	
	protected void VisitCollectionMeasurement(CollectionMeasurement measurement) {
		Indent().Append("<measurement>").EOL();
		RaiseIndent();
		Indent().Append("<short-name>").Append(measurement.ShortName()).Append("</short-name>").EOL();
		Indent().Append("<long-name>").Append(measurement.LongName()).Append("</long-name>").EOL();
		Indent().Append("<value>").Append(measurement.Value()).Append("</value>").EOL();
		Indent().Append("<members>").EOL();
		RaiseIndent();
		Iterator i = measurement.Values().iterator();
		while (i.hasNext()) {
			Indent().Append("<member>").Append(i.next()).Append("</member>").EOL();
		}
		LowerIndent();
		Indent().Append("</members>").EOL();
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
