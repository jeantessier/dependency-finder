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

package com.jeantessier.dependencyfinder.gui;

import java.util.*;

import javax.swing.table.*;

import com.jeantessier.metrics.*;

public class OOMetricsTableModel extends AbstractTableModel {
	private static final Integer LOCAL_DISPOSE_IGNORE             = new Integer(StatisticalMeasurement.DISPOSE_IGNORE);
	private static final Integer LOCAL_DISPOSE_MINIMUM            = new Integer(StatisticalMeasurement.DISPOSE_MINIMUM);
	private static final Integer LOCAL_DISPOSE_MEDIAN             = new Integer(StatisticalMeasurement.DISPOSE_MEDIAN);
	private static final Integer LOCAL_DISPOSE_AVERAGE            = new Integer(StatisticalMeasurement.DISPOSE_AVERAGE);
	private static final Integer LOCAL_DISPOSE_STANDARD_DEVIATION = new Integer(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION);
	private static final Integer LOCAL_DISPOSE_MAXIMUM            = new Integer(StatisticalMeasurement.DISPOSE_MAXIMUM);
	private static final Integer LOCAL_DISPOSE_SUM                = new Integer(StatisticalMeasurement.DISPOSE_SUM);
	private static final Integer LOCAL_DISPOSE_NB_DATA_POINTS     = new Integer(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS);

	private List   descriptors;
	private List   metricsList;
	
	private String[]                measurementNames;
	private MeasurementDescriptor[] measurementDescriptors;
	private int[]                   measurementDispose;
	private Object[][]              measurementValues;

	private MetricsComparator comparator = new MetricsComparator("name");

	public OOMetricsTableModel(List descriptors) {
		this.descriptors = descriptors;
		
		buildMetricNames();
		buildMetricValues();
	}
	
	public void setMetrics(Collection metricsList) {
		this.metricsList = new ArrayList(metricsList);
		
		if (metricsList.isEmpty()) {
			buildMetricValues();
		} else {
			Collections.sort(this.metricsList, comparator);
			buildMetricValues(this.metricsList);
		}
		
		fireTableStructureChanged();
	}

	public MeasurementDescriptor getColumnDescriptor(int column) {
		return measurementDescriptors[column];
	}
	
	public void updateMetrics(Collection metricsList) {
		this.metricsList = new ArrayList(metricsList);
		
		if (metricsList.isEmpty()) {
			buildMetricValues();
		} else {
			Collections.sort(this.metricsList, comparator);
			buildMetricValues(this.metricsList);
		}
		
		fireTableDataChanged();
	}
	
	public void sortOn(String name, int dispose) {
		comparator.sortOn(name, dispose);
		
		Collections.sort(metricsList, comparator);
		buildMetricValues(metricsList);
		
		fireTableDataChanged();
	}

	private void buildMetricNames() {
		List names = new LinkedList();
		names.add("name");

		List columnDescriptors = new LinkedList();
		columnDescriptors.add(null);

		List dispose = new LinkedList();
		dispose.add(LOCAL_DISPOSE_IGNORE);

		Iterator i = descriptors.iterator();
		while (i.hasNext()) {
			MeasurementDescriptor descriptor = (MeasurementDescriptor) i.next();

			if (descriptor.isVisible()) {
				if (descriptor.getClassFor().equals(StatisticalMeasurement.class)) {
					names.add(descriptor.getShortName());
					columnDescriptors.add(descriptor);
					dispose.add(LOCAL_DISPOSE_MINIMUM);
					names.add(descriptor.getShortName());
					columnDescriptors.add(descriptor);
					dispose.add(LOCAL_DISPOSE_MEDIAN);
					names.add(descriptor.getShortName());
					columnDescriptors.add(descriptor);
					dispose.add(LOCAL_DISPOSE_AVERAGE);
					names.add(descriptor.getShortName());
					columnDescriptors.add(descriptor);
					dispose.add(LOCAL_DISPOSE_STANDARD_DEVIATION);
					names.add(descriptor.getShortName());
					columnDescriptors.add(descriptor);
					dispose.add(LOCAL_DISPOSE_MAXIMUM);
					names.add(descriptor.getShortName());
					columnDescriptors.add(descriptor);
					dispose.add(LOCAL_DISPOSE_SUM);
				} else {
					names.add(descriptor.getShortName());
					columnDescriptors.add(descriptor);
					dispose.add(LOCAL_DISPOSE_IGNORE);
				}
			}
		}
		
		measurementNames = (String[]) names.toArray(new String[0]);
		measurementDescriptors  = (MeasurementDescriptor[]) columnDescriptors.toArray(new MeasurementDescriptor[0]);
		measurementDispose = new int[dispose.size()];
		for (int j=0; j<dispose.size(); j++) {
			measurementDispose[j] = ((Integer) dispose.get(j)).intValue();
		}
	}

	private void buildMetricValues() {
		measurementValues  = new Object[0][];
	}

	private void buildMetricValues(Collection metricsList) {
		List values = new ArrayList(metricsList.size());
		
		Iterator i = metricsList.iterator();
		while (i.hasNext()) {
			Metrics currentMetrics = (Metrics) i.next();
			
			Collection currentValues = new ArrayList(measurementNames.length);
			values.add(currentValues);
			
			currentValues.add(currentMetrics);
			
			Iterator j = descriptors.iterator();
			while (j.hasNext()) {
				MeasurementDescriptor descriptor = (MeasurementDescriptor) j.next();

				if (descriptor.isVisible()) {
					Measurement measurement = currentMetrics.getMeasurement(descriptor.getShortName());
					
					if (measurement instanceof StatisticalMeasurement) {
						currentValues.add(measurement);
						currentValues.add(measurement);
						currentValues.add(measurement);
						currentValues.add(measurement);
						currentValues.add(measurement);
						currentValues.add(measurement);
					} else {
						currentValues.add(measurement);
					}
				}
			}
		}
		
		measurementValues = new Object[values.size()][];
		for (int j=0; j<values.size(); j++) {
			measurementValues[j] = ((Collection) values.get(j)).toArray();
		}
	}
	
	public int getColumnCount() {
		return measurementNames.length;
	}

	public int getRowCount() {
		return measurementValues.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return measurementValues[rowIndex][columnIndex];
	}

	public String getRawColumnName(int column) {
		return measurementNames[column];
	}

	public int getRawColumnDispose(int column) {
		return measurementDispose[column];
	}

	public String getColumnName(int column) {
		String result  = getRawColumnName(column);

		switch (getRawColumnDispose(column)) {
			case StatisticalMeasurement.DISPOSE_MINIMUM:
			case StatisticalMeasurement.DISPOSE_MEDIAN:
			case StatisticalMeasurement.DISPOSE_AVERAGE:
			case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
			case StatisticalMeasurement.DISPOSE_MAXIMUM:
			case StatisticalMeasurement.DISPOSE_SUM:
				result += " (" + StatisticalMeasurement.getDisposeAbbreviation(getRawColumnDispose(column)) + ")";
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
