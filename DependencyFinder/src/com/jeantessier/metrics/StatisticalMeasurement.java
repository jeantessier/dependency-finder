/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.log4j.*;

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
 *  distinguish between StatisticalMeasurent and other Measurements.  The
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

    public static String getDisposeAbbreviation(int dispose) {
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

    private String monitoredMeasurement;
    private int    dispose;
    private int    selfDispose;
    
    private List<Double> data = new LinkedList<Double>();

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
            Logger.getLogger(getClass()).debug("Cannot initialize with \"" + initText + "\"", ex);
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
                    data = new LinkedList<Double>();
                    setEmpty(true);

                    for (Metrics metrics : getContext().getSubMetrics()) {
                        visitMetrics(metrics);
                    }
                    
                    if (!data.isEmpty()) {
                        Collections.sort(data);
                        
                        minimum      = data.get(0);
                        median       = data.get(data.size() / 2);
                        maximum      = data.get(data.size() - 1);
                        nbDataPoints = data.size();
                        
                        sum = 0.0;
                        for (Double number : data) {
                            sum += number;
                        }
                    } else {
                        minimum      = Double.NaN;
                        median       = Double.NaN;
                        maximum      = Double.NaN;
                        nbDataPoints = 0;
                        sum            = 0.0;
                    }
                    
                    average = sum / nbDataPoints;
                    
                    if (!data.isEmpty()) {
                        double temp = 0.0;

                        for (Double number : data) {
                            temp += Math.pow(number - average, 2);
                        }
                        
                        standardDeviation = Math.sqrt(temp / nbDataPoints);
                    } else {
                        standardDeviation = Double.NaN;
                    }
                    
                    nbSubmetrics = getContext().getSubMetrics().size();
                }
            }
        }
    }
    
    private void visitMetrics(Metrics metrics) {
        Logger.getLogger(getClass()).debug("VisitMetrics: " + metrics.getName());
        
        Measurement measurement = metrics.getMeasurement(monitoredMeasurement);

        Logger.getLogger(getClass()).debug("measurement for " + monitoredMeasurement + " is " + measurement.getClass());
        
        if (measurement instanceof StatisticalMeasurement) {
            StatisticalMeasurement stats = (StatisticalMeasurement) measurement;
            
            Logger.getLogger(getClass()).debug("dispose of StatisticalMeasurements is " + dispose);

            switch (dispose) {
                case DISPOSE_MINIMUM:
                    Logger.getLogger(getClass()).debug("using Minimum(): " + stats.getMinimum());
                    data.add(stats.getMinimum());
                    break;
                    
                case DISPOSE_MEDIAN:
                    Logger.getLogger(getClass()).debug("using Median(): " + stats.getMedian());
                    data.add(stats.getMedian());
                    break;
                    
                case DISPOSE_AVERAGE:
                    Logger.getLogger(getClass()).debug("using Average(): " + stats.getAverage());
                    data.add(stats.getAverage());
                    break;
                            
                case DISPOSE_STANDARD_DEVIATION:
                    Logger.getLogger(getClass()).debug("using StandardDeviation(): " + stats.getStandardDeviation());
                    data.add(stats.getStandardDeviation());
                    break;
            
                case DISPOSE_MAXIMUM:
                    Logger.getLogger(getClass()).debug("using Maximum(): " + stats.getMaximum());
                    data.add(stats.getMaximum());
                    break;
                    
                case DISPOSE_SUM:
                    Logger.getLogger(getClass()).debug("using Sum(): " + stats.getSum());
                    data.add(stats.getSum());
                    break;
                    
                case DISPOSE_NB_DATA_POINTS:
                    Logger.getLogger(getClass()).debug("using NbDataPoints(): " + stats.getNbDataPoints());
                    data.add((double) stats.getNbDataPoints());
                    break;

                case DISPOSE_IGNORE:
                default:
                    Logger.getLogger(getClass()).debug("Skipping to next level ...");
                    for (Metrics subMetrics : metrics.getSubMetrics()) {
                        visitMetrics(subMetrics);
                    }
                    break;
            }
        } else if (measurement instanceof NullMeasurement) {
            Logger.getLogger(getClass()).debug("Skipping to next level ...");
            for (Metrics subMetrics : metrics.getSubMetrics()) {
                visitMetrics(subMetrics);
            }
        } else {
            Number value = measurement.getValue();
            
            Logger.getLogger(getClass()).debug(monitoredMeasurement + " on " + metrics.getName() + " is " + value);

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
        double result = Double.NaN;
        
        switch (selfDispose) {
            case DISPOSE_MINIMUM:
                result = getMinimum();
                break;
                
            case DISPOSE_MEDIAN:
                result = getMedian();
                break;
                
            case DISPOSE_AVERAGE:
                result = getAverage();
                break;
                
            case DISPOSE_STANDARD_DEVIATION:
                result = getStandardDeviation();
                break;
                
            case DISPOSE_MAXIMUM:
                result = getMaximum();
                break;
                
            case DISPOSE_SUM:
                result = getSum();
                break;
                
            case DISPOSE_NB_DATA_POINTS:
                result = getNbDataPoints();
                break;

            case DISPOSE_IGNORE:
            default:
                break;
        }

        return result;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();

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
