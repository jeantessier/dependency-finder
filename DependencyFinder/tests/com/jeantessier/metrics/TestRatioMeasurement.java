/*
 *  Copyright (c) 2001-2002, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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
	private RatioMeasurement measurement;
	private Metrics metrics;
	private Measurement visited;
	
	private Measurement m1;
	private Measurement m2;

	public TestRatioMeasurement(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		metrics = new Metrics("foobar");

		measurement = new RatioMeasurement(null, metrics, "base\ndivider");

		m1 = new CounterMeasurement(null, metrics, null);
		m2 = new CounterMeasurement(null, metrics, null);

		metrics.Track("base", m1);
		metrics.Track("divider", m2);
	}

	public void testMeasurementDescriptor() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(RatioMeasurement.class);

		measurement = (RatioMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement.Descriptor());
		assertEquals(RatioMeasurement.class, measurement.Descriptor().Class());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
	}

	public void testCreateFromMeasurementDescriptor() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(RatioMeasurement.class);

		measurement = (RatioMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement);
		assertEquals(descriptor, measurement.Descriptor());
		assertSame(descriptor, measurement.Descriptor());
		assertEquals(RatioMeasurement.class, measurement.getClass());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());

		assertNull(((RatioMeasurement) measurement).BaseName());
		assertNull(((RatioMeasurement) measurement).DividerName());
		assertTrue(Double.isNaN(((RatioMeasurement) measurement).doubleValue()));
	}
	
	public void testCreateAndInitFromMeasurementDescriptor() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(RatioMeasurement.class);
		descriptor.InitText("base\ndivider");

		measurement = (RatioMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement);
		assertEquals(descriptor, measurement.Descriptor());
		assertSame(descriptor, measurement.Descriptor());
		assertEquals(RatioMeasurement.class, measurement.getClass());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
		assertEquals("base", ((RatioMeasurement) measurement).BaseName());
		assertEquals("divider", ((RatioMeasurement) measurement).DividerName());

		assertTrue(Double.isNaN(((RatioMeasurement) measurement).doubleValue()));
	}

	public void testCreate() {
		measurement = new RatioMeasurement(null, null, null);
		
		assertNull(measurement.BaseName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.BaseDispose());
		assertNull(measurement.DividerName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.DividerDispose());

		measurement = new RatioMeasurement(null, null, "base\ndivider");

		assertEquals("base",    measurement.BaseName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.BaseDispose());
		assertEquals("divider", measurement.DividerName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.DividerDispose());

		measurement = new RatioMeasurement(null, null, "base");

		assertNull(measurement.BaseName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.BaseDispose());
		assertNull(measurement.DividerName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.DividerDispose());

		measurement = new RatioMeasurement(null, null, null);

		assertNull(measurement.BaseName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.BaseDispose());
		assertNull(measurement.DividerName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.DividerDispose());

		measurement = new RatioMeasurement(null, null, "foo\nbar");

		assertEquals("foo", measurement.BaseName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.BaseDispose());
		assertEquals("bar", measurement.DividerName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.DividerDispose());

		measurement = new RatioMeasurement(null, null, "foo\nbar");

		assertEquals("foo", measurement.BaseName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.BaseDispose());
		assertEquals("bar", measurement.DividerName());
		assertEquals(StatisticalMeasurement.DISPOSE_IGNORE, measurement.DividerDispose());

		measurement = new RatioMeasurement(null, null, "foo DISPOSE_MINIMUM\nbar DISPOSE_AVERAGE");

		assertEquals("foo", measurement.BaseName());
		assertEquals(StatisticalMeasurement.DISPOSE_MINIMUM, measurement.BaseDispose());
		assertEquals("bar", measurement.DividerName());
		assertEquals(StatisticalMeasurement.DISPOSE_AVERAGE, measurement.DividerDispose());
	}

	public void testStatistical() {
		Metrics c  = new Metrics("foobar");
		Metrics m1 = new Metrics("foo");
		Metrics m2 = new Metrics("bar");

		c.AddSubMetrics(m1);
		c.AddSubMetrics(m2);

		m1.Track("base",    new CounterMeasurement(null, null, null));
		m1.Track("divider", new CounterMeasurement(null, null, null));
		m2.Track("base",    new CounterMeasurement(null, null, null));
		m2.Track("divider", new CounterMeasurement(null, null, null));

		m1.AddToMeasurement("base",    1);
		m1.AddToMeasurement("divider", 2);
		m2.AddToMeasurement("base",    3);
		m2.AddToMeasurement("divider", 4);

		c.Track("base",    new StatisticalMeasurement(null, c, "base"));
		c.Track("divider", new StatisticalMeasurement(null, c, "divider"));
		
		measurement = new RatioMeasurement(null, c, "base DISPOSE_MINIMUM\ndivider DISPOSE_MINIMUM");
		assertEquals(0.5, measurement.doubleValue(), 0.01);
		
		measurement = new RatioMeasurement(null, c, "base DISPOSE_AVERAGE\ndivider DISPOSE_AVERAGE");
		assertEquals(2.0 / 3.0, measurement.doubleValue(), 0.01);
		
		measurement = new RatioMeasurement(null, c, "base DISPOSE_AVERAGE\ndivider DISPOSE_NB_DATA_POINTS");
		assertEquals(1.0, measurement.doubleValue(), 0.01);
	}
	
	public void testNormal() {
		m1.Add(10);
		m2.Add(1);

		assertEquals(10 / 1, measurement.intValue());
		assertEquals(10 / 1, measurement.doubleValue(), 0.01);
		assertEquals(10 / 1, measurement.Value().intValue());

		m2.Add(1);

		assertEquals(10 / 2, measurement.intValue());
		assertEquals(10 / 2, measurement.doubleValue(), 0.01);
		assertEquals(10 / 2, measurement.Value().intValue());

		m1.Add(m1.Value());

		assertEquals(20 / 2, measurement.intValue());
		assertEquals(20 / 2, measurement.doubleValue(), 0.01);
		assertEquals(20 / 2, measurement.Value().intValue());
	}
	
	public void testDevideByZero() {
		assertTrue("0/0 not NaN", Double.isNaN(measurement.Value().doubleValue()));

		m1.Add(1);

		assertTrue("1/0 not infitity", Double.isInfinite(measurement.Value().doubleValue()));

		m1.Add(-2);

		assertTrue("-1/0 not infitity", Double.isInfinite(measurement.Value().doubleValue()));
	}
	
	public void testZeroDevidedBy() {
		assertTrue("0/0 not NaN", Double.isNaN(measurement.Value().doubleValue()));

		m2.Add(1);

		assertEquals("0/1 not zero", 0.0, measurement.Value().doubleValue(), 0.01);

		m2.Add(-2);

		assertEquals("0/-1 not zero", 0.0, measurement.Value().doubleValue(), 0.01);
	}

	public void testInUndefinedRange() {
		m2.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(2);
		
		assertTrue(measurement.InRange());
	}

	public void testInOpenRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(RatioMeasurement.class);

		measurement = (RatioMeasurement) descriptor.CreateMeasurement(metrics);
		
		m2.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(2);

		assertTrue(measurement.InRange());
	}

	public void testInLowerBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(RatioMeasurement.class);
		descriptor.InitText("base\ndivider");
		descriptor.LowerThreshold(new Integer(1));

		measurement = (RatioMeasurement) descriptor.CreateMeasurement(metrics);
		
		m2.Add(1);
		
		assertTrue(!measurement.InRange());

		m1.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(2);
		
		assertTrue(measurement.InRange());
	}

	public void testInUpperBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(RatioMeasurement.class);
		descriptor.InitText("base\ndivider");
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (RatioMeasurement) descriptor.CreateMeasurement(metrics);
		
		m2.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(2);
		
		assertTrue(!measurement.InRange());
	}

	public void testInBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(RatioMeasurement.class);
		descriptor.InitText("base\ndivider");
		descriptor.LowerThreshold(new Integer(1));
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (RatioMeasurement) descriptor.CreateMeasurement(metrics);
		
		m2.Add(1);
		
		assertTrue(!measurement.InRange());

		m1.Add(1);
		
		assertTrue(measurement.InRange());

		m1.Add(2);
		
		assertTrue(!measurement.InRange());
	}

	public void testAccept() {
		visited = null;
		measurement.Accept(this);
		assertSame(measurement, visited);
	}
	
	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitRatioMeasurement(RatioMeasurement measurement) {
		visited = measurement;
	}
	
	public void VisitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitCounterMeasurement(CounterMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitAccumulatorMeasurement(AccumulatorMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitSumMeasurement(SumMeasurement measurement) {
		// Do nothing
	}
}
