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

package com.jeantessier.dependencyfinder.gui;

import java.util.*;

import javax.swing.table.*;

import org.apache.oro.text.perl.*;

import com.jeantessier.metrics.*;

public class OOMetricsTableModel extends AbstractTableModel {
	private static final Perl5Util perl = new Perl5Util();

	private static final Integer LOCAL_DISPOSE_IGNORE         = new Integer(StatisticalMeasurement.DISPOSE_IGNORE);
	private static final Integer LOCAL_DISPOSE_MINIMUM        = new Integer(StatisticalMeasurement.DISPOSE_MINIMUM);
	private static final Integer LOCAL_DISPOSE_MEDIAN         = new Integer(StatisticalMeasurement.DISPOSE_MEDIAN);
	private static final Integer LOCAL_DISPOSE_AVERAGE        = new Integer(StatisticalMeasurement.DISPOSE_AVERAGE);
	private static final Integer LOCAL_DISPOSE_MAXIMUM        = new Integer(StatisticalMeasurement.DISPOSE_MAXIMUM);
	private static final Integer LOCAL_DISPOSE_SUM            = new Integer(StatisticalMeasurement.DISPOSE_SUM);
	private static final Integer LOCAL_DISPOSE_NB_DATA_POINTS = new Integer(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS);
	
	private List              metrics_list;
	
	private String            metric_names[];
	private int               metric_dispose[];
	private Object            metric_values[][];
	private MetricsComparator comparator        = new MetricsComparator("name");

	public OOMetricsTableModel() {
		BuildMetricNames();
		BuildMetricValues();
	}
	
	public void Metrics(Collection metrics_list) {
		this.metrics_list = new ArrayList(metrics_list);
		
		if (metrics_list.isEmpty()) {
			BuildMetricNames();
			BuildMetricValues();
		} else {
			Collections.sort(this.metrics_list, comparator);

			BuildMetricNames((Metrics) this.metrics_list.get(0));
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
		metric_names   = new String[0];
		metric_dispose = new int[0];
	}
	
	private void BuildMetricNames(Metrics metrics) {
		List names  = new LinkedList();
		names.add("name");

		List dispose = new LinkedList();
		dispose.add(LOCAL_DISPOSE_IGNORE);

		Iterator i = metrics.MeasurementNames().iterator();
		while (i.hasNext()) {
			String metric_name = (String) i.next();
			Measurement metric = metrics.Measurement(metric_name);
			
			if (metric instanceof NumericalMeasurement) {
				names.add(metric_name);
				dispose.add(LOCAL_DISPOSE_IGNORE);
			} else if (metric instanceof StatisticalMeasurement) {
				names.add(metric_name);
				dispose.add(LOCAL_DISPOSE_MINIMUM);
				names.add(metric_name);
				dispose.add(LOCAL_DISPOSE_MEDIAN);
				names.add(metric_name);
				dispose.add(LOCAL_DISPOSE_AVERAGE);
				names.add(metric_name);
				dispose.add(LOCAL_DISPOSE_MAXIMUM);
				names.add(metric_name);
				dispose.add(LOCAL_DISPOSE_SUM);
			}
		}
		
		metric_names = (String[]) names.toArray(new String[0]);
		metric_dispose = new int[dispose.size()];
		for (int j=0; j<dispose.size(); j++) {
			metric_dispose[j] = ((Integer) dispose.get(j)).intValue();
		}
	}

	private void BuildMetricValues() {
		metric_values  = new Object[0][];
	}

	private void BuildMetricValues(Collection metrics_list) {
		List values = new ArrayList(metrics_list.size());
		
		Iterator j = metrics_list.iterator();
		while (j.hasNext()) {
			Metrics current_metrics = (Metrics) j.next();
			
			Collection current_values = new ArrayList(metric_names.length);
			values.add(current_values);
			
			current_values.add(current_metrics.Name());
			
			Iterator k = current_metrics.MeasurementNames().iterator();
			while (k.hasNext()) {
				Measurement metric = current_metrics.Measurement((String) k.next());
				
				if (metric instanceof NumericalMeasurement) {
					NumericalMeasurement num = (NumericalMeasurement) metric;
					current_values.add(num.Value());
				} else if (metric instanceof StatisticalMeasurement) {
					StatisticalMeasurement stats = (StatisticalMeasurement) metric;
					current_values.add(new Double(stats.Minimum()));
					current_values.add(new Double(stats.Median()));
					current_values.add(new Double(stats.Average()));
					current_values.add(new Double(stats.Maximum()));
					current_values.add(new Double(stats.Sum()));
				}
			}
		}
		
		metric_values = new Object[values.size()][];
		for (int i=0; i<values.size(); i++) {
			metric_values[i] = ((Collection) values.get(i)).toArray();
		}
	}
	
	public int getColumnCount() {
		return metric_names.length;
	}

	public int getRowCount() {
		return metric_values.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return metric_values[rowIndex][columnIndex];
	}

	public String RawColumnName(int column) {
		return metric_names[column];
	}

	public int RawColumnDispose(int column) {
		return metric_dispose[column];
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
