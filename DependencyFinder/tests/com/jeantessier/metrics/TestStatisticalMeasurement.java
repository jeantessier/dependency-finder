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

public class TestStatisticalMeasurement extends TestCase {
	public TestStatisticalMeasurement(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		Category.getInstance(getClass().getName()).info("Starting test: " + getName());
	}
	
	protected void tearDown() throws Exception {
		Category.getInstance(getClass().getName()).info("End of " + getName());
	}

	public void testAdd() {
		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", new Metrics("test"));

		measure.Add(new Integer(1));

		assertEquals(0, measure.NbDataPoints());
	}

	public void testComputeEmpty() {
		Metrics c = new Metrics("c");

		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", c);

		assertEquals("size", 0, measure.NbDataPoints());
		assertTrue("minimum", Double.isNaN(measure.Minimum()));
		assertTrue("median", Double.isNaN(measure.Median()));
		assertTrue("average", Double.isNaN(measure.Average()));
		assertTrue("maximum", Double.isNaN(measure.Maximum()));
		assertEquals("sum", 0.0, measure.Sum(), 0.01);
	}

	public void testComputeSingle() {
		Metrics m = new Metrics("m");
		Metrics c = new Metrics("c");

		c.AddSubMetrics(m);
		
		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", c);

		m.TrackMetric("bar", 1);

		assertEquals("size",    1,   measure.NbDataPoints());
		assertEquals("minimum", 1.0, measure.Minimum(), 0.01);
		assertEquals("median",  1.0, measure.Median(),  0.01);
		assertEquals("average", 1.0, measure.Average(), 0.01);
		assertEquals("maximum", 1.0, measure.Maximum(), 0.01);
		assertEquals("sum",     1.0, measure.Sum(),     0.01);
	}

	public void testComputePair() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");

		Metrics c = new Metrics("c");

		c.AddSubMetrics(m1);
		c.AddSubMetrics(m2);
		
		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", c);

		m1.TrackMetric("bar", 1);
		m2.TrackMetric("bar", 100);

		assertEquals("size",      2,   measure.NbDataPoints());
		assertEquals("minimum",   1.0, measure.Minimum(), 0.01);
		assertEquals("median",  100.0, measure.Median(),  0.01);
		assertEquals("average",  50.5, measure.Average(), 0.01);
		assertEquals("maximum", 100.0, measure.Maximum(), 0.01);
		assertEquals("sum",     101.0, measure.Sum(),     0.01);
	}

	public void testComputeTriple() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");
		Metrics m3 = new Metrics("m3");

		Metrics c = new Metrics("c");

		c.AddSubMetrics(m1);
		c.AddSubMetrics(m2);
		c.AddSubMetrics(m3);
		
		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", c);

		m1.TrackMetric("bar", 1);
		m2.TrackMetric("bar", 10);
		m3.TrackMetric("bar", 100);

		assertEquals("size",      3,   measure.NbDataPoints());
		assertEquals("minimum",   1.0, measure.Minimum(), 0.01);
		assertEquals("median",   10.0, measure.Median(),  0.01);
		assertEquals("average",  37.0, measure.Average(), 0.01);
		assertEquals("maximum", 100.0, measure.Maximum(), 0.01);
		assertEquals("sum",     111.0, measure.Sum(),     0.01);
	}

	public void testComputeDie() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");
		Metrics m3 = new Metrics("m3");
		Metrics m4 = new Metrics("m4");
		Metrics m5 = new Metrics("m5");
		Metrics m6 = new Metrics("m6");

		Metrics c = new Metrics("c");

		c.AddSubMetrics(m1);
		c.AddSubMetrics(m2);
		c.AddSubMetrics(m3);
		c.AddSubMetrics(m4);
		c.AddSubMetrics(m5);
		c.AddSubMetrics(m6);
		
		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", c);

		m1.TrackMetric("bar", 1);
		m2.TrackMetric("bar", 2);
		m3.TrackMetric("bar", 3);
		m4.TrackMetric("bar", 4);
		m5.TrackMetric("bar", 5);
		m6.TrackMetric("bar", 6);

		assertEquals("size",     6,   measure.NbDataPoints());
		assertEquals("minimum",  1.0, measure.Minimum(), 0.01);
		assertEquals("median",   4.0, measure.Median(),  0.01);
		assertEquals("average",  3.5, measure.Average(), 0.01);
		assertEquals("maximum",  6.0, measure.Maximum(), 0.01);
		assertEquals("sum",     21.0, measure.Sum(),     0.01);
	}

	public void testComputeConstant() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");
		Metrics m3 = new Metrics("m3");
		Metrics m4 = new Metrics("m4");
		Metrics m5 = new Metrics("m5");
		Metrics m6 = new Metrics("m6");

		Metrics c = new Metrics("c");

		c.AddSubMetrics(m1);
		c.AddSubMetrics(m2);
		c.AddSubMetrics(m3);
		c.AddSubMetrics(m4);
		c.AddSubMetrics(m5);
		c.AddSubMetrics(m6);
		
		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", c);

		m1.TrackMetric("bar", 1);
		m2.TrackMetric("bar", 1);
		m3.TrackMetric("bar", 1);
		m4.TrackMetric("bar", 1);
		m5.TrackMetric("bar", 1);
		m6.TrackMetric("bar", 1);

		assertEquals("size",    6,   measure.NbDataPoints());
		assertEquals("minimum", 1.0, measure.Minimum(), 0.01);
		assertEquals("median",  1.0, measure.Median(),  0.01);
		assertEquals("average", 1.0, measure.Average(), 0.01);
		assertEquals("maximum", 1.0, measure.Maximum(), 0.01);
		assertEquals("sum",     6.0, measure.Sum(),     0.01);
	}

	public void testComputeExponential() {
		Metrics m01 = new Metrics("m01");
		Metrics m02 = new Metrics("m02");
		Metrics m03 = new Metrics("m03");
		Metrics m04 = new Metrics("m04");
		Metrics m05 = new Metrics("m05");
		Metrics m06 = new Metrics("m06");
		Metrics m07 = new Metrics("m07");
		Metrics m08 = new Metrics("m08");
		Metrics m09 = new Metrics("m09");
		Metrics m10 = new Metrics("m10");
		Metrics m11 = new Metrics("m11");

		Metrics c = new Metrics("c");

		c.AddSubMetrics(m01);
		c.AddSubMetrics(m02);
		c.AddSubMetrics(m03);
		c.AddSubMetrics(m04);
		c.AddSubMetrics(m05);
		c.AddSubMetrics(m06);
		c.AddSubMetrics(m07);
		c.AddSubMetrics(m08);
		c.AddSubMetrics(m09);
		c.AddSubMetrics(m10);
		c.AddSubMetrics(m11);
		
		StatisticalMeasurement measure = new StatisticalMeasurement("foo", "bar", c);

		m01.TrackMetric("bar", 1);
		m02.TrackMetric("bar", 2);
		m03.TrackMetric("bar", 4);
		m04.TrackMetric("bar", 8);
		m05.TrackMetric("bar", 16);
		m06.TrackMetric("bar", 32);
		m07.TrackMetric("bar", 64);
		m08.TrackMetric("bar", 128);
		m09.TrackMetric("bar", 256);
		m10.TrackMetric("bar", 512);
		m11.TrackMetric("bar", 1024);

		assertEquals("size",      11,   measure.NbDataPoints());
		assertEquals("minimum",    1.0, measure.Minimum(), 0.01);
		assertEquals("median",    32.0, measure.Median(),  0.01);
		assertEquals("average",  186.1, measure.Average(), 0.01);
		assertEquals("maximum", 1024.0, measure.Maximum(), 0.01);
		assertEquals("sum",     2047.0, measure.Sum(),     0.01);
	}
}
