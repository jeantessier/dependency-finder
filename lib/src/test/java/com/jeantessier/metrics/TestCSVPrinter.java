package com.jeantessier.metrics;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

public class TestCSVPrinter {
    private final Random random = new Random();

    private final List<String> standardSubnames = List.of("minimum", "median", "average", "std dev", "maximum", "sum", "nb");
    private final int numStandardSubnames = standardSubnames.size();

    private final StringWriter buffer = new StringWriter();
    private final List<MeasurementDescriptor> descriptors = new ArrayList<>();

    @Test
    public void testHeaders_NoDescriptors() {
        // Given
        var expectedLongNames = Stream.of("name").map(this::formatName).collect(joining(", "));
        var expectedShortNames = Stream.of("").map(this::formatName).collect(joining(", "));
        var expectedSubnames = Stream.of("").map(this::formatName).collect(joining(", "));

        // and
        var expectedHeaders = Stream.of(
                expectedLongNames,
                expectedShortNames,
                expectedSubnames
        );

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors).visitMetrics(Collections.emptyList());

        // Then
        assertLinesMatch(expectedHeaders, buffer.toString().lines());
    }

    @Test
    public void testHeaders_DescriptorForHiddenMeasurement() {
        // Given
        var descriptor = new MeasurementDescriptor();
        descriptor.setVisible(false);

        // and
        descriptors.add(descriptor);

        // and
        var expectedLongNames = Stream.of("name").map(this::formatName).collect(joining(", "));
        var expectedShortNames = Stream.of("").map(this::formatName).collect(joining(", "));
        var expectedSubnames = Stream.of("").map(this::formatName).collect(joining(", "));

        // and
        var expectedHeaders = Stream.of(
                expectedLongNames,
                expectedShortNames,
                expectedSubnames
        );

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors).visitMetrics(Collections.emptyList());

        // Then
        assertLinesMatch(expectedHeaders, buffer.toString().lines());
    }

    @Test
    public void testHeaders_DescriptorForSingleValueMeasurement() {
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
        var expectedLongNames = Stream.of("name", longName).map(this::formatName).collect(joining(", "));
        var expectedShortNames = Stream.of("", shortName).map(this::formatName).collect(joining(", "));
        var expectedSubnames = Stream.of("", "").map(this::formatName).collect(joining(", "));

        // and
        var expectedHeaders = Stream.of(
                expectedLongNames,
                expectedShortNames,
                expectedSubnames
        );

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors).visitMetrics(Collections.emptyList());

        // Then
        assertLinesMatch(expectedHeaders, buffer.toString().lines());
    }

    @Test
    public void testHeaders_DescriptorForStatisticalMeasurement_NoPercentiles() {
        // Given
        var longName = "long name " + random.nextInt(1_000);
        var shortName = "SN" + random.nextInt(1_000);
        var initText = "";

        // and
        var descriptor = new MeasurementDescriptor();
        descriptor.setLongName(longName);
        descriptor.setShortName(shortName);
        descriptor.setClassFor(StatisticalMeasurement.class);
        descriptor.setInitText(initText);

        // and
        descriptors.add(descriptor);

        // and
        var expectedLongNames =
                Stream
                        .of(
                                Stream.of("name"),
                                Collections.nCopies(numStandardSubnames, longName).stream())
                        .flatMap(Function.identity())
                        .map(this::formatName)
                        .collect(joining(", "));

        var expectedShortNames =
                Stream
                        .of(
                                Stream.of(""),
                                Collections.nCopies(numStandardSubnames, shortName).stream())
                        .flatMap(Function.identity())
                        .map(this::formatName)
                        .collect(joining(", "));

        var expectedSubnames =
                Stream
                        .of(
                                Stream.of(""),
                                standardSubnames.stream())
                        .flatMap(Function.identity())
                        .map(this::formatName)
                        .collect(joining(", "));

        // and
        var expectedHeaders = Stream.of(
                expectedLongNames,
                expectedShortNames,
                expectedSubnames
        );

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors).visitMetrics(Collections.emptyList());

        // Then
        assertLinesMatch(expectedHeaders, buffer.toString().lines());
    }

    @Test
    public void testHeaders_DescriptorForStatisticalMeasurement_WithPercentiles() {
        // Given
        var numPercentiles = random.nextInt(10) + 1;
        var percentiles = IntStream.rangeClosed(1, numPercentiles)
                .mapToObj(n -> random.nextInt(100) + 1)
                .toList();

        // and
        var longName = "long name " + random.nextInt(1_000);
        var shortName = "SN" + random.nextInt(1_000);
        var initText = "P " + percentiles.stream()
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
        var expectedLongNames =
                Stream
                        .of(
                                Stream.of("name"),
                                Collections.nCopies(numStandardSubnames + numPercentiles, longName).stream())
                        .flatMap(Function.identity())
                        .map(this::formatName)
                        .collect(joining(", "));

        var expectedShortNames =
                Stream
                        .of(
                                Stream.of(""),
                                Collections.nCopies(numStandardSubnames + numPercentiles, shortName).stream())
                        .flatMap(Function.identity())
                        .map(this::formatName)
                        .collect(joining(", "));

        var expectedSubnames =
                Stream
                        .of(
                                Stream.of(""),
                                standardSubnames.stream().map(this::formatName),
                                percentiles.stream().map(this::formatPercentile))
                        .flatMap(Function.identity())
                        .collect(joining(", "));

        // and
        var expectedHeaders = Stream.of(
                expectedLongNames,
                expectedShortNames,
                expectedSubnames
        );

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors).visitMetrics(Collections.emptyList());

        // Then
        assertLinesMatch(expectedHeaders, buffer.toString().lines());
    }

    @Test
    public void testReport_DescriptorForHiddenMeasurement() throws Exception {
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
        var printer = new CSVPrinter(new PrintWriter(buffer), descriptors);

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
        assertLinesMatch(expectedLines, buffer.toString().lines().skip(3)); // Skipping the headers
    }

    @Test
    public void testReport_DescriptorForSingleValueMeasurement() throws Exception {
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
        var printer = new CSVPrinter(new PrintWriter(buffer), descriptors);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);
        metrics.track(descriptor.createMeasurement(metrics));

        // and
        var measurementValue = random.nextInt(1_000);
        metrics.addToMeasurement(shortName, measurementValue);

        // and
        var measurementLine =
                Stream
                        .of(
                                Stream.of(metricsName).map(this::formatName),
                                Stream.of(measurementValue).map(this::formatValue))
                        .flatMap(Function.identity())
                        .collect(joining(", "));

        // and
        var expectedLines = Stream.of(
                measurementLine
        );

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines().skip(3)); // Skipping the headers
    }

    @Test
    public void testReport_DescriptorForStatisticalMeasurement_NoPercentiles() throws Exception {
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
        var printer = new CSVPrinter(new PrintWriter(buffer), descriptors);
        printer.setShowEmptyMetrics(true);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);
        metrics.track(descriptor.createMeasurement(metrics));

        // and
        var measurementLine =
                Stream
                        .of(
                                Stream.of(metricsName).map(this::formatName),
                                Collections.nCopies(numStandardSubnames - 1, Float.NaN).stream().map(this::formatValue),
                                IntStream.of(0).mapToObj(String::valueOf))
                        .flatMap(Function.identity())
                        .collect(joining(", "));

        // and
        var expectedLines = Stream.of(
                measurementLine
        );

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines().skip(3)); // Skipping the headers
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
        var printer = new CSVPrinter(new PrintWriter(buffer), descriptors);
        printer.setShowEmptyMetrics(true);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);
        metrics.track(descriptor.createMeasurement(metrics));

        // and
        var measurementLine =
                Stream
                        .of(
                                Stream.of(metricsName).map(this::formatName),
                                Collections.nCopies(numStandardSubnames - 1, Float.NaN).stream().map(this::formatValue),
                                IntStream.of(0).mapToObj(String::valueOf),
                                Collections.nCopies(numPercentiles, Float.NaN).stream().map(this::formatValue))
                        .flatMap(Function.identity())
                        .collect(joining(", "));

        // and
        var expectedLines = Stream.of(
                measurementLine
        );

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines().skip(3)); // Skipping the headers
    }

    private String formatName(String s) {
        return s.isEmpty() ? "" : "\"" + s + "\"";
    }

    private String formatPercentile(Integer n) {
        return "\"p" + n + "\"";
    }

    private String formatValue(Integer n) {
        return formatValue((float) n);
    }

    private String formatValue(Float f) {
        return String.format("%.1f", f);
    }
}
