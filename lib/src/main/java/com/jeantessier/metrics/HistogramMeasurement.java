package com.jeantessier.metrics;

import com.jeantessier.classreader.AttributeType;
import org.apache.logging.log4j.LogManager;
import org.apache.oro.text.perl.Perl5Util;

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
 *      [PLOT_x]
 *  &lt;/init&gt;
 *  </pre>
 */
public class HistogramMeasurement extends MeasurementBase {
    public enum Plot {
        /**
         * Plot both the x-axis and y-axis using a linear scale.
         */
        LINEAR("linear", "/plot_linear$/i"),

        /**
         * Plot the x-axis using a linear scale and the y-axis using a logarithmic scale.
         * Log-lin plot.  https://en.wikipedia.org/wiki/Semi-log_plot.
         */
        LOG_LINEAR("log-lin", "/plot_log_lin(ear)?$/i"),

        /**
         * Plot the x-axis using a logarithmic scale and the y-axis using a linear scale.
         * Lin-log plot.  https://en.wikipedia.org/wiki/Semi-log_plot.
         */
        LINEAR_LOG("lin-log", "/plot_lin(ear)?_log$/i"),

        /**
         * Plot both the x-axis and y-axis using a logarithmic scale.
         * Log-log plot.  See https://en.wikipedia.org/wiki/Log%E2%80%93log_plot.
         */
        LOG_LOG("log-log", "/plot_log_log$/i");

        private final static Perl5Util perl = new Perl5Util();

        private final String label;
        private final String regex;

        Plot(String label, String regex) {
            this.label = label;
            this.regex = regex;
        }

        public String getLabel() {
            return label;
        }

        public static Plot forName(String name) {
            return Arrays.stream(values())
                    .filter(plot -> perl.match(plot.regex, name))
                    .findFirst()
                    .orElse(null);
        }
    }

    private String monitoredMeasurement;
    private int dispose;

    private Map<Integer, Integer> histogram = new HashMap<>();
    private Plot plot;

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

            String plotText = in.readLine();
            if (plotText != null) {
                plot = Plot.forName(plotText);
            }
            if (plot == null) {
                plot = Plot.LINEAR;
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

    public Plot getPlot() {
        return plot;
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
