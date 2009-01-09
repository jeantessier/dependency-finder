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

public class TestSubMetricsAccumulatorMeasurement extends TestCase implements MeasurementVisitor {
    private MeasurementDescriptor descriptor;
    private AccumulatorMeasurement measurement;
    private Metrics metrics;
    private Measurement visited;

    private MeasurementDescriptor nameList;
    private MeasurementDescriptor numberList;
    private MeasurementDescriptor counter;

    private Metrics m1;
    private Metrics m2;
    private Metrics m3;

    protected void setUp() throws Exception {
        m1 = new Metrics("m1");
        m2 = new Metrics("m2");
        m3 = new Metrics("m3");

        nameList = new MeasurementDescriptor();
        nameList.setShortName("NL");
        nameList.setLongName("name list");
        nameList.setClassFor(NameListMeasurement.class);

        numberList = new MeasurementDescriptor();
        numberList.setShortName("NbL");
        numberList.setLongName("number list");
        numberList.setClassFor(NameListMeasurement.class);

        counter = new MeasurementDescriptor();
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

        metrics = new Metrics("metrics");

        descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(SubMetricsAccumulatorMeasurement.class);
        descriptor.setCached(false);
    }

    public void testCreateFromMeasurementDescriptor() throws Exception {
        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);

        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(SubMetricsAccumulatorMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    public void testNullInit() throws Exception {
        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());
    }

    public void testEmptyInit() throws Exception {
        descriptor.setInitText("");

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());
    }

    public void testRawValues() throws Exception {
        descriptor.setInitText("NL");

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(4, measurement.getValue().intValue());
        assertTrue("\"abc\" not in " + measurement.getValues(), measurement.getValues().contains("abc"));
        assertTrue("\"def\" not in " + measurement.getValues(), measurement.getValues().contains("def"));
        assertTrue("\"ghi\" not in " + measurement.getValues(), measurement.getValues().contains("ghi"));
        assertTrue("\"jkl\" not in " + measurement.getValues(), measurement.getValues().contains("jkl"));
    }

    public void testCachedValues() throws Exception {
        descriptor.setInitText("NL");
        descriptor.setCached(true);

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());
    }

    public void testSingleFiltered() throws Exception {
        descriptor.setInitText("NL /a/");

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(1, measurement.getValue().intValue());
        assertTrue("\"abc\" not in " + measurement.getValues(), measurement.getValues().contains("abc"));
    }

    public void testMultiFilterFiltered() throws Exception {
        descriptor.setInitText("NL /a/\nNL /k/");

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(2, measurement.getValue().intValue());
        assertTrue("\"abc\" not in " + measurement.getValues(), measurement.getValues().contains("abc"));
        assertTrue("\"jkl\" not in " + measurement.getValues(), measurement.getValues().contains("jkl"));
    }

    public void testModifiedValues() throws Exception {
        descriptor.setInitText("NL /(a)/");

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(1, measurement.getValue().intValue());
        assertTrue("\"a\" not in " + measurement.getValues(), measurement.getValues().contains("a"));
    }

    public void testMultiMeasurements() throws Exception {
        descriptor.setInitText("NL /a/\nNbL /2/");

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(0, measurement.getValue().intValue());
        assertTrue(measurement.getValues().isEmpty());

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        assertEquals(3, measurement.getValue().intValue());
        assertTrue("\"abc\" not in " + measurement.getValues(), measurement.getValues().contains("abc"));
        assertTrue("\"123\" not in " + measurement.getValues(), measurement.getValues().contains("123"));
        assertTrue("\"248\" not in " + measurement.getValues(), measurement.getValues().contains("248"));
    }

    public void testAccept() throws Exception {
        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);

        visited = null;
        measurement.accept(this);
        assertSame(measurement, visited);
    }

    public void testEmpty() throws Exception {
        descriptor.setInitText("NL");

        measurement = (AccumulatorMeasurement) descriptor.createMeasurement(metrics);
        metrics.track(nameList.createMeasurement(metrics));

        Metrics submetrics = new Metrics("submetrics");
        submetrics.track(nameList.createMeasurement(submetrics));
        metrics.addSubMetrics(submetrics);
        
        assertTrue("Before Add()", measurement.isEmpty());

        submetrics.addToMeasurement("NL", "foo");

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
        // Do nothing
    }
    
    public void visitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
        visited = measurement;
    }

    public void visitSumMeasurement(SumMeasurement measurement) {
        // Do nothing
    }
}
