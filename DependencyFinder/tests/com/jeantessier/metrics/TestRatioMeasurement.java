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

public class TestRatioMeasurement extends TestCase {
	public TestRatioMeasurement(String name) {
		super(name);
	}

	private NumericalMeasurement m1;
	private NumericalMeasurement m2;

	private RatioMeasurement measure;
	
	protected void setUp() throws Exception {
		m1 = new CounterMeasurement("base");
		m2 = new CounterMeasurement("divider");
		
		measure = new RatioMeasurement("foobar", m1, m2);
	}
	
	public void testNormal() {
		m1.Add(new Integer(10));
		m2.Add(new Integer(1));

		assertEquals(10 / 1, measure.Value().doubleValue(), 0.01);
		assertEquals(10 / 1, measure.Value().intValue());

		m2.Add(new Integer(1));

		assertEquals(10 / 2, measure.Value().doubleValue(), 0.01);
		assertEquals(10 / 2, measure.Value().intValue());

		m1.Add(m1.Value());

		assertEquals(20 / 2, measure.Value().doubleValue(), 0.01);
		assertEquals(20 / 2, measure.Value().intValue());
	}
	
	public void testDevideByZero() {
		assertTrue("0/0 not NaN", Double.isNaN(measure.Value().doubleValue()));

		m1.Add(new Integer(1));

		assertTrue("1/0 not infitity", Double.isInfinite(measure.Value().doubleValue()));

		m1.Add(new Integer(-2));

		assertTrue("-1/0 not infitity", Double.isInfinite(measure.Value().doubleValue()));
	}
	
	public void testZeroDevidedBy() {
		assertTrue("0/0 not NaN", Double.isNaN(measure.Value().doubleValue()));

		m2.Add(new Integer(1));

		assertEquals("0/1 not zero", 0.0, measure.Value().doubleValue(), 0.01);

		m2.Add(new Integer(-2));

		assertEquals("0/-1 not zero", 0.0, measure.Value().doubleValue(), 0.01);
	}
}
