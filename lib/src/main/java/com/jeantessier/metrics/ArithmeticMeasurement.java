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

/**
 *  Base class for measurements that perform arithmetic operations.
 *  They can operate on constants or on other measurements in the context.
 */
public abstract class ArithmeticMeasurement extends MeasurementBase {
    public ArithmeticMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);
    }

    protected double evaluateTerm(String term) {
        try {
            return Double.parseDouble(term);
        } catch (NumberFormatException ex) {
            if (term.startsWith("-")) {
                return -1 * evaluateMeasurement(term.substring(1));
            } else {
                return evaluateMeasurement(term);
            }
        }
    }

    protected double evaluateMeasurement(String name) {
        double result = 0;

        if (!name.isEmpty() && getContext() != null) {
            int dispose;
            synchronized (perl()) {
                if (perl().match("/(.*)\\s+(dispose_\\w+)$/i", name)) {
                    name = perl().group(1);

                    String disposeText = perl().group(2);
                    dispose = StatisticalMeasurement.getDispose(disposeText.toUpperCase());
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
