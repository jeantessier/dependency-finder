/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

package com.jeantessier.metrics;

import org.apache.logging.log4j.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 *  <p>Computes the statistical properties of a given measurement across the
 *  submetrics of the measurement's context.  Given a measurement name, it
 *  explores the tree of metrics rooted at the context and finds the numerical
 *  value of these named measurements in the tree.  For these measurements, it
 *  computes:</p>
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
 *  <p>This is the syntax for initializing this type of measurement:</p>
 *
 *  <pre>
 *  &lt;init&gt;
 *      monitored measurement name [DISPOSE_x]
 *      [DISPOSE_x]
 *  &lt;/init&gt;
 *  </pre>
 *
 *  <p>If the monitored measurement is itself a statistical measurement, the
 *  disposition indicates how to deal with it, which of its values to use in
 *  this measurement's calculation.  The default is {@link #DISPOSE_IGNORE},
 *  meaning it should skip statistical measurements look in further submetrics
 *  for raw values.</p>
 *
 *  <p>The second disposition tells which internal value to return in calls to
 *  its {@link #compute} method, which will be used by clients that do not
 *  distinguish between StatisticalMeasurement and other Measurements.  The
 *  default is {@link #DISPOSE_AVERAGE}.</p>
 */
public class StatisticalMeasurement extends MeasurementBase {
    private static final NumberFormat valueFormat = new DecimalFormat("#.##");

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

    public static String getDisposeLabel(int dispose) {
        return switch (dispose) {
            case DISPOSE_MINIMUM -> "minimum";
            case DISPOSE_MEDIAN -> "median";
            case DISPOSE_AVERAGE -> "average";
            case DISPOSE_STANDARD_DEVIATION -> "standard deviation";
            case DISPOSE_MAXIMUM -> "maximum";
            case DISPOSE_SUM -> "sum";
            case DISPOSE_NB_DATA_POINTS -> "number of data points";
            default -> "";
        };
    }

    public static String getDisposeAbbreviation(int dispose) {
        return switch (dispose) {
            case DISPOSE_MINIMUM -> "min";
            case DISPOSE_MEDIAN -> "med";
            case DISPOSE_AVERAGE -> "avg";
            case DISPOSE_STANDARD_DEVIATION -> "sdv";
            case DISPOSE_MAXIMUM -> "max";
            case DISPOSE_SUM -> "sum";
            case DISPOSE_NB_DATA_POINTS -> "nb";
            default -> "";
        };
    }

    private String monitoredMeasurement;
    private int    dispose;
    private int    selfDispose;

    private List<Double> data = new LinkedList<>();

    private double minimum           = 0.0;
    private double median            = 0.0;
    private double average           = 0.0;
    private double standardDeviation = 0.0;
    private double maximum           = 0.0;
    private double sum               = 0.0;
    private int    nbDataPoints      = 0;

    private int nbSubmetrics = -1;

    public StatisticalMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try {
            BufferedReader in = new BufferedReader(new StringReader(initText));
            monitoredMeasurement = in.readLine().trim();

            synchronized (perl()) {
                if (perl().match("/(.*)\\s+(dispose_\\w+)$/i", monitoredMeasurement)) {
                    monitoredMeasurement = perl().group(1);

                    String disposeText = perl().group(2);

                    if (disposeText.equalsIgnoreCase("DISPOSE_IGNORE")) {
                        dispose = DISPOSE_IGNORE;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_MINIMUM")) {
                        dispose = DISPOSE_MINIMUM;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_MEDIAN")) {
                        dispose = DISPOSE_MEDIAN;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_AVERAGE")) {
                        dispose = DISPOSE_AVERAGE;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_STANDARD_DEVIATION")) {
                        dispose = DISPOSE_STANDARD_DEVIATION;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
                        dispose = DISPOSE_MAXIMUM;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_SUM")) {
                        dispose = DISPOSE_SUM;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
                        dispose = DISPOSE_NB_DATA_POINTS;
                    } else {
                        dispose = DISPOSE_IGNORE;
                    }
                } else {
                    dispose = DISPOSE_IGNORE;
                }
            }

            String selfDisposeText = in.readLine();
            if (selfDisposeText != null) {
                selfDisposeText = selfDisposeText.trim();

                if (selfDisposeText.equalsIgnoreCase("DISPOSE_IGNORE")) {
                    selfDispose = DISPOSE_IGNORE;
                } else if (selfDisposeText.equalsIgnoreCase("DISPOSE_MINIMUM")) {
                    selfDispose = DISPOSE_MINIMUM;
                } else if (selfDisposeText.equalsIgnoreCase("DISPOSE_MEDIAN")) {
                    selfDispose = DISPOSE_MEDIAN;
                } else if (selfDisposeText.equalsIgnoreCase("DISPOSE_AVERAGE")) {
                    selfDispose = DISPOSE_AVERAGE;
                } else if (selfDisposeText.equalsIgnoreCase("DISPOSE_STANDARD_DEVIATION")) {
                    selfDispose = DISPOSE_STANDARD_DEVIATION;
                } else if (selfDisposeText.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
                    selfDispose = DISPOSE_MAXIMUM;
                } else if (selfDisposeText.equalsIgnoreCase("DISPOSE_SUM")) {
                    selfDispose = DISPOSE_SUM;
                } else if (selfDisposeText.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
                    selfDispose = DISPOSE_NB_DATA_POINTS;
                } else {
                    selfDispose = DISPOSE_AVERAGE;
                }
            } else {
                selfDispose = DISPOSE_AVERAGE;
            }

            in.close();
        } catch (Exception ex) {
            LogManager.getLogger(getClass()).debug("Cannot initialize with \"{}\"", initText, ex);
            monitoredMeasurement = null;
        }
    }

    public double getMinimum() {
        collectData();
        return minimum;
    }

    public double getMedian() {
        collectData();
        return median;
    }

    public double getAverage() {
        collectData();
        return average;
    }

    /**
     *  Real standard deviation of the data set.
     *  This is NOT the estimator "s".
     */
    public double getStandardDeviation() {
        collectData();
        return standardDeviation;
    }

    public double getMaximum() {
        collectData();
        return maximum;
    }

    public double getSum() {
        collectData();
        return sum;
    }

    public int getNbDataPoints() {
        collectData();
        return nbDataPoints;
    }

    private void collectData() {
        if (getContext().getSubMetrics().size() != nbSubmetrics) {
            synchronized (this) {
                if (getContext().getSubMetrics().size() != nbSubmetrics) {
                    data = new LinkedList<>();
                    setEmpty(true);

                    getContext().getSubMetrics().forEach(this::visitMetrics);

                    if (!data.isEmpty()) {
                        Collections.sort(data);

                        minimum      = data.get(0);
                        maximum      = data.get(data.size() - 1);

                    } else {
                        minimum      = Double.NaN;
                        maximum      = Double.NaN;
                    }

                    nbDataPoints = data.size();
                    sum = data.stream()
                            .reduce(Double::sum)
                            .orElse(0.0);
                    median = findMedianOf(data);
                    average = sum / nbDataPoints;

                    if (!data.isEmpty()) {
                        var temp = data.parallelStream()
                                .map(n -> Math.pow(n - average, 2))
                                .reduce(Double::sum)
                                .orElse(0.0);

                        standardDeviation = Math.sqrt(temp / nbDataPoints);
                    } else {
                        standardDeviation = Double.NaN;
                    }

                    nbSubmetrics = getContext().getSubMetrics().size();
                }
            }
        }
    }

    private double findMedianOf(List<Double> data) {
        if (data.isEmpty()) {
            return findMedianOfEmptyList(data);
        }
        if (data.size() % 2 == 0) {
            return findMedianEvenSizedList(data);
        }
        return findMedianOfOddSizedList(data);
    }

    private double findMedianOfEmptyList(List<Double> data) {
        return Double.NaN;
    }

    private double findMedianEvenSizedList(List<Double> data) {
        int pos = data.size() / 2;
        double leftMiddleElement = data.get(pos - 1);
        double rightMiddleElement = data.get(pos);
        return (leftMiddleElement + rightMiddleElement) / 2;
    }

    private double findMedianOfOddSizedList(List<Double> data) {
        return data.get(data.size() / 2);
    }

    private void visitMetrics(Metrics metrics) {
        LogManager.getLogger(getClass()).debug("VisitMetrics: {}", metrics.getName());

        Measurement measurement = metrics.getMeasurement(monitoredMeasurement);

        LogManager.getLogger(getClass()).debug("measurement for {} is {}", monitoredMeasurement, measurement.getClass());

        if (measurement instanceof StatisticalMeasurement stats) {
            LogManager.getLogger(getClass()).debug("dispose of StatisticalMeasurements is {}", dispose);

            switch (dispose) {
                case DISPOSE_MINIMUM -> {
                    LogManager.getLogger(getClass()).debug("using Minimum(): {}", stats.getMinimum());
                    data.add(stats.getMinimum());
                }
                case DISPOSE_MEDIAN -> {
                    LogManager.getLogger(getClass()).debug("using Median(): {}", stats.getMedian());
                    data.add(stats.getMedian());
                }
                case DISPOSE_AVERAGE -> {
                    LogManager.getLogger(getClass()).debug("using Average(): {}", stats.getAverage());
                    data.add(stats.getAverage());
                }
                case DISPOSE_STANDARD_DEVIATION -> {
                    LogManager.getLogger(getClass()).debug("using StandardDeviation(): {}", stats.getStandardDeviation());
                    data.add(stats.getStandardDeviation());
                }
                case DISPOSE_MAXIMUM -> {
                    LogManager.getLogger(getClass()).debug("using Maximum(): {}", stats.getMaximum());
                    data.add(stats.getMaximum());
                }
                case DISPOSE_SUM -> {
                    LogManager.getLogger(getClass()).debug("using Sum(): {}", stats.getSum());
                    data.add(stats.getSum());
                }
                case DISPOSE_NB_DATA_POINTS -> {
                    LogManager.getLogger(getClass()).debug("using NbDataPoints(): {}", stats.getNbDataPoints());
                    data.add((double) stats.getNbDataPoints());
                }
                default -> {
                    LogManager.getLogger(getClass()).debug("Skipping to next level ...");
                    metrics.getSubMetrics().forEach(this::visitMetrics);
                }
            }
        } else if (measurement instanceof NullMeasurement) {
            LogManager.getLogger(getClass()).debug("Skipping to next level ...");
            metrics.getSubMetrics().forEach(this::visitMetrics);
        } else {
            Number value = measurement.getValue();

            LogManager.getLogger(getClass()).debug("{} on {} is {}", monitoredMeasurement, metrics.getName(), value);

            if (value != null) {
                data.add(value.doubleValue());
            }
        }

        if (super.isEmpty()) {
            setEmpty(measurement.isEmpty());
        }
    }

    public boolean isEmpty() {
        collectData();

        return super.isEmpty();
    }

    public void accept(MeasurementVisitor visitor) {
        visitor.visitStatisticalMeasurement(this);
    }

    protected double compute() {
        return switch (selfDispose) {
            case DISPOSE_MINIMUM -> getMinimum();
            case DISPOSE_MEDIAN -> getMedian();
            case DISPOSE_AVERAGE -> getAverage();
            case DISPOSE_STANDARD_DEVIATION -> getStandardDeviation();
            case DISPOSE_MAXIMUM -> getMaximum();
            case DISPOSE_SUM -> getSum();
            case DISPOSE_NB_DATA_POINTS -> getNbDataPoints();
            default -> Double.NaN;
        };
    }

    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append("[").append(valueFormat.format(getMinimum()));
        result.append(" ").append(valueFormat.format(getMedian()));
        result.append("/").append(valueFormat.format(getAverage()));
        result.append(" ").append(valueFormat.format(getStandardDeviation()));
        result.append(" ").append(valueFormat.format(getMaximum()));
        result.append(" ").append(valueFormat.format(getSum()));
        result.append(" (").append(valueFormat.format(getNbDataPoints())).append(")]");

        return result.toString();
    }
}
