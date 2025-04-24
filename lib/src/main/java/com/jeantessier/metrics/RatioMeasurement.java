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

package com.jeantessier.metrics;

import java.io.*;

import org.apache.logging.log4j.*;

/**
 *  <p>Divides one measurement (base) by another (divider).  Both
 *  must be in the same context.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      base measurement name [DISPOSE_x]
 *      divider measurement name [DISPOSE_x]
 *  &lt;/init&gt;
 *  </pre>
 *  
 *  <p>If either is missing, this measurement will be NaN.</p>
 */
public class RatioMeasurement extends MeasurementBase {
    private String baseName;
    private int    baseDispose;
    private String dividerName;
    private int    dividerDispose;

    private double value = 0.0;
    
    public RatioMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try (var in = new BufferedReader(new StringReader(initText))) {
            synchronized (perl()) {
                baseName = in.readLine().trim();
                if (perl().match("/(.*)\\s+(dispose_\\w+)$/i", baseName)) {
                    baseName = perl().group(1);
                    
                    String disposeText = perl().group(2);
                    baseDispose = StatisticalMeasurement.getDispose(disposeText, () -> {
                        LogManager.getLogger(getClass()).error("Unknown dispose value \"{}\" for base \"{}\" of measurement \"{}\", defaulting to DISPOSE_IGNORE", disposeText, baseName, descriptor.getLongName());
                        return StatisticalMeasurement.DISPOSE_IGNORE;
                    });
                } else {
                    baseDispose = StatisticalMeasurement.DISPOSE_IGNORE;
                }
                
                dividerName = in.readLine().trim();
                if (perl().match("/(.*)\\s+(dispose_\\w+)$/i", dividerName)) {
                    dividerName = perl().group(1);
                    
                    String disposeText = perl().group(2);
                    dividerDispose = StatisticalMeasurement.getDispose(disposeText, () -> {
                        LogManager.getLogger(getClass()).error("Unknown dispose value \"{}\" for divider \"{}\" of measurement \"{}\", defaulting to DISPOSE_IGNORE", disposeText, dividerName, descriptor.getLongName());
                        return StatisticalMeasurement.DISPOSE_IGNORE;
                    });
                } else {
                    dividerDispose = StatisticalMeasurement.DISPOSE_IGNORE;
                }
            }
        } catch (Exception ex) {
            LogManager.getLogger(getClass()).debug("Cannot initialize with \"{}\"", initText, ex);
            baseName    = null;
            dividerName = null;
        }
    }
    
    public String getBaseName() {
        return baseName;
    }
    
    public int getBaseDispose() {
        return baseDispose;
    }

    public String getDividerName() {
        return dividerName;
    }

    public int getDividerDispose() {
        return dividerDispose;
    }

    public void accept(MeasurementVisitor visitor) {
        visitor.visitRatioMeasurement(this);
    }

    public boolean isEmpty() {
        if (!isCached()) {
            compute();
        }

        return super.isEmpty();
    }

    protected double compute() {
        if (!isCached()) {
            value = Double.NaN;

            if (getContext() != null && getBaseName() != null && getDividerName() != null) {
                Measurement base    = getContext().getMeasurement(getBaseName());
                Measurement divider = getContext().getMeasurement(getDividerName());
                
                double baseValue    = Double.NaN;
                double dividerValue = Double.NaN;
                
                if (base instanceof StatisticalMeasurement stats) {
                    baseValue = switch (getBaseDispose()) {
                        case StatisticalMeasurement.DISPOSE_MINIMUM -> stats.getMinimum();
                        case StatisticalMeasurement.DISPOSE_MEDIAN -> stats.getMedian();
                        case StatisticalMeasurement.DISPOSE_AVERAGE -> stats.getAverage();
                        case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION -> stats.getStandardDeviation();
                        case StatisticalMeasurement.DISPOSE_MAXIMUM -> stats.getMaximum();
                        case StatisticalMeasurement.DISPOSE_SUM -> stats.getSum();
                        case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS -> stats.getNbDataPoints();
                        default -> stats.getValue().doubleValue();
                    };
                } else if (base != null) {
                    baseValue = base.getValue().doubleValue();
                }
                
                if (divider instanceof StatisticalMeasurement stats) {
                    dividerValue = switch (getDividerDispose()) {
                        case StatisticalMeasurement.DISPOSE_MINIMUM -> stats.getMinimum();
                        case StatisticalMeasurement.DISPOSE_MEDIAN -> stats.getMedian();
                        case StatisticalMeasurement.DISPOSE_AVERAGE -> stats.getAverage();
                        case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION -> stats.getStandardDeviation();
                        case StatisticalMeasurement.DISPOSE_MAXIMUM -> stats.getMaximum();
                        case StatisticalMeasurement.DISPOSE_SUM -> stats.getSum();
                        case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS -> stats.getNbDataPoints();
                        default -> stats.getValue().doubleValue();
                    };
                } else if (divider != null) {
                    dividerValue = divider.getValue().doubleValue();
                }
                
                value = baseValue / dividerValue;
            }

            setEmpty(Double.isNaN(value) || Double.isInfinite(value));

            setCached(true);
        }
        
        return value;
    }
}
