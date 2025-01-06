package com.jeantessier.metrics;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.file.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestXMLPrinterVisibility extends TestPrinterVisibilityBase {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("standard options", XMLPrinter.class, false, false, false, "martin.metrics.xml"),
                arguments("expand option", XMLPrinter.class, true, false, false, "martin.metrics.expand.xml"),
                arguments("show-empty-metrics option", XMLPrinter.class, false, true, false, "martin.metrics.show-empty-metrics.xml"),
                arguments("show-hidden-measurements option", XMLPrinter.class, false, false, true, "martin.metrics.show-hidden-measurements.xml"),
                arguments("expand and show-hidden-measurements options", XMLPrinter.class, true, false, true, "martin.metrics.expand.show-hidden-measurements.xml")
        );
    }

    @DisplayName("report")
    @ParameterizedTest(name = "generate a report with {0} and compare to {5}")
    @MethodSource("dataProvider")
    void generateReportAndCompareToFile(String variation, Class<? extends Printer> printerClass, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements, String expectedOutput) throws Exception {
        // Given
        var buffer = new StringWriter();
        var out = new PrintWriter(buffer);

        // and
        var printer = createPrinter(printerClass, out, expandCollectionMeasurements, showEmptyMetrics, showHiddenMeasurements);

        // When
        printer.visitMetrics(projectMetrics);

        // Then
        var expectedReport = Files.readString(REPORTS_DIR.resolve(expectedOutput));
        var actualReport = buffer.toString();
        assertEquals(expectedReport, actualReport);
    }
}
