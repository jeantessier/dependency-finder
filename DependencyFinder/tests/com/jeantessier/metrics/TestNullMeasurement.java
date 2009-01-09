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

import junit.framework.*;

public class TestNullMeasurement extends TestCase implements MeasurementVisitor {
    private NullMeasurement measurement;
    private Measurement visited;
    
    protected void setUp() {
        measurement = new NullMeasurement();
    }
    
    public void testMeasurementDescriptor() {
        assertNull(measurement.getDescriptor());
        assertNull(measurement.getShortName());
        assertNull(measurement.getLongName());
    }

    public void testAdd() {
        measurement.add(null);
        measurement.add(new Object());
        measurement.add(measurement);
    }

    public void testEmpty() {
        assertTrue("Before Add()", measurement.isEmpty());

        measurement.add(new Object());

        assertTrue("After Add()", measurement.isEmpty());
    }

    public void testAccept() {
        visited = null;
        measurement.accept(this);
        assertNull(visited);
    }
    
    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        visited = measurement;
    }
    
    public void visitRatioMeasurement(RatioMeasurement measurement) {
        visited = measurement;
    }
    
    public void visitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
        visited = measurement;
    }
    
    public void visitCounterMeasurement(CounterMeasurement measurement) {
        visited = measurement;
    }
    
    public void visitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
        // Do nothing
    }
    
    public void visitNameListMeasurement(NameListMeasurement measurement) {
        // Do nothing
    }
    
    public void visitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
        // Do nothing
    }
    
    public void visitSumMeasurement(SumMeasurement measurement) {
        visited = measurement;
    }
}
