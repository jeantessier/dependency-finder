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

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestStatisticalMeasurementPercentile {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("Single", 1, 1, 1, 1, 1, 1, 1),
                arguments("Tiny", 3, 1, 1, 2, 3, 3, 3),
                arguments("Small", 10, 1, 1, 5, 9, 10, 10),
                arguments("Large", 1_000, 10, 100, 500, 900, 990, 1_000)
        );
    }

    @DisplayName("P1")
    @ParameterizedTest(name="P1 with {0} dataset of size {1} is {2}")
    @MethodSource("dataProvider")
    void p1(String variation, int sampleSize, double expectedP1, double expectedP10, double expectedP50, double expectedP90, double expectedP99, double expectedP100) {
        var measurement = buildMeasurement(sampleSize);
        assertEquals(expectedP1, measurement.getPercentile(1), 0.01, "p1");
    }

    @DisplayName("P10")
    @ParameterizedTest(name="P10 with {0} dataset of size {1} is {3}")
    @MethodSource("dataProvider")
    void p10(String variation, int sampleSize, double expectedP1, double expectedP10, double expectedP50, double expectedP90, double expectedP99, double expectedP100) {
        var measurement = buildMeasurement(sampleSize);
        assertEquals(expectedP10, measurement.getPercentile(10), 0.01, "p10");
    }

    @DisplayName("P50")
    @ParameterizedTest(name="P50 with {0} dataset of size {1} is {4}")
    @MethodSource("dataProvider")
    void p50(String variation, int sampleSize, double expectedP1, double expectedP10, double expectedP50, double expectedP90, double expectedP99, double expectedP100) {
        var measurement = buildMeasurement(sampleSize);
        assertEquals(expectedP50, measurement.getPercentile(50), 0.01, "p50");
    }

    @DisplayName("P90")
    @ParameterizedTest(name="P90 with {0} dataset of size {1} is {5}")
    @MethodSource("dataProvider")
    void p90(String variation, int sampleSize, double expectedP1, double expectedP10, double expectedP50, double expectedP90, double expectedP99, double expectedP100) {
        var measurement = buildMeasurement(sampleSize);
        assertEquals(expectedP90, measurement.getPercentile(90), 0.01, "p90");
    }

    @DisplayName("P99")
    @ParameterizedTest(name="P99 with {0} dataset of size {1} is {6}")
    @MethodSource("dataProvider")
    void p99(String variation, int sampleSize, double expectedP1, double expectedP10, double expectedP50, double expectedP90, double expectedP99, double expectedP100) {
        var measurement = buildMeasurement(sampleSize);
        assertEquals(expectedP99, measurement.getPercentile(99), 0.01, "p99");
    }

    @DisplayName("P100")
    @ParameterizedTest(name="P100 with {0} dataset of size {1} is {7}")
    @MethodSource("dataProvider")
    void p100(String variation, int sampleSize, double expectedP1, double expectedP10, double expectedP50, double expectedP90, double expectedP99, double expectedP100) {
        var measurement = buildMeasurement(sampleSize);
        assertEquals(expectedP100, measurement.getPercentile(100), 0.01, "p100");
    }
    
    private StatisticalMeasurement buildMeasurement(int sampleSize) {
        var metrics = new Metrics("foo");
        IntStream.rangeClosed(1, sampleSize).forEach(n ->
                metrics.addSubMetrics(new Metrics("m" + n)
                        .track("bar", new CounterMeasurement(null, null, null))
                        .addToMeasurement("bar", n))
        );

        return new StatisticalMeasurement(null, metrics, "bar");
    }
}
