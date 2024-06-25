package com.jeantessier.metrics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestXMLPrinterVisibility extends TestPrinterVisibilityBase {
    @Parameters(name="generate a report with {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"standard options", XMLPrinter.class, false, false, false, "martin.metrics.xml"},
                {"expand option", XMLPrinter.class, true, false, false, "martin.metrics.expand.xml"},
                {"show-empty-metrics option", XMLPrinter.class, false, true, false, "martin.metrics.show-empty-metrics.xml"},
                {"show-hidden-measurements option", XMLPrinter.class, false, false, true, "martin.metrics.show-hidden-measurements.xml"},
                {"expand and show-hidden-measurements options", XMLPrinter.class, true, false, true, "martin.metrics.expand.show-hidden-measurements.xml"},
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
    public void generateReportAndCompareToFile() throws Exception {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = printerClass.getConstructor(out.getClass(), configuration.getClass()).newInstance(out, configuration);
        printer.setExpandCollectionMeasurements(expandCollectionMeasurements);
        printer.setShowEmptyMetrics(showEmptyMetrics);
        printer.setShowHiddenMeasurements(showHiddenMeasurements);

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
