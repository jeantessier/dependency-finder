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

public class TestNbSubMetricsMeasurement extends TestCase {
	public TestNbSubMetricsMeasurement(String name) {
		super(name);
	}

	public void testAddSubMetrics() {
		Metrics metrics = new Metrics("foo");

		NbSubMetricsMeasurement measure = new NbSubMetricsMeasurement("foobar", metrics);

		assertEquals(0.0, measure.Value().doubleValue(), 0.01);
		assertEquals(0, measure.Value().intValue());

		metrics.AddSubMetrics(new Metrics("bar"));

		assertEquals(1, measure.Value().doubleValue(), 0.01);
		assertEquals(1, measure.Value().intValue());

		metrics.AddSubMetrics(new Metrics("bar"));

		assertEquals(1, measure.Value().doubleValue(), 0.01);
		assertEquals(1, measure.Value().intValue());

		metrics.AddSubMetrics(new Metrics("baz"));

		assertEquals(2, measure.Value().doubleValue(), 0.01);
		assertEquals(2, measure.Value().intValue());
	}
}
