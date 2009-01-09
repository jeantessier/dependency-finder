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

public class TestNbSubMetricsMeasurement extends TestCase implements MeasurementVisitor {
    private MeasurementDescriptor descriptor;
    private NbSubMetricsMeasurement measurement;
    private Metrics metrics;
    private Measurement visited;
    
    protected void setUp() {
        metrics = new Metrics("foo");

        descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NbSubMetricsMeasurement.class);
        descriptor.setCached(false);
    }

    public void testCreateFromMeasurementDescriptor() throws Exception {
        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(NbSubMetricsMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    public void testAddSubMetrics() throws Exception {
        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(0, measurement.getValue().intValue());
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals(0, measurement.getValue().intValue());

        metrics.addSubMetrics(new Metrics("bar"));

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        metrics.addSubMetrics(new Metrics("bar"));

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        metrics.addSubMetrics(new Metrics("baz"));

        assertEquals(2, measurement.getValue().intValue());
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals(2, measurement.getValue().intValue());
    }

    public void testInUndefinedRange() throws Exception {
        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("foo"));
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("bar"));
        metrics.addSubMetrics(new Metrics("baz"));

        assertTrue(measurement.isInRange());
    }

    public void testInOpenRange() throws Exception {
        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("foo"));
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("bar"));
        metrics.addSubMetrics(new Metrics("baz"));

        assertTrue(measurement.isInRange());
    }

    public void testInLowerBoundRange() throws Exception {
        descriptor.setLowerThreshold(new Integer(1));

        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(!measurement.isInRange());

        metrics.addSubMetrics(new Metrics("foo"));
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("bar"));
        metrics.addSubMetrics(new Metrics("baz"));
        
        assertTrue(measurement.isInRange());
    }

    public void testInUpperBoundRange() throws Exception {
        descriptor.setUpperThreshold(new Float(1.5));

        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("foo"));
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("bar"));
        metrics.addSubMetrics(new Metrics("baz"));
        
        assertTrue(!measurement.isInRange());
    }

    public void testInBoundRange() throws Exception {
        descriptor.setLowerThreshold(new Integer(1));
        descriptor.setUpperThreshold(new Float(1.5));

        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(!measurement.isInRange());

        metrics.addSubMetrics(new Metrics("foo"));
        
        assertTrue(measurement.isInRange());

        metrics.addSubMetrics(new Metrics("bar"));
        metrics.addSubMetrics(new Metrics("baz"));
        
        assertTrue(!measurement.isInRange());
    }

    public void testCachedValue() throws Exception {
        descriptor.setCached(true);

        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);

        assertEquals("empty metrics", 0, measurement.getValue().intValue());
        
        metrics.addSubMetrics(new Metrics("foo"));
        metrics.addSubMetrics(new Metrics("bar"));
        metrics.addSubMetrics(new Metrics("baz"));

        assertEquals("empty metrics", 0, measurement.getValue().intValue());
    }

    public void testAccept() throws Exception {
        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        
        visited = null;
        measurement.accept(this);
        assertSame(measurement, visited);
    }

    public void testEmpty() throws Exception {
        measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);

        assertTrue("Before AddSubMetrics()", measurement.isEmpty());
        
        metrics.addSubMetrics(new Metrics("foo"));

        assertFalse("After AddSubMetrics()", measurement.isEmpty());
    }
    
    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        // Do nothing
    }
    
    public void visitRatioMeasurement(RatioMeasurement measurement) {
        // Do nothing
    }
    
    public void visitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
        visited = measurement;
    }
    
    public void visitCounterMeasurement(CounterMeasurement measurement) {
        // Do nothing
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
        // Do nothing
    }
}
