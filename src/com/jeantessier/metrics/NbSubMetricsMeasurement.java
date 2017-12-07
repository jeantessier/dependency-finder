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

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *  Counts the number of submetrics according to selection
 *  criteria.  If there are no criteria, it matches all
 *  submetrics.  Each criterion is a boolean expression
 *  with measurement names, numbers, and boolean operators
 *  (&lt;, &lt;=, &gt;, &gt;=, ==, and !=).  If a submetric matches
 *  any one of the expressions in the criteria, it is
 *  included in the count.
 *  
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      (number | measurement name [DISPOSE_x]) [operator [(number | measurement name [DISPOSE_x])]]*
 *      ...
 *  &lt;/init&gt;
 *  </pre>
 */
public class NbSubMetricsMeasurement extends MeasurementBase {
    private static final String LESSER_THAN = "<";
    private static final String LESSER_THAN_OR_EQUAL = "<=";
    private static final String GREATER_THAN = ">";
    private static final String GREATER_THAN_OR_EQUAL = ">=";
    private static final String EQUALS = "==";
    private static final String NOT_EQUALS = "!=";

    private static final String OPERATORS_REGULAR_EXPRESSION =
        "/" +
        "(" + LESSER_THAN_OR_EQUAL + ")|" +
        "(" + LESSER_THAN + ")|" +
        "(" + GREATER_THAN_OR_EQUAL + ")|" +
        "(" + GREATER_THAN + ")|" +
        "(" + EQUALS + ")|" +
        "(" + NOT_EQUALS + ")" +
        "/";

    private static final double DELTA = 0.1;

    private List<String> terms = new LinkedList<String>();
    private int  value = 0;

    public NbSubMetricsMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try {
            BufferedReader in   = new BufferedReader(new StringReader(initText));
            String         line;

            while ((line = in.readLine()) != null) {
                terms.add(line.trim());
            }

            in.close();
        } catch (Exception ex) {
            Logger.getLogger(getClass()).debug("Cannot initialize with \"" + initText + "\"", ex);
            terms.clear();
        }
    }

    public List<String> getTerms() {
        return terms;
    }
    
    public void accept(MeasurementVisitor visitor) {
        visitor.visitNbSubMetricsMeasurement(this);
    }

    public boolean isEmpty() {
        if (!isCached()) {
            compute();
        }

        return super.isEmpty();
    }
    
    protected double compute() {
        if (!isCached()) {
            synchronized (this) {
                if (!isCached()) {
                    value = 0;
                    
                    if (getTerms().isEmpty()) {
                        value = getContext().getSubMetrics().size();
                    } else {
                        for (Metrics metrics : getContext().getSubMetrics()) {
                            if (getSelectMetrics(metrics)) {
                                value++;
                            }
                        }
                    }

                    setEmpty(value == 0);

                    setCached(true);
                }
            }
        }
        
        return value;
    }

    private boolean getSelectMetrics(Metrics metrics) {
        boolean result = getTerms().isEmpty();
        
        Iterator<String> i = getTerms().iterator();
        while (!result && i.hasNext()) {
            result = evaluateTerm(i.next(), metrics);
        }

        return result;
    }

    private boolean evaluateTerm(String term, Metrics metrics) {
        boolean result;

        Logger.getLogger(getClass()).debug("EvaluateTerm(\"" + term + "\", " + metrics + ")");
        
        List<String> elements = new ArrayList<String>();
        perl().split(elements, OPERATORS_REGULAR_EXPRESSION, term);

        result = (elements.size() > 0) && ((elements.size() % 2) == 1);
        
        if (elements.size() == 1) {
            result = metrics.hasMeasurement(elements.remove(0));
        } else {
            while (result && (elements.size() > 2) && ((elements.size() % 2) == 1)) {
                String leftString  = elements.remove(0);
                String operator    = elements.remove(0);
                String rightString = elements.get(0);

                double leftOperand = 0;
                try {
                    leftOperand = Double.parseDouble(leftString);
                } catch (NumberFormatException ex) {
                    try {
                        leftOperand = resolveOperand(leftString, metrics);
                    } catch (NullPointerException ex2) {
                        result = false;
                    }
                }

                double rightOperand = 0;
                try {
                    rightOperand = Double.parseDouble(rightString);
                } catch (NumberFormatException ex) {
                    try {
                        rightOperand = resolveOperand(rightString, metrics);
                    } catch (NullPointerException ex2) {
                        result = false;
                    }
                }

                if (result) {
                    if (operator.equals(LESSER_THAN)) {
                        result = leftOperand < rightOperand;
                    } else if (operator.equals(LESSER_THAN_OR_EQUAL)) {
                        result = leftOperand <= rightOperand;
                    } else if (operator.equals(GREATER_THAN)) {
                        result = leftOperand > rightOperand;
                    } else if (operator.equals(GREATER_THAN_OR_EQUAL)) {
                        result = leftOperand >= rightOperand;
                    } else if (operator.equals(EQUALS)) {
                        result = Math.abs(leftOperand - rightOperand) <= DELTA;
                    } else if (operator.equals(NOT_EQUALS)) {
                        result = Math.abs(leftOperand - rightOperand) > DELTA;
                    }
                }
            }
        }

        Logger.getLogger(getClass()).debug("EvaluateTerm(\"" + term + "\", " + metrics + "): " + result);

        return result;
    }

    private double resolveOperand(String name, Metrics metrics) {
        double result = 0;
            
        name = name.trim();

        Logger.getLogger(getClass()).debug("ResolveOperand(\"" + name + "\", " + metrics + ")");

        if (name.length() != 0) {
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
            
            Measurement measurement = metrics.getMeasurement(name);
            
            if (measurement instanceof StatisticalMeasurement) {
                StatisticalMeasurement stats = (StatisticalMeasurement) measurement;
                
                switch (dispose) {
                    case StatisticalMeasurement.DISPOSE_MINIMUM:
                        result = stats.getMinimum();
                        break;
                    case StatisticalMeasurement.DISPOSE_MEDIAN:
                        result = stats.getMedian();
                        break;
                    case StatisticalMeasurement.DISPOSE_AVERAGE:
                        result = stats.getAverage();
                        break;
                    case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
                        result = stats.getStandardDeviation();
                        break;
                    case StatisticalMeasurement.DISPOSE_MAXIMUM:
                        result = stats.getMaximum();
                        break;
                    case StatisticalMeasurement.DISPOSE_SUM:
                        result = stats.getSum();
                        break;
                    case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
                        result = stats.getNbDataPoints();
                        break;
                    case StatisticalMeasurement.DISPOSE_IGNORE:
                    default:
                        result = stats.getValue().doubleValue();
                        break;
                }
            } else if (measurement instanceof NullMeasurement) {
                throw new NullPointerException();
            } else {
                result = measurement.getValue().doubleValue();
            }
        }

        Logger.getLogger(getClass()).debug("ResolveOperand(\"" + name + "\", " + metrics + "): " + result);
        
        return result;
    }
}
