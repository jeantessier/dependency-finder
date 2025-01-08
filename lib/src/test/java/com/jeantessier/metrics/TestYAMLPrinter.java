package com.jeantessier.metrics;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;

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

        // and
        var expectedLines = Stream.of(
                "metrics:"
        );

        // When
        printer.visitMetrics(Collections.singleton(metrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
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

        // and
        var expectedLines = Stream.of(
                "metrics:"
        );

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
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

        // and
        var expectedLines = Stream.of(
                "metrics:",
                "    -",
                "        name: " + projectMetricsName,
                "        measurements:",
                "            -",
                "                short-name: " + projectShortName,
                "                long-name: " + projectLongName,
                "                value: " + projectMeasurementValue + ".0",
                "        groups:",
                "            -",
                "                name: " + groupMetricsName,
                "                measurements:",
                "                    -",
                "                        short-name: " + groupShortName,
                "                        long-name: " + groupLongName,
                "                        value: " + groupMeasurementValue + ".0",
                "                classes:",
                "                    -",
                "                        name: " + classMetricsName,
                "                        measurements:",
                "                            -",
                "                                short-name: " + classShortName,
                "                                long-name: " + classLongName,
                "                                value: " + classMeasurementValue + ".0",
                "                        methods:",
                "                            -",
                "                                name: " + methodMetricsName,
                "                                measurements:",
                "                                    -",
                "                                        short-name: " + methodShortName,
                "                                        long-name: " + methodLongName,
                "                                        value: " + methodMeasurementValue + ".0"
        );

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
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

        // and
        var expectedLines = Stream.of(
                "metrics:",
                "    -",
                "        name: " + projectMetricsName,
                "        measurements:",
                "            -",
                "                short-name: " + projectShortName,
                "                long-name: " + projectLongName,
                "                value: " + methodMeasurementValue + ".0",
                "                minimum: " + methodMeasurementValue + ".0",
                "                median: " + methodMeasurementValue + ".0",
                "                average: " + methodMeasurementValue + ".0",
                "                standard-deviation: 0.0",
                "                maximum: " + methodMeasurementValue + ".0",
                "                sum: " + methodMeasurementValue + ".0",
                "                nb-data-points: 1",
                "        groups:",
                "            -",
                "                name: " + groupMetricsName,
                "                measurements:",
                "                    -",
                "                        short-name: " + groupShortName,
                "                        long-name: " + groupLongName,
                "                        value: " + methodMeasurementValue + ".0",
                "                        minimum: " + methodMeasurementValue + ".0",
                "                        median: " + methodMeasurementValue + ".0",
                "                        average: " + methodMeasurementValue + ".0",
                "                        standard-deviation: 0.0",
                "                        maximum: " + methodMeasurementValue + ".0",
                "                        sum: " + methodMeasurementValue + ".0",
                "                        nb-data-points: 1",
                "                classes:",
                "                    -",
                "                        name: " + classMetricsName,
                "                        measurements:",
                "                            -",
                "                                short-name: " + classShortName,
                "                                long-name: " + classLongName,
                "                                value: " + methodMeasurementValue + ".0",
                "                                minimum: " + methodMeasurementValue + ".0",
                "                                median: " + methodMeasurementValue + ".0",
                "                                average: " + methodMeasurementValue + ".0",
                "                                standard-deviation: 0.0",
                "                                maximum: " + methodMeasurementValue + ".0",
                "                                sum: " + methodMeasurementValue + ".0",
                "                                nb-data-points: 1",
                "                        methods:",
                "                            -",
                "                                name: " + methodMetricsName,
                "                                measurements:",
                "                                    -",
                "                                        short-name: " + methodShortName,
                "                                        long-name: " + methodLongName,
                "                                        value: " + methodMeasurementValue + ".0"
        );

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
    }

    @Test
    public void testVisitMetrics_DescriptorForStatisticalMeasurement_WithPercentiles() throws Exception {
        // Given
        var numPercentiles = random.nextInt(10) + 1;
        var percentiles = IntStream.rangeClosed(1, numPercentiles)
                .mapToObj(n -> random.nextInt(100) + 1)
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

        // and
        var expectedLines = Stream.of(
                Stream.of(
                        "metrics:",
                        "    -",
                        "        name: " + projectMetricsName,
                        "        measurements:",
                        "            -",
                        "                short-name: " + projectShortName,
                        "                long-name: " + projectLongName,
                        "                value: " + methodMeasurementValue + ".0",
                        "                minimum: " + methodMeasurementValue + ".0",
                        "                median: " + methodMeasurementValue + ".0",
                        "                average: " + methodMeasurementValue + ".0",
                        "                standard-deviation: 0.0",
                        "                maximum: " + methodMeasurementValue + ".0",
                        "                sum: " + methodMeasurementValue + ".0",
                        "                nb-data-points: 1",
                        "                percentiles:"
                ),

                percentiles.stream().map(percentile -> "                    p" + percentile + ": " + methodMeasurementValue + ".0"),

                Stream.of(
                        "        groups:",
                        "            -",
                        "                name: " + groupMetricsName,
                        "                measurements:",
                        "                    -",
                        "                        short-name: " + groupShortName,
                        "                        long-name: " + groupLongName,
                        "                        value: " + methodMeasurementValue + ".0",
                        "                        minimum: " + methodMeasurementValue + ".0",
                        "                        median: " + methodMeasurementValue + ".0",
                        "                        average: " + methodMeasurementValue + ".0",
                        "                        standard-deviation: 0.0",
                        "                        maximum: " + methodMeasurementValue + ".0",
                        "                        sum: " + methodMeasurementValue + ".0",
                        "                        nb-data-points: 1",
                        "                        percentiles:"
                ),

                percentiles.stream().map(percentile -> "                            p" + percentile + ": " + methodMeasurementValue + ".0"),

                Stream.of(
                        "                classes:",
                        "                    -",
                        "                        name: " + classMetricsName,
                        "                        measurements:",
                        "                            -",
                        "                                short-name: " + classShortName,
                        "                                long-name: " + classLongName,
                        "                                value: " + methodMeasurementValue + ".0",
                        "                                minimum: " + methodMeasurementValue + ".0",
                        "                                median: " + methodMeasurementValue + ".0",
                        "                                average: " + methodMeasurementValue + ".0",
                        "                                standard-deviation: 0.0",
                        "                                maximum: " + methodMeasurementValue + ".0",
                        "                                sum: " + methodMeasurementValue + ".0",
                        "                                nb-data-points: 1",
                        "                                percentiles:"
                ),

                percentiles.stream().map(percentile -> "                                    p" + percentile + ": " + methodMeasurementValue + ".0"),

                Stream.of(
                        "                        methods:",
                        "                            -",
                        "                                name: " + methodMetricsName,
                        "                                measurements:",
                        "                                    -",
                        "                                        short-name: " + methodShortName,
                        "                                        long-name: " + methodLongName,
                        "                                        value: " + methodMeasurementValue + ".0"
                )
        ).flatMap(Function.identity());

        // When
        printer.visitMetrics(Collections.singleton(projectMetrics));

        // Then
        assertLinesMatch(expectedLines, buffer.toString().lines());
    }
}
