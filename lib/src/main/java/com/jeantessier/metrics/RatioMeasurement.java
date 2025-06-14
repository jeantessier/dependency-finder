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
 *  <p>Divides one numerical value (base) by another (divider).  Either can be
 *  a numerical constant or the name of another measurement be in the same
 *  context.</p>
 *
 *  <p>If the result of the division is NaN, you can provide a replacement
 *  value.  The measurement will attempt to coerce the value to a double and
 *  will fall back to NaN if that fails.</p>
 *
 *  <p>If the result of the division is positive infinity, you can provide a
 *  separate replacement value.  The measurement will attempt to coerce the
 *  value to a double and will fall back to positive infinity if that fails.</p>
 *
 *  <p>If the result of the division is negative infinity, you can provide a
 *  separate replacement value.  The measurement will attempt to coerce the
 *  value to a double and will fall back to negative infinity if that fails.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      base number | measurement name [DISPOSE_x]
 *      divider number | measurement name [DISPOSE_x]
 *      [number default for NaN]
 *      [number default for positive infinity]
 *      [number default for negative infinity]
 *  &lt;/init&gt;
 *  </pre>
 *  
 *  <p>If either is missing, this measurement will be NaN.</p>
 */
public class RatioMeasurement extends ArithmeticMeasurement {
    private String baseTerm;
    private double baseValue = Double.NaN;

    private String dividerTerm;
    private double dividerValue = Double.NaN;

    private double defaultForNaN = Double.NaN;
    private double defaultForPositiveInfinity = Double.POSITIVE_INFINITY;
    private double defaultForNegativeInfinity = Double.NEGATIVE_INFINITY;

    private double value = 0.0;
    
    public RatioMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try (var in = new BufferedReader(new StringReader(initText))) {
            baseTerm = in.readLine().trim();
            dividerTerm = in.readLine().trim();

            try {
                defaultForNaN = Double.parseDouble(in.readLine());
            } catch (NullPointerException | NumberFormatException ex) {
                defaultForNaN = Double.NaN;
            }

            try {
                defaultForPositiveInfinity = Double.parseDouble(in.readLine());
            } catch (NullPointerException | NumberFormatException ex) {
                defaultForPositiveInfinity = Double.POSITIVE_INFINITY;
            }

            try {
                defaultForNegativeInfinity = Double.parseDouble(in.readLine());
            } catch (NullPointerException | NumberFormatException ex) {
                defaultForNegativeInfinity = Double.NEGATIVE_INFINITY;
            }
        } catch (Exception ex) {
            LogManager.getLogger(getClass()).debug("Cannot initialize with \"{}\"", initText, ex);
            baseTerm = null;
            dividerTerm = null;
        }
    }
    
    public String getBaseTerm() {
        return baseTerm;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public String getDividerTerm() {
        return dividerTerm;
    }

    public double getDividerValue() {
        return dividerValue;
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

            if (getBaseTerm() != null && getDividerTerm() != null) {
                baseValue = evaluateTerm(getBaseTerm());
                dividerValue = evaluateTerm(getDividerTerm());

                value = baseValue / dividerValue;

                if (Double.isNaN(value)) {
                    value = defaultForNaN;
                } else if (Double.isInfinite(value)) {
                    value = value > 0 ? defaultForPositiveInfinity : defaultForNegativeInfinity;
                }
            }

            setEmpty(Double.isNaN(value) || Double.isInfinite(value));

            setCached(true);
        }
        
        return value;
    }
}
