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

package com.jeantessier.dependencyfinder.gui;

import java.util.*;

import javax.swing.table.*;

import org.apache.oro.text.perl.*;

import com.jeantessier.metrics.*;

public class OOMetricsTableModel extends AbstractTableModel {
	private static final Perl5Util perl = new Perl5Util();

	private static final Integer LOCAL_DISPOSE_IGNORE             = new Integer(StatisticalMeasurement.DISPOSE_IGNORE);
	private static final Integer LOCAL_DISPOSE_MINIMUM            = new Integer(StatisticalMeasurement.DISPOSE_MINIMUM);
	private static final Integer LOCAL_DISPOSE_MEDIAN             = new Integer(StatisticalMeasurement.DISPOSE_MEDIAN);
	private static final Integer LOCAL_DISPOSE_AVERAGE            = new Integer(StatisticalMeasurement.DISPOSE_AVERAGE);
	private static final Integer LOCAL_DISPOSE_STANDARD_DEVIATION = new Integer(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION);
	private static final Integer LOCAL_DISPOSE_MAXIMUM            = new Integer(StatisticalMeasurement.DISPOSE_MAXIMUM);
	private static final Integer LOCAL_DISPOSE_SUM                = new Integer(StatisticalMeasurement.DISPOSE_SUM);
	private static final Integer LOCAL_DISPOSE_NB_DATA_POINTS     = new Integer(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS);

	private List              descriptors;
	private List              metrics_list;
	
	private String            measurement_names[];
	private int               measurement_dispose[];
	private Object            measurement_values[][];
	private MetricsComparator comparator             = new MetricsComparator("name");

	public OOMetricsTableModel(List descriptors) {
		this.descriptors = descriptors;
		
		BuildMetricNames();
		BuildMetricValues();
	}
	
	public void Metrics(Collection metrics_list) {
		this.metrics_list = new ArrayList(metrics_list);
		
		if (metrics_list.isEmpty()) {
			BuildMetricValues();
		} else {
			Collections.sort(this.metrics_list, comparator);
			BuildMetricValues(this.metrics_list);
		}
		
		fireTableStructureChanged();
	}

	public void UpdateMetrics(Collection metrics_list) {
		this.metrics_list = new ArrayList(metrics_list);
		
		if (metrics_list.isEmpty()) {
			BuildMetricValues();
		} else {
			Collections.sort(this.metrics_list, comparator);
			BuildMetricValues(this.metrics_list);
		}
		
		fireTableDataChanged();
	}
	
	public void SortOn(String name, int dispose) {
		comparator.SortOn(name, dispose);
		
		Collections.sort(metrics_list, comparator);
		BuildMetricValues(metrics_list);
		
		fireTableDataChanged();
	}

	private void BuildMetricNames() {
		List names  = new LinkedList();
		names.add("name");

		List dispose = new LinkedList();
		dispose.add(LOCAL_DISPOSE_IGNORE);

		Iterator i = descriptors.iterator();
		while (i.hasNext()) {
			MeasurementDescriptor descriptor  = (MeasurementDescriptor) i.next();

			if (descriptor.Visible()) {
				if (descriptor.Class().equals(StatisticalMeasurement.class)) {
					names.add(descriptor.ShortName());
					dispose.add(LOCAL_DISPOSE_MINIMUM);
					names.add(descriptor.ShortName());
					dispose.add(LOCAL_DISPOSE_MEDIAN);
					names.add(descriptor.ShortName());
					dispose.add(LOCAL_DISPOSE_AVERAGE);
					names.add(descriptor.ShortName());
					dispose.add(LOCAL_DISPOSE_STANDARD_DEVIATION);
					names.add(descriptor.ShortName());
					dispose.add(LOCAL_DISPOSE_MAXIMUM);
					names.add(descriptor.ShortName());
					dispose.add(LOCAL_DISPOSE_SUM);
				} else {
					names.add(descriptor.ShortName());
					dispose.add(LOCAL_DISPOSE_IGNORE);
				}
			}
		}
		
		measurement_names = (String[]) names.toArray(new String[0]);
		measurement_dispose = new int[dispose.size()];
		for (int j=0; j<dispose.size(); j++) {
			measurement_dispose[j] = ((Integer) dispose.get(j)).intValue();
		}
	}

	private void BuildMetricValues() {
		measurement_values  = new Object[0][];
	}

	private void BuildMetricValues(Collection metrics_list) {
		List values = new ArrayList(metrics_list.size());
		
		Iterator i = metrics_list.iterator();
		while (i.hasNext()) {
			Metrics current_metrics = (Metrics) i.next();
			
			Collection current_values = new ArrayList(measurement_names.length);
			values.add(current_values);
			
			current_values.add(current_metrics);
			
			Iterator j = descriptors.iterator();
			while (j.hasNext()) {
				MeasurementDescriptor descriptor = (MeasurementDescriptor) j.next();

				if (descriptor.Visible()) {
					Measurement measurement = current_metrics.Measurement(descriptor.ShortName());
					
					if (measurement instanceof StatisticalMeasurement) {
						current_values.add(measurement);
						current_values.add(measurement);
						current_values.add(measurement);
						current_values.add(measurement);
						current_values.add(measurement);
						current_values.add(measurement);
					} else {
						current_values.add(measurement);
					}
				}
			}
		}
		
		measurement_values = new Object[values.size()][];
		for (int j=0; j<values.size(); j++) {
			measurement_values[j] = ((Collection) values.get(j)).toArray();
		}
	}
	
	public int getColumnCount() {
		return measurement_names.length;
	}

	public int getRowCount() {
		return measurement_values.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return measurement_values[rowIndex][columnIndex];
	}

	public String RawColumnName(int column) {
		return measurement_names[column];
	}

	public int RawColumnDispose(int column) {
		return measurement_dispose[column];
	}

	public String getColumnName(int column) {
		String result  = RawColumnName(column);
		int    dispose = RawColumnDispose(column);

		switch (dispose) {
			case StatisticalMeasurement.DISPOSE_MINIMUM:
				result += " (min)";
				break;
			case StatisticalMeasurement.DISPOSE_MEDIAN:
				result += " (med)";
				break;
			case StatisticalMeasurement.DISPOSE_AVERAGE:
				result += " (avg)";
				break;
			case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
				result += " (sdv)";
				break;
			case StatisticalMeasurement.DISPOSE_MAXIMUM:
				result += " (max)";
				break;
			case StatisticalMeasurement.DISPOSE_SUM:
				result += " (sum)";
				break;
			case StatisticalMeasurement.DISPOSE_IGNORE:
			case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
			default:
				// Ignore
				break;
		}

		return result;
	}
}
