/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.metrics;

import java.util.*;

import org.apache.log4j.*;

public class StatisticalMeasurement extends SubMetricsBasedMeasurement {

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

	private String monitored_metric;
	private int    dispose;
	
	private List data = new LinkedList();

	private double minimum        = 0.0;
	private double median         = 0.0;
	private double average        = 0.0;
	private double maximum        = 0.0;
	private double sum            = 0.0;
	private int    nb_data_points = 0;

	private int nb_submetrics = -1;
	
	public StatisticalMeasurement(String name, String monitored_metric, Metrics metrics) {
		this(name, monitored_metric, metrics, DISPOSE_IGNORE);
	}
	
	public StatisticalMeasurement(String name, String monitored_metric, Metrics metrics, int dispose) {
		super(name, metrics);

		this.monitored_metric = monitored_metric;
		this.dispose          = dispose;

		Category.getInstance(getClass().getName()).debug("Created for " + this.monitored_metric + " with dispose of " + this.dispose);
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
		if (SubMetrics().size() != nb_submetrics) {
			data = new LinkedList();

			Iterator i = SubMetrics().iterator();
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

			nb_submetrics = SubMetrics().size();
		}
	}
	
	private void VisitMetrics(Metrics metrics) {
		Category.getInstance(getClass().getName()).debug("VisitMetrics: " + metrics);
		
		Measurement measure = metrics.Metric(monitored_metric);

		Category.getInstance(getClass().getName()).debug("measure for " + monitored_metric + " is " + measure);
		
		if (measure instanceof NumericalMeasurement) {
			Number value = ((NumericalMeasurement) measure).Value();
			
			Category.getInstance(getClass().getName()).debug(monitored_metric + " on " + metrics.Name() + " is " + value);

			if (value != null) {
				data.add(value);
			}
		} else if (measure instanceof StatisticalMeasurement) {
			StatisticalMeasurement stats = (StatisticalMeasurement) measure;
			
			Category.getInstance(getClass().getName()).debug("dispose of StatisticalMeasurements is " + dispose);

			switch (dispose) {
				case DISPOSE_MINIMUM:
					Category.getInstance(getClass().getName()).debug("using Minimum(): " + stats.Minimum());
					data.add(new Double(stats.Minimum()));
					break;
					
				case DISPOSE_MEDIAN:
					Category.getInstance(getClass().getName()).debug("using Median(): " + stats.Median());
					data.add(new Double(stats.Median()));
					break;
					
				case DISPOSE_AVERAGE:
					Category.getInstance(getClass().getName()).debug("using Average(): " + stats.Average());
					data.add(new Double(stats.Average()));
					break;
					
				case DISPOSE_MAXIMUM:
					Category.getInstance(getClass().getName()).debug("using Maximum(): " + stats.Maximum());
					data.add(new Double(stats.Maximum()));
					break;
					
				case DISPOSE_SUM:
					Category.getInstance(getClass().getName()).debug("using Sum(): " + stats.Sum());
					data.add(new Double(stats.Sum()));
					break;
					
				case DISPOSE_NB_DATA_POINTS:
					Category.getInstance(getClass().getName()).debug("using NbDataPoints(): " + stats.NbDataPoints());
					data.add(new Integer(stats.NbDataPoints()));
					break;

				case DISPOSE_IGNORE:
				default:
					Category.getInstance(getClass().getName()).debug("Skipping to next level ...");
					Iterator i = metrics.SubMetrics().iterator();
					while (i.hasNext()) {
						VisitMetrics((Metrics) i.next());
					}
					break;
			}
		} else if (measure == null) {
			Category.getInstance(getClass().getName()).debug("Skipping to next level ...");
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
