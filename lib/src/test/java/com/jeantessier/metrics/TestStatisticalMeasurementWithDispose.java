/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestStatisticalMeasurementWithDispose {
    private final Metrics m1 = new Metrics("m1");
    private final Metrics m2 = new Metrics("m2");
    private final Metrics m3 = new Metrics("m3");
    private final Metrics m4 = new Metrics("m4");
    private final Metrics m5 = new Metrics("m5");
    private final Metrics m6 = new Metrics("m6");

    private final Metrics c1 = new Metrics("c1");
    private final Metrics c2 = new Metrics("c2");

    private final Metrics g = new Metrics("g");

    private final MeasurementDescriptor descriptor = new MeasurementDescriptor();

    @BeforeEach
    void setUp() {
        descriptor.setShortName("bar");

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

        c1.track("bar", new StatisticalMeasurement(descriptor, c1, "bar"));
        c2.track("bar", new StatisticalMeasurement(descriptor, c2, "bar"));

        c1.addSubMetrics(m1);
        c1.addSubMetrics(m2);
        c2.addSubMetrics(m3);
        c2.addSubMetrics(m4);
        c2.addSubMetrics(m5);
        c2.addSubMetrics(m6);

        g.addSubMetrics(c1);
        g.addSubMetrics(c2);
    }

    @Test
    void testDefault() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar");

        assertEquals(6, sm.getNbDataPoints(), "size " + sm);
        assertEquals(1.0,  sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(3.5,  sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(3.5,  sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(1.71, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(6.0,  sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(21.0,  sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testIgnore() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_IGNORE");

        assertEquals(6, sm.getNbDataPoints(), "size " + sm);
        assertEquals(1.0,  sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(3.5,  sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(3.5,  sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(1.71, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(6.0,  sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(21.0,  sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testMinimum() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_MINIMUM");

        assertEquals(2, sm.getNbDataPoints(), "size " + sm);
        assertEquals(1.0, sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(2.0, sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(2.0, sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(1.0, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(3.0, sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(4.0, sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testMedian() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_MEDIAN");

        assertEquals(2, sm.getNbDataPoints(), "size " + sm);
        assertEquals(1.5, sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(3.0, sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(3.0, sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(1.5, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(4.5, sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(6.0, sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testAverage() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_AVERAGE");

        assertEquals(2, sm.getNbDataPoints(), "size " + sm);
        assertEquals(1.5, sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(3.0, sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(3.0, sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(1.5, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(4.5, sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(6.0, sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testStandardDeviation() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_STANDARD_DEVIATION");

        assertEquals(2, sm.getNbDataPoints(), "size " + sm);
        assertEquals(0.5,  sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(0.81, sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(0.81, sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(0.31, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(1.12, sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(1.62, sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testMaximum() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_MAXIMUM");

        assertEquals(2, sm.getNbDataPoints(), "size " + sm);
        assertEquals(2.0, sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(4.0, sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(4.0, sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(2.0, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(6.0, sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(8.0, sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testSum() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_SUM");

        assertEquals(2, sm.getNbDataPoints(), "size " + sm);
        assertEquals(3.0, sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(10.5, sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(10.5, sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(7.5, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(18.0, sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(21.0, sm.getSum(), 0.01, "Sum " + sm);
    }

    @Test
    void testNbDataPoints() {
        StatisticalMeasurement sm = new StatisticalMeasurement(descriptor, g, "bar DISPOSE_NB_DATA_POINTS");

        assertEquals(2, sm.getNbDataPoints(), "size " + sm);
        assertEquals(2.0, sm.getMinimum(), 0.01, "Minimum " + sm);
        assertEquals(3.0, sm.getMedian(), 0.01, "Median " + sm);
        assertEquals(3.0, sm.getAverage(), 0.01, "Average " + sm);
        assertEquals(1.0, sm.getStandardDeviation(), 0.01, "Standard Deviation " + sm);
        assertEquals(4.0, sm.getMaximum(), 0.01, "Maximum " + sm);
        assertEquals(6.0, sm.getSum(), 0.01, "Sum " + sm);
    }
}
