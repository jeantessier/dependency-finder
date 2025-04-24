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

import org.jmock.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestSumMeasurement {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final Metrics metrics = new Metrics("foobar");
    private final MeasurementDescriptor descriptor = new MeasurementDescriptor();

    private SumMeasurement measurement;

    @BeforeEach
    void setUp() {
        descriptor.setShortName("foo");
        descriptor.setLongName("FOO");
        descriptor.setClassFor(SumMeasurement.class);
        descriptor.setCached(false);
    }

    @Test
    void testMeasurementDescriptor() throws Exception {
        measurement = (SumMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement.getDescriptor());
        assertEquals(SumMeasurement.class, measurement.getDescriptor().getClassFor());
        assertEquals("foo", measurement.getShortName());
        assertEquals("FOO", measurement.getLongName());
    }

    @Test
    void testCreateFromMeasurementDescriptor() throws Exception {
        measurement = (SumMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(SumMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("FOO", measurement.getLongName());
    }

    @Test
    void testCreateDefault() {
        measurement = new SumMeasurement(descriptor, null,  null);

        assertEquals(0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testEmptyInitText() throws Exception {
        descriptor.setInitText("");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testEmptyLineInitText() throws Exception {
        descriptor.setInitText("\n");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testDashInitText() throws Exception {
        descriptor.setInitText("-");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testConstant() throws Exception {
        descriptor.setInitText("2");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testConstantAndEmptyLine() throws Exception {
        descriptor.setInitText("\n2\n");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testAddition() throws Exception {
        descriptor.setInitText("1\n1");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testNegative() throws Exception {
        descriptor.setInitText("-2");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(-2, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testSubtraction() throws Exception {
        descriptor.setInitText("2\n-1");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(1, measurement.getValue().doubleValue(), 0.01);

        descriptor.setInitText("1\n-2");

        measurement = (SumMeasurement) descriptor.createMeasurement();

        assertEquals(-1, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testSubMeasurement() throws Exception {
        descriptor.setInitText("bar");

        metrics.track("bar", new CounterMeasurement(null, metrics, "2"));
        
        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testStatisticalMeasurement() throws Exception {
        descriptor.setInitText("bar DISPOSE_SUM");

        metrics.track("bar", new StatisticalMeasurement(null, metrics, "bar"));

        Metrics submetrics = new Metrics("submetrics");
        submetrics.track("bar", new CounterMeasurement(null, submetrics, "2"));
        metrics.addSubMetrics(submetrics);
        
        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testAddMeasurements() throws Exception {
        descriptor.setInitText("bar\nbaz");

        metrics.track("bar", new CounterMeasurement(null, metrics, "1"));
        metrics.track("baz", new CounterMeasurement(null, metrics, "1"));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(2, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testSubtractMeasurements() throws Exception {
        descriptor.setInitText("bar\n-baz");

        metrics.track("bar", new CounterMeasurement(null, metrics, "1"));
        metrics.track("baz", new CounterMeasurement(null, metrics, "2"));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(-1, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testInUndefinedRange() throws Exception {
        descriptor.setInitText("bar");

        metrics.track("bar", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);
        
        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);

        assertTrue(measurement.isInRange());
    }

    @Test
    void testInOpenRange() throws Exception {
        descriptor.setInitText("bar");

        metrics.track("bar", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);
        
        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);

        assertTrue(measurement.isInRange());
    }

    @Test
    void testInLowerBoundRange() throws Exception {
        descriptor.setInitText("bar");
        descriptor.setLowerThreshold(1.0);

        metrics.track("bar", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(0, measurement.getValue().intValue());
        assertFalse(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);

        assertEquals(1, measurement.getValue().intValue());
        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);

        assertEquals(2, measurement.getValue().intValue());
        assertTrue(measurement.isInRange());
    }

    @Test
    void testInUpperBoundRange() throws Exception {
        descriptor.setInitText("bar");
        descriptor.setUpperThreshold(1.5);

        metrics.track("bar", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);
        
        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);

        assertFalse(measurement.isInRange());
    }

    @Test
    void testInBoundRange() throws Exception {
        descriptor.setInitText("bar");
        descriptor.setLowerThreshold(1.0);
        descriptor.setUpperThreshold(1.5);

        metrics.track("bar", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertFalse(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);
        
        assertTrue(measurement.isInRange());

        metrics.addToMeasurement("bar", 1);

        assertFalse(measurement.isInRange());
    }

    @Test
    void testCachedValue() throws Exception {
        descriptor.setInitText("bar");
        descriptor.setCached(true);

        metrics.track("bar", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(0, measurement.getValue().doubleValue(), 0.01);

        metrics.addToMeasurement("bar", 1);

        assertEquals(0, measurement.getValue().doubleValue(), 0.01);
    }
    
    @Test
    void testAccept() {
        measurement = new SumMeasurement(null, null, null);

        var visitor = context.mock(MeasurementVisitor.class);

        context.checking(new Expectations() {{
            oneOf (visitor).visitSumMeasurement(measurement);
        }});

        measurement.accept(visitor);
    }

    @Test
    void testEmptyWithOneMeasurement() throws Exception {
        descriptor.setInitText("bar");

        metrics.track("bar", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(measurement.isEmpty(), "Before Add()");

        metrics.addToMeasurement("bar", 1);
        
        assertFalse(measurement.isEmpty(), "After Add(1)");

        metrics.addToMeasurement("bar", -1);

        assertFalse(measurement.isEmpty(), "After Add(-1)");
    }

    @Test
    void testEmptyWithTwoMeasurements() throws Exception {
        descriptor.setInitText("bar\nbaz");

        metrics.track("bar", new CounterMeasurement(null, null, null));
        metrics.track("baz", new CounterMeasurement(null, null, null));

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertTrue(metrics.getMeasurement("bar").isEmpty(), "bar is not empty");
        assertTrue(metrics.getMeasurement("baz").isEmpty(), "baz is not empty");
        assertTrue(measurement.isEmpty(), "Before Add()");

        metrics.addToMeasurement("bar", 1);
        
        assertFalse(metrics.getMeasurement("bar").isEmpty(), "bar is empty");
        assertTrue(metrics.getMeasurement("baz").isEmpty(), "baz is not empty");
        assertFalse(measurement.isEmpty(), "After Add(1)");

        metrics.addToMeasurement("bar", -1);

        assertFalse(metrics.getMeasurement("bar").isEmpty(), "bar is empty");
        assertTrue(metrics.getMeasurement("baz").isEmpty(), "baz is not empty");
        assertFalse(measurement.isEmpty(), "After Add(-1)");
    }

    @Test
    void testEmptyWithConstant() throws Exception {
        descriptor.setInitText("2");

        measurement = (SumMeasurement) descriptor.createMeasurement(metrics);

        assertTrue(measurement.isEmpty(), "with constants");
    }
}
