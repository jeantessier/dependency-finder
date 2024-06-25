#!/usr/bin/env bash

# Generates expected outputs from tools for use in integration tests.

# Run this script from the `integration-tests` directory:
#     ./generate_expected_outputs.sh

# To generate outputs using a different version of Dependency Finder,
# set $DEPENDENCYFINDER_HOME environment variable to the location of
# this other version.

readonly BINDIR=$DEPENDENCYFINDER_HOME/bin
readonly ETCDIR=$DEPENDENCYFINDER_HOME/etc

readonly OUTPUTDIR=src/test/resources

# OOMetrics JSON output
echo "Generating OOMetrics JSON output"
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.json -json
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.json -json -expand
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-empty-metrics.json -json -show-empty-metrics
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-hidden-measurements.json -json -show-hidden-measurements
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.show-hidden-measurements.json -json -expand -show-hidden-measurements

# OOMetrics YAML output
echo "Generating OOMetrics YAML output"
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.yml -yml
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.yml -yml -expand
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-empty-metrics.yml -yml -show-empty-metrics
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-hidden-measurements.yml -yml -show-hidden-measurements
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.show-hidden-measurements.yml -yml -expand -show-hidden-measurements

# OOMetrics XML output
echo "Generating OOMetrics XML output"
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.xml -xml
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.xml -xml -expand
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-empty-metrics.xml -xml -show-empty-metrics
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-hidden-measurements.xml -xml -show-hidden-measurements
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.show-hidden-measurements.xml -xml -expand -show-hidden-measurements

# OOMetrics text output
for level in project groups classes methods
do
    echo "Generating OOMetrics text output for $level"
    $BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics_$level.txt -txt -$level
    $BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand_$level.txt -txt -$level -expand
    $BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-empty-metrics_$level.txt -txt -$level -show-empty-metrics
    $BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-hidden-measurements_$level.txt -txt -$level -show-hidden-measurements
    $BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.show-hidden-measurements_$level.txt -txt -$level -expand -show-hidden-measurements
done

# OOMetrics CSV output
echo "Generating OOMetrics CSV output"
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics -csv
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand -csv -expand
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-empty-metrics -csv -show-empty-metrics
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.show-hidden-measurements -csv -show-hidden-measurements
$BINDIR/OOMetrics -configuration $ETCDIR/MartinConfig.xml metrics/build/classes/java/main/{client,provider} -out $OUTPUTDIR/martin.metrics.expand.show-hidden-measurements -csv -expand -show-hidden-measurements
