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
	private Measurement visited;

	private Metrics metrics;

	private Metrics m1;
	private Metrics m2;
	private Metrics m3;

	MeasurementDescriptor descriptor;

	protected void setUp() throws Exception {
		m1 = new Metrics("m1");
		m2 = new Metrics("m2");
		m3 = new Metrics("m3");

		MeasurementDescriptor name_list = new MeasurementDescriptor();
		name_list.ShortName("NL");
		name_list.LongName("name list");
		name_list.Class(NameListMeasurement.class);

		MeasurementDescriptor number_list = new MeasurementDescriptor();
		number_list.ShortName("NbL");
		number_list.LongName("number list");
		number_list.Class(NameListMeasurement.class);

		MeasurementDescriptor counter = new MeasurementDescriptor();
		counter.ShortName("NL");
		counter.LongName("counter");
		counter.Class(CounterMeasurement.class);

		m1.Track(name_list.CreateMeasurement(m1));
		m1.AddToMeasurement("NL", "abc");
		m1.AddToMeasurement("NL", "def");
		m1.AddToMeasurement("NL", "ghi");

		m1.Track(number_list.CreateMeasurement(m1));
		m1.AddToMeasurement("NbL", "123");
		m1.AddToMeasurement("NbL", "456");
		m1.AddToMeasurement("NbL", "789");

		m2.Track(name_list.CreateMeasurement(m2));
		m2.AddToMeasurement("NL", "jkl");
		m2.AddToMeasurement("NL", "abc");

		m2.Track(number_list.CreateMeasurement(m2));
		m2.AddToMeasurement("NbL", "159");
		m2.AddToMeasurement("NbL", "248");

		m3.Track(counter.CreateMeasurement(m3));
		m3.AddToMeasurement("NL", 1);

		metrics = new Metrics("metrics");

		descriptor = new MeasurementDescriptor();
		descriptor.ShortName("foo");
		descriptor.LongName("bar");
		descriptor.Class(AccumulatorMeasurement.class);
	}

	public void testCreateFromMeasurementDescriptor() throws Exception {
		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);

		assertNotNull(measurement);
		assertEquals(descriptor, measurement.Descriptor());
		assertSame(descriptor, measurement.Descriptor());
		assertEquals(AccumulatorMeasurement.class, measurement.getClass());
		assertEquals("foo", measurement.ShortName());
		assertEquals("bar", measurement.LongName());
	}

	public void testNullInit() throws Exception {
		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);

		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());
	}

	public void testEmptyInit() throws Exception {
		descriptor.InitText("");

		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);

		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());
	}

	public void testRawValues() throws Exception {
		descriptor.InitText("NL");

		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);

		assertEquals(4, measurement.intValue());
		assertTrue("\"abc\" not in " + measurement.Values(), measurement.Values().contains("abc"));
		assertTrue("\"def\" not in " + measurement.Values(), measurement.Values().contains("def"));
		assertTrue("\"ghi\" not in " + measurement.Values(), measurement.Values().contains("ghi"));
		assertTrue("\"jkl\" not in " + measurement.Values(), measurement.Values().contains("jkl"));
	}

	public void testSingleFiltered() throws Exception {
		descriptor.InitText("NL /a/");

		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);

		assertEquals(1, measurement.intValue());
		assertTrue("\"abc\" not in " + measurement.Values(), measurement.Values().contains("abc"));
	}

	public void testMultiFilterFiltered() throws Exception {
		descriptor.InitText("NL /a/\nNL /k/");

		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);

		assertEquals(2, measurement.intValue());
		assertTrue("\"abc\" not in " + measurement.Values(), measurement.Values().contains("abc"));
		assertTrue("\"jkl\" not in " + measurement.Values(), measurement.Values().contains("jkl"));
	}

	public void testModifiedValues() throws Exception {
		descriptor.InitText("NL /(a)/");

		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);

		assertEquals(1, measurement.intValue());
		assertTrue("\"a\" not in " + measurement.Values(), measurement.Values().contains("a"));
	}

	public void testMultiMeasurements() throws Exception {
		descriptor.InitText("NL /a/\nNbL /2/");

		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);
		assertEquals(0, measurement.intValue());
		assertTrue(measurement.Values().isEmpty());

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);

		assertEquals(3, measurement.intValue());
		assertTrue("\"abc\" not in " + measurement.Values(), measurement.Values().contains("abc"));
		assertTrue("\"123\" not in " + measurement.Values(), measurement.Values().contains("123"));
		assertTrue("\"248\" not in " + measurement.Values(), measurement.Values().contains("248"));
	}

	public void testAccept() throws Exception {
		AccumulatorMeasurement measurement = (AccumulatorMeasurement) descriptor.CreateMeasurement(metrics);

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
		
	public void VisitNameListMeasurement(NameListMeasurement measurement) {
		// Do nothing
	}

	public void VisitSumMeasurement(SumMeasurement measurement) {
		// Do nothing
	}
}
