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

public class TestSubMetricsAccumulatorMeasurement {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    
    private final Metrics metrics = new Metrics("metrics");
    private final MeasurementDescriptor descriptor = new MeasurementDescriptor();

    private final MeasurementDescriptor nameList = new MeasurementDescriptor();
    private final MeasurementDescriptor numberList = new MeasurementDescriptor();
    private final MeasurementDescriptor counter = new MeasurementDescriptor();

    private final Metrics m1 = new Metrics("m1");
    private final Metrics m2 = new Metrics("m2");
    private final Metrics m3 = new Metrics("m3");

    private SubMetricsAccumulatorMeasurement measurement;

    @BeforeEach
    void setUp() throws Exception {
        nameList.setShortName("NL");
        nameList.setLongName("name list");
        nameList.setClassFor(NameListMeasurement.class);

        numberList.setShortName("NbL");
        numberList.setLongName("number list");
        numberList.setClassFor(NameListMeasurement.class);

        counter.setShortName("NL");
        counter.setLongName("counter");
        counter.setClassFor(CounterMeasurement.class);

        m1.track(nameList.createMeasurement(m1));
        m1.addToMeasurement("NL", "abc");
        m1.addToMeasurement("NL", "def");
        m1.addToMeasurement("NL", "ghi");

        m1.track(numberList.createMeasurement(m1));
        m1.addToMeasurement("NbL", "123");
        m1.addToMeasurement("NbL", "456");
        m1.addToMeasurement("NbL", "789");

        m2.track(nameList.createMeasurement(m2));
        m2.addToMeasurement("NL", "jkl");
        m2.addToMeasurement("NL", "abc");

        m2.track(numberList.createMeasurement(m2));
        m2.addToMeasurement("NbL", "159");
        m2.addToMeasurement("NbL", "248");

        m3.track(counter.createMeasurement(m3));
        m3.addToMeasurement("NL", 1);

        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SubMetricsAccumulatorMeasurement.class);
        descriptor.setCached(false);
    }

    @Test
    void testCreateFromMeasurementDescriptor() throws Exception {
        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);

        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(SubMetricsAccumulatorMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    @Test
    void testNullInit() throws Exception {
        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());
    }

    @Test
    void testEmptyInit() throws Exception {
        descriptor.setInitText("");

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());
    }

    @Test
    void testRawValues() throws Exception {
        descriptor.setInitText("NL");

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(4, measurement.getValue().intValue());
        assertTrue(measurement.getValues().contains("abc"), "\"abc\" not in " + measurement.getValues());
        assertTrue(measurement.getValues().contains("def"), "\"def\" not in " + measurement.getValues());
        assertTrue(measurement.getValues().contains("ghi"), "\"ghi\" not in " + measurement.getValues());
        assertTrue(measurement.getValues().contains("jkl"), "\"jkl\" not in " + measurement.getValues());
    }

    @Test
    void testCachedValues() throws Exception {
        descriptor.setInitText("NL");
        descriptor.setCached(true);

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());
    }

    @Test
    void testSingleFiltered() throws Exception {
        descriptor.setInitText("NL /a/");

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(1, measurement.getValue().intValue());
        assertTrue(measurement.getValues().contains("abc"), "\"abc\" not in " + measurement.getValues());
    }

    @Test
    void testMultiFilterFiltered() throws Exception {
        descriptor.setInitText("NL /a/\nNL /k/");

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(2, measurement.getValue().intValue());
        assertTrue(measurement.getValues().contains("abc"), "\"abc\" not in " + measurement.getValues());
        assertTrue(measurement.getValues().contains("jkl"), "\"jkl\" not in " + measurement.getValues());
    }

    @Test
    void testModifiedValues() throws Exception {
        descriptor.setInitText("NL /(a)/");

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(1, measurement.getValue().intValue());
        assertTrue(measurement.getValues().contains("a"), "\"a\" not in " + measurement.getValues());
    }

    @Test
    void testMultiMeasurements() throws Exception {
        descriptor.setInitText("NL /a/\nNbL /2/");

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(3, measurement.getValue().intValue());
        assertTrue(measurement.getValues().contains("abc"), "\"abc\" not in " + measurement.getValues());
        assertTrue(measurement.getValues().contains("123"), "\"123\" not in " + measurement.getValues());
        assertTrue(measurement.getValues().contains("248"), "\"248\" not in " + measurement.getValues());
    }

    @Test
    void testAccept() throws Exception {
        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);

        var visitor = context.mock(MeasurementVisitor.class);

        context.checking(new Expectations() {{
            oneOf (visitor).visitSubMetricsAccumulatorMeasurement(measurement);
        }});

        measurement.accept(visitor);
    }

    @Test
    void testEmpty() throws Exception {
        descriptor.setInitText("NL");

        measurement = (SubMetricsAccumulatorMeasurement) descriptor.createMeasurement(metrics);
        metrics.track(nameList.createMeasurement(metrics));

        Metrics submetrics = new Metrics("submetrics");
        submetrics.track(nameList.createMeasurement(submetrics));
        metrics.addSubMetrics(submetrics);
        
        assertTrue(measurement.isEmpty(), "Before Add()");

        submetrics.addToMeasurement("NL", "foo");

        assertFalse(measurement.isEmpty(), "After Add()");
    }
}
