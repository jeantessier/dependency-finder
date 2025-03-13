/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.jmock.Expectations;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestStatisticalMeasurement {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    
    private final Metrics metrics = new Metrics("foo");

    private final StatisticalMeasurement measurement = new StatisticalMeasurement(null, metrics, "bar");

    @Test
    void testAdd() {
        measurement.add(1);

        assertEquals(0, measurement.getNbDataPoints());
    }

    @Test
    void testComputeEmpty() {
        assertEquals(0, measurement.getNbDataPoints(), "size");
        assertTrue(Double.isNaN(measurement.getMinimum()), "minimum");
        assertTrue(Double.isNaN(measurement.getMedian()), "median");
        assertTrue(Double.isNaN(measurement.getAverage()), "average");
        assertTrue(Double.isNaN(measurement.getStandardDeviation()), "standard deviation");
        assertTrue(Double.isNaN(measurement.getMaximum()), "maximum");
        assertEquals(0.0, measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testComputeSingle() {
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertEquals(1, measurement.getNbDataPoints(), "size");
        assertEquals(1.0, measurement.getMinimum(), 0.01, "minimum");
        assertEquals(1.0, measurement.getMedian(), 0.01, "median");
        assertEquals(1.0, measurement.getAverage(), 0.01, "average");
        assertEquals(0.0, measurement.getStandardDeviation(), 0.01, "standard deviation");
        assertEquals(1.0, measurement.getMaximum(), 0.01, "maximum");
        assertEquals(1.0, measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testComputePair() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);

        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("bar", 1);
        m2.addToMeasurement("bar", 100);

        assertEquals(2, measurement.getNbDataPoints(), "size");
        assertEquals(1.0, measurement.getMinimum(), 0.01, "minimum");
        assertEquals(50.5, measurement.getMedian(), 0.01, "median");
        assertEquals(50.5, measurement.getAverage(), 0.01, "average");
        assertEquals(49.5, measurement.getStandardDeviation(), 0.01, "standard deviation");
        assertEquals(100.0, measurement.getMaximum(), 0.01, "maximum");
        assertEquals(101.0, measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testComputeTriplet() {
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

        assertEquals(3, measurement.getNbDataPoints(), "size");
        assertEquals(1.0, measurement.getMinimum(), 0.01, "minimum");
        assertEquals(10.0, measurement.getMedian(), 0.01, "median");
        assertEquals(37.0, measurement.getAverage(), 0.01, "average");
        assertEquals(44.7, measurement.getStandardDeviation(), 0.01, "standard deviation");
        assertEquals(100.0, measurement.getMaximum(), 0.01, "maximum");
        assertEquals(111.0, measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testComputeDie() {
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

        assertEquals(6, measurement.getNbDataPoints(), "size");
        assertEquals(1.0,  measurement.getMinimum(), 0.01, "minimum");
        assertEquals(3.5,  measurement.getMedian(), 0.01, "median");
        assertEquals(3.5,  measurement.getAverage(), 0.01, "average");
        assertEquals(1.71, measurement.getStandardDeviation(), 0.01, "standard deviation");
        assertEquals(6.0,  measurement.getMaximum(), 0.01, "maximum");
        assertEquals(21.0,  measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testComputeConstant() {
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

        assertEquals(6, measurement.getNbDataPoints(), "size");
        assertEquals(1.0, measurement.getMinimum(), 0.01, "minimum");
        assertEquals(1.0, measurement.getMedian(), 0.01, "median");
        assertEquals(1.0, measurement.getAverage(), 0.01, "average");
        assertEquals(0.0, measurement.getStandardDeviation(), 0.01, "standard deviation");
        assertEquals(1.0, measurement.getMaximum(), 0.01, "maximum");
        assertEquals(6.0, measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testComputeExponential() {
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

        assertEquals(11, measurement.getNbDataPoints(), "size");
        assertEquals(1.0,  measurement.getMinimum(), 0.01, "minimum");
        assertEquals(32.0,  measurement.getMedian(), 0.01, "median");
        assertEquals(186.1,  measurement.getAverage(), 0.01, "average");
        assertEquals(304.09, measurement.getStandardDeviation(), 0.01, "standard deviation");
        assertEquals(1024.0,  measurement.getMaximum(), 0.01, "maximum");
        assertEquals(2047.0,  measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testCompute1000() {
        IntStream.rangeClosed(1, 1000).forEach(n -> metrics.addSubMetrics(new Metrics("m" + n).track("bar", new CounterMeasurement(null, null, null)).addToMeasurement("bar", n)));

        assertEquals(1000, measurement.getNbDataPoints(), "size");
        assertEquals(1.0,  measurement.getMinimum(), 0.01, "minimum");
        assertEquals(500.5,  measurement.getMedian(), 0.01, "median");
        assertEquals(500.5,  measurement.getAverage(), 0.01, "average");
        assertEquals(288.67, measurement.getStandardDeviation(), 0.01, "standard deviation");
        assertEquals(1000.0,  measurement.getMaximum(), 0.01, "maximum");
        assertEquals(500500.0,  measurement.getSum(), 0.01, "sum");
    }

    @Test
    void testAccept() {
        var visitor = context.mock(MeasurementVisitor.class);

        context.checking(new Expectations() {{
            oneOf (visitor).visitStatisticalMeasurement(measurement);
        }});

        measurement.accept(visitor);
    }

    @Test
    void testToString() {
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertEquals("[1 1/1 0 1 1 (1)]", measurement.toString(), "toString()");
    }

    @Test
    void testDisposeLabel() {
        assertEquals("", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_IGNORE), "StatisticalMeasurement.DISPOSE_IGNORE");
        assertEquals("minimum", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_MINIMUM), "StatisticalMeasurement.DISPOSE_MINIMUM");
        assertEquals("median", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_MEDIAN), "StatisticalMeasurement.DISPOSE_MEDIAN");
        assertEquals("average", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_AVERAGE), "StatisticalMeasurement.DISPOSE_AVERAGE");
        assertEquals("standard deviation", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION), "StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION");
        assertEquals("maximum", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_MAXIMUM), "StatisticalMeasurement.DISPOSE_MAXIMUM");
        assertEquals("sum", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_SUM), "StatisticalMeasurement.DISPOSE_SUM");
        assertEquals("number of data points", StatisticalMeasurement.getDisposeLabel(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS), "StatisticalMeasurement.DISPOSE_NB_DATA_POINTS");
    }

    @Test
    void testDisposeAbbreviation() {
        assertEquals("", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_IGNORE), "StatisticalMeasurement.DISPOSE_IGNORE");
        assertEquals("min", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_MINIMUM), "StatisticalMeasurement.DISPOSE_MINIMUM");
        assertEquals("med", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_MEDIAN), "StatisticalMeasurement.DISPOSE_MEDIAN");
        assertEquals("avg", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_AVERAGE), "StatisticalMeasurement.DISPOSE_AVERAGE");
        assertEquals("sdv", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION), "StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION");
        assertEquals("max", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_MAXIMUM), "StatisticalMeasurement.DISPOSE_MAXIMUM");
        assertEquals("sum", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_SUM), "StatisticalMeasurement.DISPOSE_SUM");
        assertEquals("nb", StatisticalMeasurement.getDisposeAbbreviation(StatisticalMeasurement.DISPOSE_NB_DATA_POINTS), "StatisticalMeasurement.DISPOSE_NB_DATA_POINTS");
    }

    @Test
    void testEmpty() {
        assertTrue(measurement.isEmpty(), "Before AddSubMetrics()");

        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertFalse(measurement.isEmpty(), "After AddSubMetrics()");
    }
}
