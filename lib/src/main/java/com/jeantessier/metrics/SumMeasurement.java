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
public class SumMeasurement extends ArithmeticMeasurement {
    private final List<String> terms = new LinkedList<>();

    private double value = 0.0;

    public SumMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try (var in = new BufferedReader(new StringReader(initText))) {
            in.lines()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(terms::add);
        } catch (Exception ex) {
            LogManager.getLogger(getClass()).debug("Cannot initialize with \"{}\"", initText, ex);
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
                    setEmpty(true);

                    value = getTerms().stream()
                            .mapToDouble(this::evaluateTerm)
                            .sum();

                    setCached(true);
                }
            }
        }

        return value;
    }
}
