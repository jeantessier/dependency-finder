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

public class TestStatisticalMeasurement extends TestCase implements MeasurementVisitor {
	private StatisticalMeasurement measurement;
	private Metrics metrics;
	private Measurement visited;
	
	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).info("Starting test: " + getName());

		metrics = new Metrics("foo");
		measurement = new StatisticalMeasurement(null, metrics, "bar");
	}
	
	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).info("End of " + getName());
	}

	public void testAdd() {
		measurement.Add(new Integer(1));

		assertEquals(0, measurement.NbDataPoints());
	}

	public void testComputeEmpty() {
		assertEquals("size", 0, measurement.NbDataPoints());
		assertTrue("minimum", Double.isNaN(measurement.Minimum()));
		assertTrue("median", Double.isNaN(measurement.Median()));
		assertTrue("average", Double.isNaN(measurement.Average()));
		assertTrue("standard deviation", Double.isNaN(measurement.StandardDeviation()));
		assertTrue("maximum", Double.isNaN(measurement.Maximum()));
		assertEquals("sum", 0.0, measurement.Sum(), 0.01);
	}

	public void testComputeSingle() {
		Metrics m = new Metrics("m");
		m.Track("bar", new CounterMeasurement(null, null, null));
		m.AddToMeasurement("bar", 1);

		metrics.AddSubMetrics(m);

		assertEquals("size",               1,   measurement.NbDataPoints());
		assertEquals("minimum",            1.0, measurement.Minimum(), 0.01);
		assertEquals("median",             1.0, measurement.Median(),  0.01);
		assertEquals("average",            1.0, measurement.Average(), 0.01);
		assertEquals("standard deviation", 0.0, measurement.StandardDeviation(), 0.01);
		assertEquals("maximum",            1.0, measurement.Maximum(), 0.01);
		assertEquals("sum",                1.0, measurement.Sum(),     0.01);
	}

	public void testComputePair() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		
		m1.Track("bar", new CounterMeasurement(null, null, null));
		m2.Track("bar", new CounterMeasurement(null, null, null));

		m1.AddToMeasurement("bar", 1);
		m2.AddToMeasurement("bar", 100);

		assertEquals("size",      2,   measurement.NbDataPoints());
		assertEquals("minimum",   1.0, measurement.Minimum(), 0.01);
		assertEquals("median",  100.0, measurement.Median(),  0.01);
		assertEquals("average",  50.5, measurement.Average(), 0.01);
		assertEquals("standard deviation", 49.5, measurement.StandardDeviation(), 0.01);
		assertEquals("maximum", 100.0, measurement.Maximum(), 0.01);
		assertEquals("sum",     101.0, measurement.Sum(),     0.01);
	}

	public void testComputeTriple() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");
		Metrics m3 = new Metrics("m3");

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);
		
		m1.Track("bar", new CounterMeasurement(null, null, null));
		m2.Track("bar", new CounterMeasurement(null, null, null));
		m3.Track("bar", new CounterMeasurement(null, null, null));

		m1.AddToMeasurement("bar", 1);
		m2.AddToMeasurement("bar", 10);
		m3.AddToMeasurement("bar", 100);

		assertEquals("size",      3,   measurement.NbDataPoints());
		assertEquals("minimum",   1.0, measurement.Minimum(), 0.01);
		assertEquals("median",   10.0, measurement.Median(),  0.01);
		assertEquals("average",  37.0, measurement.Average(), 0.01);
		assertEquals("standard deviation", 44.7, measurement.StandardDeviation(), 0.01);
		assertEquals("maximum", 100.0, measurement.Maximum(), 0.01);
		assertEquals("sum",     111.0, measurement.Sum(),     0.01);
	}

	public void testComputeDie() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");
		Metrics m3 = new Metrics("m3");
		Metrics m4 = new Metrics("m4");
		Metrics m5 = new Metrics("m5");
		Metrics m6 = new Metrics("m6");

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);
		metrics.AddSubMetrics(m4);
		metrics.AddSubMetrics(m5);
		metrics.AddSubMetrics(m6);
		
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

		assertEquals("size",                6,    measurement.NbDataPoints());
		assertEquals("minimum",             1.0,  measurement.Minimum(), 0.01);
		assertEquals("median",              4.0,  measurement.Median(),  0.01);
		assertEquals("average",             3.5,  measurement.Average(), 0.01);
		assertEquals("standard deviation",  1.71, measurement.StandardDeviation(), 0.01);
		assertEquals("maximum",             6.0,  measurement.Maximum(), 0.01);
		assertEquals("sum",                21.0,  measurement.Sum(),     0.01);
	}

	public void testComputeConstant() {
		Metrics m1 = new Metrics("m1");
		Metrics m2 = new Metrics("m2");
		Metrics m3 = new Metrics("m3");
		Metrics m4 = new Metrics("m4");
		Metrics m5 = new Metrics("m5");
		Metrics m6 = new Metrics("m6");

		metrics.AddSubMetrics(m1);
		metrics.AddSubMetrics(m2);
		metrics.AddSubMetrics(m3);
		metrics.AddSubMetrics(m4);
		metrics.AddSubMetrics(m5);
		metrics.AddSubMetrics(m6);
		
		m1.Track("bar", new CounterMeasurement(null, null, null));
		m2.Track("bar", new CounterMeasurement(null, null, null));
		m3.Track("bar", new CounterMeasurement(null, null, null));
		m4.Track("bar", new CounterMeasurement(null, null, null));
		m5.Track("bar", new CounterMeasurement(null, null, null));
		m6.Track("bar", new CounterMeasurement(null, null, null));

		m1.AddToMeasurement("bar", 1);
		m2.AddToMeasurement("bar", 1);
		m3.AddToMeasurement("bar", 1);
		m4.AddToMeasurement("bar", 1);
		m5.AddToMeasurement("bar", 1);
		m6.AddToMeasurement("bar", 1);

		assertEquals("size",               6,    measurement.NbDataPoints());
		assertEquals("minimum",            1.0,  measurement.Minimum(), 0.01);
		assertEquals("median",             1.0,  measurement.Median(),  0.01);
		assertEquals("average",            1.0,  measurement.Average(), 0.01);
		assertEquals("standard deviation", 0.0, measurement.StandardDeviation(), 0.01);
		assertEquals("maximum",            1.0,  measurement.Maximum(), 0.01);
		assertEquals("sum",                6.0,  measurement.Sum(),     0.01);
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

		metrics.AddSubMetrics(m01);
		metrics.AddSubMetrics(m02);
		metrics.AddSubMetrics(m03);
		metrics.AddSubMetrics(m04);
		metrics.AddSubMetrics(m05);
		metrics.AddSubMetrics(m06);
		metrics.AddSubMetrics(m07);
		metrics.AddSubMetrics(m08);
		metrics.AddSubMetrics(m09);
		metrics.AddSubMetrics(m10);
		metrics.AddSubMetrics(m11);
		
		m01.Track("bar", new CounterMeasurement(null, null, null));
		m02.Track("bar", new CounterMeasurement(null, null, null));
		m03.Track("bar", new CounterMeasurement(null, null, null));
		m04.Track("bar", new CounterMeasurement(null, null, null));
		m05.Track("bar", new CounterMeasurement(null, null, null));
		m06.Track("bar", new CounterMeasurement(null, null, null));
		m07.Track("bar", new CounterMeasurement(null, null, null));
		m08.Track("bar", new CounterMeasurement(null, null, null));
		m09.Track("bar", new CounterMeasurement(null, null, null));
		m10.Track("bar", new CounterMeasurement(null, null, null));
		m11.Track("bar", new CounterMeasurement(null, null, null));

		m01.AddToMeasurement("bar", 1);
		m02.AddToMeasurement("bar", 2);
		m03.AddToMeasurement("bar", 4);
		m04.AddToMeasurement("bar", 8);
		m05.AddToMeasurement("bar", 16);
		m06.AddToMeasurement("bar", 32);
		m07.AddToMeasurement("bar", 64);
		m08.AddToMeasurement("bar", 128);
		m09.AddToMeasurement("bar", 256);
		m10.AddToMeasurement("bar", 512);
		m11.AddToMeasurement("bar", 1024);

		assertEquals("size",                 11,    measurement.NbDataPoints());
		assertEquals("minimum",               1.0,  measurement.Minimum(), 0.01);
		assertEquals("median",               32.0,  measurement.Median(),  0.01);
		assertEquals("average",             186.1,  measurement.Average(), 0.01);
		assertEquals("standard deviation",  304.09, measurement.StandardDeviation(), 0.01);
		assertEquals("maximum",            1024.0,  measurement.Maximum(), 0.01);
		assertEquals("sum",                2047.0,  measurement.Sum(),     0.01);
	}

	public void testAccept() {
		visited = null;
		measurement.Accept(this);
		assertSame(measurement, visited);
	}

	public void testToString() {
		Metrics m = new Metrics("m");
		m.Track("bar", new CounterMeasurement(null, null, null));
		m.AddToMeasurement("bar", 1);

		metrics.AddSubMetrics(m);

		assertEquals("toString()", "[1 1/1 0 1 1 (1)]", measurement.toString());
	}

	public void testDisposeLabel() {
		assertEquals("StatisticalMeasurement.DISPOSE_IGNORE",             "",                      StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_IGNORE));
		assertEquals("StatisticalMeasurement.DISPOSE_MINIMUM",            "minimum",               StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_MINIMUM));
		assertEquals("StatisticalMeasurement.DISPOSE_MEDIAN",             "median",                StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_MEDIAN));
		assertEquals("StatisticalMeasurement.DISPOSE_AVERAGE",            "average",               StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_AVERAGE));
		assertEquals("StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION", "standard deviation",    StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION));
		assertEquals("StatisticalMeasurement.DISPOSE_MAXIMUM",            "maximum",               StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_MAXIMUM));
		assertEquals("StatisticalMeasurement.DISPOSE_SUM",                "sum",                   StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_SUM));
		assertEquals("StatisticalMeasurement.DISPOSE_NB_DATA_POINTS",     "number of data points", StatisticalMeasurement.DisposeLabel(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS));
	}
	
	public void testDisposeAbbreviation() {
		assertEquals("StatisticalMeasurement.DISPOSE_IGNORE",             "",    StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_IGNORE));
		assertEquals("StatisticalMeasurement.DISPOSE_MINIMUM",            "min", StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_MINIMUM));
		assertEquals("StatisticalMeasurement.DISPOSE_MEDIAN",             "med", StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_MEDIAN));
		assertEquals("StatisticalMeasurement.DISPOSE_AVERAGE",            "avg", StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_AVERAGE));
		assertEquals("StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION", "sdv", StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION));
		assertEquals("StatisticalMeasurement.DISPOSE_MAXIMUM",            "max", StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_MAXIMUM));
		assertEquals("StatisticalMeasurement.DISPOSE_SUM",                "sum", StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_SUM));
		assertEquals("StatisticalMeasurement.DISPOSE_NB_DATA_POINTS",     "nb",  StatisticalMeasurement.DisposeAbbreviation(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS));
	}

	public void testEmpty() throws Exception {
		assertTrue("Before AddSubMetrics()", measurement.Empty());
		
		Metrics m = new Metrics("m");
		m.Track("bar", new CounterMeasurement(null, null, null));
		m.AddToMeasurement("bar", 1);

		metrics.AddSubMetrics(m);

		assertFalse("After AddSubMetrics()", measurement.Empty());
	}

	public void VisitStatisticalMeasurement(StatisticalMeasurement measurement) {
		visited = measurement;
	}
	
	public void VisitRatioMeasurement(RatioMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitCounterMeasurement(CounterMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitNameListMeasurement(NameListMeasurement measurement) {
		// Do nothing
	}
	
	public void VisitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
		// Do nothing
	}

	public void VisitSumMeasurement(SumMeasurement measurement) {
		// Do nothing
	}
}
