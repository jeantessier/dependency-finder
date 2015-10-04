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

public class TestCounterMeasurement extends TestCase implements MeasurementVisitor {
    private CounterMeasurement measurement;
    private Measurement visited;
    
    protected void setUp() {
        measurement = new CounterMeasurement(null, null, null);
    }

    public void testMeasurementDescriptor() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement.getDescriptor());
        assertEquals(CounterMeasurement.class, measurement.getDescriptor().getClassFor());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    public void testCreateFromMeasurementDescriptor() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(CounterMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }
    
    public void testCreateNumber() {
        measurement = new CounterMeasurement(null, null, "2");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01);

        measurement = new CounterMeasurement(null, null, "2.0");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01);

        measurement = new CounterMeasurement(null, null, "-2.5");
        assertEquals(-2.5, measurement.getValue().doubleValue(), 0.01);

        measurement = new CounterMeasurement(null, null, " 2.0 ");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01);
    }
    
    public void testCreateInvalid() {
        measurement = new CounterMeasurement(null, null, null);
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);

        measurement = new CounterMeasurement(null, null, "foobar");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);
    }

    public void testCreateDefault() {
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);
    }
    
    public void testAddObject() {
        measurement.add(new Object());

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        measurement.add(new Object());

        assertEquals(2, measurement.getValue().intValue());
        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
    }
    
    public void testAddNumber() {
        measurement.add(new Integer(1));

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        measurement.add(new Float(0.5));

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1.5, measurement.getValue().doubleValue(), 0.01);
    }
    
    public void testAddInt() {
        measurement.add(1);

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        measurement.add(2);

        assertEquals(3, measurement.getValue().intValue());
        assertEquals(3, measurement.getValue().doubleValue(), 0.01);
        assertEquals(3, measurement.getValue().intValue());
    }
    
    public void testAddFloat() {
        measurement.add(1.0);

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        measurement.add(0.5);

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1.5, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());
    }

    public void testSubstract() {
        measurement.add(new Integer(-1));

        assertEquals(-1, measurement.getValue().intValue());
        assertEquals(-1, measurement.getValue().doubleValue(), 0.01);

        measurement.add(new Float(0.4));

        assertEquals(0, measurement.getValue().intValue());
        assertEquals(-0.6, measurement.getValue().doubleValue(), 0.01);

        measurement.add(new Float(0.1));

        assertEquals(0, measurement.getValue().intValue());
        assertEquals(-0.5, measurement.getValue().doubleValue(), 0.01);
    }

    public void testInUndefinedRange() {
        assertTrue(measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(measurement.isInRange());
    }

    public void testInOpenRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue(measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(measurement.isInRange());
    }

    public void testInLowerBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);
        descriptor.setLowerThreshold(new Integer(1));

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue(!measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(measurement.isInRange());
    }

    public void testInStringLowerBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);
        descriptor.setLowerThreshold("1");

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue(!measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(measurement.isInRange());
    }

    public void testInUpperBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);
        descriptor.setUpperThreshold(new Float(1.5));

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue(measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(!measurement.isInRange());
    }

    public void testInStringUpperBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);
        descriptor.setUpperThreshold("1.5");

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue(measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(!measurement.isInRange());
    }

    public void testInBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);
        descriptor.setLowerThreshold(new Integer(1));
        descriptor.setUpperThreshold(new Float(1.5));

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue(!measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(!measurement.isInRange());
    }

    public void testInStringBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);
        descriptor.setLowerThreshold("1");
        descriptor.setUpperThreshold("1.5");

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue(!measurement.isInRange());

        measurement.add(1);
        
        assertTrue(measurement.isInRange());

        measurement.add(2);
        
        assertTrue(!measurement.isInRange());
    }

    public void testAccept() {
        visited = null;
        measurement.accept(this);
        assertSame(measurement, visited);
    }

    public void testEmpty() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(CounterMeasurement.class);

        measurement = (CounterMeasurement) descriptor.createMeasurement();
        
        assertTrue("Before Add()", measurement.isEmpty());

        measurement.add(1);
        
        assertFalse("After Add(1)", measurement.isEmpty());

        measurement.add(-1);
        
        assertFalse("After Add(-1)", measurement.isEmpty());
    }
    
    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        // Do nothing
    }
    
    public void visitRatioMeasurement(RatioMeasurement measurement) {
        // Do nothing
    }
    
    public void visitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
        // Do nothing
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
        // Do nothing
    }
}
