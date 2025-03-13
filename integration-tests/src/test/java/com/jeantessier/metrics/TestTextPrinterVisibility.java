package com.jeantessier.metrics;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestTextPrinterVisibility extends TestPrinterVisibilityBase {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("standard options", TextPrinter.class, "MartinConfig", false, false, false, "martin.metrics"),
                arguments("expand option", TextPrinter.class, "MartinConfig", true, false, false, "martin.metrics.expand"),
                arguments("show-empty-metrics option", TextPrinter.class, "MartinConfig", false, true, false, "martin.metrics.show-empty-metrics"),
                arguments("show-hidden-measurements option", TextPrinter.class, "MartinConfig", false, false, true, "martin.metrics.show-hidden-measurements"),
                arguments("expand and show-hidden-measurements options", TextPrinter.class, "MartinConfig", true, false, true, "martin.metrics.expand.show-hidden-measurements"),

                arguments("histograms", TextPrinter.class, "MethodLengthConfig", false, false, false, "method.length.metrics")
        );
    }

    @DisplayName("project-level report")
    @ParameterizedTest(name = "generate a project-level report with {0} and compare to {5}")
    @MethodSource("dataProvider")
    void generateProjectReportAndCompareToFile(String variation, Class<? extends Printer> printerClass, String configurationName, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements, String expectedOutput) throws Exception {
        // Given
        loadTestData(configurationName);

        // and
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(printerClass, out, configuration.getProjectMeasurements(), expandCollectionMeasurements, showEmptyMetrics, showHiddenMeasurements);

        // When
        printer.visitMetrics(projectMetrics);

        // Then
        var expectedReport = readExpectedReport("project", expectedOutput);
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @DisplayName("group-level report")
    @ParameterizedTest(name = "generate a group-level report with {0} and compare to {5}")
    @MethodSource("dataProvider")
    void generateGroupReportAndCompareToFile(String variation, Class<? extends Printer> printerClass, String configurationName, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements, String expectedOutput) throws Exception {
        // Given
        loadTestData(configurationName);

        // and
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(printerClass, out, configuration.getGroupMeasurements(), expandCollectionMeasurements, showEmptyMetrics, showHiddenMeasurements);

        // When
        printer.visitMetrics(groupMetrics);

        // Then
        var expectedReport = readExpectedReport("groups", expectedOutput);
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @DisplayName("class-level report")
    @ParameterizedTest(name = "generate a class-level report with {0} and compare to {5}")
    @MethodSource("dataProvider")
    void generateClassReportAndCompareToFile(String variation, Class<? extends Printer> printerClass, String configurationName, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements, String expectedOutput) throws Exception {
        // Given
        loadTestData(configurationName);

        // and
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(printerClass, out, configuration.getClassMeasurements(), expandCollectionMeasurements, showEmptyMetrics, showHiddenMeasurements);

        // When
        printer.visitMetrics(classMetrics);

        // Then
        var expectedReport = readExpectedReport("classes", expectedOutput);
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @DisplayName("method-level report")
    @ParameterizedTest(name = "generate a method-level report with {0} and compare to {5}")
    @MethodSource("dataProvider")
    void generateMethodReportAndCompareToFile(String variation, Class<? extends Printer> printerClass, String configurationName, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements, String expectedOutput) throws Exception {
        // Given
        loadTestData(configurationName);

        // and
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(printerClass, out, configuration.getMethodMeasurements(), expandCollectionMeasurements, showEmptyMetrics, showHiddenMeasurements);

        // When
        printer.visitMetrics(methodMetrics);

        // Then
        var expectedReport = readExpectedReport("methods", expectedOutput);
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    private String readExpectedReport(String section, String expectedOutput) throws IOException {
        try (var stream = Files.lines(REPORTS_DIR.resolve(expectedOutput + "_" + section + ".txt"))) {
            return stream
                    .skip(2) // The first two lines come from OOMetrics, not the printer
                    .collect(joining(System.getProperty("line.separator", "\n")));
        }
    }
}
