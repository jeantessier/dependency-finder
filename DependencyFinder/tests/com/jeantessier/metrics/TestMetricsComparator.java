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

import java.util.*;

import junit.framework.*;

public class TestMetricsComparator extends TestCase {
	public TestMetricsComparator(String name) {
		super(name);
	}

	public void testSortOn() {
		MetricsComparator c = new MetricsComparator("foo", StatisticalMeasurement.DISPOSE_IGNORE);

		assertEquals("c.Name()", "foo", c.Name());
		assertEquals("c.Direction()", MetricsComparator.ASCENDING, c.Direction());

		c.SortOn("foo", StatisticalMeasurement.DISPOSE_IGNORE);
		
		assertEquals("c.Name()", "foo", c.Name());
		assertEquals("c.Direction()", MetricsComparator.DESCENDING, c.Direction());
		
		c.SortOn("foo", StatisticalMeasurement.DISPOSE_IGNORE);
		
		assertEquals("c.Name()", "foo", c.Name());
		assertEquals("c.Direction()", MetricsComparator.ASCENDING, c.Direction());

		c.SortOn("bar", StatisticalMeasurement.DISPOSE_IGNORE);

		assertEquals("c.Name()", "bar", c.Name());
		assertEquals("c.Direction()", MetricsComparator.ASCENDING, c.Direction());

		c.SortOn("bar", StatisticalMeasurement.DISPOSE_IGNORE);

		assertEquals("c.Name()", "bar", c.Name());
		assertEquals("c.Direction()", MetricsComparator.DESCENDING, c.Direction());

		c.SortOn("baz", StatisticalMeasurement.DISPOSE_IGNORE);

		assertEquals("c.Name()", "baz", c.Name());
		assertEquals("c.Direction()", MetricsComparator.ASCENDING, c.Direction());

		c.SortOn("foobar", StatisticalMeasurement.DISPOSE_IGNORE);

		assertEquals("c.Name()", "foobar", c.Name());
		assertEquals("c.Direction()", MetricsComparator.ASCENDING, c.Direction());

		c.SortOn("foobar", StatisticalMeasurement.DISPOSE_IGNORE);

		assertEquals("c.Name()", "foobar", c.Name());
		assertEquals("c.Direction()", MetricsComparator.DESCENDING, c.Direction());

		c.SortOn("foobar", StatisticalMeasurement.DISPOSE_MINIMUM);

		assertEquals("c.Name()", "foobar", c.Name());
		assertEquals("c.Direction()", MetricsComparator.ASCENDING, c.Direction());
	}
	
	public void testCompareTo() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");

		m1.TrackMetric("foo", 1);
		m1.TrackMetric("bar", 2);
		m1.TrackMetric("baz", 3);
		m2.TrackMetric("foo", 3);
		m2.TrackMetric("bar", 2);
		m2.TrackMetric("baz", 1);
		
		Comparator c1 = new MetricsComparator("foo");
		Comparator c2 = new MetricsComparator("bar");
		Comparator c3 = new MetricsComparator("baz");

		assertTrue(c1.compare(m1, m2) < 0);
		assertTrue(c2.compare(m1, m2) == 0);
		assertTrue(c3.compare(m1, m2) > 0);
	}
}
