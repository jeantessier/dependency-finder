/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

public class CSVPrinter extends Printer {
	private List descriptors;
	
	public CSVPrinter(PrintWriter out, List descriptors) {
		super(out);
		
		this.descriptors = descriptors;

		AppendHeader();
	}

	private void AppendHeader() {
		Append("\"name\", ");
		
		Iterator i = descriptors.iterator();
		while (i.hasNext()) {
			MeasurementDescriptor descriptor = (MeasurementDescriptor) i.next();

			if (descriptor.Visible()) {
				if (descriptor.Class().equals(StatisticalMeasurement.class)) {
					Append("\"").Append(descriptor.ShortName()).Append(" (min)\", ");
					Append("\"").Append(descriptor.ShortName()).Append(" (med)\", ");
					Append("\"").Append(descriptor.ShortName()).Append(" (avg)\", ");
					Append("\"").Append(descriptor.ShortName()).Append(" (sdv)\", ");
					Append("\"").Append(descriptor.ShortName()).Append(" (max)\", ");
					Append("\"").Append(descriptor.ShortName()).Append(" (sum)\", ");
					Append("\"").Append(descriptor.ShortName()).Append(" (nb)\"");
				} else {
					Append("\"").Append(descriptor.ShortName()).Append("\"");
				}
				
				if (i.hasNext()) {
					Append(", ");
				}
			}
		}
		
		EOL();
	}
			
	public void VisitMetrics(Metrics metrics) {
		if (ShowEmptyMetrics() || ShowHiddenMeasurements() || !metrics.Empty()) {
			Append("\"").Append(metrics.Name()).Append("\", ");
			
			Iterator i = descriptors.iterator();
			while (i.hasNext()) {
				MeasurementDescriptor descriptor = (MeasurementDescriptor) i.next();
				
				if (ShowHiddenMeasurements() || descriptor.Visible()) {
					Measurement measurement = metrics.Measurement(descriptor.ShortName());
					
					measurement.Accept(this);
					
					if (i.hasNext()) {
						Append(", ");
					}
				}
			}
			
			EOL();
		}
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		Append(measurement.Minimum()).Append(", ");
		Append(measurement.Median()).Append(", ");
		Append(measurement.Average()).Append(", ");
		Append(measurement.StandardDeviation()).Append(", ");
		Append(measurement.Maximum()).Append(", ");
		Append(measurement.Sum()).Append(", ");
		Append(measurement.NbDataPoints());
	}
	
	protected void VisitMeasurement(Measurement measurement) {
		Append(measurement.Value());
	}
}
