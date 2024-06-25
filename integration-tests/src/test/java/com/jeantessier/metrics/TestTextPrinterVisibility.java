package com.jeantessier.metrics;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestTextPrinterVisibility extends TestPrinterVisibilityBase {
    @Parameters(name="generate a report with {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"standard options", TextPrinter.class, false, false, false, "martin.metrics"},
                {"expand option", TextPrinter.class, true, false, false, "martin.metrics.expand"},
                {"show-empty-metrics option", TextPrinter.class, false, true, false, "martin.metrics.show-empty-metrics"},
                {"show-hidden-measurements option", TextPrinter.class, false, false, true, "martin.metrics.show-hidden-measurements"},
                {"expand and show-hidden-measurements options", TextPrinter.class, true, false, true, "martin.metrics.expand.show-hidden-measurements"},
        };
    }

    @Parameter(0)
    public String variation;

    @Parameter(1)
    public Class<? extends Printer> printerClass;

    @Parameter(2)
    public boolean expandCollectionMeasurements;

    @Parameter(3)
    public boolean showEmptyMetrics;

    @Parameter(4)
    public boolean showHiddenMeasurements;

    @Parameter(5)
    public String expectedOutput;

    @Test
    public void generateProjectReportAndCompareToFile() throws Exception {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(out, configuration.getProjectMeasurements());

        // When
        printer.visitMetrics(projectMetrics);

        // Then
        var expectedReport = readExpectedReport("project");
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void generateGroupReportAndCompareToFile() throws Exception {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(out, configuration.getGroupMeasurements());

        // When
        printer.visitMetrics(groupMetrics);

        // Then
        var expectedReport = readExpectedReport("groups");
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void generateClassReportAndCompareToFile() throws Exception {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(out, configuration.getClassMeasurements());

        // When
        printer.visitMetrics(classMetrics);

        // Then
        var expectedReport = readExpectedReport("classes");
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void generateMethodReportAndCompareToFile() throws Exception {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(out, configuration.getMethodMeasurements());

        // When
        printer.visitMetrics(methodMetrics);

        // Then
        var expectedReport = readExpectedReport("methods");
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }

    private Printer createPrinter(PrintWriter out, List<MeasurementDescriptor> descriptors) throws Exception {
        var printer = printerClass.getConstructor(out.getClass(), List.class).newInstance(out, descriptors);
        printer.setExpandCollectionMeasurements(expandCollectionMeasurements);
        printer.setShowEmptyMetrics(showEmptyMetrics);
        printer.setShowHiddenMeasurements(showHiddenMeasurements);

        return printer;
    }

    private String readExpectedReport(String section) throws IOException {
        try (var stream = Files.lines(REPORTS_DIR.resolve(expectedOutput + "_" + section + ".txt"))) {
            return stream
                    .skip(2) // The first two lines come from OOMetrics, not the printer
                    .collect(joining(System.getProperty("line.separator", "\n")));
        }
    }
}
