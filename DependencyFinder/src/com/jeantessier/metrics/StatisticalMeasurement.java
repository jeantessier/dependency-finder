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

public class StatisticalMeasurement extends MeasurementBase {

	/** Ignore StatisticalMeasurements and drill down to the next level */
	public static final int DISPOSE_IGNORE = 0;

	/** Use Minimum() value on StatisticalMeasurements */
	public static final int DISPOSE_MINIMUM = 1;

	/** Use Median() value on StatisticalMeasurements */
	public static final int DISPOSE_MEDIAN = 2;

	/** Use Average() value on StatisticalMeasurements */
	public static final int DISPOSE_AVERAGE = 3;

	/** Use Maximum() value on StatisticalMeasurements */
	public static final int DISPOSE_MAXIMUM = 4;

	/** Use Sum() value on StatisticalMeasurements */
	public static final int DISPOSE_SUM = 5;

	/** Use NbDataPoints() value on StatisticalMeasurements */
	public static final int DISPOSE_NB_DATA_POINTS = 6;

	private String monitored_measurement;
	private int    dispose;
	
	private List data = new LinkedList();

	private double minimum        = 0.0;
	private double median         = 0.0;
	private double average        = 0.0;
	private double maximum        = 0.0;
	private double sum            = 0.0;
	private int    nb_data_points = 0;

	private int nb_submetrics = -1;
	
	public StatisticalMeasurement(MeasurementDescriptor descriptor, Metrics context, String init_text) {
		super(descriptor, context, init_text);

		try {
			BufferedReader in = new BufferedReader(new StringReader(init_text));
			monitored_measurement = in.readLine().trim();

			String dispose_text = in.readLine();
			if (dispose_text != null) {
				dispose_text = dispose_text.trim();

				if (dispose_text.equalsIgnoreCase("DISPOSE_IGNORE")) {
					dispose = DISPOSE_IGNORE;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_MINIMUM")) {
					dispose = DISPOSE_MINIMUM;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_MEDIAN")) {
					dispose = DISPOSE_MEDIAN;
				} else if (dispose_text.equalsIgnoreCase("DISPOSE_AVERAGE")) {
					dispose = DISPOSE_AVERAGE;
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
			
			in.close();
		} catch (Exception ex) {
			Logger.getLogger(getClass()).debug("Cannot initialize with \"" + init_text + "\"", ex);
			monitored_measurement = null;
		}
	}
	
	public double Minimum() {
		Compute();
		return minimum;
	}

	public double Median() {
		Compute();
		return median;
	}

	public double Average() {
		Compute();
		return average;
	}

	public double Maximum() {
		Compute();
		return maximum;
	}

	public double Sum() {
		Compute();
		return sum;
	}

	public int NbDataPoints() {
		Compute();
		return nb_data_points;
	}
	
	private synchronized void Compute() {
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

			nb_submetrics = Context().SubMetrics().size();
		}
	}
	
	private void VisitMetrics(Metrics metrics) {
		Logger.getLogger(getClass()).debug("VisitMetrics: " + metrics);
		
		Measurement measure = metrics.Measurement(monitored_measurement);

		Logger.getLogger(getClass()).debug("measure for " + monitored_measurement + " is " + measure);
		
		if (measure instanceof NumericalMeasurement) {
			Number value = ((NumericalMeasurement) measure).Value();
			
			Logger.getLogger(getClass()).debug(monitored_measurement + " on " + metrics.Name() + " is " + value);

			if (value != null) {
				data.add(value);
			}
		} else if (measure instanceof StatisticalMeasurement) {
			StatisticalMeasurement stats = (StatisticalMeasurement) measure;
			
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
		} else {
			Logger.getLogger(getClass()).debug("Skipping to next level ...");
			Iterator i = metrics.SubMetrics().iterator();
			while (i.hasNext()) {
				VisitMetrics((Metrics) i.next());
			}
		}
	}

	public void Accept(MeasurementVisitor visitor) {
		visitor.VisitStatisticalMeasurement(this);
	}

	public String toString() {
		Compute();
		return data.toString();
	}
}
