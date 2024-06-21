package com.jeantessier.metrics;

import org.junit.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.Assert.*;

public class TestJSONPrinterVisibility extends TestPrinterVisibilityBase {
    @Test
    public void testStandardReport() throws IOException {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = new JSONPrinter(out, configuration);

        // When
        printer.visitMetrics(projectMetrics);
        out.println();

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve("martin.metrics.json"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void testExpandCollectionMeasurements() throws IOException {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = new JSONPrinter(out, configuration);
        printer.setExpandCollectionMeasurements(true);

        // When
        printer.visitMetrics(projectMetrics);
        out.println();

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve("martin.metrics.expand.json"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void testShowEmptyMetrics() throws IOException {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = new JSONPrinter(out, configuration);
        printer.setShowEmptyMetrics(true);

        // When
        printer.visitMetrics(projectMetrics);
        out.println();

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve("martin.metrics.show-empty-metrics.json"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }
}
