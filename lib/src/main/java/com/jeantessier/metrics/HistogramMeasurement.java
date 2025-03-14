package com.jeantessier.metrics;

import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

/**
 *  <p>Creates a histogram of a measurement in submetrics.</p>
 *
 *  <p>This is the syntax for initializing this type of
 *  measurement:</p>
 *
 *  <pre>
 *  &lt;init&gt;
 *      measurement name [DISPOSE_x]
 *  &lt;/init&gt;
 *  </pre>
 */
public class HistogramMeasurement extends MeasurementBase {
    private String monitoredMeasurement;
    private int dispose;

    private Map<Integer, Integer> histogram = new HashMap<>();

    private int nbSubmetrics = -1;

    public HistogramMeasurement(MeasurementDescriptor descriptor, Metrics context, String initText) {
        super(descriptor, context, initText);

        try (var in = new BufferedReader(new StringReader(initText))) {
            monitoredMeasurement = in.readLine().trim();

            synchronized (perl()) {
                if (perl().match("/(.*)\\s+(dispose_\\w+)$/i", monitoredMeasurement)) {
                    monitoredMeasurement = perl().group(1);

                    String disposeText = perl().group(2);
                    dispose = StatisticalMeasurement.getDispose(disposeText, () -> {
                        LogManager.getLogger(getClass()).error("Unknown dispose value \"{}\" for monitored measurement \"{}\" of measurement \"{}\", defaulting to DISPOSE_IGNORE", disposeText, monitoredMeasurement, descriptor.getLongName());
                        return StatisticalMeasurement.DISPOSE_IGNORE;
                    });
                } else {
                    dispose = StatisticalMeasurement.DISPOSE_IGNORE;
                }
            }
        } catch (Exception ex) {
            LogManager.getLogger(getClass()).debug("Cannot initialize with \"{}\"", initText, ex);
            monitoredMeasurement = null;
        }
    }

    public Map<Integer, Integer> getHistogram() {
        collectData();
        return histogram;
    }

    private void collectData() {
        if (getContext().getSubMetrics().size() != nbSubmetrics) {
            synchronized (this) {
                if (getContext().getSubMetrics().size() != nbSubmetrics) {
                    histogram = new HashMap<>();
                    setEmpty(true);

                    getContext().getSubMetrics().forEach(this::visitMetrics);

                    nbSubmetrics = getContext().getSubMetrics().size();
                }
            }
        }
    }

    private void visitMetrics(Metrics metrics) {
        LogManager.getLogger(getClass()).debug("VisitMetrics: {}", metrics.getName());

        Measurement measurement = metrics.getMeasurement(monitoredMeasurement);

        LogManager.getLogger(getClass()).debug("measurement for {} is {}", monitoredMeasurement, measurement.getClass());

        if (measurement instanceof StatisticalMeasurement stats) {
            LogManager.getLogger(getClass()).debug("dispose of StatisticalMeasurements is {}", dispose);

            switch (dispose) {
                case StatisticalMeasurement.DISPOSE_MINIMUM -> {
                    LogManager.getLogger(getClass()).debug("using Minimum(): {}", stats.getMinimum());
                    increment(stats.getMinimum());
                }
                case StatisticalMeasurement.DISPOSE_MEDIAN -> {
                    LogManager.getLogger(getClass()).debug("using Median(): {}", stats.getMedian());
                    increment(stats.getMedian());
                }
                case StatisticalMeasurement.DISPOSE_AVERAGE -> {
                    LogManager.getLogger(getClass()).debug("using Average(): {}", stats.getAverage());
                    increment(stats.getAverage());
                }
                case StatisticalMeasurement.DISPOSE_STANDARD_DEVIATION -> {
                    LogManager.getLogger(getClass()).debug("using StandardDeviation(): {}", stats.getStandardDeviation());
                    increment(stats.getStandardDeviation());
                }
                case StatisticalMeasurement.DISPOSE_MAXIMUM -> {
                    LogManager.getLogger(getClass()).debug("using Maximum(): {}", stats.getMaximum());
                    increment(stats.getMaximum());
                }
                case StatisticalMeasurement.DISPOSE_SUM -> {
                    LogManager.getLogger(getClass()).debug("using Sum(): {}", stats.getSum());
                    increment(stats.getSum());
                }
                case StatisticalMeasurement.DISPOSE_NB_DATA_POINTS -> {
                    LogManager.getLogger(getClass()).debug("using NbDataPoints(): {}", stats.getNbDataPoints());
                    increment(stats.getNbDataPoints());
                }
                default -> {
                    LogManager.getLogger(getClass()).debug("Skipping to next level ...");
                    metrics.getSubMetrics().forEach(this::visitMetrics);
                }
            }
        } else if (measurement instanceof NullMeasurement) {
            LogManager.getLogger(getClass()).debug("Skipping to next level ...");
            metrics.getSubMetrics().forEach(this::visitMetrics);
        } else {
            Number value = measurement.getValue();

            LogManager.getLogger(getClass()).debug("{} on {} is {}", monitoredMeasurement, metrics.getName(), value);

            if (value != null) {
                increment(value.doubleValue());
            }
        }

        if (super.isEmpty()) {
            setEmpty(measurement.isEmpty());
        }
    }

    private void increment(double value) {
        histogram.compute((int) Math.round(value), (bucket, count) -> count == null ? 1 : count + 1);
    }

    public boolean isEmpty() {
        collectData();
        return super.isEmpty();
    }

    public void accept(MeasurementVisitor visitor) {
        visitor.visitHistogramMeasurement(this);
    }

    protected double compute() {
        collectData();

        if (histogram.isEmpty()) {
            return Double.NaN;
        }

        return histogram.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .get();
    }

    public String toString() {
        collectData();
        return histogram.toString();
    }
}
