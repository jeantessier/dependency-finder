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

import java.io.*;

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

	public TestStatisticalMeasurementWithMetrics(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		Category.getInstance(getClass().getName()).info("Starting test: " + getName());

		m1 = new Metrics("a.A.a");
		m2 = new Metrics("a.A.b");
		m3 = new Metrics("a.B.a");
		m4 = new Metrics("a.B.b");
		m5 = new Metrics("b.A.a");
		m6 = new Metrics("b.A.b");
		m7 = new Metrics("b.B.a");
		m8 = new Metrics("b.B.b");

		m1.TrackMetric("001", 1.0);
		m1.TrackMetric("011", 1.0);
		m1.TrackMetric("101", 1.0);
		m1.TrackMetric("111", 1.0);
		m2.TrackMetric("001", 1.0);
		m2.TrackMetric("011", 1.0);
		m2.TrackMetric("101", 1.0);
		m2.TrackMetric("111", 1.0);
		m3.TrackMetric("001", 1.0);
		m3.TrackMetric("011", 1.0);
		m3.TrackMetric("101", 1.0);
		m3.TrackMetric("111", 1.0);
		m4.TrackMetric("001", 1.0);
		m4.TrackMetric("011", 1.0);
		m4.TrackMetric("101", 1.0);
		m4.TrackMetric("111", 1.0);
		m5.TrackMetric("001", 1.0);
		m5.TrackMetric("011", 1.0);
		m5.TrackMetric("101", 1.0);
		m5.TrackMetric("111", 1.0);
		m6.TrackMetric("001", 1.0);
		m6.TrackMetric("011", 1.0);
		m6.TrackMetric("101", 1.0);
		m6.TrackMetric("111", 1.0);
		m7.TrackMetric("001", 1.0);
		m7.TrackMetric("011", 1.0);
		m7.TrackMetric("101", 1.0);
		m7.TrackMetric("111", 1.0);
		m8.TrackMetric("001", 1.0);
		m8.TrackMetric("011", 1.0);
		m8.TrackMetric("101", 1.0);
		m8.TrackMetric("111", 1.0);
	
		c1 = new Metrics("a.A");
		c2 = new Metrics("a.B");
		c3 = new Metrics("b.A");
		c4 = new Metrics("b.B");

		c1.TrackMetric("010", 10.0);
		c1.TrackMetric("011", 10.0);
		c1.TrackMetric("110", 10.0);
		c1.TrackMetric("111", 10.0);
		c2.TrackMetric("010", 10.0);
		c2.TrackMetric("011", 10.0);
		c2.TrackMetric("110", 10.0);
		c2.TrackMetric("111", 10.0);
		c3.TrackMetric("010", 10.0);
		c3.TrackMetric("011", 10.0);
		c3.TrackMetric("110", 10.0);
		c3.TrackMetric("111", 10.0);
		c4.TrackMetric("010", 10.0);
		c4.TrackMetric("011", 10.0);
		c4.TrackMetric("110", 10.0);
		c4.TrackMetric("111", 10.0);
	
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

		g1.TrackMetric("100", 100.0);
		g1.TrackMetric("101", 100.0);
		g1.TrackMetric("110", 100.0);
		g1.TrackMetric("111", 100.0);
		g2.TrackMetric("100", 100.0);
		g2.TrackMetric("101", 100.0);
		g2.TrackMetric("110", 100.0);
		g2.TrackMetric("111", 100.0);
		
		g1.AddSubMetrics(c1);
		g1.AddSubMetrics(c2);
		g2.AddSubMetrics(c3);
		g2.AddSubMetrics(c4);

		p = new Metrics("test");
		
		p.AddSubMetrics(g1);
		p.AddSubMetrics(g2);
	}

	protected void tearDown() throws Exception {
		Category.getInstance(getClass().getName()).info("End of " + getName());
	}

	public void testProject() {
		StatisticalMeasurement m000 = new StatisticalMeasurement("foo", "000", p);
		StatisticalMeasurement m001 = new StatisticalMeasurement("foo", "001", p);
		StatisticalMeasurement m010 = new StatisticalMeasurement("foo", "010", p);
		StatisticalMeasurement m011 = new StatisticalMeasurement("foo", "011", p);
		StatisticalMeasurement m100 = new StatisticalMeasurement("foo", "100", p);
		StatisticalMeasurement m101 = new StatisticalMeasurement("foo", "101", p);
		StatisticalMeasurement m110 = new StatisticalMeasurement("foo", "110", p);
		StatisticalMeasurement m111 = new StatisticalMeasurement("foo", "111", p);

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
		StatisticalMeasurement m000 = new StatisticalMeasurement("foo", "000", g1);
		StatisticalMeasurement m001 = new StatisticalMeasurement("foo", "001", g1);
		StatisticalMeasurement m010 = new StatisticalMeasurement("foo", "010", g1);
		StatisticalMeasurement m011 = new StatisticalMeasurement("foo", "011", g1);
		StatisticalMeasurement m100 = new StatisticalMeasurement("foo", "100", g1);
		StatisticalMeasurement m101 = new StatisticalMeasurement("foo", "101", g1);
		StatisticalMeasurement m110 = new StatisticalMeasurement("foo", "110", g1);
		StatisticalMeasurement m111 = new StatisticalMeasurement("foo", "111", g1);

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
		StatisticalMeasurement m000 = new StatisticalMeasurement("foo", "000", c1);
		StatisticalMeasurement m001 = new StatisticalMeasurement("foo", "001", c1);
		StatisticalMeasurement m010 = new StatisticalMeasurement("foo", "010", c1);
		StatisticalMeasurement m011 = new StatisticalMeasurement("foo", "011", c1);
		StatisticalMeasurement m100 = new StatisticalMeasurement("foo", "100", c1);
		StatisticalMeasurement m101 = new StatisticalMeasurement("foo", "101", c1);
		StatisticalMeasurement m110 = new StatisticalMeasurement("foo", "110", c1);
		StatisticalMeasurement m111 = new StatisticalMeasurement("foo", "111", c1);

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
		StatisticalMeasurement m000 = new StatisticalMeasurement("foo", "000", m1);
		StatisticalMeasurement m001 = new StatisticalMeasurement("foo", "001", m1);
		StatisticalMeasurement m010 = new StatisticalMeasurement("foo", "010", m1);
		StatisticalMeasurement m011 = new StatisticalMeasurement("foo", "011", m1);
		StatisticalMeasurement m100 = new StatisticalMeasurement("foo", "100", m1);
		StatisticalMeasurement m101 = new StatisticalMeasurement("foo", "101", m1);
		StatisticalMeasurement m110 = new StatisticalMeasurement("foo", "110", m1);
		StatisticalMeasurement m111 = new StatisticalMeasurement("foo", "111", m1);

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

		m11.TrackMetric("bar");
		m12.TrackMetric("bar");
		m21.TrackMetric("bar");
		m22.TrackMetric("bar");

		c1.TrackMetric("bar");

		StatisticalMeasurement sm = new StatisticalMeasurement("foo", "bar", g);
		assertEquals(3, sm.NbDataPoints());
	}
}
