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

package com.jeantessier.dependency;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.stream.*;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestMetricsGatherer {
    private final NodeFactory factory = new NodeFactory();

    private final Node _package = factory.createPackage("", true);
    private final Node test_class = factory.createClass("test", true);
    private final Node test_main_method = factory.createFeature("test.main(java.lang.String[]): void", true);
    private final Node test_test_method = factory.createFeature("test.test()", true);

    private final Node java_io_package = factory.createPackage("java.io");
    private final Node java_io_PrintStream_class = factory.createClass("java.io.PrintStream");
    private final Node java_io_PrintStream_println_method = factory.createFeature("java.io.PrintStream.println(java.lang.String[]): void");

    private final Node java_lang_package = factory.createPackage("java.lang");
    private final Node java_lang_NullPointerException_class = factory.createClass("java.lang.NullPointerException");
    private final Node java_lang_Object_class = factory.createClass("java.lang.Object");
    private final Node java_lang_Object_Object_method = factory.createFeature("java.lang.Object.Object()");
    private final Node java_lang_String_class = factory.createClass("java.lang.String");
    private final Node java_lang_System_class = factory.createClass("java.lang.System");
    private final Node java_lang_System_out_field = factory.createFeature("java.lang.System.out");

    private final Node java_util_package = factory.createPackage("java.util");
    private final Node java_util_Collections_class = factory.createClass("java.util.Collections");
    private final Node java_util_Collections_singleton_method = factory.createFeature("java.util.Collections.singleton(java.lang.Object): java.util.Set");
    private final Node java_util_Set_class = factory.createClass("java.util.Set");

    private final MetricsGatherer metrics = new MetricsGatherer();

    @BeforeEach
    void setUp() {
        test_class.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_io_PrintStream_class);
        test_main_method.addDependency(java_io_PrintStream_println_method);
        test_main_method.addDependency(java_lang_NullPointerException_class);
        test_main_method.addDependency(java_lang_Object_class);
        test_main_method.addDependency(java_lang_Object_Object_method);
        test_main_method.addDependency(java_lang_String_class);
        test_main_method.addDependency(java_lang_System_out_field);
        test_main_method.addDependency(java_util_Collections_singleton_method);
        test_main_method.addDependency(java_util_Set_class);
        test_test_method.addDependency(java_lang_Object_Object_method);

        metrics.traverseNodes(factory.getPackages().values());
    }

    @Test
    void testNodeCounts() {
        assertEquals(4, metrics.getPackages().size(), "Number of packages");
        assertEquals(8, metrics.getClasses().size(), "Number of classes");
        assertEquals(6, metrics.getFeatures().size(), "Number of features");
    }

    @Test
    void testOutboundDependencies() {
        assertEquals(11, metrics.getNbOutbound(), "Number of outbounds");
        assertEquals(0, metrics.getNbOutboundPackages(), "Number of outbounds from packages");
        assertEquals(1, metrics.getNbOutboundClasses(), "Number of outbounds from classes");
        assertEquals(10, metrics.getNbOutboundFeatures(), "Number of outbounds from features");
    }

    @Test
    void testInboundDependencies() {
        assertEquals(11, metrics.getNbInbound(), "Number of inbounds");
        assertEquals(0, metrics.getNbInboundPackages(), "Number of inbounds to packages");
        assertEquals(6, metrics.getNbInboundClasses(), "Number of inbounds to classes");
        assertEquals(5, metrics.getNbInboundFeatures(), "Number of inbounds to features");
    }

    static Stream<Arguments> chartDataProvider() {
        return Stream.of(
                arguments("classes per package",   MetricsGatherer.CLASSES_PER_PACKAGE,   new long[] {0, 2, 1, 0, 1, 0, 0, 0, 0, 0}),
                arguments("features per class",    MetricsGatherer.FEATURES_PER_CLASS,    new long[] {3, 4, 1, 0, 0, 0, 0, 0, 0, 0}),
                arguments("inbounds per package",  MetricsGatherer.INBOUNDS_PER_PACKAGE,  new long[] {4, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                arguments("outbounds per package", MetricsGatherer.OUTBOUNDS_PER_PACKAGE, new long[] {4, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                arguments("inbounds per class",    MetricsGatherer.INBOUNDS_PER_CLASS,    new long[] {3, 4, 1, 0, 0, 0, 0, 0, 0, 0}),
                arguments("outbounds per class",   MetricsGatherer.OUTBOUNDS_PER_CLASS,   new long[] {7, 1, 0, 0, 0, 0, 0, 0, 0, 0}),
                arguments("inbounds per feature",  MetricsGatherer.INBOUNDS_PER_FEATURE,  new long[] {2, 3, 1, 0, 0, 0, 0, 0, 0, 0}),
                arguments("outbounds per feature", MetricsGatherer.OUTBOUNDS_PER_FEATURE, new long[] {4, 1, 0, 0, 0, 0, 0, 0, 0, 1})
        );
    }

    @DisplayName("chart data")
    @ParameterizedTest(name = "for {0} should be {2}")
    @MethodSource("chartDataProvider")
    void testChartData(String variation, int chart, long[] expectedChart) {
        var actualChart = IntStream.rangeClosed(0, metrics.getChartMaximum())
                .mapToLong(i -> metrics.getChartData(i)[chart])
                .toArray();

        assertArrayEquals(expectedChart, actualChart);
    }

    @DisplayName("chart")
    @ParameterizedTest(name = "for {0} should be {2}")
    @MethodSource("chartDataProvider")
    void testChart(String variation, int chart, long[] expectedChart) {
        var actualChart = metrics.getChart(chart);

        assertArrayEquals(expectedChart, actualChart);
    }

    static Stream<Arguments> histogramDataProvider() {
        return Stream.of(
                arguments("classes per package",   MetricsGatherer.CLASSES_PER_PACKAGE,   Map.of(1L, 2L, 2L, 1L, 4L, 1L)),
                arguments("features per class",    MetricsGatherer.FEATURES_PER_CLASS,    Map.of(0L, 3L, 1L, 4L, 2L, 1L)),
                arguments("inbounds per package",  MetricsGatherer.INBOUNDS_PER_PACKAGE,  Map.of(0L, 4L)),
                arguments("outbounds per package", MetricsGatherer.OUTBOUNDS_PER_PACKAGE, Map.of(0L, 4L)),
                arguments("inbounds per class",    MetricsGatherer.INBOUNDS_PER_CLASS,    Map.of(0L, 3L, 1L, 4L, 2L, 1L)),
                arguments("outbounds per class",   MetricsGatherer.OUTBOUNDS_PER_CLASS,   Map.of(0L, 7L, 1L, 1L)),
                arguments("inbounds per feature",  MetricsGatherer.INBOUNDS_PER_FEATURE,  Map.of(0L, 2L, 1L, 3L, 2L, 1L)),
                arguments("outbounds per feature", MetricsGatherer.OUTBOUNDS_PER_FEATURE, Map.of(0L, 4L, 1L, 1L, 9L, 1L))
        );
    }

    @DisplayName("histogram")
    @ParameterizedTest(name = "for {0} should be {2}")
    @MethodSource("histogramDataProvider")
    void testHistogram(String variation, int histogram, Map<Long, Long> expectedHistogram) {
        var actualHistogram = metrics.getHistogram(histogram);
        assertMapEquals(expectedHistogram, actualHistogram);
    }

    private void assertMapEquals(Map<Long, Long> expected, Map<Long, Long> actual) {
        assertEquals(expected.size(), actual.size(), () -> format("map size mismatch: expected: %s, actual: %s", expected, actual));

        var diffs = expected.keySet().stream()
                .filter(key -> !expected.get(key).equals(actual.get(key)))
                .collect(toMap(
                        key -> key,
                        key -> List.of(Optional.of(expected.get(key)), Optional.ofNullable(actual.get(key)))
                ));

        assertEquals(Collections.emptyMap(), diffs, () -> format("Keys mismatch: %s expected: %s, actual: %s", diffs, expected, actual));
    }
}
