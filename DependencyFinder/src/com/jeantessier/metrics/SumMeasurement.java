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

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

/**
 *  <pre>
 *  &lt;init-text&gt;
 *      base measurement name [DISPOSE_x]
 *      divider measurement name [DISPOSE_x]
 *  &lt;/init-text&gt;
 *  </pre>
 *  
 *  <p>If either is missing, this measurement will be NaN.</p>
 */
public class SumMeasurement extends MeasurementBase {
	private List terms = new LinkedList();

	public SumMeasurement(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		super(descriptor, context, init_text);

		try {
			BufferedReader in   = new BufferedReader(new StringReader(init_text));
			String         line;

			while ((line = in.readLine()) != null) {
				terms.add(line.trim());
			}

			in.close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).debug("Cannot initialize with \"" + init_text + "\"", ex);
			terms.clear();
		}
	}

	public List Terms() {
		return terms;
	}

	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitSumMeasurement(this);
	}

	protected double Compute() {
		double result = 0;

		Iterator i = Terms().iterator();
		while (i.hasNext()) {
			String term = (String) i.next();

			double term_value = Double.NaN;

			try {
				term_value = Double.parseDouble(term);
			} catch (NumberFormatException ex) {
				if (term.startsWith("-")) {
					term_value = -1 * EvaluateMeasurement(term.substring(1));
				} else {
					term_value = EvaluateMeasurement(term);
				}
			}

			result += term_value;
		}

		return result;
	}

	private double EvaluateMeasurement(String name) {
		double result = Double.NaN;

		int dispose;
		
		synchronized (Perl()) {
			if (Perl().match("/(.*)\\s+(dispose_\\w+)$/i", name)) {
				name = Perl().group(2);
				
				String dispose_text = Perl().group(3);
				
				if (dispose_text.equalsIgnoreCase("DISPOSE_IGNORE")) {
					dispose = StatisticalMeasurement.DISPOSE_IGNORE;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_MINIMUM")) {
					dispose = StatisticalMeasurement.DISPOSE_MINIMUM;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_MEDIAN")) {
					dispose = StatisticalMeasurement.DISPOSE_MEDIAN;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_AVERAGE")) {
					dispose = StatisticalMeasurement.DISPOSE_AVERAGE;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
					dispose = StatisticalMeasurement.DISPOSE_MAXIMUM;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_SUM")) {
					dispose = StatisticalMeasurement.DISPOSE_SUM;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
					dispose = StatisticalMeasurement.DISPOSE_NB_DATA_POINTS;
				} else {
					dispose = StatisticalMeasurement.DISPOSE_IGNORE;
				}
			} else {
				dispose = StatisticalMeasurement.DISPOSE_IGNORE;
			}
		}

		Measurement measurement = Context().Measurement(name);
		
		if (measurement instanceof StatisticalMeasurement) {
			StatisticalMeasurement stats = (StatisticalMeasurement) measurement;

			switch (dispose) {
				case StatisticalMeasurement.DISPOSE_MINIMUM:
					result = stats.Minimum();
					break;
				case StatisticalMeasurement.DISPOSE_MEDIAN:
					result = stats.Median();
					break;
				case StatisticalMeasurement.DISPOSE_AVERAGE:
					result = stats.Average();
					break;
				case StatisticalMeasurement.DISPOSE_MAXIMUM:
					result = stats.Maximum();
					break;
				case StatisticalMeasurement.DISPOSE_SUM:
					result = stats.Sum();
					break;
				case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
					result = stats.NbDataPoints();
					break;
				case StatisticalMeasurement.DISPOSE_IGNORE:
				default:
					result = stats.doubleValue();
					break;
			}
		} else {
			result = measurement.doubleValue();
		}
				
		return result;
	}
}
