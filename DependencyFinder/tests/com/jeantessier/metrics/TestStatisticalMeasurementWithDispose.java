/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

	private MeasurementDescriptor descriptor;
	
	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).info("Starting test: " + getName());

		descriptor = new MeasurementDescriptor();
		descriptor.ShortName("bar");
		
		m1 = new Metrics("m1");
		m2 = new Metrics("m2");
		m3 = new Metrics("m3");
		m4 = new Metrics("m4");
		m5 = new Metrics("m5");
		m6 = new Metrics("m6");

		m1.Track("bar", new CounterMeasurement(null, null, null));
		m2.Track("bar", new CounterMeasurement(null, null, null));
		m3.Track("bar", new CounterMeasurement(null, null, null));
		m4.Track("bar", new CounterMeasurement(null, null, null));
		m5.Track("bar", new CounterMeasurement(null, null, null));
		m6.Track("bar", new CounterMeasurement(null, null, null));
	
		m1.AddToMeasurement("bar", 1);
		m2.AddToMeasurement("bar", 2);
		m3.AddToMeasurement("bar", 3);
		m4.AddToMeasurement("bar", 4);
		m5.AddToMeasurement("bar", 5);
		m6.AddToMeasurement("bar", 6);

		c1 = new Metrics("c1");
		c2 = new Metrics("c2");

		c1.Track("bar", new StatisticalMeasurement(descriptor, c1, "bar"));
		c2.Track("bar", new StatisticalMeasurement(descriptor, c2, "bar"));
		
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
		Logger.getLogger(getClass()).info("End of " + getName());
	}

	public void testDefault() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar");

		assertEquals("size "               + sm,  6,    sm.NbDataPoints());
		assertEquals("Minimum "            + sm,  1.0,  sm.Minimum(), 0.01);
		assertEquals("Median "             + sm,  4.0,  sm.Median(),  0.01);
		assertEquals("Average "            + sm,  3.5,  sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm,  1.71, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm,  6.0,  sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 21.0,  sm.Sum(),     0.01);
	}

	public void testIgnore() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_IGNORE");

		assertEquals("size "               + sm,  6,    sm.NbDataPoints());
		assertEquals("Minimum "            + sm,  1.0,  sm.Minimum(), 0.01);
		assertEquals("Median "             + sm,  4.0,  sm.Median(),  0.01);
		assertEquals("Average "            + sm,  3.5,  sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm,  1.71, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm,  6.0,  sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 21.0,  sm.Sum(),     0.01);
	}

	public void testMinimum() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_MINIMUM");

		assertEquals("size "               + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum "            + sm, 1.0, sm.Minimum(), 0.01);
		assertEquals("Median "             + sm, 3.0, sm.Median(),  0.01);
		assertEquals("Average "            + sm, 2.0, sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm, 1.0, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm, 3.0, sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 4.0, sm.Sum(),     0.01);
	}

	public void testMedian() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_MEDIAN");

		assertEquals("size "               + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum "            + sm, 2.0, sm.Minimum(), 0.01);
		assertEquals("Median "             + sm, 5.0, sm.Median(),  0.01);
		assertEquals("Average "            + sm, 3.5, sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm, 1.5, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm, 5.0, sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 7.0, sm.Sum(),     0.01);
	}

	public void testAverage() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_AVERAGE");

		assertEquals("size "               + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum "            + sm, 1.5, sm.Minimum(), 0.01);
		assertEquals("Median "             + sm, 4.5, sm.Median(),  0.01);
		assertEquals("Average "            + sm, 3.0, sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm, 1.5, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm, 4.5, sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 6.0, sm.Sum(),     0.01);
	}

	public void testStandardDeviation() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_STANDARD_DEVIATION");

		assertEquals("size "               + sm, 2,    sm.NbDataPoints());
		assertEquals("Minimum "            + sm, 0.5,  sm.Minimum(),            0.01);
		assertEquals("Median "             + sm, 1.12, sm.Median(),             0.01);
		assertEquals("Average "            + sm, 0.81, sm.Average(),            0.01);
		assertEquals("Standard Deviation " + sm, 0.31, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm, 1.12, sm.Maximum(),            0.01);
		assertEquals("Sum "                + sm, 1.62, sm.Sum(),                0.01);
	}

	public void testMaximum() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_MAXIMUM");

		assertEquals("size "               + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum "            + sm, 2.0, sm.Minimum(), 0.01);
		assertEquals("Median "             + sm, 6.0, sm.Median(),  0.01);
		assertEquals("Average "            + sm, 4.0, sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm, 2.0, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm, 6.0, sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 8.0, sm.Sum(),     0.01);
	}

	public void testSum() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_SUM");

		assertEquals("size "               + sm,  2,   sm.NbDataPoints());
		assertEquals("Minimum "            + sm,  3.0, sm.Minimum(), 0.01);
		assertEquals("Median "             + sm, 18.0, sm.Median(),  0.01);
		assertEquals("Average "            + sm, 10.5, sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm,  7.5, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm, 18.0, sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 21.0, sm.Sum(),     0.01);
	}

	public void testNbDataPoints() {
		StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_NB_DATA_POINTS");

		assertEquals("size "               + sm, 2,   sm.NbDataPoints());
		assertEquals("Minimum "            + sm, 2.0, sm.Minimum(), 0.01);
		assertEquals("Median "             + sm, 4.0, sm.Median(),  0.01);
		assertEquals("Average "            + sm, 3.0, sm.Average(), 0.01);
		assertEquals("Standard Deviation " + sm, 1.0, sm.StandardDeviation(),  0.01);
		assertEquals("Maximum "            + sm, 4.0, sm.Maximum(), 0.01);
		assertEquals("Sum "                + sm, 6.0, sm.Sum(),     0.01);
	}
}
