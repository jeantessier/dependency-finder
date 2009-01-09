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

import java.util.*;

public class MetricsComparator implements Comparator<Metrics> {
    public static final int DESCENDING = -1;
    public static final int ASCENDING  =  1;
    
    private String name;
    private int    direction;
    private int    dispose;

    public MetricsComparator(String name) {
        this(name, StatisticalMeasurement.DISPOSE_IGNORE);
    }
        
    public MetricsComparator(String name, int dispose) {
        setName(name);
        setDispose(dispose);
        setDirection(ASCENDING);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDispose() {
        return dispose;
    }

    public void setDispose(int dispose) {
        this.dispose = dispose;
    }

    public void sortOn(String name, int dispose) {
        if (name.equals(this.name) && dispose == this.dispose) {
            reverse();
        } else {
            setName(name);
            setDirection(ASCENDING);
            setDispose(dispose);
        }
    }
    
    public void reverse() {
        direction *= -1;
    }
    
    public int compare(Metrics metrics1, Metrics metrics2) {
        int result;

        if ("name".equals(name)) {
            result = metrics1.getName().compareTo(metrics2.getName());
        } else {
            Measurement m1 = metrics1.getMeasurement(name);
            Measurement m2 = metrics2.getMeasurement(name);
            
            if (m1 == null && m2 != null) {
                result = -1;
            } else if (m1 != null && m2 == null) {
                result = 1;
            } else if (m1 == m2) {
                result = 0;
            } else {
                double v1 = extractValue(m1);
                double v2 = extractValue(m2);
                
                if (Double.isNaN(v1) && !Double.isNaN(v2)) {
                    result = 1 * getDirection();
                } else if (!Double.isNaN(v1) && Double.isNaN(v2)) {
                    result = -1 * getDirection();
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
        
        result *= getDirection();

        return result;
    }

    private double extractValue(Measurement m) {
        double result = Double.NaN;

        if (m instanceof StatisticalMeasurement) {
            StatisticalMeasurement sm = (StatisticalMeasurement) m;
            switch (getDispose()) {
                case StatisticalMeasurement.DISPOSE_MINIMUM:
                    result = sm.getMinimum();
                    break;
                    
                case StatisticalMeasurement.DISPOSE_MEDIAN:
                    result = sm.getMedian();
                    break;
                    
                case StatisticalMeasurement.DISPOSE_AVERAGE:
                    result = sm.getAverage();
                    break;
                    
                case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION:
                    result = sm.getStandardDeviation();
                    break;
                    
                case StatisticalMeasurement.DISPOSE_MAXIMUM:
                    result = sm.getMaximum();
                    break;
                    
                case StatisticalMeasurement.DISPOSE_SUM:
                    result = sm.getSum();
                    break;
                    
                case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS:
                    result = sm.getNbDataPoints();
                    break;

                default:
                case StatisticalMeasurement.DISPOSE_IGNORE:
                    break;
            }
        } else {
            result = m.getValue().doubleValue();
        }
        
        return result;
    }
}
