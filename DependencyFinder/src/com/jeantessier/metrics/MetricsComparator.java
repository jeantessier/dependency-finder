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

public class MetricsComparator implements Comparator {
	public static final int DESCENDING = -1;
	public static final int ASCENDING  =  1;
	
	private String name;
	private int    direction;
	private int    dispose;

	public MetricsComparator(String name) {
		this(name, StatisticalMeasurement.DISPOSE_IGNORE);
	}
		
	public MetricsComparator(String name, int dispose) {
		Name(name);
		Dispose(dispose);
		Direction(ASCENDING);
	}

	public String Name() {
		return name;
	}

	public void Name(String name) {
		this.name = name;
	}

	public int Direction() {
		return direction;
	}

	public void Direction(int direction) {
		this.direction = direction;
	}

	public int Dispose() {
		return dispose;
	}

	public void Dispose(int dispose) {
		this.dispose = dispose;
	}

	public void SortOn(String name, int dispose) {
		if (name.equals(this.name) && dispose == this.dispose) {
			Reverse();
		} else {
			Name(name);
			Direction(ASCENDING);
			Dispose(dispose);
		}
	}
	
	public void Reverse() {
		direction *= -1;
	}
	
	public int compare(Object o1, Object o2) {
		int result;

		Metrics metrics1 = (Metrics) o1;
		Metrics metrics2 = (Metrics) o2;

		if ("name".equals(name)) {
			result = metrics1.Name().compareTo(metrics2.Name());
		} else {
			Measurement m1 = metrics1.Metric(name);
			Measurement m2 = metrics2.Metric(name);
			
			if (m1 == null && m2 != null) {
				result = -1;
			} else if (m1 != null && m2 == null) {
				result = 1;
			} else if (m1 == m2) {
				result = 0;
			} else {
				double v1 = ExtractValue(m1);
				double v2 = ExtractValue(m2);
				
				if (v1 < v2) {
					result = -1;
				} else if (v1 > v2) {
					result = 1;
				} else {
					result = 0;
				}
			}
		}
		
		result *= Direction();

		return result;
	}

	private double ExtractValue(Measurement m) {
		double result = Double.NaN;

		if (m instanceof NumericalMeasurement) {
			result = ((NumericalMeasurement) m).Value().doubleValue();
		} else if (m instanceof StatisticalMeasurement) {
			StatisticalMeasurement sm = (StatisticalMeasurement) m;
			switch (Dispose()) {
				case StatisticalMeasurement.DISPOSE_MINIMUM:
					result = sm.Minimum();
					break;
					
				case StatisticalMeasurement.DISPOSE_MEDIAN:
					result = sm.Median();
					break;
					
				case StatisticalMeasurement.DISPOSE_AVERAGE:
					result = sm.Average();
					break;
					
				case StatisticalMeasurement.DISPOSE_MAXIMUM:
					result = sm.Maximum();
					break;
					
				case StatisticalMeasurement.DISPOSE_SUM:
					result = sm.Sum();
					break;
					
				case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
					result = sm.NbDataPoints();
					break;

				default:
				case StatisticalMeasurement.DISPOSE_IGNORE:
					break;
			}
		}

		return result;
	}
}
