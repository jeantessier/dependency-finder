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

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.*;

/**
 *  <p>Adds up numerical values.  Use it with just one term to alias
 *  other measurements.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      number | measurement name [DISPOSE_x]
 *      ...
 *  &lt;/init&gt;
 *  </pre>
 */
public class SumMeasurement extends MeasurementBase {
    private final List<String> terms = new LinkedList<>();

    private double value = 0.0;

    public SumMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try {
            BufferedReader in   = new BufferedReader(new StringReader(initText));
            String         line;

            while ((line = in.readLine()) != null) {
                terms.add(line.trim());
            }

            in.close();
        } catch (Exception ex) {
            LogManager.getLogger(getClass()).debug("Cannot initialize with \"" + initText + "\"", ex);
            terms.clear();
        }
    }

    public List<String> getTerms() {
        return terms;
    }

    public boolean isEmpty() {
        compute();

        return super.isEmpty();
    }
    
    public void accept(MeasurementVisitor visitor) {
        visitor.visitSumMeasurement(this);
    }

    protected double compute() {
        if (!isCached()) {
            synchronized (this) {
                if (!isCached()) {
                    value = 0.0;
                    setEmpty(true);

                    if (getContext() != null) {
                        LogManager.getLogger(getClass()).debug("Start computing \"" + getShortName() + "\" on \"" + getContext().getName() + "\": value=" + value);
                    } else {
                        LogManager.getLogger(getClass()).debug("Start computing \"" + getShortName() + "\" on null: value=" + value);
                    }

                    for (String term : getTerms()) {
                        LogManager.getLogger(getClass()).debug("Evaluating term \"" + term + "\"");

                        double termValue = Double.NaN;

                        try {
                            termValue = Double.parseDouble(term);
                        } catch (NumberFormatException ex) {
                            if (term.startsWith("-")) {
                                termValue = -1 * evaluateMeasurement(term.substring(1));
                            } else {
                                termValue = evaluateMeasurement(term);
                            }
                        }

                        LogManager.getLogger(getClass()).debug("term \"" + term + "\" is " + termValue);

                        value += termValue;

                        LogManager.getLogger(getClass()).debug("value=" + value);
                    }
                    
                    if (getContext() != null) {
                        LogManager.getLogger(getClass()).debug("Stop computing \"" + getShortName() + "\" on \"" + getContext().getName() + "\": value=" + value);
                    } else {
                        LogManager.getLogger(getClass()).debug("Stop computing \"" + getShortName() + "\" on null: value=" + value);
                    }
                    
                    setCached(true);
                }
            }
        }

        if (getContext() != null) {
            LogManager.getLogger(getClass()).debug("\"" + getShortName() + "\" on \"" + getContext().getName() + "\": value=" + value);
        } else {
            LogManager.getLogger(getClass()).debug("\"" + getShortName() + "\" on null: value=" + value);
        }

        return value;
    }

    private double evaluateMeasurement(String name) {
        double result = 0;

        if (!name.isEmpty()) {
            int dispose;
            
            synchronized (perl()) {
                if (perl().match("/(.*)\\s+(dispose_\\w+)$/i", name)) {
                    name = perl().group(1);
                    
                    String disposeText = perl().group(2);
                    
                    if (disposeText.equalsIgnoreCase("DISPOSE_IGNORE")) {
                        dispose = StatisticalMeasurement.DISPOSE_IGNORE;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_MINIMUM")) {
                        dispose = StatisticalMeasurement.DISPOSE_MINIMUM;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_MEDIAN")) {
                        dispose = StatisticalMeasurement.DISPOSE_MEDIAN;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_AVERAGE")) {
                        dispose = StatisticalMeasurement.DISPOSE_AVERAGE;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_STANDARD_DEVIATION")) {
                        dispose = StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_MAXIMUM")) {
                        dispose = StatisticalMeasurement.DISPOSE_MAXIMUM;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_SUM")) {
                        dispose = StatisticalMeasurement.DISPOSE_SUM;
                    } else if (disposeText.equalsIgnoreCase("DISPOSE_NB_DATA_POINTS")) {
                        dispose = StatisticalMeasurement.DISPOSE_NB_DATA_POINTS;
                    } else {
                        dispose = StatisticalMeasurement.DISPOSE_IGNORE;
                    }
                } else {
                    dispose = StatisticalMeasurement.DISPOSE_IGNORE;
                }
            }
            
            Measurement measurement = getContext().getMeasurement(name);
            
            if (measurement instanceof StatisticalMeasurement stats) {
                result = switch (dispose) {
                    case StatisticalMeasurement.DISPOSE_MINIMUM -> stats.getMinimum();
                    case StatisticalMeasurement.DISPOSE_MEDIAN -> stats.getMedian();
                    case StatisticalMeasurement.DISPOSE_AVERAGE -> stats.getAverage();
                    case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION -> stats.getStandardDeviation();
                    case StatisticalMeasurement.DISPOSE_MAXIMUM -> stats.getMaximum();
                    case StatisticalMeasurement.DISPOSE_SUM -> stats.getSum();
                    case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS -> stats.getNbDataPoints();
                    default -> stats.getValue().doubleValue();
                };
            } else {
                result = measurement.getValue().doubleValue();
            }

            if (super.isEmpty()) {
                setEmpty(measurement.isEmpty());
            }
        }
                
        return result;
    }
}
