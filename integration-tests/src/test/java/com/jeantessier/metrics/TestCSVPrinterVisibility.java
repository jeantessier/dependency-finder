package com.jeantessier.metrics;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestCSVPrinterVisibility extends TestPrinterVisibilityBase {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("standard options", CSVPrinter.class, "MartinConfig", false, false, false, "martin.metrics"),
                arguments("expand option", CSVPrinter.class, "MartinConfig", true, false, false, "martin.metrics.expand"),
                arguments("show-empty-metrics option", CSVPrinter.class, "MartinConfig", false, true, false, "martin.metrics.show-empty-metrics"),
                arguments("show-hidden-measurements option", CSVPrinter.class, "MartinConfig", false, false, true, "martin.metrics.show-hidden-measurements"),
                arguments("expand and show-hidden-measurements options", CSVPrinter.class, "MartinConfig", true, false, true, "martin.metrics.expand.show-hidden-measurements"),

                arguments("histograms", CSVPrinter.class, "MethodLengthConfig", false, false, false, "method.length.metrics")
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
        var expectedReport = Files.readString(REPORTS_DIR.resolve(expectedOutput + "_project.csv"));
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
        var expectedReport = Files.readString(REPORTS_DIR.resolve(expectedOutput + "_groups.csv"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @DisplayName("class-level report")
    @ParameterizedTest(name = "generate a class-level report with {0} and compare to {5}")
    @MethodSource("dataProvider")
    public void generateClassReportAndCompareToFile(String variation, Class<? extends Printer> printerClass, String configurationName, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements, String expectedOutput) throws Exception {
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
        var expectedReport = Files.readString(REPORTS_DIR.resolve(expectedOutput + "_classes.csv"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @DisplayName("method-level report")
    @ParameterizedTest(name = "generate a method-level report with {0} and compare to {5}")
    @MethodSource("dataProvider")
    public void generateMethodReportAndCompareToFile(String variation, Class<? extends Printer> printerClass, String configurationName, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements, String expectedOutput) throws Exception {
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
        var expectedReport = Files.readString(REPORTS_DIR.resolve(expectedOutput + "_methods.csv"));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }
}
