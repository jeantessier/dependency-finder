package com.jeantessier.metrics;

import org.jmock.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestHistogramMeasurement {
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final Metrics metrics = new Metrics("foo");

    private final HistogramMeasurement measurement = new HistogramMeasurement(null, metrics, "bar");

    @Test
    void testAdd() {
        measurement.add(1);

        assertTrue(measurement.isEmpty(), "empty");
        assertEquals(0, measurement.getHistogram().size(), "histogram size");
    }

    @Test
    void testComputeEmpty() {
        assertEquals(0, measurement.getHistogram().size(), "histogram size");
        assertEquals(0, measurement.compute(), "compute returns largest bucket");
    }

    @Test
    void testComputeSingleInteger() {
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertEquals(1, measurement.getHistogram().size(), "histogram size");
        assertEquals(1, measurement.getHistogram().get(1), "histogram bucket 1");
        assertEquals(1, measurement.compute(), "compute returns largest bucket");
    }

    @Test
    void testComputeRounding() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");
        Metrics m3 = new Metrics("m3");

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);

        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));
        m3.track("bar", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("bar", 0.4);
        m2.addToMeasurement("bar", 0.5);
        m3.addToMeasurement("bar", 0.6);

        assertEquals(2, measurement.getHistogram().size(), "histogram size");
        assertEquals(1, measurement.getHistogram().get(0), "histogram bucket 0");
        assertEquals(2, measurement.getHistogram().get(1), "histogram bucket 1");
        assertEquals(1, measurement.compute(), "compute returns largest bucket");
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
        m2.addToMeasurement("bar", 10);

        assertEquals(2, measurement.getHistogram().size(), "histogram size");
        assertEquals(1, measurement.getHistogram().get(1), "histogram bucket 1");
        assertEquals(1, measurement.getHistogram().get(10), "histogram bucket 10");
        assertEquals(1, measurement.compute(), "compute returns largest bucket");
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

        assertEquals(3, measurement.getHistogram().size(), "histogram size");
        assertEquals(1, measurement.getHistogram().get(1), "histogram bucket 1");
        assertEquals(1, measurement.getHistogram().get(10), "histogram bucket 10");
        assertEquals(1, measurement.getHistogram().get(100), "histogram bucket 100");
        assertEquals(1, measurement.compute(), "compute returns largest bucket");
    }

    @Test
    void testComputeQuintuplet() {
        Metrics m1 = new Metrics("m1");
        Metrics m2 = new Metrics("m2");
        Metrics m3 = new Metrics("m3");
        Metrics m4 = new Metrics("m4");
        Metrics m5 = new Metrics("m5");

        metrics.addSubMetrics(m1);
        metrics.addSubMetrics(m2);
        metrics.addSubMetrics(m3);
        metrics.addSubMetrics(m4);
        metrics.addSubMetrics(m5);

        m1.track("bar", new CounterMeasurement(null, null, null));
        m2.track("bar", new CounterMeasurement(null, null, null));
        m3.track("bar", new CounterMeasurement(null, null, null));
        m4.track("bar", new CounterMeasurement(null, null, null));
        m5.track("bar", new CounterMeasurement(null, null, null));

        m1.addToMeasurement("bar", 1);
        m2.addToMeasurement("bar", 10);
        m3.addToMeasurement("bar", 10);
        m4.addToMeasurement("bar", 10);
        m5.addToMeasurement("bar", 100);

        assertEquals(3, measurement.getHistogram().size(), "histogram size");
        assertEquals(1, measurement.getHistogram().get(1), "histogram bucket 1");
        assertEquals(3, measurement.getHistogram().get(10), "histogram bucket 10");
        assertEquals(1, measurement.getHistogram().get(100), "histogram bucket 100");
        assertEquals(10, measurement.compute(), "compute returns largest bucket");
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

        assertEquals(1, measurement.getHistogram().size(), "histogram size");
        assertEquals(6, measurement.getHistogram().get(1), "histogram bucket 1");
        assertEquals(1, measurement.compute(), "compute returns largest bucket");
    }

    @Test
    void testAccept() {
        var visitor = context.mock(MeasurementVisitor.class);

        context.checking(new Expectations() {{
            oneOf (visitor).visitHistogramMeasurement(measurement);
        }});

        measurement.accept(visitor);
    }

    @Test
    void testToString() {
        Metrics m = new Metrics("m");
        m.track("bar", new CounterMeasurement(null, null, null));
        m.addToMeasurement("bar", 1);

        metrics.addSubMetrics(m);

        assertEquals("{1=1}", measurement.toString(), "toString()");
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
