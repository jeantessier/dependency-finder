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

import org.apache.log4j.*;

/**
 *  <p>Divides one measurement (base) by another (divider).  Both
 *  must be in the same context.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      base measurement name [DISPOSE_x]
 *      divider measurement name [DISPOSE_x]
 *  &lt;/init&gt;
 *  </pre>
 *  
 *  <p>If either is missing, this measurement will be NaN.</p>
 */
public class RatioMeasurement extends MeasurementBase {
	private String base_name;
	private int    base_dispose;
	private String divider_name;
	private int    divider_dispose;

	private double value = 0.0;
	
	public RatioMeasurement(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		super(descriptor, context, init_text);

		try {
			BufferedReader in = new BufferedReader(new StringReader(init_text));

			synchronized (Perl()) {
				base_name = in.readLine().trim();
				if (Perl().match("/(.*)\\s+(dispose_\\w+)$/i", base_name)) {
					base_name = Perl().group(1);
					
					String dispose_text = Perl().group(2);
					
					if (dispose_text.equalsIgnoreCase("DISPOSE_IGNORE")) {
						base_dispose = StatisticalMeasurement.DISPOSE_IGNORE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MINIMUM")) {
						base_dispose = StatisticalMeasurement.DISPOSE_MINIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MEDIAN")) {
						base_dispose = StatisticalMeasurement.DISPOSE_MEDIAN;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_AVERAGE")) {
						base_dispose = StatisticalMeasurement.DISPOSE_AVERAGE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_STANDARD_DEVIATION")) {
						base_dispose = StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
						base_dispose = StatisticalMeasurement.DISPOSE_MAXIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_SUM")) {
						base_dispose = StatisticalMeasurement.DISPOSE_SUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
						base_dispose = StatisticalMeasurement.DISPOSE_NB_DATA_POINTS;
					} else {
						base_dispose = StatisticalMeasurement.DISPOSE_IGNORE;
					}
				} else {
					base_dispose = StatisticalMeasurement.DISPOSE_IGNORE;
				}
				
				divider_name = in.readLine().trim();
				if (Perl().match("/(.*)\\s+(dispose_\\w+)$/i", divider_name)) {
					divider_name = Perl().group(1);
					
					String dispose_text = Perl().group(2);
					
					if (dispose_text.equalsIgnoreCase("DISPOSE_IGNORE")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_IGNORE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MINIMUM")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_MINIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MEDIAN")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_MEDIAN;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_AVERAGE")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_AVERAGE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_STANDARD_DEVIATION")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_MAXIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_SUM")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_SUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
						divider_dispose = StatisticalMeasurement.DISPOSE_NB_DATA_POINTS;
					} else {
						divider_dispose = StatisticalMeasurement.DISPOSE_IGNORE;
					}
				} else {
					divider_dispose = StatisticalMeasurement.DISPOSE_IGNORE;
				}
			}

			in.close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).debug("Cannot initialize with \"" + init_text + "\"", ex);
			base_name    = null;
			divider_name = null;
		}
	}
	
	public String BaseName() {
		return base_name;
	}
	
	public int BaseDispose() {
		return base_dispose;
	}

	public String DividerName() {
		return divider_name;
	}

	public int DividerDispose() {
		return divider_dispose;
	}

	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitRatioMeasurement(this);
	}

	public boolean Empty() {
		if (!Cached()) {
			Compute();
		}

		return super.Empty();
	}

	protected double Compute() {
		if (!Cached()) {
			value = Double.NaN;

			if (Context() != null && BaseName() != null && DividerName() != null) {
				Measurement base    = Context().Measurement(BaseName());
				Measurement divider = Context().Measurement(DividerName());
				
				double base_value    = Double.NaN;
				double divider_value = Double.NaN;
				
				if (base instanceof StatisticalMeasurement) {
					StatisticalMeasurement stats = (StatisticalMeasurement) base;
					
					switch (BaseDispose()) {
						case StatisticalMeasurement.DISPOSE_MINIMUM:
							base_value = stats.Minimum();
							break;
						case StatisticalMeasurement.DISPOSE_MEDIAN:
							base_value = stats.Median();
							break;
						case StatisticalMeasurement.DISPOSE_AVERAGE:
							base_value = stats.Average();
							break;
						case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
							base_value = stats.StandardDeviation();
							break;
						case StatisticalMeasurement.DISPOSE_MAXIMUM:
							base_value = stats.Maximum();
							break;
						case StatisticalMeasurement.DISPOSE_SUM:
							base_value = stats.Sum();
							break;
						case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
							base_value = stats.NbDataPoints();
							break;
						case StatisticalMeasurement.DISPOSE_IGNORE:
						default:
							base_value = stats.doubleValue();
							break;
					}
				} else if (base != null) {
					base_value = base.doubleValue();
				}
				
				if (divider instanceof StatisticalMeasurement) {
					StatisticalMeasurement stats = (StatisticalMeasurement) divider;
					
					switch (DividerDispose()) {
						case StatisticalMeasurement.DISPOSE_MINIMUM:
							divider_value = stats.Minimum();
							break;
						case StatisticalMeasurement.DISPOSE_MEDIAN:
							divider_value = stats.Median();
							break;
						case StatisticalMeasurement.DISPOSE_AVERAGE:
							divider_value = stats.Average();
							break;
						case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
							divider_value = stats.StandardDeviation();
							break;
						case StatisticalMeasurement.DISPOSE_MAXIMUM:
							divider_value = stats.Maximum();
							break;
						case StatisticalMeasurement.DISPOSE_SUM:
							divider_value = stats.Sum();
							break;
						case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
							divider_value = stats.NbDataPoints();
							break;
						case StatisticalMeasurement.DISPOSE_IGNORE:
						default:
							divider_value = stats.doubleValue();
							break;
					}
				} else if (divider != null) {
					divider_value = divider.doubleValue();
				}
				
				value = base_value / divider_value;
			}

			Empty(Double.isNaN(value) || Double.isInfinite(value));

			Cached(true);
		}
		
		return value;
	}
}
