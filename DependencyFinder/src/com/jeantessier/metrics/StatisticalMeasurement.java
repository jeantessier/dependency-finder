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
import java.text.*;
import java.util.*;

import org.apache.log4j.*;

/**
 *  <p>Computes the statistical properties of a given measurement
 *  across the submetrics of the measurement's context.  Given a
 *  measurement name, it explores the tree of metrics rooted at the
 *  context and finds the numerical value of these named measurements
 *  in the tree.  For these measurements, it computes:</p>
 *  
 *  <ul>
 *      <li>minimum value</li>
 *      <li>median value</li>
 *      <li>average value</li>
 *      <li>standard deviation</li>
 *      <li>maximum value</li>
 *      <li>sum</li>
 *      <li>number of data points</li>
 *  </ul>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      monitored measurement name [DISPOSE_x]
 *      [DISPOSE_x]
 *  &lt;/init&gt;
 *  </pre>
 */
public class StatisticalMeasurement extends MeasurementBase {
	private static final NumberFormat value_format = new DecimalFormat("#.##");

	/** Ignore StatisticalMeasurements and drill down to the next level */
	public static final int DISPOSE_IGNORE = 0;

	/** Use Minimum() value on StatisticalMeasurements */
	public static final int DISPOSE_MINIMUM = 1;

	/** Use Median() value on StatisticalMeasurements */
	public static final int DISPOSE_MEDIAN = 2;

	/** Use Average() value on StatisticalMeasurements */
	public static final int DISPOSE_AVERAGE = 3;

	/** Use StandardDeviation() value on StatisticalMeasurements */
	public static final int DISPOSE_STANDARD_DEVIATION = 4;

	/** Use Maximum() value on StatisticalMeasurements */
	public static final int DISPOSE_MAXIMUM = 5;

	/** Use Sum() value on StatisticalMeasurements */
	public static final int DISPOSE_SUM = 6;

	/** Use NbDataPoints() value on StatisticalMeasurements */
	public static final int DISPOSE_NB_DATA_POINTS = 7;

	public static String DisposeLabel(int dispose) {
		String result = "";

		switch (dispose) {
			case DISPOSE_MINIMUM:
				result = "minimum";
				break;
		
			case DISPOSE_MEDIAN:
				result = "median";
				break;
		
			case DISPOSE_AVERAGE:
				result = "average";
				break;
		
			case DISPOSE_STANDARD_DEVIATION:
				result = "standard deviation";
				break;
		
			case DISPOSE_MAXIMUM:
				result = "maximum";
				break;
		
			case DISPOSE_SUM:
				result = "sum";
				break;
		
			case DISPOSE_NB_DATA_POINTS:
				result = "number of data points";
				break;
		
			case DISPOSE_IGNORE:
			default:
				break;
		}
		
		return result;
	}

	public static String DisposeAbbreviation(int dispose) {
		String result = "";

		switch (dispose) {
			case DISPOSE_MINIMUM:
				result = "min";
				break;
		
			case DISPOSE_MEDIAN:
				result = "med";
				break;
		
			case DISPOSE_AVERAGE:
				result = "avg";
				break;
		
			case DISPOSE_STANDARD_DEVIATION:
				result = "sdv";
				break;
		
			case DISPOSE_MAXIMUM:
				result = "max";
				break;
		
			case DISPOSE_SUM:
				result = "sum";
				break;
		
			case DISPOSE_NB_DATA_POINTS:
				result = "nb";
				break;
		
			case DISPOSE_IGNORE:
			default:
				break;
		}
		
		return result;
	}

	private String monitored_measurement;
	private int    dispose;
	private int    self_dispose;
	
	private List data = new LinkedList();

	private double minimum            = 0.0;
	private double median             = 0.0;
	private double average            = 0.0;
	private double standard_deviation = 0.0;
	private double maximum            = 0.0;
	private double sum                = 0.0;
	private int    nb_data_points     = 0;

	private int nb_submetrics = -1;

	public StatisticalMeasurement(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		super(descriptor, context, init_text);

		try {
			BufferedReader in = new BufferedReader(new StringReader(init_text));
			monitored_measurement = in.readLine().trim();

			synchronized (Perl()) {
				if (Perl().match("/(.*)\\s+(dispose_\\w+)$/i", monitored_measurement)) {
					monitored_measurement = Perl().group(1);
					
					String dispose_text = Perl().group(2);
					
					if (dispose_text.equalsIgnoreCase("DISPOSE_IGNORE")) {
						dispose = DISPOSE_IGNORE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MINIMUM")) {
						dispose = DISPOSE_MINIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MEDIAN")) {
						dispose = DISPOSE_MEDIAN;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_AVERAGE")) {
						dispose = DISPOSE_AVERAGE;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_STANDARD_DEVIATION")) {
						dispose = DISPOSE_STANDARD_DEVIATION;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
						dispose = DISPOSE_MAXIMUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_SUM")) {
						dispose = DISPOSE_SUM;
					} else if (dispose_text.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
						dispose = DISPOSE_NB_DATA_POINTS;
					} else {
						dispose = DISPOSE_IGNORE;
					}
				} else {
					dispose = DISPOSE_IGNORE;
				}
			}

			String self_dispose_text = in.readLine();
			if (self_dispose_text != null) {
				self_dispose_text = self_dispose_text.trim();

				if (self_dispose_text.equalsIgnoreCase("DISPOSE_IGNORE")) {
					self_dispose = DISPOSE_IGNORE;
				} else if (self_dispose_text.equalsIgnoreCase("DISPOSE_MINIMUM")) {
					self_dispose = DISPOSE_MINIMUM;
				} else if (self_dispose_text.equalsIgnoreCase("DISPOSE_MEDIAN")) {
					self_dispose = DISPOSE_MEDIAN;
				} else if (self_dispose_text.equalsIgnoreCase("DISPOSE_AVERAGE")) {
					self_dispose = DISPOSE_AVERAGE;
				} else if (self_dispose_text.equalsIgnoreCase("DISPOSE_STANDARD_DEVIATION")) {
					self_dispose = DISPOSE_STANDARD_DEVIATION;
				} else if (self_dispose_text.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
					self_dispose = DISPOSE_MAXIMUM;
				} else if (self_dispose_text.equalsIgnoreCase("DISPOSE_SUM")) {
					self_dispose = DISPOSE_SUM;
				} else if (self_dispose_text.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
					self_dispose = DISPOSE_NB_DATA_POINTS;
				} else {
					self_dispose = DISPOSE_AVERAGE;
				}
			} else {
				self_dispose = DISPOSE_AVERAGE;
			}
			
			in.close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).debug("Cannot initialize with \"" + init_text + "\"", ex);
			monitored_measurement = null;
		}
	}
	
	public double Minimum() {
		CollectData();
		return minimum;
	}

	public double Median() {
		CollectData();
		return median;
	}

	public double Average() {
		CollectData();
		return average;
	}

	/**
	 *  Real standard deviation of the data set.
	 *  This is NOT the estimator "s".
	 */
	public double StandardDeviation() {
		CollectData();
		return standard_deviation;
	}

	public double Maximum() {
		CollectData();
		return maximum;
	}

	public double Sum() {
		CollectData();
		return sum;
	}

	public int NbDataPoints() {
		CollectData();
		return nb_data_points;
	}
	
	private synchronized void CollectData() {
		if (Context().SubMetrics().size() != nb_submetrics) {
			synchronized (this) {
				if (Context().SubMetrics().size() != nb_submetrics) {
					data = new LinkedList();
					
					Iterator i = Context().SubMetrics().iterator();
					while (i.hasNext()) {
						VisitMetrics((Metrics) i.next());
					}
					
					if (!data.isEmpty()) {
						Collections.sort(data);
						
						minimum        = ((Number) data.get(0)).doubleValue();
						median         = ((Number) data.get(data.size() / 2)).doubleValue();
						maximum        = ((Number) data.get(data.size() - 1)).doubleValue();
						nb_data_points = data.size();
						
						sum = 0.0;
						Iterator j = data.iterator();
						while (j.hasNext()) {
							sum += ((Number) j.next()).doubleValue();
						}
					} else {
						minimum        = Double.NaN;
						median         = Double.NaN;
						maximum        = Double.NaN;
						nb_data_points = 0;
						sum            = 0.0;
					}
					
					average = sum / nb_data_points;
					
					if (!data.isEmpty()) {
						double temp = 0.0;
						
						Iterator j = data.iterator();
						while (j.hasNext()) {
							temp += Math.pow(((Number) j.next()).doubleValue() - average, 2);
						}
						
						standard_deviation = Math.sqrt(temp / nb_data_points);
					} else {
						standard_deviation = Double.NaN;
					}
					
					nb_submetrics = Context().SubMetrics().size();
				}
			}
		}
	}
	
	private void VisitMetrics(Metrics metrics) {
		Logger.getLogger(getClass()).debug("VisitMetrics: " + metrics.Name());
		
		Measurement measurement = metrics.Measurement(monitored_measurement);

		Logger.getLogger(getClass()).debug("measurement for " + monitored_measurement + " is " + measurement.getClass());
		
		if (measurement instanceof StatisticalMeasurement) {
			StatisticalMeasurement stats = (StatisticalMeasurement) measurement;
			
			Logger.getLogger(getClass()).debug("dispose of StatisticalMeasurements is " + dispose);

			switch (dispose) {
				case DISPOSE_MINIMUM:
					Logger.getLogger(getClass()).debug("using Minimum(): " + stats.Minimum());
					data.add(new Double(stats.Minimum()));
					break;
					
				case DISPOSE_MEDIAN:
					Logger.getLogger(getClass()).debug("using Median(): " + stats.Median());
					data.add(new Double(stats.Median()));
					break;
					
				case DISPOSE_AVERAGE:
					Logger.getLogger(getClass()).debug("using Average(): " + stats.Average());
					data.add(new Double(stats.Average()));
					break;
							
				case DISPOSE_STANDARD_DEVIATION:
					Logger.getLogger(getClass()).debug("using StandardDeviation(): " + stats.StandardDeviation());
					data.add(new Double(stats.StandardDeviation()));
					break;
			
				case DISPOSE_MAXIMUM:
					Logger.getLogger(getClass()).debug("using Maximum(): " + stats.Maximum());
					data.add(new Double(stats.Maximum()));
					break;
					
				case DISPOSE_SUM:
					Logger.getLogger(getClass()).debug("using Sum(): " + stats.Sum());
					data.add(new Double(stats.Sum()));
					break;
					
				case DISPOSE_NB_DATA_POINTS:
					Logger.getLogger(getClass()).debug("using NbDataPoints(): " + stats.NbDataPoints());
					data.add(new Integer(stats.NbDataPoints()));
					break;

				case DISPOSE_IGNORE:
				default:
					Logger.getLogger(getClass()).debug("Skipping to next level ...");
					Iterator i = metrics.SubMetrics().iterator();
					while (i.hasNext()) {
						VisitMetrics((Metrics) i.next());
					}
					break;
			}
		} else if (measurement instanceof NullMeasurement) {
			Logger.getLogger(getClass()).debug("Skipping to next level ...");
			Iterator i = metrics.SubMetrics().iterator();
			while (i.hasNext()) {
				VisitMetrics((Metrics) i.next());
			}
		} else {
			Number value = measurement.Value();
			
			Logger.getLogger(getClass()).debug(monitored_measurement + " on " + metrics.Name() + " is " + value);

			if (value != null) {
				data.add(value);
			}
		}
	}

	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitStatisticalMeasurement(this);
	}

	protected double Compute() {
		double result = Double.NaN;
		
		switch (self_dispose) {
			case DISPOSE_MINIMUM:
				result = Minimum();
				break;
				
			case DISPOSE_MEDIAN:
				result = Median();
				break;
				
			case DISPOSE_AVERAGE:
				result = Average();
				break;
				
			case DISPOSE_STANDARD_DEVIATION:
				result = StandardDeviation();
				break;
				
			case DISPOSE_MAXIMUM:
				result = Maximum();
				break;
				
			case DISPOSE_SUM:
				result = Sum();
				break;
				
			case DISPOSE_NB_DATA_POINTS:
				result = NbDataPoints();
				break;

			case DISPOSE_IGNORE:
			default:
				break;
		}

		return result;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		result.append("[").append(value_format.format(Minimum()));
		result.append(" ").append(value_format.format(Median()));
		result.append("/").append(value_format.format(Average()));
		result.append(" ").append(value_format.format(StandardDeviation()));
		result.append(" ").append(value_format.format(Maximum()));
		result.append(" ").append(value_format.format(Sum()));
		result.append(" (").append(value_format.format(NbDataPoints())).append(")]");
		
		return result.toString();
	}
}
