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

public class TestSumMeasurement extends TestCase implements MeasurementVisitor {
	private MeasurementDescriptor descriptor;
	private Metrics metrics;
	private SumMeasurement measurement;
	private Measurement visited;
	
	protected void setUp() {
		descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("FOO");
		descriptor.Class(SumMeasurement.class);
		
		metrics = new Metrics("foobar");
	}

	public void testMeasurementDescriptor() throws Exception {
		measurement = (SumMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement.Descriptor());
		assertEquals(SumMeasurement.class, measurement.Descriptor().Class());
		assertEquals("foo", measurement.ShortName());
		assertEquals("FOO", measurement.LongName());
	}

	public void testCreateFromMeasurementDescriptor() throws Exception {
		measurement = (SumMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement);
		assertEquals(descriptor, measurement.Descriptor());
		assertSame(descriptor, measurement.Descriptor());
		assertEquals(SumMeasurement.class, measurement.getClass());
		assertEquals("foo", measurement.ShortName());
		assertEquals("FOO", measurement.LongName());
	}

	public void testCreateDefault() {
		measurement = new SumMeasurement(null, null,  null);
		
		assertEquals(0, measurement.doubleValue(), 0.01);
	}

	public void testEmptyInitText() throws Exception {
		descriptor.InitText("");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(0, measurement.doubleValue(), 0.01);
	}

	public void testEmptyLineInitText() throws Exception {
		descriptor.InitText("\n");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(0, measurement.doubleValue(), 0.01);
	}

	public void testDashInitText() throws Exception {
		descriptor.InitText("-");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(0, measurement.doubleValue(), 0.01);
	}

	public void testConstant() throws Exception {
		descriptor.InitText("2");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(2, measurement.doubleValue(), 0.01);
	}

	public void testConstantAndEmptyLine() throws Exception {
		descriptor.InitText("\n2\n");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(2, measurement.doubleValue(), 0.01);
	}

	public void testAddition() throws Exception {
		descriptor.InitText("1\n1");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(2, measurement.doubleValue(), 0.01);
	}

	public void testNegative() throws Exception {
		descriptor.InitText("-2");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(-2, measurement.doubleValue(), 0.01);
	}

	public void testSubstraction() throws Exception {
		descriptor.InitText("2\n-1");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(1, measurement.doubleValue(), 0.01);

		descriptor.InitText("1\n-2");

		measurement = (SumMeasurement) descriptor.CreateMeasurement();

		assertEquals(-1, measurement.doubleValue(), 0.01);
	}

	public void testSubMeasurement() throws Exception {
		descriptor.InitText("bar");

		metrics.Track("bar", new CounterMeasurement(null, metrics, "2"));
		
		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);

		assertEquals(2, measurement.doubleValue(), 0.01);
	}

	public void testStatisticalMeasurement() throws Exception {
		descriptor.InitText("bar DISPOSE_SUM");

		metrics.Track("bar", new StatisticalMeasurement(null, metrics, "bar"));

		Metrics submetrics = new Metrics("submetrics");
		submetrics.Track("bar", new CounterMeasurement(null, submetrics, "2"));
		metrics.AddSubMetrics(submetrics);
		
		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);

		assertEquals(2, measurement.doubleValue(), 0.01);
	}

	public void testAddMeasurements() throws Exception {
		descriptor.InitText("bar\nbaz");

		metrics.Track("bar", new CounterMeasurement(null, metrics, "1"));
		metrics.Track("baz", new CounterMeasurement(null, metrics, "1"));

		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);

		assertEquals(2, measurement.doubleValue(), 0.01);
	}

	public void testSubstractMeasurements() throws Exception {
		descriptor.InitText("bar\n-baz");

		metrics.Track("bar", new CounterMeasurement(null, metrics, "1"));
		metrics.Track("baz", new CounterMeasurement(null, metrics, "2"));

		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);

		assertEquals(-1, measurement.doubleValue(), 0.01);
	}

	public void testInUndefinedRange() throws Exception {
		descriptor.InitText("bar");

		metrics.Track("bar", new CounterMeasurement(null, null, null));

		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);

		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);

		assertTrue(measurement.InRange());
	}

	public void testInOpenRange() throws Exception {
		descriptor.InitText("bar");

		metrics.Track("bar", new CounterMeasurement(null, null, null));

		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);

		assertTrue(measurement.InRange());
	}

	public void testInLowerBoundRange() throws Exception {
		descriptor.InitText("bar");
		descriptor.LowerThreshold(new Integer(1));

		metrics.Track("bar", new CounterMeasurement(null, null, null));

		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertEquals(0, measurement.intValue());
		assertTrue(!measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertEquals(1, measurement.intValue());
		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertEquals(2, measurement.intValue());
		assertTrue(measurement.InRange());
	}

	public void testInUpperBoundRange() throws Exception {
		descriptor.InitText("bar");
		descriptor.UpperThreshold(new Float(1.5));

		metrics.Track("bar", new CounterMeasurement(null, null, null));

		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertTrue(!measurement.InRange());
	}

	public void testInBoundRange() throws Exception {
		descriptor.InitText("bar");
		descriptor.LowerThreshold(new Integer(1));
		descriptor.UpperThreshold(new Float(1.5));

		metrics.Track("bar", new CounterMeasurement(null, null, null));

		measurement = (SumMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(!measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertTrue(measurement.InRange());

		metrics.AddToMeasurement("bar", 1);
		
		assertTrue(!measurement.InRange());
	}
	
	public void testAccept() {
		measurement = new SumMeasurement(null, null, null);

		visited = null;
		measurement.Accept(this);
		assertSame(measurement, visited);
	}
	
	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitRatioMeasurement(RatioMeasurement measurement) {
		// Do nothing
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
	
	public void VisitNameListMeasurement(NameListMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitSumMeasurement(SumMeasurement measurement) {
		visited = measurement;
	}
}
