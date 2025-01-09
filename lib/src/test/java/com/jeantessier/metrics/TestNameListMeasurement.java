/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.jmock.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestNameListMeasurement {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    
    private NameListMeasurement measurement = new NameListMeasurement(null, null, null);
    
    @Test
    void testMeasurementDescriptor() throws Exception {
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

    @Test
    void testCreateFromMeasurementDescriptor() throws Exception {
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

    @Test
    void testCreateSet() {
        measurement = new NameListMeasurement(null, null, "SET");

        measurement.add("abc");
        measurement.add("abc");

        assertEquals(1, measurement.getValue().intValue());
    }
    
    @Test
    void testCreateList() {
        measurement = new NameListMeasurement(null, null, "LIST");

        measurement.add("abc");
        measurement.add("abc");

        assertEquals(2, measurement.getValue().intValue());
    }

    @Test
    void testCreateDefault() {
        measurement.add("abc");
        measurement.add("abc");

        assertEquals(1, measurement.getValue().intValue());
    }

    @Test
    void testAddObject() {
        Object o = new Object();

        assertEquals(0, measurement.getValue().intValue(), "zero");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "zero");
        assertEquals(0, measurement.getValue().intValue(), "zero");
        assertEquals(0, measurement.getValues().size(), "zero");
        assertTrue(measurement.isEmpty(), "zero");

        measurement.add(o);

        assertEquals(0, measurement.getValue().intValue(), "one");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "one");
        assertEquals(0, measurement.getValue().intValue(), "one");
        assertEquals(0, measurement.getValues().size(), "zero");
        assertTrue(measurement.isEmpty(), "zero");
    }

    @Test
    void testAddString() {
        String s1 = "foo";
        String s2 = "bar";

        assertEquals(0, measurement.getValue().intValue(), "zero");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "zero");
        assertEquals(0, measurement.getValue().intValue(), "zero");

        measurement.add(s1);
        assertEquals(1, measurement.getValue().intValue(), "one");
        assertEquals(1.0, measurement.getValue().doubleValue(), 0.01, "one");
        assertEquals(1, measurement.getValue().intValue(), "one");

        measurement.add(s2);
        assertEquals(2, measurement.getValue().intValue(), "two");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01, "two");
        assertEquals(2, measurement.getValue().intValue(), "two");

        measurement.add(s1);
        assertEquals(2, measurement.getValue().intValue(), "three");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01, "three");
        assertEquals(2, measurement.getValue().intValue(), "three");
    }

    @Test
    void testValues() {
        String s1 = "foo";
        String s2 = "bar";

        measurement.add(s1);
        measurement.add(s2);

        assertEquals(2, measurement.getValues().size(), "size");
        assertTrue(measurement.getValues().contains(s1), "Missing s1");
        assertTrue(measurement.getValues().contains(s2), "Missing s2");

        assertThrows(UnsupportedOperationException.class, () -> measurement.getValues().remove(s2));
    }

    @Test
    void testAddInt() {
        assertEquals(0, measurement.getValue().intValue(), "zero");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "zero");
        assertEquals(0, measurement.getValue().intValue(), "zero");

        measurement.add(1);
        assertEquals(0, measurement.getValue().intValue(), "one");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "one");
        assertEquals(0, measurement.getValue().intValue(), "one");

        measurement.add(1);
        assertEquals(0, measurement.getValue().intValue(), "two");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "two");
        assertEquals(0, measurement.getValue().intValue(), "two");
    }

    @Test
    void testAddFloat() {
        assertEquals(0, measurement.getValue().intValue(), "zero");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "zero");
        assertEquals(0, measurement.getValue().intValue(), "zero");

        measurement.add(1.0);
        assertEquals(0, measurement.getValue().intValue(), "one");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "one");
        assertEquals(0, measurement.getValue().intValue(), "one");

        measurement.add(1.0);
        assertEquals(0, measurement.getValue().intValue(), "two");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01, "two");
        assertEquals(0, measurement.getValue().intValue(), "two");
    }

    @Test
    void testInUndefinedRange() {
        assertTrue(measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");

        assertTrue(measurement.isInRange());
    }

    @Test
    void testInOpenRange() throws Exception {
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

    @Test
    void testInLowerBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);
        descriptor.setLowerThreshold(1.0);

        measurement = (NameListMeasurement) descriptor.createMeasurement();

        assertFalse(measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");
        
        assertTrue(measurement.isInRange());
    }

    @Test
    void testInUpperBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);
        descriptor.setUpperThreshold(1.5);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertTrue(measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");

        assertFalse(measurement.isInRange());
    }

    @Test
    void testInBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);
        descriptor.setLowerThreshold(1.0);
        descriptor.setUpperThreshold(1.5);

        measurement = (NameListMeasurement) descriptor.createMeasurement();

        assertFalse(measurement.isInRange());

        measurement.add("foo");
        
        assertTrue(measurement.isInRange());

        measurement.add("bar");
        measurement.add("baz");

        assertFalse(measurement.isInRange());
    }

    @Test
    void testAccept() {
        var visitor = context.mock(MeasurementVisitor.class);
        
        context.checking(new Expectations() {{
            oneOf (visitor).visitNameListMeasurement(measurement);
        }});
        
        measurement.accept(visitor);
    }

    @Test
    void testEmpty() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(NameListMeasurement.class);

        measurement = (NameListMeasurement) descriptor.createMeasurement();
        
        assertTrue(measurement.isEmpty(), "Before Add()");

        measurement.add("foo");

        assertFalse(measurement.isEmpty(), "After Add()");
    }
}
