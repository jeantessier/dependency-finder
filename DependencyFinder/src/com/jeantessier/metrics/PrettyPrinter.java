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

import java.text.*;
import java.util.*;

public class PrettyPrinter extends Printer {
	private static final NumberFormat value_format = new DecimalFormat("#.##");
	private static final NumberFormat ratio_format = new DecimalFormat("#%");

	private List descriptors;

	private boolean expand_collection_measurements;
	
	private Metrics current_metrics = null;
	
	public PrettyPrinter(List descriptors) {
		this.descriptors = descriptors;
	}

	public boolean ExpandCollectionMeasurements() {
		return expand_collection_measurements;
	}

	public void ExpandCollectionMeasurements(boolean expand_collection_measurements) {
		this.expand_collection_measurements = expand_collection_measurements;
	}
	
	public void VisitMetrics(Metrics metrics) {
		current_metrics = metrics;
		
		Indent().Append(metrics.Name()).EOL();
		RaiseIndent();
			
		Iterator i = descriptors.iterator();
		while (i.hasNext()) {
			MeasurementDescriptor descriptor = (MeasurementDescriptor) i.next();

			if (descriptor.Visible()) {
				metrics.Measurement(descriptor.ShortName()).Accept(this);
			}
		}

		LowerIndent();

		EOL();
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		Indent();
		Append(measurement.LongName()).Append(" (").Append(measurement.ShortName()).Append("):");
		Append(" ").Append(value_format.format(measurement.doubleValue()));
		Append(" [").Append(value_format.format(measurement.Minimum()));
		Append(" ").Append(value_format.format(measurement.Median()));
		Append("/").Append(value_format.format(measurement.Average()));
		Append(" ").Append(value_format.format(measurement.StandardDeviation()));
		Append(" ").Append(value_format.format(measurement.Maximum()));
		Append(" ").Append(value_format.format(measurement.Sum()));
		Append(" (").Append(value_format.format(measurement.NbDataPoints())).Append(")]");
		EOL();
	}
	
	public void VisitRatioMeasurement(RatioMeasurement measurement) {
		if (!measurement.ShortName().endsWith("R")) {
			super.VisitRatioMeasurement(measurement);
		}
	}
	
	public void VisitAccumulatorMeasurement(AccumulatorMeasurement measurement) {
		super.VisitAccumulatorMeasurement(measurement);

		VisitCollectionMeasurement(measurement);
	}
	
	public void VisitNameListMeasurement(NameListMeasurement measurement) {
		super.VisitNameListMeasurement(measurement);

		VisitCollectionMeasurement(measurement);
	}
	
	protected void VisitCollectionMeasurement(CollectionMeasurement measurement) {
		if (ExpandCollectionMeasurements()) {
			RaiseIndent();
			Iterator i = measurement.Values().iterator();
			while (i.hasNext()) {
				Indent().Append(i.next()).EOL();
			}
			LowerIndent();
		}
	}
	
	protected void VisitMeasurement(Measurement measurement) {
		Indent().Append(measurement.LongName()).Append(" (").Append(measurement.ShortName()).Append("): ").Append(value_format.format(measurement.Value()));

		try {
			RatioMeasurement ratio = (RatioMeasurement) current_metrics.Measurement(measurement.ShortName() + "R");
			Append(" (").Append(ratio_format.format(ratio.Value())).Append(")");
		} catch (ClassCastException ex) {
			// Do nothing, no ratio for this measurement
		}
		
		EOL();
	}
}
