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

public class TestNbSubMetricsMeasurement extends TestCase implements MeasurementVisitor {
	private MeasurementDescriptor descriptor;
	private NbSubMetricsMeasurement measurement;
	private Metrics metrics;
	private Measurement visited;
	
	protected void setUp() {
		metrics = new Metrics("foo");

		descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(NbSubMetricsMeasurement.class);
		descriptor.Cached(false);
	}

	public void testCreateFromMeasurementDescriptor() throws Exception {
		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement);
		assertEquals(descriptor, measurement.Descriptor());
		assertSame(descriptor, measurement.Descriptor());
		assertEquals(NbSubMetricsMeasurement.class, measurement.getClass());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
	}

	public void testAddSubMetrics() throws Exception {
		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertEquals(0, measurement.intValue());
		assertEquals(0.0, measurement.doubleValue(), 0.01);
		assertEquals(0, measurement.Value().intValue());

		metrics.AddSubMetrics(new Metrics("bar"));

		assertEquals(1, measurement.intValue());
		assertEquals(1.0, measurement.doubleValue(), 0.01);
		assertEquals(1, measurement.Value().intValue());

		metrics.AddSubMetrics(new Metrics("bar"));

		assertEquals(1, measurement.intValue());
		assertEquals(1.0, measurement.doubleValue(), 0.01);
		assertEquals(1, measurement.Value().intValue());

		metrics.AddSubMetrics(new Metrics("baz"));

		assertEquals(2, measurement.intValue());
		assertEquals(2.0, measurement.doubleValue(), 0.01);
		assertEquals(2, measurement.Value().intValue());
	}

	public void testInUndefinedRange() throws Exception {
		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("foo"));
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("bar"));
		metrics.AddSubMetrics(new Metrics("baz"));

		assertTrue(measurement.InRange());
	}

	public void testInOpenRange() throws Exception {
		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("foo"));
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("bar"));
		metrics.AddSubMetrics(new Metrics("baz"));

		assertTrue(measurement.InRange());
	}

	public void testInLowerBoundRange() throws Exception {
		descriptor.LowerThreshold(new Integer(1));

		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(!measurement.InRange());

		metrics.AddSubMetrics(new Metrics("foo"));
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("bar"));
		metrics.AddSubMetrics(new Metrics("baz"));
		
		assertTrue(measurement.InRange());
	}

	public void testInUpperBoundRange() throws Exception {
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("foo"));
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("bar"));
		metrics.AddSubMetrics(new Metrics("baz"));
		
		assertTrue(!measurement.InRange());
	}

	public void testInBoundRange() throws Exception {
		descriptor.LowerThreshold(new Integer(1));
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		
		assertTrue(!measurement.InRange());

		metrics.AddSubMetrics(new Metrics("foo"));
		
		assertTrue(measurement.InRange());

		metrics.AddSubMetrics(new Metrics("bar"));
		metrics.AddSubMetrics(new Metrics("baz"));
		
		assertTrue(!measurement.InRange());
	}

	public void testSelectionCriteria() throws Exception {
		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);

		assertEquals("empty metrics", 0, measurement.intValue());
		
		metrics.AddSubMetrics(new Metrics("foo"));
		metrics.AddSubMetrics(new Metrics("bar"));
		metrics.AddSubMetrics(new Metrics("baz"));

		assertEquals("empty metrics", 3, measurement.intValue());
	}

	public void testCachedValue() throws Exception {
		descriptor.Cached(true);

		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);

		assertEquals("empty metrics", 0, measurement.intValue());
		
		metrics.AddSubMetrics(new Metrics("foo"));
		metrics.AddSubMetrics(new Metrics("bar"));
		metrics.AddSubMetrics(new Metrics("baz"));

		assertEquals("empty metrics", 0, measurement.intValue());
	}

	public void testAccept() throws Exception {
		measurement = (NbSubMetricsMeasurement) descriptor.CreateMeasurement(metrics);
		
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
		visited = measurement;
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
		// Do nothing
	}
}
