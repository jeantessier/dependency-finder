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

public class TestRatioMeasurement extends TestCase implements MeasurementVisitor {
    private MeasurementDescriptor descriptor;
    private RatioMeasurement measurement;
    private Metrics metrics;
    private Measurement visited;
    
    private Measurement m1;
    private Measurement m2;

    protected void setUp() throws Exception {
        metrics = new Metrics("foobar");

        m1 = new CounterMeasurement(null, metrics, null);
        m2 = new CounterMeasurement(null, metrics, null);

        metrics.track("base", m1);
        metrics.track("divider", m2);

        descriptor = new MeasurementDescriptor();
        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(RatioMeasurement.class);
        descriptor.setInitText("base\ndivider");
        descriptor.setCached(false);
    }

    public void testMeasurementDescriptor() {
        measurement = new RatioMeasurement(descriptor, metrics, "base\ndivider");
        
        assertNotNull(measurement.getDescriptor());
        assertEquals(RatioMeasurement.class, measurement.getDescriptor().getClassFor());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    public void testCreateFromMeasurementDescriptor() throws Exception {
        descriptor.setInitText(null);

        measurement = (RatioMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(RatioMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());

        assertNull(measurement.getBaseName());
        assertNull(measurement.getDividerName());
        assertTrue(Double.isNaN(measurement.getValue().doubleValue()));
    }
    
    public void testCreateAndInitFromMeasurementDescriptor() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(RatioMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
        assertEquals("base", measurement.getBaseName());
        assertEquals("divider", measurement.getDividerName());

        assertTrue(Double.isNaN(measurement.getValue().doubleValue()));
    }

    public void testCreate() {
        measurement = new RatioMeasurement(null, null, null);
        
        assertNull(measurement.getBaseName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getBaseDispose());
        assertNull(measurement.getDividerName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getDividerDispose());

        measurement = new RatioMeasurement(null, null, "base\ndivider");

        assertEquals("base",    measurement.getBaseName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getBaseDispose());
        assertEquals("divider", measurement.getDividerName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getDividerDispose());

        measurement = new RatioMeasurement(null, null, "base");

        assertNull(measurement.getBaseName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getBaseDispose());
        assertNull(measurement.getDividerName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getDividerDispose());

        measurement = new RatioMeasurement(null, null, null);

        assertNull(measurement.getBaseName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getBaseDispose());
        assertNull(measurement.getDividerName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getDividerDispose());

        measurement = new RatioMeasurement(null, null, "foo\nbar");

        assertEquals("foo", measurement.getBaseName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getBaseDispose());
        assertEquals("bar", measurement.getDividerName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getDividerDispose());

        measurement = new RatioMeasurement(null, null, "foo\nbar");

        assertEquals("foo", measurement.getBaseName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getBaseDispose());
        assertEquals("bar", measurement.getDividerName());
        assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.getDividerDispose());

        measurement = new RatioMeasurement(null, null, "foo DISPOSE_MINIMUM\nbar DISPOSE_AVERAGE");

        assertEquals("foo", measurement.getBaseName());
        assertEquals(StatisticalMeasurement.DISPOSE_MINIMUM, measurement.getBaseDispose());
        assertEquals("bar", measurement.getDividerName());
        assertEquals(StatisticalMeasurement.DISPOSE_AVERAGE, measurement.getDividerDispose());
    }

    public void testStatistical() {
        Metrics c  = new Metrics("foobar");
        Metrics m1 = new Metrics("foo");
        Metrics m2 = new Metrics("bar");

        c.addSubMetrics(m1);
        c.addSubMetrics(m2);

        m1.track("base",    new CounterMeasurement(null, null, null));
        m1.track("divider", new CounterMeasurement(null, null, null));
        m2.track("base",    new CounterMeasurement(null, null, null));
        m2.track("divider", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("base",    1);
        m1.addToMeasurement("divider", 2);
        m2.addToMeasurement("base",    3);
        m2.addToMeasurement("divider", 4);

        c.track("base",    new StatisticalMeasurement(null, c, "base"));
        c.track("divider", new StatisticalMeasurement(null, c, "divider"));
        
        measurement = new RatioMeasurement(descriptor, c, "base DISPOSE_MINIMUM\ndivider DISPOSE_MINIMUM");
        assertEquals(0.5, measurement.getValue().doubleValue(), 0.01);
        
        measurement = new RatioMeasurement(descriptor, c, "base DISPOSE_AVERAGE\ndivider DISPOSE_AVERAGE");
        assertEquals(2.0 / 3.0, measurement.getValue().doubleValue(), 0.01);
        
        measurement = new RatioMeasurement(descriptor, c, "base DISPOSE_AVERAGE\ndivider DISPOSE_NB_DATA_POINTS");
        assertEquals(1.0, measurement.getValue().doubleValue(), 0.01);
    }
    
    public void testNormal() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m1.add(10);
        m2.add(1);

        assertEquals(10 / 1, measurement.getValue().intValue());

        m2.add(1);

        assertEquals(10 / 2, measurement.getValue().intValue());

        m1.add(m1.getValue());

        assertEquals(20 / 2, measurement.getValue().intValue());
    }
    
    public void testDevideByZero() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue("0/0 not NaN", Double.isNaN(measurement.getValue().doubleValue()));

        m1.add(1);

        assertTrue("1/0 not infitity", Double.isInfinite(measurement.getValue().doubleValue()));

        m1.add(-2);

        assertTrue("-1/0 not infitity", Double.isInfinite(measurement.getValue().doubleValue()));
    }
    
    public void testZeroDevidedBy() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue("0/0 not NaN", Double.isNaN(measurement.getValue().doubleValue()));

        m2.add(1);

        assertEquals("0/1 not zero", 0.0, measurement.getValue().doubleValue(), 0.01);

        m2.add(-2);

        assertEquals("0/-1 not zero", 0.0, measurement.getValue().doubleValue(), 0.01);
    }

    public void testInUndefinedRange() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);
        
        assertTrue(measurement.isInRange());
    }

    public void testInOpenRange() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);

        assertTrue(measurement.isInRange());
    }

    public void testInLowerBoundRange() throws Exception {
        descriptor.setLowerThreshold(new Integer(1));

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(!measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);
        
        assertTrue(measurement.isInRange());
    }

    public void testInUpperBoundRange() throws Exception {
        descriptor.setUpperThreshold(new Float(1.5));

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);
        
        assertTrue(!measurement.isInRange());
    }

    public void testInBoundRange() throws Exception {
        descriptor.setLowerThreshold(new Integer(1));
        descriptor.setUpperThreshold(new Float(1.5));

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(!measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);
        
        assertTrue(!measurement.isInRange());
    }

    public void testCachedValue() throws Exception {
        descriptor.setCached(true);

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue("0/0 not NaN", Double.isNaN(measurement.getValue().doubleValue()));

        m2.add(1);
        
        assertTrue("cached 0/0 not NaN", Double.isNaN(measurement.getValue().doubleValue()));
    }

    public void testAccept() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        visited = null;
        measurement.accept(this);
        assertSame(measurement, visited);
    }

    public void testEmpty() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);

        assertEquals("base == 0", 0, m1.getValue().intValue());
        assertEquals("divider == 0", 0, m2.getValue().intValue());
        assertTrue("0/0", measurement.isEmpty());

        m1.add(1);

        assertEquals("base != 1", 1, m1.getValue().intValue());
        assertEquals("divider != 0", 0, m2.getValue().intValue());
        assertTrue("1/0", measurement.isEmpty());

        m2.add(1);

        assertEquals("base != 1", 1, m1.getValue().intValue());
        assertEquals("divider != 1", 1, m2.getValue().intValue());
        assertFalse("1/1", measurement.isEmpty());

        m1.add(-1);

        assertEquals("base != 0", 0, m1.getValue().intValue());
        assertEquals("divider != 1", 1, m2.getValue().intValue());
        assertFalse("0/1", measurement.isEmpty());

        m2.add(-1);

        assertEquals("base != 0", 0, m1.getValue().intValue());
        assertEquals("divider != 0", 0, m2.getValue().intValue());
        assertTrue("0/0", measurement.isEmpty());
    }
    
    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        // Do nothing
    }
    
    public void visitRatioMeasurement(RatioMeasurement measurement) {
        visited = measurement;
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
        // Do nothing
    }

    public void visitSumMeasurement(SumMeasurement measurement) {
        // Do nothing
    }
}
