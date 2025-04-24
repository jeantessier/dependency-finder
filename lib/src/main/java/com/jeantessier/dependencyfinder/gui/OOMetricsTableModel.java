/*
 *  Copyright (c) 2001-2025, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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

import com.jeantessier.metrics.Measurement;
import com.jeantessier.metrics.MeasurementDescriptor;
import com.jeantessier.metrics.Metrics;
import com.jeantessier.metrics.MetricsComparator;
import com.jeantessier.metrics.StatisticalMeasurement;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OOMetricsTableModel extends AbstractTableModel {
    private static final Integer LOCAL_DISPOSE_IGNORE = StatisticalMeasurement.DISPOSE_IGNORE;
    private static final Integer LOCAL_DISPOSE_MINIMUM = StatisticalMeasurement.DISPOSE_MINIMUM;
    private static final Integer LOCAL_DISPOSE_MEDIAN = StatisticalMeasurement.DISPOSE_MEDIAN;
    private static final Integer LOCAL_DISPOSE_AVERAGE = StatisticalMeasurement.DISPOSE_AVERAGE;
    private static final Integer LOCAL_DISPOSE_STANDARD_DEVIATION = StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION;
    private static final Integer LOCAL_DISPOSE_MAXIMUM = StatisticalMeasurement.DISPOSE_MAXIMUM;
    private static final Integer LOCAL_DISPOSE_SUM = StatisticalMeasurement.DISPOSE_SUM;
//    private static final Integer LOCAL_DISPOSE_NB_DATA_POINTS = StatisticalMeasurement.DISPOSE_NB_DATA_POINTS;

    private final List<MeasurementDescriptor> descriptors;
    private List<Metrics> metricsList;

    private String[] measurementNames;
    private MeasurementDescriptor[] measurementDescriptors;
    private int[] measurementDispose;
    private Object[][] measurementValues;

    private final MetricsComparator comparator = new MetricsComparator("name");

    public OOMetricsTableModel(List<MeasurementDescriptor> descriptors) {
        this.descriptors = descriptors;

        buildMetricNames();
        buildMetricValues();
    }

    public void setMetrics(Collection<Metrics> metricsList) {
        this.metricsList = new ArrayList<>(metricsList);

        if (metricsList.isEmpty()) {
            buildMetricValues();
        } else {
            this.metricsList.sort(comparator);
            buildMetricValues(this.metricsList);
        }

        fireTableStructureChanged();
    }

    public MeasurementDescriptor getColumnDescriptor(int column) {
        return measurementDescriptors[column];
    }

    public void updateMetrics(Collection<Metrics> metricsList) {
        this.metricsList = new ArrayList<>(metricsList);

        if (metricsList.isEmpty()) {
            buildMetricValues();
        } else {
            this.metricsList.sort(comparator);
            buildMetricValues(this.metricsList);
        }

        fireTableDataChanged();
    }

    public void sortOn(String name, int dispose) {
        comparator.sortOn(name, dispose);

        metricsList.sort(comparator);
        buildMetricValues(metricsList);

        fireTableDataChanged();
    }

    private void buildMetricNames() {
        List<String> names = new LinkedList<>();
        names.add("name");

        List<MeasurementDescriptor> columnDescriptors = new LinkedList<>();
        columnDescriptors.add(null);

        List<Integer> dispose = new LinkedList<>();
        dispose.add(LOCAL_DISPOSE_IGNORE);

        descriptors.stream()
                .filter(MeasurementDescriptor::isVisible)
                .forEach(descriptor -> {
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
                });

        measurementNames = names.toArray(new String[0]);
        measurementDescriptors = columnDescriptors.toArray(new MeasurementDescriptor[0]);
        measurementDispose = new int[dispose.size()];
        IntStream.range(0, dispose.size()).forEach(i -> measurementDispose[i] = dispose.get(i));
    }

    private void buildMetricValues() {
        measurementValues  = new Object[0][];
    }

    private void buildMetricValues(Collection<Metrics> metricsList) {
        measurementValues = new Object[metricsList.size()][];

        int i = 0;
        for (Metrics currentMetrics : metricsList) {
            var measurements = descriptors.stream()
                    .filter(MeasurementDescriptor::isVisible)
                    .map(descriptor -> currentMetrics.getMeasurement(descriptor.getShortName()))
                    .flatMap(measurement -> measurement instanceof StatisticalMeasurement ? Stream.of(measurement, measurement, measurement, measurement, measurement, measurement) : Stream.of(measurement))
                    .toList();

            measurementValues[i] = new Object[measurements.size() + 1];

            int j = 0;
            measurementValues[i][j++] = currentMetrics.getName();
            for (Measurement measurement : measurements) {
                measurementValues[i][j++] = measurement;
            }

            i++;
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
        return switch (getRawColumnDispose(column)) {
            case StatisticalMeasurement.DISPOSE_MINIMUM:
            case StatisticalMeasurement.DISPOSE_MEDIAN:
            case StatisticalMeasurement.DISPOSE_AVERAGE:
            case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
            case StatisticalMeasurement.DISPOSE_MAXIMUM:
            case StatisticalMeasurement.DISPOSE_SUM:
                yield getRawColumnName(column) + " (" + StatisticalMeasurement.getDisposeAbbreviation(getRawColumnDispose(column)) + ")";
            case StatisticalMeasurement.DISPOSE_IGNORE:
            case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
            default:
                yield getRawColumnName(column);
        };
    }
}
