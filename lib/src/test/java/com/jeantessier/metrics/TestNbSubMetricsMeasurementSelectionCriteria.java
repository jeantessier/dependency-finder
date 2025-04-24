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

import org.apache.oro.text.perl.Perl5Util;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestNbSubMetricsMeasurementSelectionCriteria {
    private final Metrics metrics = new Metrics("metrics");
    private final MeasurementDescriptor descriptor = new MeasurementDescriptor();

    private final Metrics m1 = new Metrics("m1");
    private final Metrics m2 = new Metrics("m2");
    private final Metrics m3 = new Metrics("m3");
    private final Metrics m4 = new Metrics("m4");
    private final Metrics m5 = new Metrics("m5");
    private final Metrics m6 = new Metrics("m6");
    
    @BeforeEach
    void setUp() throws Exception {
        MeasurementDescriptor present = new MeasurementDescriptor();
        present.setShortName("P");
        present.setLongName("present");
        present.setClassFor(CounterMeasurement.class);

        MeasurementDescriptor counter = new MeasurementDescriptor();
        counter.setShortName("C");
        counter.setLongName("counter");
        counter.setClassFor(CounterMeasurement.class);

        m1.track(present.createMeasurement(m1));

        m2.track(present.createMeasurement(m2));
        m2.track(counter.createMeasurement(m2));
        m2.addToMeasurement("C", 0);

        m3.track(counter.createMeasurement(m3));
        m3.addToMeasurement("C", 1);

        m4.track(counter.createMeasurement(m4));
        m4.addToMeasurement("C", 2);

        m5.track(counter.createMeasurement(m5));
        m5.addToMeasurement("C", 3);

        m6.track(counter.createMeasurement(m6));
        m6.addToMeasurement("C", 4);

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);
        metrics.addSubMetrics(m4);
        metrics.addSubMetrics(m5);
        metrics.addSubMetrics(m6);

        descriptor.setShortName("Nb");
        descriptor.setLongName("Number");
        descriptor.setClassFor(NbSubMetricsMeasurement.class);
    }

    @Test
    void testDefault() throws Exception {
        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(6, measurement.getValue().intValue(), "default");
    }

    @Test
    void testPresence() throws Exception {
        descriptor.setInitText("P");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(2, measurement.getValue().intValue(), "presence");
    }

    @Test
    void testLesserThan() throws Exception {
        descriptor.setInitText("C < 3");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(3, measurement.getValue().intValue(), "lesser than");
    }

    @Test
    void testLesserThanOrEqual() throws Exception {
        descriptor.setInitText("C <= 3");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(4, measurement.getValue().intValue(), "lesser than or equal");
    }

    @Test
    void testGreaterThan() throws Exception {
        descriptor.setInitText("C > 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(3, measurement.getValue().intValue(), "greater than");
    }

    @Test
    void testGreaterThanOrEqual() throws Exception {
        descriptor.setInitText("C >= 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(4, measurement.getValue().intValue(), "greater than or equal");
    }

    @Test
    void testEqual() throws Exception {
        descriptor.setInitText("C == 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(1, measurement.getValue().intValue(), "equal");
    }

    @Test
    void testNotEqual() throws Exception {
        descriptor.setInitText("C != 1");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(4, measurement.getValue().intValue(), "not equal");
    }

    @Test
    void testAnd() throws Exception {
        descriptor.setInitText("1 <= C <= 3");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(3, measurement.getValue().intValue(), "and");
    }

    @Test
    void testOr() throws Exception {
        descriptor.setInitText("C == 1\nC == 2");

        NbSubMetricsMeasurement measurement = (NbSubMetricsMeasurement) descriptor.createMeasurement(metrics);
        assertEquals(2, measurement.getValue().intValue(), "or");
    }

    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("empty string", "", List.of()),
                arguments("single name", "P", List.of("P")),
                arguments("binary expression", "P > 0", List.of("P ", ">", " 0")),
                arguments("trinary expression", "1 < P < 3", List.of("1 ", "<", " P ", "<", " 3")),
                arguments("trinary expression with DISPOSE", "1 < P DISPOSE_MEAN < 3", List.of("1 ", "<", " P DISPOSE_MEAN ", "<", " 3"))
        );
    }

    @DisplayName("testSplit")
    @ParameterizedTest(name="with {0} should return {2}")
    @MethodSource("dataProvider")
    void testSplit(String variation, String term, List<String> expectedTokens) {
        // Given
        var perl = new Perl5Util();
        var operators = NbSubMetricsMeasurement.OPERATORS_REGULAR_EXPRESSION;

        // When
        var actualTokens = new ArrayList<String>();
        perl.split(actualTokens, operators, term);

        // Then
        assertLinesMatch(expectedTokens, actualTokens);
    }
}
