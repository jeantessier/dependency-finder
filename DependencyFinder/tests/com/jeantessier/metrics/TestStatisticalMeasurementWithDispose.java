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

import java.io.*;

import junit.framework.*;

import org.apache.log4j.*;

public class TestStatisticalMeasurementWithDispose extends TestCase {
	private Metrics m1;
	private Metrics m2;
	private Metrics m3;
	private Metrics m4;
	private Metrics m5;
	private Metrics m6;

	private Metrics c1;
	private Metrics c2;

	private Metrics g;

	public TestStatisticalMeasurementWithDispose(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		Category.getInstance(getClass().getName()).info("Starting test: " + getName());

		m1 = new Metrics("m1");
		m2 = new Metrics("m2");
		m3 = new Metrics("m3");
		m4 = new Metrics("m4");
		m5 = new Metrics("m5");
		m6 = new Metrics("m6");

		m1.TrackMetric("bar", 1.0);
		m2.TrackMetric("bar", 2.0);
		m3.TrackMetric("bar", 3.0);
		m4.TrackMetric("bar", 4.0);
		m5.TrackMetric("bar", 5.0);
		m6.TrackMetric("bar", 6.0);
	
		c1 = new Metrics("c1");
		c2 = new Metrics("c2");

		c1.TrackMetric(new StatisticalMeasurement("bar", "bar", c1));
		c2.TrackMetric(new StatisticalMeasurement("bar", "bar", c2));
		
		c1.AddSubMetrics(m1);
		c1.AddSubMetrics(m2);
		c2.AddSubMetrics(m3);
		c2.AddSubMetrics(m4);
		c2.AddSubMetrics(m5);
		c2.AddSubMetrics(m6);

		g = new Metrics("g");

		g.AddSubMetrics(c1);
		g.AddSubMetrics(c2);
	}

	protected void tearDown() throws Exception {
		Category.getInstance(getClass().getName()).info("End of " + getName());
	}

	public void testDefault() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g);

		assertEquals("size "    + sm,  6,   sm.NbDataPoints());
		assertEquals("Minimum " + sm,  1.0, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm,  4.0, sm.Median(),  0.01);
		assertEquals("Average " + sm,  3.5, sm.Average(), 0.01);
		assertEquals("Maximum " + sm,  6.0, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 21.0, sm.Sum(),     0.01);
	}

	public void testIgnore() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g, StatisticalMeasurement.DISPOSE_IGNORE);

		assertEquals("size "    + sm,  6,   sm.NbDataPoints());
		assertEquals("Minimum " + sm,  1.0, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm,  4.0, sm.Median(),  0.01);
		assertEquals("Average " + sm,  3.5, sm.Average(), 0.01);
		assertEquals("Maximum " + sm,  6.0, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 21.0, sm.Sum(),     0.01);
	}

	public void testMinimum() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g, StatisticalMeasurement.DISPOSE_MINIMUM);

		assertEquals("size "    + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum " + sm, 1.0, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm, 3.0, sm.Median(),  0.01);
		assertEquals("Average " + sm, 2.0, sm.Average(), 0.01);
		assertEquals("Maximum " + sm, 3.0, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 4.0, sm.Sum(),     0.01);
	}

	public void testMedian() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g, StatisticalMeasurement.DISPOSE_MEDIAN);

		assertEquals("size "    + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum " + sm, 2.0, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm, 5.0, sm.Median(),  0.01);
		assertEquals("Average " + sm, 3.5, sm.Average(), 0.01);
		assertEquals("Maximum " + sm, 5.0, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 7.0, sm.Sum(),     0.01);
	}

	public void testAverage() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g, StatisticalMeasurement.DISPOSE_AVERAGE);

		assertEquals("size "    + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum " + sm, 1.5, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm, 4.5, sm.Median(),  0.01);
		assertEquals("Average " + sm, 3.0, sm.Average(), 0.01);
		assertEquals("Maximum " + sm, 4.5, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 6.0, sm.Sum(),     0.01);
	}

	public void testMaximum() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g, StatisticalMeasurement.DISPOSE_MAXIMUM);

		assertEquals("size "    + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum " + sm, 2.0, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm, 6.0, sm.Median(),  0.01);
		assertEquals("Average " + sm, 4.0, sm.Average(), 0.01);
		assertEquals("Maximum " + sm, 6.0, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 8.0, sm.Sum(),     0.01);
	}

	public void testSum() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g, StatisticalMeasurement.DISPOSE_SUM);

		assertEquals("size "    + sm,  2,   sm.NbDataPoints());
		assertEquals("Minimum " + sm,  3.0, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm, 18.0, sm.Median(),  0.01);
		assertEquals("Average " + sm, 10.5, sm.Average(), 0.01);
		assertEquals("Maximum " + sm, 18.0, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 21.0, sm.Sum(),     0.01);
	}

	public void testNbDataPoints() {
		StatisticalMeasurement sm = new StatisticalMeasurement("bar", "bar", g, StatisticalMeasurement.DISPOSE_NB_DATA_POINTS);

		assertEquals("size "    + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum " + sm, 2.0, sm.Minimum(), 0.01);
		assertEquals("Median "  + sm, 4.0, sm.Median(),  0.01);
		assertEquals("Average " + sm, 3.0, sm.Average(), 0.01);
		assertEquals("Maximum " + sm, 4.0, sm.Maximum(), 0.01);
		assertEquals("Sum "     + sm, 6.0, sm.Sum(),     0.01);
	}
}
