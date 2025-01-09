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

public class TestSingleValueMeasurement {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private SingleValueMeasurement measurement = new SingleValueMeasurement(null, null, null);

    @Test
    void testMeasurementDescriptor() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SingleValueMeasurement.class);

        measurement = (SingleValueMeasurement) descriptor.createMeasurement();

        assertNotNull(measurement.getDescriptor());
        assertEquals(SingleValueMeasurement.class, measurement.getDescriptor().getClassFor());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    @Test
    void testCreateFromMeasurementDescriptor() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SingleValueMeasurement.class);

        measurement = (SingleValueMeasurement) descriptor.createMeasurement();

        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(SingleValueMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    @Test
    void testCreateNumber() {
        measurement = new SingleValueMeasurement(null, null, "2");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01);

        measurement = new SingleValueMeasurement(null, null, "2.0");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01);

        measurement = new SingleValueMeasurement(null, null, "-2.5");
        assertEquals(-2.5, measurement.getValue().doubleValue(), 0.01);

        measurement = new SingleValueMeasurement(null, null, " 2.0 ");
        assertEquals(2.0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testCreateInvalid() {
        measurement = new SingleValueMeasurement(null, null, null);
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);

        measurement = new SingleValueMeasurement(null, null, "foobar");
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testCreateDefault() {
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testAddObject() {
        measurement.add(new Object());

        assertEquals(0, measurement.getValue().intValue());
        assertEquals(0, measurement.getValue().doubleValue(), 0.01);
        assertEquals(0, measurement.getValue().intValue());

        measurement.add(new Object());

        assertEquals(0, measurement.getValue().intValue());
        assertEquals(0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testAddNumber() {
        measurement.add(Double.valueOf(1));

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        measurement.add(Double.valueOf(0.5));

        assertEquals(0, measurement.getValue().intValue());
        assertEquals(0.5, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testAddInt() {
        measurement.add(1);

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        measurement.add(2);

        assertEquals(2, measurement.getValue().intValue());
        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
        assertEquals(2, measurement.getValue().intValue());
    }

    @Test
    void testAddFloat() {
        measurement.add(1.0);

        assertEquals(1, measurement.getValue().intValue());
        assertEquals(1, measurement.getValue().doubleValue(), 0.01);
        assertEquals(1, measurement.getValue().intValue());

        measurement.add(0.5);

        assertEquals(0, measurement.getValue().intValue());
        assertEquals(0.5, measurement.getValue().doubleValue(), 0.01);
        assertEquals(0, measurement.getValue().intValue());
    }

    @Test
    void testInUndefinedRange() {
        assertTrue(measurement.isInRange());

        measurement.add(1);

        assertTrue(measurement.isInRange());

        measurement.add(2);

        assertTrue(measurement.isInRange());
    }

    @Test
    void testInOpenRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SingleValueMeasurement.class);

        measurement = (SingleValueMeasurement) descriptor.createMeasurement();

        assertTrue(measurement.isInRange());

        measurement.add(1);

        assertTrue(measurement.isInRange());

        measurement.add(2);

        assertTrue(measurement.isInRange());
    }

    @Test
    void testInLowerBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SingleValueMeasurement.class);
        descriptor.setLowerThreshold(1.0);

        measurement = (SingleValueMeasurement) descriptor.createMeasurement();

        assertFalse(measurement.isInRange());

        measurement.add(1);

        assertTrue(measurement.isInRange());

        measurement.add(2);

        assertTrue(measurement.isInRange());
    }

    @Test
    void testInUpperBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SingleValueMeasurement.class);
        descriptor.setUpperThreshold(1.5);

        measurement = (SingleValueMeasurement) descriptor.createMeasurement();

        assertTrue(measurement.isInRange());

        measurement.add(1);

        assertTrue(measurement.isInRange());

        measurement.add(2);

        assertFalse(measurement.isInRange());
    }

    @Test
    void testInBoundRange() throws Exception {
        MeasurementDescriptor descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SingleValueMeasurement.class);
        descriptor.setLowerThreshold(1.0);
        descriptor.setUpperThreshold(1.5);

        measurement = (SingleValueMeasurement) descriptor.createMeasurement();

        assertFalse(measurement.isInRange());

        measurement.add(1);

        assertTrue(measurement.isInRange());

        measurement.add(2);

        assertFalse(measurement.isInRange());
    }

    @Test
    void testAccept() {
        var visitor = context.mock(MeasurementVisitor.class);

        context.checking(new Expectations() {{
            oneOf (visitor).visitSingleValueMeasurement(measurement);
        }});

        measurement.accept(visitor);
    }

    @Test
    void testEmpty() {
        assertTrue(measurement.isEmpty(), "Before Add()");

        measurement.add(1);

        assertFalse(measurement.isEmpty(), "After Add(1)");

        measurement.add(-1);

        assertFalse(measurement.isEmpty(), "After Add(-1)");
    }
}
