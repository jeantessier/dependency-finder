package com.jeantessier.metrics;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

public class TestYAMLPrinter {
    private final Random random = new Random();

    private final StringWriter buffer = new StringWriter();

    @Test
    public void testVisitMetrics_NoDescriptors() {
        // Given
        var configuration = new MetricsConfiguration();

        // and
        var printer = new YAMLPrinter(new PrintWriter(buffer), configuration);

        // and
        var metricsName = "metrics name " + random.nextInt(1_000);
        var metrics = new Metrics(metricsName);

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        var lines = buffer.toString().lines().iterator();
        var i = 0;
        assertEquals("Line " + ++i, "metrics:", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testVisitMetrics_DescriptorForHiddenMeasurement() throws Exception {
        // Given
        var configuration = new MetricsConfiguration();

        // and
        var projectLongName = "project long name " + random.nextInt(1_000);
        var projectShortName = "PSN" + random.nextInt(1_000);
        var projectDescriptor = new MeasurementDescriptor();
        projectDescriptor.setLongName(projectLongName);
        projectDescriptor.setShortName(projectShortName);
        projectDescriptor.setClassFor(CounterMeasurement.class);
        projectDescriptor.setVisible(false);
        configuration.addProjectMeasurement(projectDescriptor);

        // and
        var groupLongName = "class long name " + random.nextInt(1_000);
        var groupShortName = "CSN" + random.nextInt(1_000);
        var groupDescriptor = new MeasurementDescriptor();
        groupDescriptor.setLongName(groupLongName);
        groupDescriptor.setShortName(groupShortName);
        groupDescriptor.setClassFor(CounterMeasurement.class);
        groupDescriptor.setVisible(false);
        configuration.addGroupMeasurement(groupDescriptor);

        // and
        var classLongName = "group long name " + random.nextInt(1_000);
        var classShortName = "GSN" + random.nextInt(1_000);
        var classDescriptor = new MeasurementDescriptor();
        classDescriptor.setLongName(classLongName);
        classDescriptor.setShortName(classShortName);
        classDescriptor.setClassFor(CounterMeasurement.class);
        classDescriptor.setVisible(false);
        configuration.addClassMeasurement(classDescriptor);

        // and
        var methodLongName = "method long name " + random.nextInt(1_000);
        var methodShortName = "MSN" + random.nextInt(1_000);
        var methodDescriptor = new MeasurementDescriptor();
        methodDescriptor.setLongName(methodLongName);
        methodDescriptor.setShortName(methodShortName);
        methodDescriptor.setClassFor(CounterMeasurement.class);
        methodDescriptor.setVisible(false);
        configuration.addMethodMeasurement(methodDescriptor);

        // and
        var printer = new YAMLPrinter(new PrintWriter(buffer), configuration);

        // and
        var projectMetricsName = "project metrics name " + random.nextInt(1_000);
        var projectMetrics = new Metrics(projectMetricsName);
        projectMetrics.track(projectDescriptor.createMeasurement(projectMetrics));
        var projectMeasurementValue = random.nextInt(1_000);
        projectMetrics.addToMeasurement(projectShortName, projectMeasurementValue);

        // and
        var groupMetricsName = "group metrics name " + random.nextInt(1_000);
        var groupMetrics = new Metrics(groupMetricsName);
        groupMetrics.track(groupDescriptor.createMeasurement(groupMetrics));
        var groupMeasurementValue = random.nextInt(1_000);
        groupMetrics.addToMeasurement(groupShortName, groupMeasurementValue);

        // and
        var classMetricsName = "class metrics name " + random.nextInt(1_000);
        var classMetrics = new Metrics(classMetricsName);
        classMetrics.track(classDescriptor.createMeasurement(classMetrics));
        var classMeasurementValue = random.nextInt(1_000);
        classMetrics.addToMeasurement(classShortName, classMeasurementValue);

        // and
        var methodMetricsName = "method metrics name " + random.nextInt(1_000);
        var methodMetrics = new Metrics(methodMetricsName);
        methodMetrics.track(methodDescriptor.createMeasurement(methodMetrics));
        var methodMeasurementValue = random.nextInt(1_000);
        methodMetrics.addToMeasurement(methodShortName, methodMeasurementValue);

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        var lines = buffer.toString().lines().iterator(); // Skipping the headers
        var i = 0;
        assertEquals("Line " + ++i, "metrics:", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testVisitMetrics_DescriptorForSingleValueMeasurement() throws Exception {
        // Given
        var configuration = new MetricsConfiguration();

        // and
        var projectLongName = "project long name " + random.nextInt(1_000);
        var projectShortName = "PSN" + random.nextInt(1_000);
        var projectDescriptor = new MeasurementDescriptor();
        projectDescriptor.setLongName(projectLongName);
        projectDescriptor.setShortName(projectShortName);
        projectDescriptor.setClassFor(CounterMeasurement.class);
        configuration.addProjectMeasurement(projectDescriptor);

        // and
        var groupLongName = "group long name " + random.nextInt(1_000);
        var groupShortName = "GSN" + random.nextInt(1_000);
        var groupDescriptor = new MeasurementDescriptor();
        groupDescriptor.setLongName(groupLongName);
        groupDescriptor.setShortName(groupShortName);
        groupDescriptor.setClassFor(CounterMeasurement.class);
        configuration.addGroupMeasurement(groupDescriptor);

        // and
        var classLongName = "class long name " + random.nextInt(1_000);
        var classShortName = "CSN" + random.nextInt(1_000);
        var classDescriptor = new MeasurementDescriptor();
        classDescriptor.setLongName(classLongName);
        classDescriptor.setShortName(classShortName);
        classDescriptor.setClassFor(CounterMeasurement.class);
        configuration.addClassMeasurement(classDescriptor);

        // and
        var methodLongName = "method long name " + random.nextInt(1_000);
        var methodShortName = "MSN" + random.nextInt(1_000);
        var methodDescriptor = new MeasurementDescriptor();
        methodDescriptor.setLongName(methodLongName);
        methodDescriptor.setShortName(methodShortName);
        methodDescriptor.setClassFor(CounterMeasurement.class);
        configuration.addMethodMeasurement(methodDescriptor);

        // and
        var printer = new YAMLPrinter(new PrintWriter(buffer), configuration);

        // and
        var projectMetricsName = "project metrics name " + random.nextInt(1_000);
        var projectMetrics = new Metrics(projectMetricsName);
        projectMetrics.track(projectDescriptor.createMeasurement(projectMetrics));
        var projectMeasurementValue = random.nextInt(1_000);
        projectMetrics.addToMeasurement(projectShortName, projectMeasurementValue);

        // and
        var groupMetricsName = "group metrics name " + random.nextInt(1_000);
        var groupMetrics = new Metrics(groupMetricsName);
        projectMetrics.addSubMetrics(groupMetrics);
        groupMetrics.track(groupDescriptor.createMeasurement(groupMetrics));
        var groupMeasurementValue = random.nextInt(1_000);
        groupMetrics.addToMeasurement(groupShortName, groupMeasurementValue);

        // and
        var classMetricsName = "class metrics name " + random.nextInt(1_000);
        var classMetrics = new Metrics(classMetricsName);
        groupMetrics.addSubMetrics(classMetrics);
        classMetrics.track(classDescriptor.createMeasurement(classMetrics));
        var classMeasurementValue = random.nextInt(1_000);
        classMetrics.addToMeasurement(classShortName, classMeasurementValue);

        // and
        var methodMetricsName = "method metrics name " + random.nextInt(1_000);
        var methodMetrics = new Metrics(methodMetricsName);
        classMetrics.addSubMetrics(methodMetrics);
        methodMetrics.track(methodDescriptor.createMeasurement(methodMetrics));
        var methodMeasurementValue = random.nextInt(1_000);
        methodMetrics.addToMeasurement(methodShortName, methodMeasurementValue);

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        var lines = buffer.toString().lines().iterator(); // Skipping the headers
        var i = 0;
        assertEquals("Line " + ++i, "metrics:", lines.next());
        assertEquals("Line " + ++i, "    -", lines.next());
        assertEquals("Line " + ++i, "        name: " + projectMetricsName, lines.next());
        assertEquals("Line " + ++i, "        measurements:", lines.next());
        assertEquals("Line " + ++i, "            -", lines.next());
        assertEquals("Line " + ++i, "                short-name: " + projectShortName, lines.next());
        assertEquals("Line " + ++i, "                long-name: " + projectLongName, lines.next());
        assertEquals("Line " + ++i, "                value: " + projectMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "        groups:", lines.next());
        assertEquals("Line " + ++i, "            -", lines.next());
        assertEquals("Line " + ++i, "                name: " + groupMetricsName, lines.next());
        assertEquals("Line " + ++i, "                measurements:", lines.next());
        assertEquals("Line " + ++i, "                    -", lines.next());
        assertEquals("Line " + ++i, "                        short-name: " + groupShortName, lines.next());
        assertEquals("Line " + ++i, "                        long-name: " + groupLongName, lines.next());
        assertEquals("Line " + ++i, "                        value: " + groupMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                classes:", lines.next());
        assertEquals("Line " + ++i, "                    -", lines.next());
        assertEquals("Line " + ++i, "                        name: " + classMetricsName, lines.next());
        assertEquals("Line " + ++i, "                        measurements:", lines.next());
        assertEquals("Line " + ++i, "                            -", lines.next());
        assertEquals("Line " + ++i, "                                short-name: " + classShortName, lines.next());
        assertEquals("Line " + ++i, "                                long-name: " + classLongName, lines.next());
        assertEquals("Line " + ++i, "                                value: " + classMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        methods:", lines.next());
        assertEquals("Line " + ++i, "                            -", lines.next());
        assertEquals("Line " + ++i, "                                name: " + methodMetricsName, lines.next());
        assertEquals("Line " + ++i, "                                measurements:", lines.next());
        assertEquals("Line " + ++i, "                                    -", lines.next());
        assertEquals("Line " + ++i, "                                        short-name: " + methodShortName, lines.next());
        assertEquals("Line " + ++i, "                                        long-name: " + methodLongName, lines.next());
        assertEquals("Line " + ++i, "                                        value: " + methodMeasurementValue + ".0", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testVisitMetrics_DescriptorForStatisticalMeasurement_NoPercentiles() throws Exception {
        // Given
        var configuration = new MetricsConfiguration();

        // and
        var methodShortName = "MSN" + random.nextInt(1_000);
        var initText = methodShortName;

        // and
        var projectLongName = "project long name " + random.nextInt(1_000);
        var projectShortName = "PSN" + random.nextInt(1_000);
        var projectDescriptor = new MeasurementDescriptor();
        projectDescriptor.setLongName(projectLongName);
        projectDescriptor.setShortName(projectShortName);
        projectDescriptor.setClassFor(StatisticalMeasurement.class);
        projectDescriptor.setInitText(initText);
        configuration.addProjectMeasurement(projectDescriptor);

        // and
        var groupLongName = "group long name " + random.nextInt(1_000);
        var groupShortName = "GSN" + random.nextInt(1_000);
        var groupDescriptor = new MeasurementDescriptor();
        groupDescriptor.setLongName(groupLongName);
        groupDescriptor.setShortName(groupShortName);
        groupDescriptor.setClassFor(StatisticalMeasurement.class);
        groupDescriptor.setInitText(initText);
        configuration.addGroupMeasurement(groupDescriptor);

        // and
        var classLongName = "class long name " + random.nextInt(1_000);
        var classShortName = "CSN" + random.nextInt(1_000);
        var classDescriptor = new MeasurementDescriptor();
        classDescriptor.setLongName(classLongName);
        classDescriptor.setShortName(classShortName);
        classDescriptor.setClassFor(StatisticalMeasurement.class);
        classDescriptor.setInitText(initText);
        configuration.addClassMeasurement(classDescriptor);

        // and
        var methodLongName = "method long name " + random.nextInt(1_000);
        var methodDescriptor = new MeasurementDescriptor();
        methodDescriptor.setLongName(methodLongName);
        methodDescriptor.setShortName(methodShortName);
        methodDescriptor.setClassFor(CounterMeasurement.class);
        configuration.addMethodMeasurement(methodDescriptor);

        // and
        var printer = new YAMLPrinter(new PrintWriter(buffer), configuration);

        // and
        var projectMetricsName = "project metrics name " + random.nextInt(1_000);
        var projectMetrics = new Metrics(projectMetricsName);
        projectMetrics.track(projectDescriptor.createMeasurement(projectMetrics));

        // and
        var groupMetricsName = "group metrics name " + random.nextInt(1_000);
        var groupMetrics = new Metrics(groupMetricsName);
        projectMetrics.addSubMetrics(groupMetrics);
        groupMetrics.track(groupDescriptor.createMeasurement(groupMetrics));

        // and
        var classMetricsName = "class metrics name " + random.nextInt(1_000);
        var classMetrics = new Metrics(classMetricsName);
        groupMetrics.addSubMetrics(classMetrics);
        classMetrics.track(classDescriptor.createMeasurement(classMetrics));

        // and
        var methodMetricsName = "method metrics name " + random.nextInt(1_000);
        var methodMetrics = new Metrics(methodMetricsName);
        classMetrics.addSubMetrics(methodMetrics);
        methodMetrics.track(methodDescriptor.createMeasurement(methodMetrics));
        var methodMeasurementValue = random.nextInt(1_000);
        methodMetrics.addToMeasurement(methodShortName, methodMeasurementValue);

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        var lines = buffer.toString().lines().iterator(); // Skipping the headers
        var i = 0;
        assertEquals("Line " + ++i, "metrics:", lines.next());
        assertEquals("Line " + ++i, "    -", lines.next());
        assertEquals("Line " + ++i, "        name: " + projectMetricsName, lines.next());
        assertEquals("Line " + ++i, "        measurements:", lines.next());
        assertEquals("Line " + ++i, "            -", lines.next());
        assertEquals("Line " + ++i, "                short-name: " + projectShortName, lines.next());
        assertEquals("Line " + ++i, "                long-name: " + projectLongName, lines.next());
        assertEquals("Line " + ++i, "                value: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                minimum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                median: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                average: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                standard-deviation: 0.0", lines.next());
        assertEquals("Line " + ++i, "                maximum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                sum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                nb-data-points: 1", lines.next());
        assertEquals("Line " + ++i, "        groups:", lines.next());
        assertEquals("Line " + ++i, "            -", lines.next());
        assertEquals("Line " + ++i, "                name: " + groupMetricsName, lines.next());
        assertEquals("Line " + ++i, "                measurements:", lines.next());
        assertEquals("Line " + ++i, "                    -", lines.next());
        assertEquals("Line " + ++i, "                        short-name: " + groupShortName, lines.next());
        assertEquals("Line " + ++i, "                        long-name: " + groupLongName, lines.next());
        assertEquals("Line " + ++i, "                        value: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        minimum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        median: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        average: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        standard-deviation: 0.0", lines.next());
        assertEquals("Line " + ++i, "                        maximum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        sum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        nb-data-points: 1", lines.next());
        assertEquals("Line " + ++i, "                classes:", lines.next());
        assertEquals("Line " + ++i, "                    -", lines.next());
        assertEquals("Line " + ++i, "                        name: " + classMetricsName, lines.next());
        assertEquals("Line " + ++i, "                        measurements:", lines.next());
        assertEquals("Line " + ++i, "                            -", lines.next());
        assertEquals("Line " + ++i, "                                short-name: " + classShortName, lines.next());
        assertEquals("Line " + ++i, "                                long-name: " + classLongName, lines.next());
        assertEquals("Line " + ++i, "                                value: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                minimum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                median: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                average: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                standard-deviation: 0.0", lines.next());
        assertEquals("Line " + ++i, "                                maximum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                sum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                nb-data-points: 1", lines.next());
        assertEquals("Line " + ++i, "                        methods:", lines.next());
        assertEquals("Line " + ++i, "                            -", lines.next());
        assertEquals("Line " + ++i, "                                name: " + methodMetricsName, lines.next());
        assertEquals("Line " + ++i, "                                measurements:", lines.next());
        assertEquals("Line " + ++i, "                                    -", lines.next());
        assertEquals("Line " + ++i, "                                        short-name: " + methodShortName, lines.next());
        assertEquals("Line " + ++i, "                                        long-name: " + methodLongName, lines.next());
        assertEquals("Line " + ++i, "                                        value: " + methodMeasurementValue + ".0", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }

    @Test
    public void testVisitMetrics_DescriptorForStatisticalMeasurement_WithPercentiles() throws Exception {
        // Given
        var numPercentiles = random.nextInt(10) + 1;
        var percentiles = IntStream.rangeClosed(1, numPercentiles)
                .mapToObj(n -> random.nextInt(100))
                .toList();

        // and
        var configuration = new MetricsConfiguration();

        // and
        var methodShortName = "MSN" + random.nextInt(1_000);
        var initText = methodShortName + "\nP " + percentiles.stream()
                .map(String::valueOf)
                .collect(joining(" "));

        // and
        var projectLongName = "project long name " + random.nextInt(1_000);
        var projectShortName = "PSN" + random.nextInt(1_000);
        var projectDescriptor = new MeasurementDescriptor();
        projectDescriptor.setLongName(projectLongName);
        projectDescriptor.setShortName(projectShortName);
        projectDescriptor.setClassFor(StatisticalMeasurement.class);
        projectDescriptor.setInitText(initText);
        configuration.addProjectMeasurement(projectDescriptor);

        // and
        var groupLongName = "group long name " + random.nextInt(1_000);
        var groupShortName = "GSN" + random.nextInt(1_000);
        var groupDescriptor = new MeasurementDescriptor();
        groupDescriptor.setLongName(groupLongName);
        groupDescriptor.setShortName(groupShortName);
        groupDescriptor.setClassFor(StatisticalMeasurement.class);
        groupDescriptor.setInitText(initText);
        configuration.addGroupMeasurement(groupDescriptor);

        // and
        var classLongName = "class long name " + random.nextInt(1_000);
        var classShortName = "CSN" + random.nextInt(1_000);
        var classDescriptor = new MeasurementDescriptor();
        classDescriptor.setLongName(classLongName);
        classDescriptor.setShortName(classShortName);
        classDescriptor.setClassFor(StatisticalMeasurement.class);
        classDescriptor.setInitText(initText);
        configuration.addClassMeasurement(classDescriptor);

        // and
        var methodLongName = "method long name " + random.nextInt(1_000);
        var methodDescriptor = new MeasurementDescriptor();
        methodDescriptor.setLongName(methodLongName);
        methodDescriptor.setShortName(methodShortName);
        methodDescriptor.setClassFor(CounterMeasurement.class);
        configuration.addMethodMeasurement(methodDescriptor);

        // and
        var printer = new YAMLPrinter(new PrintWriter(buffer), configuration);

        // and
        var projectMetricsName = "project metrics name " + random.nextInt(1_000);
        var projectMetrics = new Metrics(projectMetricsName);
        projectMetrics.track(projectDescriptor.createMeasurement(projectMetrics));

        // and
        var groupMetricsName = "group metrics name " + random.nextInt(1_000);
        var groupMetrics = new Metrics(groupMetricsName);
        projectMetrics.addSubMetrics(groupMetrics);
        groupMetrics.track(groupDescriptor.createMeasurement(groupMetrics));

        // and
        var classMetricsName = "class metrics name " + random.nextInt(1_000);
        var classMetrics = new Metrics(classMetricsName);
        groupMetrics.addSubMetrics(classMetrics);
        classMetrics.track(classDescriptor.createMeasurement(classMetrics));

        // and
        var methodMetricsName = "method metrics name " + random.nextInt(1_000);
        var methodMetrics = new Metrics(methodMetricsName);
        classMetrics.addSubMetrics(methodMetrics);
        methodMetrics.track(methodDescriptor.createMeasurement(methodMetrics));
        var methodMeasurementValue = random.nextInt(1_000);
        methodMetrics.addToMeasurement(methodShortName, methodMeasurementValue);

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        var lines = buffer.toString().lines().iterator(); // Skipping the headers
        var i = 0;
        assertEquals("Line " + ++i, "metrics:", lines.next());
        assertEquals("Line " + ++i, "    -", lines.next());
        assertEquals("Line " + ++i, "        name: " + projectMetricsName, lines.next());
        assertEquals("Line " + ++i, "        measurements:", lines.next());
        assertEquals("Line " + ++i, "            -", lines.next());
        assertEquals("Line " + ++i, "                short-name: " + projectShortName, lines.next());
        assertEquals("Line " + ++i, "                long-name: " + projectLongName, lines.next());
        assertEquals("Line " + ++i, "                value: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                minimum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                median: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                average: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                standard-deviation: 0.0", lines.next());
        assertEquals("Line " + ++i, "                maximum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                sum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                nb-data-points: 1", lines.next());
        assertEquals("Line " + ++i, "                percentiles:", lines.next());

        percentiles.forEach(percentile -> assertEquals("Line ?", "                    p" + percentile + ": " + methodMeasurementValue + ".0", lines.next()));

        assertEquals("Line " + ++i, "        groups:", lines.next());
        assertEquals("Line " + ++i, "            -", lines.next());
        assertEquals("Line " + ++i, "                name: " + groupMetricsName, lines.next());
        assertEquals("Line " + ++i, "                measurements:", lines.next());
        assertEquals("Line " + ++i, "                    -", lines.next());
        assertEquals("Line " + ++i, "                        short-name: " + groupShortName, lines.next());
        assertEquals("Line " + ++i, "                        long-name: " + groupLongName, lines.next());
        assertEquals("Line " + ++i, "                        value: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        minimum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        median: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        average: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        standard-deviation: 0.0", lines.next());
        assertEquals("Line " + ++i, "                        maximum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        sum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                        nb-data-points: 1", lines.next());
        assertEquals("Line " + ++i, "                        percentiles:", lines.next());

        percentiles.forEach(percentile -> assertEquals("Line ?", "                            p" + percentile + ": " + methodMeasurementValue + ".0", lines.next()));

        assertEquals("Line " + ++i, "                classes:", lines.next());
        assertEquals("Line " + ++i, "                    -", lines.next());
        assertEquals("Line " + ++i, "                        name: " + classMetricsName, lines.next());
        assertEquals("Line " + ++i, "                        measurements:", lines.next());
        assertEquals("Line " + ++i, "                            -", lines.next());
        assertEquals("Line " + ++i, "                                short-name: " + classShortName, lines.next());
        assertEquals("Line " + ++i, "                                long-name: " + classLongName, lines.next());
        assertEquals("Line " + ++i, "                                value: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                minimum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                median: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                average: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                standard-deviation: 0.0", lines.next());
        assertEquals("Line " + ++i, "                                maximum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                sum: " + methodMeasurementValue + ".0", lines.next());
        assertEquals("Line " + ++i, "                                nb-data-points: 1", lines.next());
        assertEquals("Line " + ++i, "                                percentiles:", lines.next());

        percentiles.forEach(percentile -> assertEquals("Line ?", "                                    p" + percentile + ": " + methodMeasurementValue + ".0", lines.next()));

        assertEquals("Line " + ++i, "                        methods:", lines.next());
        assertEquals("Line " + ++i, "                            -", lines.next());
        assertEquals("Line " + ++i, "                                name: " + methodMetricsName, lines.next());
        assertEquals("Line " + ++i, "                                measurements:", lines.next());
        assertEquals("Line " + ++i, "                                    -", lines.next());
        assertEquals("Line " + ++i, "                                        short-name: " + methodShortName, lines.next());
        assertEquals("Line " + ++i, "                                        long-name: " + methodLongName, lines.next());
        assertEquals("Line " + ++i, "                                        value: " + methodMeasurementValue + ".0", lines.next());
        assertThat("End of report", lines.hasNext(), is(false));
    }
}
