/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import junit.framework.TestCase;

import java.util.stream.IntStream;

public class TestStatisticalMeasurementPercentile extends TestCase implements MeasurementVisitor {
    private StatisticalMeasurement measurement;

    protected void setUp() throws Exception {
        super.setUp();

        int sampleSize;
        if (getName().contains("Large")) {
            sampleSize = 1000;
        } else if (getName().contains("Small")) {
            sampleSize = 10;
        } else if (getName().contains("Tiny")) {
            sampleSize = 3;
        } else {
            sampleSize = 1;
        }

        var metrics = new Metrics("foo");
        IntStream.rangeClosed(1, sampleSize).forEach(n ->
                metrics.addSubMetrics(new Metrics("m" + n)
                        .track("bar", new CounterMeasurement(null, null, null))
                        .addToMeasurement("bar", n))
        );

        measurement = new StatisticalMeasurement(null, metrics, "bar");
    }

    public void testSingleP1() {
        assertEquals("p1", 1.0, measurement.getPercentile(1), 0.01);
    }

    public void testSingleP10() {
        assertEquals("p10", 1.0, measurement.getPercentile(10), 0.01);
    }

    public void testSingleP50() {
        assertEquals("p50", 1.0, measurement.getPercentile(50), 0.01);
    }

    public void testSingleP90() {
        assertEquals("p90", 1.0, measurement.getPercentile(90), 0.01);
    }

    public void testSingleP99() {
        assertEquals("p99", 1.0, measurement.getPercentile(99), 0.01);
    }

    public void testSingleP100() {
        assertEquals("p100", 1.0, measurement.getPercentile(100), 0.01);
    }

    public void testTinyP1() {
        assertEquals("p1", 1.0, measurement.getPercentile(1), 0.01);
    }

    public void testTinyP10() {
        assertEquals("p10", 1.0, measurement.getPercentile(10), 0.01);
    }

    public void testTinyP50() {
        assertEquals("p50", 2.0, measurement.getPercentile(50), 0.01);
    }

    public void testTinyP90() {
        assertEquals("p90", 3.0, measurement.getPercentile(90), 0.01);
    }

    public void testTinyP99() {
        assertEquals("p99", 3.0, measurement.getPercentile(99), 0.01);
    }

    public void testTinyP100() {
        assertEquals("p100", 3.0, measurement.getPercentile(100), 0.01);
    }

    public void testSmallP1() {
        assertEquals("p1", 1.0, measurement.getPercentile(1), 0.01);
    }

    public void testSmallP10() {
        assertEquals("p10", 1.0, measurement.getPercentile(10), 0.01);
    }

    public void testSmallP50() {
        assertEquals("p50", 5.0, measurement.getPercentile(50), 0.01);
    }

    public void testSmallP90() {
        assertEquals("p90", 9.0, measurement.getPercentile(90), 0.01);
    }

    public void testSmallP99() {
        assertEquals("p99", 10.0, measurement.getPercentile(99), 0.01);
    }

    public void testSmallP100() {
        assertEquals("p100", 10.0, measurement.getPercentile(100), 0.01);
    }

    public void testLargeP1() {
        assertEquals("p1", 10.0, measurement.getPercentile(1), 0.01);
    }

    public void testLargeP10() {
        assertEquals("p10", 100.0, measurement.getPercentile(10), 0.01);
    }

    public void testLargeP50() {
        assertEquals("p50", 500.0, measurement.getPercentile(50), 0.01);
    }

    public void testLargeP90() {
        assertEquals("p90", 900.0, measurement.getPercentile(90), 0.01);
    }

    public void testLargeP99() {
        assertEquals("p99", 990.0, measurement.getPercentile(99), 0.01);
    }

    public void testLargeP100() {
        assertEquals("p100", 1000.0, measurement.getPercentile(100), 0.01);
    }
}
