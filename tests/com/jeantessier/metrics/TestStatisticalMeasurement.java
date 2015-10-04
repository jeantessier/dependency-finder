/*
 *  Copyright (c) 2001-2009, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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
        measurement.add(new Integer(1));

        assertEquals(0, measurement.getNbDataPoints());
    }

    public void testComputeEmpty() {
        assertEquals("size", 0, measurement.getNbDataPoints());
        assertTrue("minimum", Double.isNaN(measurement.getMinimum()));
        assertTrue("median", Double.isNaN(measurement.getMedian()));
        assertTrue("average", Double.isNaN(measurement.getAverage()));
        assertTrue("standard deviation", Double.isNaN(measurement.getStandardDeviation()));
        assertTrue("maximum", Double.isNaN(measurement.getMaximum()));
        assertEquals("sum", 0.0, measurement.getSum(), 0.01);
    }

    public void testComputeSingle() {
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertEquals("size",               1,   measurement.getNbDataPoints());
        assertEquals("minimum",            1.0, measurement.getMinimum(), 0.01);
        assertEquals("median",             1.0, measurement.getMedian(),  0.01);
        assertEquals("average",            1.0, measurement.getAverage(), 0.01);
        assertEquals("standard deviation", 0.0, measurement.getStandardDeviation(), 0.01);
        assertEquals("maximum",            1.0, measurement.getMaximum(), 0.01);
        assertEquals("sum",                1.0, measurement.getSum(),     0.01);
    }

    public void testComputePair() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        
        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("bar", 1);
        m2.addToMeasurement("bar", 100);

        assertEquals("size",      2,   measurement.getNbDataPoints());
        assertEquals("minimum",   1.0, measurement.getMinimum(), 0.01);
        assertEquals("median",  100.0, measurement.getMedian(),  0.01);
        assertEquals("average",  50.5, measurement.getAverage(), 0.01);
        assertEquals("standard deviation", 49.5, measurement.getStandardDeviation(), 0.01);
        assertEquals("maximum", 100.0, measurement.getMaximum(), 0.01);
        assertEquals("sum",     101.0, measurement.getSum(),     0.01);
    }

    public void testComputeTriple() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");
        Metrics m3 = new Metrics("m3");

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);
        
        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));
        m3.track("bar", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("bar", 1);
        m2.addToMeasurement("bar", 10);
        m3.addToMeasurement("bar", 100);

        assertEquals("size",      3,   measurement.getNbDataPoints());
        assertEquals("minimum",   1.0, measurement.getMinimum(), 0.01);
        assertEquals("median",   10.0, measurement.getMedian(),  0.01);
        assertEquals("average",  37.0, measurement.getAverage(), 0.01);
        assertEquals("standard deviation", 44.7, measurement.getStandardDeviation(), 0.01);
        assertEquals("maximum", 100.0, measurement.getMaximum(), 0.01);
        assertEquals("sum",     111.0, measurement.getSum(),     0.01);
    }

    public void testComputeDie() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");
        Metrics m3 = new Metrics("m3");
        Metrics m4 = new Metrics("m4");
        Metrics m5 = new Metrics("m5");
        Metrics m6 = new Metrics("m6");

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);
        metrics.addSubMetrics(m4);
        metrics.addSubMetrics(m5);
        metrics.addSubMetrics(m6);
        
        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));
        m3.track("bar", new CounterMeasurement(null, null, null));
        m4.track("bar", new CounterMeasurement(null, null, null));
        m5.track("bar", new CounterMeasurement(null, null, null));
        m6.track("bar", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("bar", 1);
        m2.addToMeasurement("bar", 2);
        m3.addToMeasurement("bar", 3);
        m4.addToMeasurement("bar", 4);
        m5.addToMeasurement("bar", 5);
        m6.addToMeasurement("bar", 6);

        assertEquals("size",                6,    measurement.getNbDataPoints());
        assertEquals("minimum",             1.0,  measurement.getMinimum(), 0.01);
        assertEquals("median",              4.0,  measurement.getMedian(),  0.01);
        assertEquals("average",             3.5,  measurement.getAverage(), 0.01);
        assertEquals("standard deviation",  1.71, measurement.getStandardDeviation(), 0.01);
        assertEquals("maximum",             6.0,  measurement.getMaximum(), 0.01);
        assertEquals("sum",                21.0,  measurement.getSum(),     0.01);
    }

    public void testComputeConstant() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");
        Metrics m3 = new Metrics("m3");
        Metrics m4 = new Metrics("m4");
        Metrics m5 = new Metrics("m5");
        Metrics m6 = new Metrics("m6");

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);
        metrics.addSubMetrics(m4);
        metrics.addSubMetrics(m5);
        metrics.addSubMetrics(m6);
        
        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));
        m3.track("bar", new CounterMeasurement(null, null, null));
        m4.track("bar", new CounterMeasurement(null, null, null));
        m5.track("bar", new CounterMeasurement(null, null, null));
        m6.track("bar", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("bar", 1);
        m2.addToMeasurement("bar", 1);
        m3.addToMeasurement("bar", 1);
        m4.addToMeasurement("bar", 1);
        m5.addToMeasurement("bar", 1);
        m6.addToMeasurement("bar", 1);

        assertEquals("size",               6,    measurement.getNbDataPoints());
        assertEquals("minimum",            1.0,  measurement.getMinimum(), 0.01);
        assertEquals("median",             1.0,  measurement.getMedian(),  0.01);
        assertEquals("average",            1.0,  measurement.getAverage(), 0.01);
        assertEquals("standard deviation", 0.0, measurement.getStandardDeviation(), 0.01);
        assertEquals("maximum",            1.0,  measurement.getMaximum(), 0.01);
        assertEquals("sum",                6.0,  measurement.getSum(),     0.01);
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

        metrics.addSubMetrics(m01);
        metrics.addSubMetrics(m02);
        metrics.addSubMetrics(m03);
        metrics.addSubMetrics(m04);
        metrics.addSubMetrics(m05);
        metrics.addSubMetrics(m06);
        metrics.addSubMetrics(m07);
        metrics.addSubMetrics(m08);
        metrics.addSubMetrics(m09);
        metrics.addSubMetrics(m10);
        metrics.addSubMetrics(m11);
        
        m01.track("bar", new CounterMeasurement(null, null, null));
        m02.track("bar", new CounterMeasurement(null, null, null));
        m03.track("bar", new CounterMeasurement(null, null, null));
        m04.track("bar", new CounterMeasurement(null, null, null));
        m05.track("bar", new CounterMeasurement(null, null, null));
        m06.track("bar", new CounterMeasurement(null, null, null));
        m07.track("bar", new CounterMeasurement(null, null, null));
        m08.track("bar", new CounterMeasurement(null, null, null));
        m09.track("bar", new CounterMeasurement(null, null, null));
        m10.track("bar", new CounterMeasurement(null, null, null));
        m11.track("bar", new CounterMeasurement(null, null, null));

        m01.addToMeasurement("bar", 1);
        m02.addToMeasurement("bar", 2);
        m03.addToMeasurement("bar", 4);
        m04.addToMeasurement("bar", 8);
        m05.addToMeasurement("bar", 16);
        m06.addToMeasurement("bar", 32);
        m07.addToMeasurement("bar", 64);
        m08.addToMeasurement("bar", 128);
        m09.addToMeasurement("bar", 256);
        m10.addToMeasurement("bar", 512);
        m11.addToMeasurement("bar", 1024);

        assertEquals("size",                 11,    measurement.getNbDataPoints());
        assertEquals("minimum",               1.0,  measurement.getMinimum(), 0.01);
        assertEquals("median",               32.0,  measurement.getMedian(),  0.01);
        assertEquals("average",             186.1,  measurement.getAverage(), 0.01);
        assertEquals("standard deviation",  304.09, measurement.getStandardDeviation(), 0.01);
        assertEquals("maximum",            1024.0,  measurement.getMaximum(), 0.01);
        assertEquals("sum",                2047.0,  measurement.getSum(),     0.01);
    }

    public void testAccept() {
        visited = null;
        measurement.accept(this);
        assertSame(measurement, visited);
    }

    public void testToString() {
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertEquals("toString()", "[1 1/1 0 1 1 (1)]", measurement.toString());
    }

    public void testDisposeLabel() {
        assertEquals("StatisticalMeasurement.DISPOSE_IGNORE",             "",                      StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_IGNORE));
        assertEquals("StatisticalMeasurement.DISPOSE_MINIMUM",            "minimum",               StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_MINIMUM));
        assertEquals("StatisticalMeasurement.DISPOSE_MEDIAN",             "median",                StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_MEDIAN));
        assertEquals("StatisticalMeasurement.DISPOSE_AVERAGE",            "average",               StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_AVERAGE));
        assertEquals("StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION", "standard deviation",    StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION));
        assertEquals("StatisticalMeasurement.DISPOSE_MAXIMUM",            "maximum",               StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_MAXIMUM));
        assertEquals("StatisticalMeasurement.DISPOSE_SUM",                "sum",                   StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_SUM));
        assertEquals("StatisticalMeasurement.DISPOSE_NB_DATA_POINTS",     "number of data points", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS));
    }
    
    public void testDisposeAbbreviation() {
        assertEquals("StatisticalMeasurement.DISPOSE_IGNORE",             "",    StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_IGNORE));
        assertEquals("StatisticalMeasurement.DISPOSE_MINIMUM",            "min", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_MINIMUM));
        assertEquals("StatisticalMeasurement.DISPOSE_MEDIAN",             "med", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_MEDIAN));
        assertEquals("StatisticalMeasurement.DISPOSE_AVERAGE",            "avg", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_AVERAGE));
        assertEquals("StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION", "sdv", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION));
        assertEquals("StatisticalMeasurement.DISPOSE_MAXIMUM",            "max", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_MAXIMUM));
        assertEquals("StatisticalMeasurement.DISPOSE_SUM",                "sum", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_SUM));
        assertEquals("StatisticalMeasurement.DISPOSE_NB_DATA_POINTS",     "nb",  StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS));
    }

    public void testEmpty() {
        assertTrue("Before AddSubMetrics()", measurement.isEmpty());
        
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertFalse("After AddSubMetrics()", measurement.isEmpty());
    }

    public void visitStatisticalMeasurement(StatisticalMeasurement measurement) {
        visited = measurement;
    }
    
    public void visitRatioMeasurement(RatioMeasurement measurement) {
        // Do nothing
    }
    
    public void visitNbSubMetricsMeasurement(NbSubMetricsMeasurement measurement) {
        // Do nothing
    }
    
    public void visitCounterMeasurement(CounterMeasurement measurement) {
        // Do nothing
    }
    
    public void visitContextAccumulatorMeasurement(ContextAccumulatorMeasurement measurement) {
        // Do nothing
    }
    
    public void visitNameListMeasurement(NameListMeasurement measurement) {
        // Do nothing
    }
    
    public void visitSubMetricsAccumulatorMeasurement(SubMetricsAccumulatorMeasurement measurement) {
        // Do nothing
    }

    public void visitSumMeasurement(SumMeasurement measurement) {
        // Do nothing
    }
}
