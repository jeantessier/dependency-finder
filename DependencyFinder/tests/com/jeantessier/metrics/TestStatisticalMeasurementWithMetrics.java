/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

import org.apache.log4j.*;

public class TestStatisticalMeasurementWithMetrics extends TestCase {
	private Metrics m1;
	private Metrics m2;
	private Metrics m3;
	private Metrics m4;
	private Metrics m5;
	private Metrics m6;
	private Metrics m7;
	private Metrics m8;

	private Metrics c1;
	private Metrics c2;
	private Metrics c3;
	private Metrics c4;

	private Metrics g1;
	private Metrics g2;

	private Metrics p;

	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).info("Starting test: " + getName());

		m1 = new Metrics("a.A.a");
		m2 = new Metrics("a.A.b");
		m3 = new Metrics("a.B.a");
		m4 = new Metrics("a.B.b");
		m5 = new Metrics("b.A.a");
		m6 = new Metrics("b.A.b");
		m7 = new Metrics("b.B.a");
		m8 = new Metrics("b.B.b");

		m1.Track("001", new CounterMeasurement(null, null, null));
		m1.Track("011", new CounterMeasurement(null, null, null));
		m1.Track("101", new CounterMeasurement(null, null, null));
		m1.Track("111", new CounterMeasurement(null, null, null));
		m2.Track("001", new CounterMeasurement(null, null, null));
		m2.Track("011", new CounterMeasurement(null, null, null));
		m2.Track("101", new CounterMeasurement(null, null, null));
		m2.Track("111", new CounterMeasurement(null, null, null));
		m3.Track("001", new CounterMeasurement(null, null, null));
		m3.Track("011", new CounterMeasurement(null, null, null));
		m3.Track("101", new CounterMeasurement(null, null, null));
		m3.Track("111", new CounterMeasurement(null, null, null));
		m4.Track("001", new CounterMeasurement(null, null, null));
		m4.Track("011", new CounterMeasurement(null, null, null));
		m4.Track("101", new CounterMeasurement(null, null, null));
		m4.Track("111", new CounterMeasurement(null, null, null));
		m5.Track("001", new CounterMeasurement(null, null, null));
		m5.Track("011", new CounterMeasurement(null, null, null));
		m5.Track("101", new CounterMeasurement(null, null, null));
		m5.Track("111", new CounterMeasurement(null, null, null));
		m6.Track("001", new CounterMeasurement(null, null, null));
		m6.Track("011", new CounterMeasurement(null, null, null));
		m6.Track("101", new CounterMeasurement(null, null, null));
		m6.Track("111", new CounterMeasurement(null, null, null));
		m7.Track("001", new CounterMeasurement(null, null, null));
		m7.Track("011", new CounterMeasurement(null, null, null));
		m7.Track("101", new CounterMeasurement(null, null, null));
		m7.Track("111", new CounterMeasurement(null, null, null));
		m8.Track("001", new CounterMeasurement(null, null, null));
		m8.Track("011", new CounterMeasurement(null, null, null));
		m8.Track("101", new CounterMeasurement(null, null, null));
		m8.Track("111", new CounterMeasurement(null, null, null));
	
		m1.AddToMeasurement("001", 1);
		m1.AddToMeasurement("011", 1);
		m1.AddToMeasurement("101", 1);
		m1.AddToMeasurement("111", 1);
		m2.AddToMeasurement("001", 1);
		m2.AddToMeasurement("011", 1);
		m2.AddToMeasurement("101", 1);
		m2.AddToMeasurement("111", 1);
		m3.AddToMeasurement("001", 1);
		m3.AddToMeasurement("011", 1);
		m3.AddToMeasurement("101", 1);
		m3.AddToMeasurement("111", 1);
		m4.AddToMeasurement("001", 1);
		m4.AddToMeasurement("011", 1);
		m4.AddToMeasurement("101", 1);
		m4.AddToMeasurement("111", 1);
		m5.AddToMeasurement("001", 1);
		m5.AddToMeasurement("011", 1);
		m5.AddToMeasurement("101", 1);
		m5.AddToMeasurement("111", 1);
		m6.AddToMeasurement("001", 1);
		m6.AddToMeasurement("011", 1);
		m6.AddToMeasurement("101", 1);
		m6.AddToMeasurement("111", 1);
		m7.AddToMeasurement("001", 1);
		m7.AddToMeasurement("011", 1);
		m7.AddToMeasurement("101", 1);
		m7.AddToMeasurement("111", 1);
		m8.AddToMeasurement("001", 1);
		m8.AddToMeasurement("011", 1);
		m8.AddToMeasurement("101", 1);
		m8.AddToMeasurement("111", 1);
	
		c1 = new Metrics("a.A");
		c2 = new Metrics("a.B");
		c3 = new Metrics("b.A");
		c4 = new Metrics("b.B");

		c1.Track("010", new CounterMeasurement(null, null, null));
		c1.Track("011", new CounterMeasurement(null, null, null));
		c1.Track("110", new CounterMeasurement(null, null, null));
		c1.Track("111", new CounterMeasurement(null, null, null));
		c2.Track("010", new CounterMeasurement(null, null, null));
		c2.Track("011", new CounterMeasurement(null, null, null));
		c2.Track("110", new CounterMeasurement(null, null, null));
		c2.Track("111", new CounterMeasurement(null, null, null));
		c3.Track("010", new CounterMeasurement(null, null, null));
		c3.Track("011", new CounterMeasurement(null, null, null));
		c3.Track("110", new CounterMeasurement(null, null, null));
		c3.Track("111", new CounterMeasurement(null, null, null));
		c4.Track("010", new CounterMeasurement(null, null, null));
		c4.Track("011", new CounterMeasurement(null, null, null));
		c4.Track("110", new CounterMeasurement(null, null, null));
		c4.Track("111", new CounterMeasurement(null, null, null));
	
		c1.AddToMeasurement("010", 10);
		c1.AddToMeasurement("011", 10);
		c1.AddToMeasurement("110", 10);
		c1.AddToMeasurement("111", 10);
		c2.AddToMeasurement("010", 10);
		c2.AddToMeasurement("011", 10);
		c2.AddToMeasurement("110", 10);
		c2.AddToMeasurement("111", 10);
		c3.AddToMeasurement("010", 10);
		c3.AddToMeasurement("011", 10);
		c3.AddToMeasurement("110", 10);
		c3.AddToMeasurement("111", 10);
		c4.AddToMeasurement("010", 10);
		c4.AddToMeasurement("011", 10);
		c4.AddToMeasurement("110", 10);
		c4.AddToMeasurement("111", 10);
	
		c1.AddSubMetrics(m1);
		c1.AddSubMetrics(m2);
		c2.AddSubMetrics(m3);
		c2.AddSubMetrics(m4);
		c3.AddSubMetrics(m5);
		c3.AddSubMetrics(m6);
		c4.AddSubMetrics(m7);
		c4.AddSubMetrics(m8);

		g1 = new Metrics("a");
		g2 = new Metrics("b");

		g1.Track("100", new CounterMeasurement(null, null, null));
		g1.Track("101", new CounterMeasurement(null, null, null));
		g1.Track("110", new CounterMeasurement(null, null, null));
		g1.Track("111", new CounterMeasurement(null, null, null));
		g2.Track("100", new CounterMeasurement(null, null, null));
		g2.Track("101", new CounterMeasurement(null, null, null));
		g2.Track("110", new CounterMeasurement(null, null, null));
		g2.Track("111", new CounterMeasurement(null, null, null));
		
		g1.AddToMeasurement("100", 100);
		g1.AddToMeasurement("101", 100);
		g1.AddToMeasurement("110", 100);
		g1.AddToMeasurement("111", 100);
		g2.AddToMeasurement("100", 100);
		g2.AddToMeasurement("101", 100);
		g2.AddToMeasurement("110", 100);
		g2.AddToMeasurement("111", 100);
		
		g1.AddSubMetrics(c1);
		g1.AddSubMetrics(c2);
		g2.AddSubMetrics(c3);
		g2.AddSubMetrics(c4);

		p = new Metrics("test");
		
		p.AddSubMetrics(g1);
		p.AddSubMetrics(g2);
	}

	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).info("End of " + getName());
	}

	public void testProject() {
		StatisticalMeasurement m000 = new StatisticalMeasurement(null, p, "000");
		StatisticalMeasurement m001 = new StatisticalMeasurement(null, p, "001");
		StatisticalMeasurement m010 = new StatisticalMeasurement(null, p, "010");
		StatisticalMeasurement m011 = new StatisticalMeasurement(null, p, "011");
		StatisticalMeasurement m100 = new StatisticalMeasurement(null, p, "100");
		StatisticalMeasurement m101 = new StatisticalMeasurement(null, p, "101");
		StatisticalMeasurement m110 = new StatisticalMeasurement(null, p, "110");
		StatisticalMeasurement m111 = new StatisticalMeasurement(null, p, "111");

		assertEquals("000", 0, m000.NbDataPoints());
		assertEquals("001", 8, m001.NbDataPoints());
		assertEquals("010", 4, m010.NbDataPoints());
		assertEquals("011", 4, m011.NbDataPoints());
		assertEquals("100", 2, m100.NbDataPoints());
		assertEquals("101", 2, m101.NbDataPoints());
		assertEquals("110", 2, m110.NbDataPoints());
		assertEquals("111", 2, m111.NbDataPoints());
	}

	public void testGroup() {
		StatisticalMeasurement m000 = new StatisticalMeasurement(null, g1, "000");
		StatisticalMeasurement m001 = new StatisticalMeasurement(null, g1, "001");
		StatisticalMeasurement m010 = new StatisticalMeasurement(null, g1, "010");
		StatisticalMeasurement m011 = new StatisticalMeasurement(null, g1, "011");
		StatisticalMeasurement m100 = new StatisticalMeasurement(null, g1, "100");
		StatisticalMeasurement m101 = new StatisticalMeasurement(null, g1, "101");
		StatisticalMeasurement m110 = new StatisticalMeasurement(null, g1, "110");
		StatisticalMeasurement m111 = new StatisticalMeasurement(null, g1, "111");

		assertEquals("000", 0, m000.NbDataPoints());
		assertEquals("001", 4, m001.NbDataPoints());
		assertEquals("010", 2, m010.NbDataPoints());
		assertEquals("011", 2, m011.NbDataPoints());
		assertEquals("100", 0, m100.NbDataPoints());
		assertEquals("101", 4, m101.NbDataPoints());
		assertEquals("110", 2, m110.NbDataPoints());
		assertEquals("111", 2, m111.NbDataPoints());
	}

	public void testClass() {
		StatisticalMeasurement m000 = new StatisticalMeasurement(null, c1, "000");
		StatisticalMeasurement m001 = new StatisticalMeasurement(null, c1, "001");
		StatisticalMeasurement m010 = new StatisticalMeasurement(null, c1, "010");
		StatisticalMeasurement m011 = new StatisticalMeasurement(null, c1, "011");
		StatisticalMeasurement m100 = new StatisticalMeasurement(null, c1, "100");
		StatisticalMeasurement m101 = new StatisticalMeasurement(null, c1, "101");
		StatisticalMeasurement m110 = new StatisticalMeasurement(null, c1, "110");
		StatisticalMeasurement m111 = new StatisticalMeasurement(null, c1, "111");

		assertEquals("000", 0, m000.NbDataPoints());
		assertEquals("001", 2, m001.NbDataPoints());
		assertEquals("010", 0, m010.NbDataPoints());
		assertEquals("011", 2, m011.NbDataPoints());
		assertEquals("100", 0, m100.NbDataPoints());
		assertEquals("101", 2, m101.NbDataPoints());
		assertEquals("110", 0, m110.NbDataPoints());
		assertEquals("111", 2, m111.NbDataPoints());
	}

	public void testMethod() {
		StatisticalMeasurement m000 = new StatisticalMeasurement(null, m1, "000");
		StatisticalMeasurement m001 = new StatisticalMeasurement(null, m1, "001");
		StatisticalMeasurement m010 = new StatisticalMeasurement(null, m1, "010");
		StatisticalMeasurement m011 = new StatisticalMeasurement(null, m1, "011");
		StatisticalMeasurement m100 = new StatisticalMeasurement(null, m1, "100");
		StatisticalMeasurement m101 = new StatisticalMeasurement(null, m1, "101");
		StatisticalMeasurement m110 = new StatisticalMeasurement(null, m1, "110");
		StatisticalMeasurement m111 = new StatisticalMeasurement(null, m1, "111");

		assertEquals("000", 0, m000.NbDataPoints());
		assertEquals("001", 0, m001.NbDataPoints());
		assertEquals("010", 0, m010.NbDataPoints());
		assertEquals("011", 0, m011.NbDataPoints());
		assertEquals("100", 0, m100.NbDataPoints());
		assertEquals("101", 0, m101.NbDataPoints());
		assertEquals("110", 0, m110.NbDataPoints());
		assertEquals("111", 0, m111.NbDataPoints());
	}

	public void testIrregular() {
		Metrics m11 = new Metrics("m11");
		Metrics m12 = new Metrics("m12");
		Metrics m21 = new Metrics("m21");
		Metrics m22 = new Metrics("m22");

		Metrics c1 = new Metrics("c1");
		Metrics c2 = new Metrics("c2");

		c1.AddSubMetrics(m11);
		c1.AddSubMetrics(m12);
		c2.AddSubMetrics(m21);
		c2.AddSubMetrics(m22);
		
		Metrics g = new Metrics("g");
		
		g.AddSubMetrics(c1);
		g.AddSubMetrics(c2);

		m11.Track("bar", new CounterMeasurement(null, null, null));
		m12.Track("bar", new CounterMeasurement(null, null, null));
		m21.Track("bar", new CounterMeasurement(null, null, null));
		m22.Track("bar", new CounterMeasurement(null, null, null));

		c1.Track("bar", new CounterMeasurement(null, null, null));

		StatisticalMeasurement sm = new StatisticalMeasurement(null, g, "bar");
		assertEquals(3, sm.NbDataPoints());
	}
}
