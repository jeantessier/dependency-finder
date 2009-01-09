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

/**
 *  <p>A simple counter, it tallies the values that are put in it.
 *  If you try to add a non-number, it simply adds 1.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *  
 *  <pre>
 *  &lt;init&gt;
 *      [initial value]
 *  &lt;/init&gt;
 *  </pre>
 */
public class CounterMeasurement extends MeasurementBase {
    private double value;

    public CounterMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try {
            if (initText != null) {
                value = Double.parseDouble(initText);
            }
        } catch (NumberFormatException ex) {
            value = 0;
        }
    }

    public void add(Object object) {
        if (object instanceof Number) {
            value += ((Number) object).doubleValue();
        } else {
            value++;
        }

        setEmpty(false);
    }

    public void accept(MeasurementVisitor visitor) {
        visitor.visitCounterMeasurement(this);
    }

    protected double compute() {
        return value;
    }
}
