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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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
