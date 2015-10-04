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

public class TestNameListMeasurement extends TestCase implements MeasurementVisitor {
    private NameListMeasurement measurement;
    private Measurement visited;
    
    protected void setUp() {
        measurement = new NameListMeasurement(null, null, null);
    }

    public void testMeasurementDescriptor() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement.getDescriptor());
        assertEquals(NameListMeasurement.class, measurement.getDescriptor().getClassFor());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    public void testCreateFromMeasurementDescriptor() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(NameListMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    public void testCreateSet() {
        measurement = new NameListMeasurement(null, null, "SET");

        measurement.add("abc");
        measurement.add("abc");

        assertEquals(1, measurement.getValue().intValue());
    }
    
    public void testCreateList() {
        measurement = new NameListMeasurement(null, null, "LIST");

        measurement.add("abc");
        measurement.add("abc");

        assertEquals(2, measurement.getValue().intValue());
    }

    public void testCreateDefault() {
        measurement.add("abc");
        measurement.add("abc");

        assertEquals(1, measurement.getValue().intValue());
    }

    public void testAddObject() {
        Object o = new Object();

        assertEquals("zero", 0, measurement.getValue().intValue());
        assertEquals("zero", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("zero", 0, measurement.getValue().intValue());
        assertEquals("zero", 0, measurement.getValues().size());
        assertTrue("zero", measurement.isEmpty());

        measurement.add(o);

        assertEquals("one", 0, measurement.getValue().intValue());
        assertEquals("one", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("one", 0, measurement.getValue().intValue());
        assertEquals("zero", 0, measurement.getValues().size());
        assertTrue("zero", measurement.isEmpty());
    }

    public void testAddString() {
        String s1 = "foo";
        String s2 = "bar";

        assertEquals("zero", 0, measurement.getValue().intValue());
        assertEquals("zero", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("zero", 0, measurement.getValue().intValue());

        measurement.add(s1);
        assertEquals("one", 1, measurement.getValue().intValue());
        assertEquals("one", 1.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("one", 1, measurement.getValue().intValue());

        measurement.add(s2);
        assertEquals("two", 2, measurement.getValue().intValue());
        assertEquals("two", 2.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("two", 2, measurement.getValue().intValue());

        measurement.add(s1);
        assertEquals("three", 2, measurement.getValue().intValue());
        assertEquals("three", 2.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("three", 2, measurement.getValue().intValue());
    }

    public void testValues() {
        String s1 = "foo";
        String s2 = "bar";

        measurement.add(s1);
        measurement.add(s2);

        assertEquals("size", 2, measurement.getValues().size());
        assertTrue("Missing s1", measurement.getValues().contains(s1));
        assertTrue("Missing s2", measurement.getValues().contains(s2));

        try {
            measurement.getValues().add(s2);
            fail("Was allowed to modify the Values() collection");
        } catch (UnsupportedOperationException ex) {
            // Ignore
        }
    }

    public void testAddInt() {
        assertEquals("zero", 0, measurement.getValue().intValue());
        assertEquals("zero", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("zero", 0, measurement.getValue().intValue());

        measurement.add(1);
        assertEquals("one", 0, measurement.getValue().intValue());
        assertEquals("one", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("one", 0, measurement.getValue().intValue());

        measurement.add(1);
        assertEquals("two", 0, measurement.getValue().intValue());
        assertEquals("two", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("two", 0, measurement.getValue().intValue());
    }

    public void testAddFloat() {
        assertEquals("zero", 0, measurement.getValue().intValue());
        assertEquals("zero", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("zero", 0, measurement.getValue().intValue());

        measurement.add(1.0);
        assertEquals("one", 0, measurement.getValue().intValue());
        assertEquals("one", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("one", 0, measurement.getValue().intValue());

        measurement.add(1.0);
        assertEquals("two", 0, measurement.getValue().intValue());
        assertEquals("two", 0.0, measurement.getValue().doubleValue(), 0.01);
        assertEquals("two", 0, measurement.getValue().intValue());
    }

    public void testInUndefinedRange() {
        assertTrue(measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");

        assertTrue(measurement.isInRange());
    }

    public void testInOpenRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertTrue(measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");

        assertTrue(measurement.isInRange());
    }

    public void testInLowerBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);
        descriptor.setLowerThreshold(1);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertTrue(!measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");
        
        assertTrue(measurement.isInRange());
    }

    public void testInUpperBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);
        descriptor.setUpperThreshold(new Float(1.5));

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertTrue(measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");
        
        assertTrue(!measurement.isInRange());
    }

    public void testInBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);
        descriptor.setLowerThreshold(1);
        descriptor.setUpperThreshold(1.5);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertTrue(!measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");
        
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
        descriptor.setClassFor(NameListMeasurement.class);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertTrue("Before Add()", measurement.isEmpty());

        measurement.add("foo");

        assertFalse("After Add()", measurement.isEmpty());
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
        // Do nothing
    }
    
    public void visitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
        // Do nothing
    }
        
    public void visitNameListMeasurement(NameListMeasurement measurement) {
        visited = measurement;
    }
    
    public void visitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
        // Do nothing
    }

    public void visitSumMeasurement(SumMeasurement measurement) {
        // Do nothing
    }
}
