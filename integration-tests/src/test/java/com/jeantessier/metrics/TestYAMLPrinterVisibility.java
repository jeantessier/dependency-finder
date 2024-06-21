package com.jeantessier.metrics;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestYAMLPrinterVisibility extends TestPrinterVisibilityBase {
    @Parameters(name="generate a report with {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"standard options", YAMLPrinter.class, false, false, "martin.metrics.yml"},
                {"expand option", YAMLPrinter.class, true, false, "martin.metrics.expand.yml"},
                {"show-empty-metrics option", YAMLPrinter.class, false, true, "martin.metrics.show-empty-metrics.yml"},
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
    public String expectedOutput;

    @Test
    public void generateReportAndCompareToFile() throws Exception {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = printerClass.getConstructor(out.getClass(), configuration.getClass()).newInstance(out, configuration);
        printer.setExpandCollectionMeasurements(expandCollectionMeasurements);
        printer.setShowEmptyMetrics(showEmptyMetrics);

        // When
        projectMetrics.stream()
                .sorted(new MetricsComparator("name"))
                .forEach(printer::visitMetrics);

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve(expectedOutput));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }
}
