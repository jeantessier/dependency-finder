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

public class TestAccumulatorMeasurement extends TestCase implements MeasurementVisitor {
	private AccumulatorMeasurement measurement;
	private Measurement visited;
	
	public TestAccumulatorMeasurement(String name) {
		super(name);
	}

	protected void setUp() {
		measurement = new AccumulatorMeasurement(null, null, null);
	}

	public void testMeasurementDescriptor() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(AccumulatorMeasurement.class);

		measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement.Descriptor());
		assertEquals(AccumulatorMeasurement.class, measurement.Descriptor().Class());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
	}

	public void testCreateFromMeasurementDescriptor() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(AccumulatorMeasurement.class);

		measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement);
		assertEquals(descriptor, measurement.Descriptor());
		assertSame(descriptor, measurement.Descriptor());
		assertEquals(AccumulatorMeasurement.class, measurement.getClass());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
	}

	public void testCreateSet() {
		measurement = new AccumulatorMeasurement(null, null, "SET");

		measurement.Add("abc");
		measurement.Add("abc");

		assertEquals(1, measurement.intValue());
	}
	
	public void testCreateList() {
		measurement = new AccumulatorMeasurement(null, null, "LIST");

		measurement.Add("abc");
		measurement.Add("abc");

		assertEquals(2, measurement.intValue());
	}

	public void testCreateDefault() {
		measurement.Add("abc");
		measurement.Add("abc");

		assertEquals(1, measurement.intValue());
	}

	public void testAddObject() {
		Object o1 = new Object();
		Object o2 = new Object();

		assertEquals("zero", 0, measurement.intValue());
		assertEquals("zero", 0.0, measurement.doubleValue(), 0.01);
		assertEquals("zero", 0, measurement.Value().intValue());

		measurement.Add(o1);
		assertEquals("one", 1, measurement.intValue());
		assertEquals("one", 1.0, measurement.doubleValue(), 0.01);
		assertEquals("one", 1, measurement.Value().intValue());

		measurement.Add(o2);
		assertEquals("two", 2, measurement.intValue());
		assertEquals("two", 2.0, measurement.doubleValue(), 0.01);
		assertEquals("two", 2, measurement.Value().intValue());

		measurement.Add(o1);
		assertEquals("three", 2, measurement.intValue());
		assertEquals("three", 2.0, measurement.doubleValue(), 0.01);
		assertEquals("three", 2, measurement.Value().intValue());
	}

	public void testAddInt() {
		assertEquals("zero", 0, measurement.intValue());
		assertEquals("zero", 0.0, measurement.doubleValue(), 0.01);
		assertEquals("zero", 0, measurement.Value().intValue());

		measurement.Add(1);
		assertEquals("one", 0, measurement.intValue());
		assertEquals("one", 0.0, measurement.doubleValue(), 0.01);
		assertEquals("one", 0, measurement.Value().intValue());

		measurement.Add(1);
		assertEquals("two", 0, measurement.intValue());
		assertEquals("two", 0.0, measurement.doubleValue(), 0.01);
		assertEquals("two", 0, measurement.Value().intValue());
	}

	public void testAddFloat() {
		assertEquals("zero", 0, measurement.intValue());
		assertEquals("zero", 0.0, measurement.doubleValue(), 0.01);
		assertEquals("zero", 0, measurement.Value().intValue());

		measurement.Add(1.0);
		assertEquals("one", 0, measurement.intValue());
		assertEquals("one", 0.0, measurement.doubleValue(), 0.01);
		assertEquals("one", 0, measurement.Value().intValue());

		measurement.Add(1.0);
		assertEquals("two", 0, measurement.intValue());
		assertEquals("two", 0.0, measurement.doubleValue(), 0.01);
		assertEquals("two", 0, measurement.Value().intValue());
	}

	public void testInUndefinedRange() {
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		measurement.Add(new Object());

		assertTrue(measurement.InRange());
	}

	public void testInOpenRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(AccumulatorMeasurement.class);

		measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		measurement.Add(new Object());

		assertTrue(measurement.InRange());
	}

	public void testInLowerBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(AccumulatorMeasurement.class);
		descriptor.LowerThreshold(new Integer(1));

		measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(!measurement.InRange());

		measurement.Add(new Object());
		
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		measurement.Add(new Object());
		
		assertTrue(measurement.InRange());
	}

	public void testInUpperBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(AccumulatorMeasurement.class);
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		measurement.Add(new Object());
		
		assertTrue(!measurement.InRange());
	}

	public void testInBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(AccumulatorMeasurement.class);
		descriptor.LowerThreshold(new Integer(1));
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(!measurement.InRange());

		measurement.Add(new Object());
		
		assertTrue(measurement.InRange());

		measurement.Add(new Object());
		measurement.Add(new Object());
		
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
		// Do nothing
	}
	
	public void VisitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitCounterMeasurement(CounterMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitAccumulatorMeasurement(AccumulatorMeasurement measurement) {
		visited = measurement;
	}
}
