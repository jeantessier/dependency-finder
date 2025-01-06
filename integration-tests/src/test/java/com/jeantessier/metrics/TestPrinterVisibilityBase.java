package com.jeantessier.metrics;

import com.jeantessier.classreader.*;
import org.junit.*;
import org.junit.jupiter.api.BeforeEach;

import java.io.PrintWriter;
import java.nio.file.*;
import java.util.*;

public class TestPrinterVisibilityBase {
    protected static final Path REPORTS_DIR = Paths.get("build/resources/test");
    private static final Path CLASSES_DIR = Paths.get("metrics/build/classes/java/main");
    private static final String CLIENT_CLASSPATH = CLASSES_DIR.resolve("client").toString();
    private static final String PROVIDER_CLASSPATH = CLASSES_DIR.resolve("provider").toString();
    private static final String CONFIGURATION_FILENAME = Paths.get("../etc/MartinConfig.xml").toString();

    protected MetricsConfiguration configuration;
    protected Collection<Metrics> projectMetrics;
    protected Collection<Metrics> groupMetrics;
    protected Collection<Metrics> classMetrics;
    protected Collection<Metrics> methodMetrics;

    @Before
    @BeforeEach
    public void loadTestData() throws Exception {
        configuration = new MetricsConfigurationLoader().load(CONFIGURATION_FILENAME);

        MetricsFactory factory = new MetricsFactory("Project", configuration);

        MetricsGatherer gatherer = new MetricsGatherer(factory);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(new LoadListenerVisitorAdapter(gatherer));
        loader.load(List.of(CLIENT_CLASSPATH, PROVIDER_CLASSPATH));

        projectMetrics = factory.getProjectMetrics();
        groupMetrics = factory.getGroupMetrics();
        classMetrics = factory.getClassMetrics();
        methodMetrics = factory.getMethodMetrics();
    }

    protected Printer createPrinter(Class<? extends Printer> printerClass, PrintWriter out, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements) throws Exception {
        return configurePrinter(
                printerClass.getConstructor(out.getClass(), configuration.getClass()).newInstance(out, configuration),
                expandCollectionMeasurements,
                showEmptyMetrics,
                showHiddenMeasurements
        );
    }

    protected Printer createPrinter(Class<? extends Printer> printerClass, PrintWriter out, List<MeasurementDescriptor> descriptors, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements) throws Exception {
        return configurePrinter(
                printerClass.getConstructor(out.getClass(), List.class).newInstance(out, descriptors),
                expandCollectionMeasurements,
                showEmptyMetrics,
                showHiddenMeasurements
        );
    }

    private Printer configurePrinter(Printer printer, boolean expandCollectionMeasurements, boolean showEmptyMetrics, boolean showHiddenMeasurements) {
        printer.setExpandCollectionMeasurements(expandCollectionMeasurements);
        printer.setShowEmptyMetrics(showEmptyMetrics);
        printer.setShowHiddenMeasurements(showHiddenMeasurements);

        return printer;
    }
}
