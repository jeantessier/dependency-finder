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

public class TestAccumulatorMeasurement extends TestCase {
	public TestAccumulatorMeasurement(String name) {
		super(name);
	}

	public void testAdd() {
		NumericalMeasurement measure = new AccumulatorMeasurement("foobar");

		Object o1 = new Object();
		Object o2 = new Object();

		assertEquals("zero", 0.0, measure.Value().doubleValue(), 0.01);

		measure.Add(o1);
		assertEquals("one", 1.0, measure.Value().doubleValue(), 0.01);

		measure.Add(o2);
		assertEquals("two", 2.0, measure.Value().doubleValue(), 0.01);

		measure.Add(o1);
		assertEquals("three", 2.0, measure.Value().doubleValue(), 0.01);
	}
}
