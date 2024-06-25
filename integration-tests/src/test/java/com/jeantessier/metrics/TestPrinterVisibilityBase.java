package com.jeantessier.metrics;

import com.jeantessier.classreader.*;
import org.junit.*;

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
}
