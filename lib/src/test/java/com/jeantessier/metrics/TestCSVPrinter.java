package com.jeantessier.metrics;

import org.junit.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class TestCSVPrinter {
    private final Random random = new Random();

    private final List<String> standardSubnames = List.of("minimum", "median", "average", "std dev", "maximum", "sum", "nb");
    private final int numStandardSubnames = standardSubnames.size();

    private final StringWriter buffer = new StringWriter();
    private final List<MeasurementDescriptor> descriptors = new ArrayList<>();

    @Test
    public void testHeaders_NoDescriptors() {
        // Given

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors);

        // Then
        var lines = buffer.toString().lines().iterator();
        assertEquals("Long names", "\"name\"", lines.next());
        assertEquals("Short names", "", lines.next());
        assertEquals("Stats subnames", "", lines.next());
    }

    @Test
    public void testHeaders_DescriptorForHiddenMeasurement() {
        // Given
        var descriptor = new MeasurementDescriptor();
        descriptor.setVisible(false);

        // and
        descriptors.add(descriptor);

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors);

        // Then
        var lines = buffer.toString().lines().iterator();
        assertEquals("Long names", "\"name\"", lines.next());
        assertEquals("Short names", "", lines.next());
        assertEquals("Stats subnames", "", lines.next());
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

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors);

        // Then
        var lines = buffer.toString().lines().iterator();
        assertEquals("Long names", "\"name\", \"" + longName + "\"", lines.next());
        assertEquals("Short names", ", \"" + shortName + "\"", lines.next());
        assertEquals("Stats subnames", ", ", lines.next());
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

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors);

        // Then
        var expectedSubnames = standardSubnames.stream()
                .map(s -> "\"" + s + "\"")
                .collect(joining(", "));

        // and
        var lines = buffer.toString().lines().iterator();
        assertThat("Long names", lines.next(), matchesRegex("\"name\"(, \"" + longName + "\"){" + numStandardSubnames + "}"));
        assertThat("Short names", lines.next(), matchesRegex("(, \"" + shortName + "\"){" + numStandardSubnames + "}"));
        assertThat("Stats subnames", lines.next(), is(", " + expectedSubnames));
    }

    @Test
    public void testHeaders_DescriptorForStatisticalMeasurement_WithPercentiles() {
        // Given
        var numPercentiles = random.nextInt(10) + 1;
        var percentiles = IntStream.rangeClosed(1, numPercentiles)
                .mapToObj(n -> random.nextInt(100))
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

        // When
        new CSVPrinter(new PrintWriter(buffer), descriptors);

        // Then
        var expectedSubnames = standardSubnames.stream()
                .map(s -> "\"" + s + "\"")
                .collect(joining(", "));
        var expectedPercentiles = percentiles.stream()
                .map(percentile -> "\"p" + percentile + "\"")
                .collect(joining(", "));

        // and
        var lines = buffer.toString().lines().iterator();
        assertThat("Long names", lines.next(), matchesRegex("\"name\"(, \"" + longName + "\"){" + (numStandardSubnames + numPercentiles) + "}"));
        assertThat("Short names", lines.next(), matchesRegex("(, \"" + shortName + "\"){" + (numStandardSubnames + numPercentiles) + "}"));
        assertThat("Stats subnames", lines.next(), is(", " + expectedSubnames + ", " + expectedPercentiles));
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

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().skip(3).iterator(); // Skipping the headers
        assertEquals("Report", "\"" + metricsName + "\", " + measurementValue + ".0", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
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

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().skip(3).iterator(); // Skipping the headers
        assertThat("Report", lines.next(), matchesRegex("\"" + metricsName + "\"(, NaN){" + (numStandardSubnames - 1) + "}, 0"));
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testReport_DescriptorForStatisticalMeasurement_WithPercentiles() throws Exception {
        // Given
        var numPercentiles = random.nextInt(10) + 1;
        var percentiles = IntStream.rangeClosed(1, numPercentiles)
                .mapToObj(n -> random.nextInt(100))
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

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().skip(3).iterator(); // Skipping the headers
        assertThat("Report", lines.next(), matchesRegex("\"" + metricsName + "\"(, NaN){" + (numStandardSubnames - 1) + "}, 0(, NaN){" + numPercentiles + "}"));
        assertThat("End of report", lines.hasNext(), is(false));
    }
}
