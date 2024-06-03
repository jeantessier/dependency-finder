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

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.stream.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestStatisticalMeasurementPercentile {
    @Parameters(name="StatisticalMeasurement with {0} dataset of size {1}")
    public static Object[][] data() {
        return new Object[][]{
                {"Single", 1, 1, 1, 1, 1, 1, 1},
                {"Tiny", 3, 1, 1, 2, 3, 3, 3},
                {"Small", 10, 1, 1, 5, 9, 10, 10},
                {"Large", 1_000, 10, 100, 500, 900, 990, 1_000},
        };
    }
    
    @Parameter(0)
    public String label;
    
    @Parameter(1)
    public int sampleSize;

    @Parameter(2)
    public double expectedP1;

    @Parameter(3)
    public double expectedP10;

    @Parameter(4)
    public double expectedP50;

    @Parameter(5)
    public double expectedP90;

    @Parameter(6)
    public double expectedP99;

    @Parameter(7)
    public double expectedP100;
    
    private StatisticalMeasurement measurement;

    @Before
    public void setUp() {
        var metrics = new Metrics("foo");
        IntStream.rangeClosed(1, sampleSize).forEach(n ->
                metrics.addSubMetrics(new Metrics("m" + n)
                        .track("bar", new CounterMeasurement(null, null, null))
                        .addToMeasurement("bar", n))
        );

        measurement = new StatisticalMeasurement(null, metrics, "bar");
    }

    @Test
    public void p1() {
        assertEquals("p1", expectedP1, measurement.getPercentile(1), 0.01);
    }

    @Test
    public void p10() {
        assertEquals("p10", expectedP10, measurement.getPercentile(10), 0.01);
    }

    @Test
    public void p50() {
        assertEquals("p50", expectedP50, measurement.getPercentile(50), 0.01);
    }

    @Test
    public void p90() {
        assertEquals("p90", expectedP90, measurement.getPercentile(90), 0.01);
    }

    @Test
    public void p99() {
        assertEquals("p99", expectedP99, measurement.getPercentile(99), 0.01);
    }

    @Test
    public void p100() {
        assertEquals("p100", expectedP100, measurement.getPercentile(100), 0.01);
    }
}
