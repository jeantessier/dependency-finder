/*
 *  Copyright (c) 2001-2023, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jeantessier.metrics;

import java.util.*;
import java.util.stream.*;

import org.apache.logging.log4j.*;

public class Metrics {
    private static final Measurement NULL_MEASUREMENT = new NullMeasurement();
    
    private final Metrics parent;
    private final String  name;
    private final String  key;

    private final Map<String, Measurement> measurements = new TreeMap<>();
    private final Map<String, Metrics> submetrics = new TreeMap<>();

    public Metrics(String name) {
        this(null, name);
    }
    
    /**
     * @param parent The context for this metrics (e.g., methods's class, class'
     *               package).  You may pass <code>null</code> to create
     *               top-level metrics.
     * @param name The name of the element being measured
     *             (e.g., class name, method name).
     */
    public Metrics(Metrics parent, String name) {
        this(parent, name, name);
    }

    public Metrics(Metrics parent, String name, String key) {
        this.parent = parent;
        this.name   = name;
        this.key    = key;

        if (parent == null) {
            LogManager.getLogger(getClass()).debug("Created top-level metrics \"{}\"", name);
        } else {
            LogManager.getLogger(getClass()).debug("Created metrics \"{}\" under \"{}\"", name, parent.getName());
        }
    }

    public Metrics getParent() {
        return parent;
    }

    /**
     *  @return The name of the element being measured
     *          (e.g., class name, method name).
     */
    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    Metrics track(Measurement measurement) {
        return track(measurement.getShortName(), measurement);
    }
    
    Metrics track(String name, Measurement measurement) {
        measurements.put(name, measurement);
        return this;
    }

    public Metrics addToMeasurement(BasicMeasurements name) {
        return addToMeasurement(name.getAbbreviation());
    }

    public Metrics addToMeasurement(String name) {
        return addToMeasurement(name, 1);
    }

    public Metrics addToMeasurement(BasicMeasurements name, int delta) {
        return addToMeasurement(name.getAbbreviation(), delta);
    }

    public Metrics addToMeasurement(String name, int delta) {
        getMeasurement(name).add(delta);
        return this;
    }

    public Metrics addToMeasurement(BasicMeasurements name, long delta) {
        return addToMeasurement(name.getAbbreviation(), delta);
    }

    public Metrics addToMeasurement(String name, long delta) {
        getMeasurement(name).add(delta);
        return this;
    }
    
    public Metrics addToMeasurement(BasicMeasurements name, float delta) {
        return addToMeasurement(name.getAbbreviation(), delta);
    }

    public Metrics addToMeasurement(String name, float delta) {
        getMeasurement(name).add(delta);
        return this;
    }
    
    public Metrics addToMeasurement(BasicMeasurements name, double delta) {
        return addToMeasurement(name.getAbbreviation(), delta);
    }

    public Metrics addToMeasurement(String name, double delta) {
        getMeasurement(name).add(delta);
        return this;
    }
    
    public Metrics addToMeasurement(BasicMeasurements name, Object delta) {
        return addToMeasurement(name.getAbbreviation(), delta);
    }

    public Metrics addToMeasurement(String name, Object delta) {
        getMeasurement(name).add(delta);
        return this;
    }

    public Measurement getMeasurement(BasicMeasurements name) {
        return getMeasurement(name.getAbbreviation());
    }

    public Measurement getMeasurement(String name) {
        return measurements.getOrDefault(name, NULL_MEASUREMENT);
    }

    public boolean hasMeasurement(String name) {
        return measurements.containsKey(name);
    }
    
    public Collection<String> getMeasurementNames() {
        return Collections.unmodifiableCollection(measurements.keySet());
    }
    
    public Metrics addSubMetrics(Metrics metrics) {
        return submetrics.put(metrics.getKey(), metrics);
    }
    
    public Collection<Metrics> getSubMetrics() {
        return Collections.unmodifiableCollection(submetrics.values());
    }

    public boolean isEmpty() {
        return
                measurements.values().stream()
                        .filter(measurement -> measurement.getDescriptor().isVisible())
                        .allMatch(Measurement::isEmpty) &&
                submetrics.values().stream()
                        .allMatch(Metrics::isEmpty);
    }

    public boolean isInRange() {
        return measurements.values().stream()
                .filter(measurement -> measurement.getDescriptor().isVisible())
                .allMatch(Measurement::isInRange);
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder();

        result.append(getClass().getName()).append(" ").append(getName()).append(" with [");

        result.append(measurements.entrySet().stream()
                .map(entry -> "\"" + entry.getKey() + "\"(" + entry.getValue().getClass().getName() + ")")
                .collect(Collectors.joining(", "))
        );

        result.append("]");

        return result.toString();
    }
}
