package com.jeantessier.metrics;

import org.junit.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.Assert.*;

public class TestYAMLPrinterVisibility extends TestPrinterVisibilityBase {
    @Test
    public void testStandardReport() throws IOException {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = new YAMLPrinter(out, configuration);

        // When
        projectMetrics.stream()
                .sorted(new MetricsComparator("name"))
                .forEach(printer::visitMetrics);

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve("martin.metrics.yml"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void testExpandCollectionMeasurements() throws IOException {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = new YAMLPrinter(out, configuration);
        printer.setExpandCollectionMeasurements(true);

        // When
        projectMetrics.stream()
                .sorted(new MetricsComparator("name"))
                .forEach(printer::visitMetrics);

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve("martin.metrics.expand.yml"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void testShowEmptyMetrics() throws IOException {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = new YAMLPrinter(out, configuration);
        printer.setShowEmptyMetrics(true);

        // When
        projectMetrics.stream()
                .sorted(new MetricsComparator("name"))
                .forEach(printer::visitMetrics);

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve("martin.metrics.show-empty-metrics.yml"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }
}
