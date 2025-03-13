package com.jeantessier.metrics;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

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

        // and
        var expectedLines = Stream.<String>empty();

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
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

        // and
        var expectedLines = Stream.<String>empty();

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
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

        // and
        var expectedLines = Stream.of(
                metricsName,
                "    " + longName + " (" + shortName + "): " + measurementValue,
                ""
        );

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
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

        // and
        var expectedLines = Stream.of(
                metricsName,
                "    " + longName + " (" + shortName + "): NaN [NaN NaN/NaN NaN NaN 0 (0)]",
                ""
        );

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
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

        // and
        var expectedPercentiles = percentiles.stream()
                .map(percentile -> "p" + percentile + ":NaN")
                .collect(joining(" "));

        // and
        var expectedLines = Stream.of(
                metricsName,
                "    " + longName + " (" + shortName + "): NaN [NaN NaN/NaN NaN NaN 0 (0)] " + expectedPercentiles,
                ""
        );

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
    }
}
