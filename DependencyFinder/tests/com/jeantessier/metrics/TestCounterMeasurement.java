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

public class TestCounterMeasurement extends TestCase implements MeasurementVisitor {
	private CounterMeasurement measurement;
	private Measurement visited;
	
	public TestCounterMeasurement(String name) {
		super(name);
	}

	protected void setUp() {
		measurement = new CounterMeasurement(null, null, null);
	}

	public void testMeasurementDescriptor() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement.Descriptor());
		assertEquals(CounterMeasurement.class, measurement.Descriptor().Class());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
	}

	public void testCreateFromMeasurementDescriptor() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertNotNull(measurement);
		assertEquals(descriptor, measurement.Descriptor());
		assertSame(descriptor, measurement.Descriptor());
		assertEquals(CounterMeasurement.class, measurement.getClass());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
	}
	
	public void testCreateNumber() {
		measurement = new CounterMeasurement(null, null, "2");
		assertEquals(2.0, measurement.doubleValue(), 0.01);

		measurement = new CounterMeasurement(null, null, "2.0");
		assertEquals(2.0, measurement.doubleValue(), 0.01);

		measurement = new CounterMeasurement(null, null, "-2.5");
		assertEquals(-2.5, measurement.doubleValue(), 0.01);

		measurement = new CounterMeasurement(null, null, " 2.0 ");
		assertEquals(2.0, measurement.doubleValue(), 0.01);
	}
	
	public void testCreateInvalid() {
		measurement = new CounterMeasurement(null, null, null);
		assertEquals(0.0, measurement.doubleValue(), 0.01);

		measurement = new CounterMeasurement(null, null, "foobar");
		assertEquals(0.0, measurement.doubleValue(), 0.01);
	}

	public void testCreateDefault() {
		assertEquals(0.0, measurement.doubleValue(), 0.01);
	}
	
	public void testAddObject() {
		measurement.Add(new Integer(1));

		assertEquals(1, measurement.intValue());
		assertEquals(1.0, measurement.doubleValue(), 0.01);
		assertEquals(1, measurement.Value().intValue());

		measurement.Add(new Float(0.5));

		assertEquals(1, measurement.Value().intValue());
		assertEquals(1.5, measurement.Value().doubleValue(), 0.01);
	}
	
	public void testAddInt() {
		measurement.Add(1);

		assertEquals(1, measurement.intValue());
		assertEquals(1.0, measurement.doubleValue(), 0.01);
		assertEquals(1, measurement.Value().intValue());

		measurement.Add(2);

		assertEquals(3, measurement.intValue());
		assertEquals(3.0, measurement.doubleValue(), 0.01);
		assertEquals(3, measurement.Value().intValue());
	}
	
	public void testAddFloat() {
		measurement.Add(1.0);

		assertEquals(1, measurement.intValue());
		assertEquals(1.0, measurement.doubleValue(), 0.01);
		assertEquals(1, measurement.Value().intValue());

		measurement.Add(0.5);

		assertEquals(1, measurement.intValue());
		assertEquals(1.5, measurement.doubleValue(), 0.01);
		assertEquals(1, measurement.Value().intValue());
	}

	public void testSubstract() {
		measurement.Add(new Integer(-1));

		assertEquals(-1, measurement.Value().intValue());
		assertEquals(-1.0, measurement.Value().doubleValue(), 0.01);

		measurement.Add(new Float(0.4));

		assertEquals(0, measurement.Value().intValue());
		assertEquals(-0.6, measurement.Value().doubleValue(), 0.01);

		measurement.Add(new Float(0.1));

		assertEquals(0, measurement.Value().intValue());
		assertEquals(-0.5, measurement.Value().doubleValue(), 0.01);
	}

	public void testInUndefinedRange() {
		assertTrue(measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
		assertTrue(measurement.InRange());
	}

	public void testInOpenRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
		assertTrue(measurement.InRange());
	}

	public void testInLowerBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);
		descriptor.LowerThreshold(new Integer(1));

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(!measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
		assertTrue(measurement.InRange());
	}

	public void testInStringLowerBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);
		descriptor.LowerThreshold("1");

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(!measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
		assertTrue(measurement.InRange());
	}

	public void testInUpperBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
		assertTrue(!measurement.InRange());
	}

	public void testInStringUpperBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);
		descriptor.UpperThreshold("1.5");

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
		assertTrue(!measurement.InRange());
	}

	public void testInBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);
		descriptor.LowerThreshold(new Integer(1));
		descriptor.UpperThreshold(new Float(1.5));

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(!measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
		assertTrue(!measurement.InRange());
	}

	public void testInStringBoundRange() throws Exception {
		MeasurementDescriptor descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(CounterMeasurement.class);
		descriptor.LowerThreshold("1");
		descriptor.UpperThreshold("1.5");

		measurement = (CounterMeasurement) descriptor.CreateMeasurement();
		
		assertTrue(!measurement.InRange());

		measurement.Add(1);
		
		assertTrue(measurement.InRange());

		measurement.Add(2);
		
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
		visited = measurement;
	}
	
	public void VisitAccumulatorMeasurement(AccumulatorMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitSumMeasurement(SumMeasurement measurement) {
		// Do nothing
	}
}
