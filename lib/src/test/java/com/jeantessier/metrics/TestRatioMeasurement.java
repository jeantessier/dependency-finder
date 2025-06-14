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

import org.jmock.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestRatioMeasurement {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    
    private final Metrics metrics = new Metrics("foobar");
    private final MeasurementDescriptor descriptor = new MeasurementDescriptor();

    private final Measurement m1 = new CounterMeasurement(null, metrics, null);
    private final Measurement m2 = new CounterMeasurement(null, metrics, null);

    private RatioMeasurement measurement;

    @BeforeEach
    void setUp() {
        metrics.track("base", m1);
        metrics.track("divider", m2);

        descriptor.setShortName("foo");
        descriptor.setLongName("bar");
        descriptor.setClassFor(RatioMeasurement.class);
        descriptor.setInitText("base\ndivider");
        descriptor.setCached(false);
    }

    @Test
    void testMeasurementDescriptor() {
        measurement = new RatioMeasurement(descriptor, metrics, "base\ndivider");
        
        assertNotNull(measurement.getDescriptor());
        assertEquals(RatioMeasurement.class, measurement.getDescriptor().getClassFor());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
    }

    @Test
    void testCreateFromMeasurementDescriptor() throws Exception {
        descriptor.setInitText(null);

        measurement = (RatioMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(RatioMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());

        assertNull(measurement.getBaseTerm());
        assertNull(measurement.getDividerTerm());
        assertTrue(Double.isNaN(measurement.getValue().doubleValue()));
    }
    
    @Test
    void testCreateAndInitFromMeasurementDescriptor() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement();
        
        assertNotNull(measurement);
        assertEquals(descriptor, measurement.getDescriptor());
        assertSame(descriptor, measurement.getDescriptor());
        assertEquals(RatioMeasurement.class, measurement.getClass());
        assertEquals("foo", measurement.getShortName());
        assertEquals("bar", measurement.getLongName());
        assertEquals("base", measurement.getBaseTerm());
        assertEquals("divider", measurement.getDividerTerm());

        assertTrue(Double.isNaN(measurement.getValue().doubleValue()));
    }

    static Stream<Arguments> initTextDataProvider() {
        return Stream.of(
                arguments("missing initText", null, null, null),
                arguments("partial initText", "base", null, null),
                arguments("minimal initText", "base\ndivider", "base", "divider"),
                arguments("minimal initText with dispose clauses", "foo DISPOSE_MINIMUM\nbar DISPOSE_AVERAGE", "foo DISPOSE_MINIMUM", "bar DISPOSE_AVERAGE"),
                arguments("minimal initText with constants", "1\n2", "1", "2")
        );
    }

    @DisplayName("constructor sets baseTerm")
    @ParameterizedTest(name = "with {0}")
    @MethodSource("initTextDataProvider")
    void testBaseTerm(String variation, String initText, String expectedBaseTerm, String expectedDividerTerm) {
        measurement = new RatioMeasurement(null, null, initText);
        assertEquals(expectedBaseTerm, measurement.getBaseTerm(), "base term");
    }

    @DisplayName("constructor sets dividerTerm")
    @ParameterizedTest(name = "with {0}")
    @MethodSource("initTextDataProvider")
    void testDividerTerm(String variation, String initText, String expectedBaseTerm, String expectedDividerTerm) {
        measurement = new RatioMeasurement(null, null, initText);
        assertEquals(expectedDividerTerm, measurement.getDividerTerm(), "divider term");
    }

    @Test
    void testConstants() {
        measurement = new RatioMeasurement(descriptor, null, "1\n2");
        assertEquals(1 / 2, measurement.getValue().intValue());
    }

    @Test
    void testStatistical() {
        Metrics c  = new Metrics("foobar");
        Metrics m1 = new Metrics("foo");
        Metrics m2 = new Metrics("bar");

        c.addSubMetrics(m1);
        c.addSubMetrics(m2);

        m1.track("base",    new CounterMeasurement(null, null, null));
        m1.track("divider", new CounterMeasurement(null, null, null));
        m2.track("base",    new CounterMeasurement(null, null, null));
        m2.track("divider", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("base",    1);
        m1.addToMeasurement("divider", 2);
        m2.addToMeasurement("base",    3);
        m2.addToMeasurement("divider", 4);

        c.track("base",    new StatisticalMeasurement(null, c, "base"));
        c.track("divider", new StatisticalMeasurement(null, c, "divider"));
        
        measurement = new RatioMeasurement(descriptor, c, "base DISPOSE_MINIMUM\ndivider DISPOSE_MINIMUM");
        assertEquals(0.5, measurement.getValue().doubleValue(), 0.01);
        
        measurement = new RatioMeasurement(descriptor, c, "base DISPOSE_AVERAGE\ndivider DISPOSE_AVERAGE");
        assertEquals(2.0 / 3.0, measurement.getValue().doubleValue(), 0.01);
        
        measurement = new RatioMeasurement(descriptor, c, "base DISPOSE_AVERAGE\ndivider DISPOSE_NB_DATA_POINTS");
        assertEquals(1.0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testNormal() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m1.add(10);
        m2.add(1);

        assertEquals(10 / 1, measurement.getValue().intValue());

        m2.add(1);

        assertEquals(10 / 2, measurement.getValue().intValue());

        m1.add(m1.getValue());

        assertEquals(20 / 2, measurement.getValue().intValue());
    }

    @Test
    void testNaN() {
        measurement = new RatioMeasurement(descriptor, null, "0\n0");
        assertTrue(Double.isNaN(measurement.getValue().doubleValue()), "0/0 not NaN");
    }

    @Test
    void testNaNWithDefaultValue() {
        measurement = new RatioMeasurement(descriptor, null, "0\n0\n1");
        assertEquals(1, measurement.getValue().doubleValue(), 0.01, "0/0 should default to 1");
    }

    static Stream<Arguments> divideByZeroDataProvider() {
        return Stream.of(
                arguments("1/0", "1\n0"),
                arguments("-1/0", "-1\n0")
        );
    }

    @DisplayName("divide by zero")
    @ParameterizedTest(name = "for {0} should be infinite")
    @MethodSource("divideByZeroDataProvider")
    void testDivideByZero(String variation, String initText) {
        measurement = new RatioMeasurement(descriptor, null, initText);
        assertTrue(Double.isInfinite(measurement.getValue().doubleValue()));
    }

    static Stream<Arguments> infiniteWithDefaultDataProvider() {
        return Stream.of(
                arguments("1/0", "1\n0\n\n123\n456", 123),
                arguments("-1/0", "-1\n0\n\n123\n456", 456)
        );
    }

    @DisplayName("divide by zero")
    @ParameterizedTest(name = "for {0} should be {2}")
    @MethodSource("infiniteWithDefaultDataProvider")
    void testInfiniteWithDefaultValue(String variation, String initText, double expectedValue) {
        measurement = new RatioMeasurement(descriptor, null, initText);
        assertEquals(expectedValue, measurement.getValue().doubleValue(), 0.01);
    }

    static Stream<Arguments> zeroDividedByDataProvider() {
        return Stream.of(
                arguments("0/1", "0\n1"),
                arguments("0/-1", "0\n-1")
        );
    }

    @DisplayName("zero divided by")
    @ParameterizedTest(name = "for {0} should be zero")
    @MethodSource("zeroDividedByDataProvider")
    void testZeroDividedBy(String variation, String initText) {
        measurement = new RatioMeasurement(descriptor, null, initText);
        assertEquals(0.0, measurement.getValue().doubleValue(), 0.01);
    }

    @Test
    void testInUndefinedRange() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);
        
        assertTrue(measurement.isInRange());
    }

    @Test
    void testInOpenRange() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);

        assertTrue(measurement.isInRange());
    }

    @Test
    void testInLowerBoundRange() throws Exception {
        descriptor.setLowerThreshold(1.0);

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);

        assertFalse(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);
        
        assertTrue(measurement.isInRange());
    }

    @Test
    void testInUpperBoundRange() throws Exception {
        descriptor.setUpperThreshold(1.5);

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);

        assertFalse(measurement.isInRange());
    }

    @Test
    void testInBoundRange() throws Exception {
        descriptor.setLowerThreshold(1.0);
        descriptor.setUpperThreshold(1.5);

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        m2.add(1);

        assertFalse(measurement.isInRange());

        m1.add(1);
        
        assertTrue(measurement.isInRange());

        m1.add(2);

        assertFalse(measurement.isInRange());
    }

    @Test
    void testCachedValue() throws Exception {
        descriptor.setCached(true);

        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        assertTrue(Double.isNaN(measurement.getValue().doubleValue()), "0/0 not NaN");

        m2.add(1);
        
        assertTrue(Double.isNaN(measurement.getValue().doubleValue()), "cached 0/0 not NaN");
    }

    @Test
    void testAccept() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);
        
        var visitor = context.mock(MeasurementVisitor.class);
        
        context.checking(new Expectations() {{
            oneOf (visitor).visitRatioMeasurement(measurement);
        }});
        
        measurement.accept(visitor);
    }

    @Test
    void testEmpty() throws Exception {
        measurement = (RatioMeasurement) descriptor.createMeasurement(metrics);

        assertEquals(0, m1.getValue().intValue(), "base == 0");
        assertEquals(0, m2.getValue().intValue(), "divider == 0");
        assertTrue(measurement.isEmpty(), "0/0");

        m1.add(1);

        assertEquals(1, m1.getValue().intValue(), "base != 1");
        assertEquals(0, m2.getValue().intValue(), "divider != 0");
        assertTrue(measurement.isEmpty(), "1/0");

        m2.add(1);

        assertEquals(1, m1.getValue().intValue(), "base != 1");
        assertEquals(1, m2.getValue().intValue(), "divider != 1");
        assertFalse(measurement.isEmpty(), "1/1");

        m1.add(-1);

        assertEquals(0, m1.getValue().intValue(), "base != 0");
        assertEquals(1, m2.getValue().intValue(), "divider != 1");
        assertFalse(measurement.isEmpty(), "0/1");

        m2.add(-1);

        assertEquals(0, m1.getValue().intValue(), "base != 0");
        assertEquals(0, m2.getValue().intValue(), "divider != 0");
        assertTrue(measurement.isEmpty(), "0/0");
    }
}
