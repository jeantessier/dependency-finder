/*
 *  Dependency Finder - Computes quality factors from compiled Java code
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeantessier.metrics;

import junit.framework.*;

public class TestMetrics extends TestCase {
	public TestMetrics(String name) {
		super(name);
	}

	public void testCreate() {
		Metrics metrics = new Metrics("test");

		assertEquals("test", metrics.Name());
		assertNull(metrics.Metric("test"));
		assertTrue(metrics.SubMetrics().isEmpty());
	}

	public void testTrackMetric() {
		Metrics metrics = new Metrics("test");

		metrics.TrackMetric("test1");
		metrics.TrackMetric("test2", 2.0);
		metrics.TrackMetric("test3", new Double(3.0));
		metrics.TrackMetric("test4", new Integer(4));
		
		assertNull(metrics.Metric("test"));
		assertNotNull(metrics.Metric("test1"));
		assertNotNull(metrics.Metric("test2"));
		assertNotNull(metrics.Metric("test3"));
		assertNotNull(metrics.Metric("test4"));
		assertEquals(0.0, ((NumericalMeasurement) metrics.Metric("test1")).Value().doubleValue(), 0.01);
		assertEquals(2.0, ((NumericalMeasurement) metrics.Metric("test2")).Value().doubleValue(), 0.01);
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("test3")).Value().doubleValue(), 0.01);
		assertEquals(4.0, ((NumericalMeasurement) metrics.Metric("test4")).Value().doubleValue(), 0.01);
	}

	public void testAddToMetric() {
		Metrics metrics = new Metrics("test");

		metrics.TrackMetric("test0");
		metrics.TrackMetric("test1", 1.0);
		metrics.TrackMetric("test2", 2.0);
		metrics.TrackMetric("test3", new Double(3.0));
		metrics.TrackMetric("test4", new Integer(4));

		assertNull(metrics.Metric("test"));
		assertEquals(0.0, ((NumericalMeasurement) metrics.Metric("test0")).Value().doubleValue(), 0.01);
		assertEquals(1.0, ((NumericalMeasurement) metrics.Metric("test1")).Value().doubleValue(), 0.01);
		assertEquals(2.0, ((NumericalMeasurement) metrics.Metric("test2")).Value().doubleValue(), 0.01);
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("test3")).Value().doubleValue(), 0.01);
		assertEquals(4.0, ((NumericalMeasurement) metrics.Metric("test4")).Value().doubleValue(), 0.01);

		metrics.AddToMetric("test",  1.0);
		metrics.AddToMetric("test0", 1.0);
		metrics.AddToMetric("test1", 1.0);
		metrics.AddToMetric("test2", 1.0);
		metrics.AddToMetric("test3", 1.0);
		metrics.AddToMetric("test4", 1.0);
		
		assertNull(metrics.Metric("test"));
		assertEquals(1.0, ((NumericalMeasurement) metrics.Metric("test0")).Value().doubleValue(), 0.01);
		assertEquals(2.0, ((NumericalMeasurement) metrics.Metric("test1")).Value().doubleValue(), 0.01);
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("test2")).Value().doubleValue(), 0.01);
		assertEquals(4.0, ((NumericalMeasurement) metrics.Metric("test3")).Value().doubleValue(), 0.01);
		assertEquals(5.0, ((NumericalMeasurement) metrics.Metric("test4")).Value().doubleValue(), 0.01);

		metrics.AddToMetric("test",  new Double(1.0));
		metrics.AddToMetric("test0", new Double(1.0));
		metrics.AddToMetric("test1", new Double(1.0));
		metrics.AddToMetric("test2", new Double(1.0));
		metrics.AddToMetric("test3", new Double(1.0));
		metrics.AddToMetric("test4", new Double(1.0));
		
		assertNull(metrics.Metric("test"));
		assertEquals(2.0, ((NumericalMeasurement) metrics.Metric("test0")).Value().doubleValue(), 0.01);
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("test1")).Value().doubleValue(), 0.01);
		assertEquals(4.0, ((NumericalMeasurement) metrics.Metric("test2")).Value().doubleValue(), 0.01);
		assertEquals(5.0, ((NumericalMeasurement) metrics.Metric("test3")).Value().doubleValue(), 0.01);
		assertEquals(6.0, ((NumericalMeasurement) metrics.Metric("test4")).Value().doubleValue(), 0.01);

		metrics.AddToMetric("test",  new Integer(1));
		metrics.AddToMetric("test0", new Integer(1));
		metrics.AddToMetric("test1", new Integer(1));
		metrics.AddToMetric("test2", new Integer(1));
		metrics.AddToMetric("test3", new Integer(1));
		metrics.AddToMetric("test4", new Integer(1));
		
		assertNull(metrics.Metric("test"));
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("test0")).Value().doubleValue(), 0.01);
		assertEquals(4.0, ((NumericalMeasurement) metrics.Metric("test1")).Value().doubleValue(), 0.01);
		assertEquals(5.0, ((NumericalMeasurement) metrics.Metric("test2")).Value().doubleValue(), 0.01);
		assertEquals(6.0, ((NumericalMeasurement) metrics.Metric("test3")).Value().doubleValue(), 0.01);
		assertEquals(7.0, ((NumericalMeasurement) metrics.Metric("test4")).Value().doubleValue(), 0.01);
	}

	public void testAddSubMetrics() {
		Metrics metrics = new Metrics("test");

		metrics.AddSubMetrics(new Metrics("a"));
		metrics.AddSubMetrics(new Metrics("b"));
		
		assertEquals(2, metrics.SubMetrics().size());
	}

	public void testMetricsInSubMetrics() {
		Metrics metrics = new Metrics("test");

		Metrics a = new Metrics("test.a");
		Metrics b = new Metrics("test.b");
		
		Metrics aA = new Metrics("test.a.A");
		Metrics aB = new Metrics("test.a.B");
		Metrics bA = new Metrics("test.b.A");
		Metrics bB = new Metrics("test.b.B");

		Metrics aAf = new Metrics("test.a.A.f");
		Metrics aAg = new Metrics("test.a.A.g");
		Metrics aBf = new Metrics("test.a.B.f");
		Metrics aBg = new Metrics("test.a.B.g");
		Metrics bAf = new Metrics("test.b.A.f");
		Metrics bAg = new Metrics("test.b.A.g");
		Metrics bBf = new Metrics("test.b.B.f");
		Metrics bBg = new Metrics("test.b.B.g");

		metrics.AddSubMetrics(a);
		metrics.AddSubMetrics(b);

		a.AddSubMetrics(aA);
		a.AddSubMetrics(aB);
		b.AddSubMetrics(bA);
		b.AddSubMetrics(bB);
		
		aA.AddSubMetrics(aAf);
		aA.AddSubMetrics(aAg);
		aB.AddSubMetrics(aBf);
		aB.AddSubMetrics(aBg);
		bA.AddSubMetrics(bAf);
		bA.AddSubMetrics(bAg);
		bB.AddSubMetrics(bBf);
		bB.AddSubMetrics(bBg);

		aAf.TrackMetric("0001");
		aAf.TrackMetric("0011");
		aAf.TrackMetric("0101");
		aAf.TrackMetric("0111");
		aAg.TrackMetric("0001");
		aAg.TrackMetric("0011");
		aAg.TrackMetric("0101");
		aAg.TrackMetric("0111");
		aBf.TrackMetric("0001");
		aBf.TrackMetric("0011");
		aBf.TrackMetric("0101");
		aBf.TrackMetric("0111");
		aBg.TrackMetric("0001");
		aBg.TrackMetric("0011");
		aBg.TrackMetric("0101");
		aBg.TrackMetric("0111");
		bAf.TrackMetric("1001");
		bAf.TrackMetric("1011");
		bAf.TrackMetric("1101");
		bAf.TrackMetric("1111");
		bAg.TrackMetric("1001");
		bAg.TrackMetric("1011");
		bAg.TrackMetric("1101");
		bAg.TrackMetric("1111");
		bBf.TrackMetric("1001");
		bBf.TrackMetric("1011");
		bBf.TrackMetric("1101");
		bBf.TrackMetric("1111");
		bBg.TrackMetric("1001");
		bBg.TrackMetric("1011");
		bBg.TrackMetric("1101");
		bBg.TrackMetric("1111");

		aA.TrackMetric("0011", 1.0);
		aA.TrackMetric("0010", 1.0);
		aA.TrackMetric("0111", 1.0);
		aA.TrackMetric("0110", 1.0);
		aB.TrackMetric("0011", 1.0);
		aB.TrackMetric("0010", 1.0);
		aB.TrackMetric("0111", 1.0);
		aB.TrackMetric("0110", 1.0);
		bA.TrackMetric("1011", 1.0);
		bA.TrackMetric("1010", 1.0);
		bA.TrackMetric("1111", 1.0);
		bA.TrackMetric("1110", 1.0);
		bB.TrackMetric("1011", 1.0);
		bB.TrackMetric("1010", 1.0);
		bB.TrackMetric("1111", 1.0);
		bB.TrackMetric("1110", 1.0);

		a.TrackMetric("0100", 2.0);
		a.TrackMetric("0101", 2.0);
		a.TrackMetric("0110", 2.0);
		a.TrackMetric("0111", 2.0);
		b.TrackMetric("1100", 2.0);
		b.TrackMetric("1101", 2.0);
		b.TrackMetric("1110", 2.0);
		b.TrackMetric("1111", 2.0);

		metrics.TrackMetric("1100", 3.0);
		metrics.TrackMetric("1101", 3.0);
		metrics.TrackMetric("1110", 3.0);
		metrics.TrackMetric("1111", 3.0);
		
		assertEquals(2, metrics.SubMetrics().size());
		assertNull(metrics.Metric("0000"));
		assertNull(metrics.Metric("0001"));
		assertNull(metrics.Metric("0010"));
		assertNull(metrics.Metric("0011"));
		assertNull(metrics.Metric("0100"));
		assertNull(metrics.Metric("0101"));
		assertNull(metrics.Metric("0110"));
		assertNull(metrics.Metric("0111"));
		assertNull(metrics.Metric("1000"));
		assertNull(metrics.Metric("1001"));
		assertNull(metrics.Metric("1010"));
		assertNull(metrics.Metric("1011"));
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("1100")).Value().doubleValue(), 0.01);
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("1101")).Value().doubleValue(), 0.01);
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("1110")).Value().doubleValue(), 0.01);
		assertEquals(3.0, ((NumericalMeasurement) metrics.Metric("1111")).Value().doubleValue(), 0.01);
	}
}
