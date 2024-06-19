package com.jeantessier.metrics;

import org.junit.*;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class TestTextPrinter {
    private final Random random = new Random();

    private final StringWriter buffer = new StringWriter();
    private final List<MeasurementDescriptor> descriptors = new ArrayList<>();

    @Test
    public void testVisitMetrics_NoDescriptors() {
        // Given
        var printer = new TextPrinter(new PrintWriter(buffer), descriptors);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().iterator();
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testVisitMetrics_DescriptorForHiddenMeasurement() throws Exception {
        // Given
        var longName = "long name " + random.nextInt(1_000);
        var shortName = "SN" + random.nextInt(1_000);

        // and
        var descriptor = new MeasurementDescriptor();
        descriptor.setLongName(longName);
        descriptor.setShortName(shortName);
        descriptor.setClassFor(CounterMeasurement.class);
        descriptor.setVisible(false);

        // and
        descriptors.add(descriptor);

        // and
        var printer = new TextPrinter(new PrintWriter(buffer), descriptors);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);
        metrics.track(descriptor.createMeasurement(metrics));

        // and
        var measurementValue = random.nextInt(1_000);
        metrics.addToMeasurement(shortName, measurementValue);

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().iterator();
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testVisitMetrics_DescriptorForSingleValueMeasurement() throws Exception {
        // Given
        var longName = "long name " + random.nextInt(1_000);
        var shortName = "SN" + random.nextInt(1_000);

        // and
        var descriptor = new MeasurementDescriptor();
        descriptor.setLongName(longName);
        descriptor.setShortName(shortName);
        descriptor.setClassFor(CounterMeasurement.class);

        // and
        descriptors.add(descriptor);

        // and
        var printer = new TextPrinter(new PrintWriter(buffer), descriptors);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);
        metrics.track(descriptor.createMeasurement(metrics));

        // and
        var measurementValue = random.nextInt(1_000);
        metrics.addToMeasurement(shortName, measurementValue);

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().iterator();
        var i = 0;
        assertEquals("Line " + ++i, metricsName, lines.next());
        assertEquals("Line " + ++i, "    " + longName + " (" + shortName + "): " + measurementValue, lines.next());
        assertEquals("Line " + ++i, "", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testVisitMetrics_DescriptorForStatisticalMeasurement_NoPercentiles() throws Exception {
        // Given
        var longName = "long name " + random.nextInt(1_000);
        var shortName = "SN" + random.nextInt(1_000);
        var initText = "A";

        // and
        var descriptor = new MeasurementDescriptor();
        descriptor.setLongName(longName);
        descriptor.setShortName(shortName);
        descriptor.setClassFor(StatisticalMeasurement.class);
        descriptor.setInitText(initText);

        // and
        descriptors.add(descriptor);

        // and
        var printer = new TextPrinter(new PrintWriter(buffer), descriptors);
        printer.setShowEmptyMetrics(true);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);
        metrics.track(descriptor.createMeasurement(metrics));

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().iterator();
        var i = 0;
        assertEquals("Line " + ++i, metricsName, lines.next());
        assertEquals("Line " + ++i, "    " + longName + " (" + shortName + "): NaN [NaN NaN/NaN NaN NaN NaN (0)]", lines.next());
        assertEquals("Line " + ++i, "", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testReport_DescriptorForStatisticalMeasurement_WithPercentiles() throws Exception {
        // Given
        var numPercentiles = random.nextInt(10) + 1;
        var percentiles = IntStream.rangeClosed(1, numPercentiles)
                .mapToObj(n -> random.nextInt(100) + 1)
                .toList();

        // and
        var longName = "long name " + random.nextInt(1_000);
        var shortName = "SN" + random.nextInt(1_000);
        var initText = "A\nP " + percentiles.stream()
                .map(String::valueOf)
                .collect(joining(" "));

        // and
        var descriptor = new MeasurementDescriptor();
        descriptor.setLongName(longName);
        descriptor.setShortName(shortName);
        descriptor.setClassFor(StatisticalMeasurement.class);
        descriptor.setInitText(initText);

        // and
        descriptors.add(descriptor);

        // and
        var printer = new TextPrinter(new PrintWriter(buffer), descriptors);
        printer.setShowEmptyMetrics(true);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);
        metrics.track(descriptor.createMeasurement(metrics));

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var expectedPercentiles = percentiles.stream()
                .map(percentile -> "p" + percentile + ":NaN")
                .collect(joining(" "));

        // and
        var lines = buffer.toString().lines().iterator();
        var i = 0;
        assertEquals("Line " + ++i, metricsName, lines.next());
        assertEquals("Line " + ++i, "    " + longName + " (" + shortName + "): NaN [NaN NaN/NaN NaN NaN NaN (0)] " + expectedPercentiles, lines.next());
        assertEquals("Line " + ++i, "", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }
}
