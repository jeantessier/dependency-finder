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
			Measurement m1 = metrics1.Measurement(name);
			Measurement m2 = metrics2.Measurement(name);
			
			if (m1 == null && m2 != null) {
				result = -1;
			} else if (m1 != null && m2 == null) {
				result = 1;
			} else if (m1 == m2) {
				result = 0;
			} else {
				double v1 = ExtractValue(m1);
				double v2 = ExtractValue(m2);
				
				if (Double.isNaN(v1) && !Double.isNaN(v2)) {
					result = 1 * Direction();
				} else if (!Double.isNaN(v1) && Double.isNaN(v2)) {
					result = -1 * Direction();
				} else if (Double.isNaN(v1) && Double.isNaN(v2)) {
					result = 0;
				} else if (v1 < v2) {
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

		if (m instanceof StatisticalMeasurement) {
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
		} else {
			result = m.doubleValue();
		}
		
		return result;
	}
}
