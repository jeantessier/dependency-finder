/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

import org.apache.log4j.*;

public class Metrics {
    private static final Measurement NULL_MEASUREMENT = new NullMeasurement();
    
    private Metrics parent;
    private String  name;

    private Map<String, Measurement> measurements = new TreeMap<String, Measurement>();
    private Map<String, Metrics> submetrics = new TreeMap<String, Metrics>();

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
        this.parent = parent;
        this.name   = name;

        if (parent == null) {
            Logger.getLogger(getClass()).debug("Created top-level metrics \"" + name + "\"");
        } else {
            Logger.getLogger(getClass()).debug("Created metrics \"" + name + "\" under \"" + parent.getName() + "\"");
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

    void track(Measurement measurement) {
        track(measurement.getShortName(), measurement);
    }
    
    void track(String name, Measurement measurement) {
        measurements.put(name, measurement);
    }

    public void addToMeasurement(BasicMeasurements name) {
        addToMeasurement(name.getAbbreviation());
    }

    public void addToMeasurement(String name) {
        addToMeasurement(name, 1);
    }

    public void addToMeasurement(BasicMeasurements name, int delta) {
        addToMeasurement(name.getAbbreviation(), delta);
    }

    public void addToMeasurement(String name, int delta) {
        getMeasurement(name).add(delta);
    }

    public void addToMeasurement(BasicMeasurements name, long delta) {
        addToMeasurement(name.getAbbreviation(), delta);
    }

    public void addToMeasurement(String name, long delta) {
        getMeasurement(name).add(delta);
    }
    
    public void addToMeasurement(BasicMeasurements name, float delta) {
        addToMeasurement(name.getAbbreviation(), delta);
    }

    public void addToMeasurement(String name, float delta) {
        getMeasurement(name).add(delta);
    }
    
    public void addToMeasurement(BasicMeasurements name, double delta) {
        addToMeasurement(name.getAbbreviation(), delta);
    }

    public void addToMeasurement(String name, double delta) {
        getMeasurement(name).add(delta);
    }
    
    public void addToMeasurement(BasicMeasurements name, Object delta) {
        addToMeasurement(name.getAbbreviation(), delta);
    }

    public void addToMeasurement(String name, Object delta) {
        getMeasurement(name).add(delta);
    }

    public Measurement getMeasurement(BasicMeasurements name) {
        return getMeasurement(name.getAbbreviation());
    }

    public Measurement getMeasurement(String name) {
        Measurement result = measurements.get(name);
        
        if (result == null) {
            result = NULL_MEASUREMENT;
            Logger.getLogger(getClass()).info("Null measurement \"" + name + "\" on \"" + getName() + "\"");
        }

        return result;
    }

    public boolean hasMeasurement(String name) {
        return measurements.get(name) != null;
    }
    
    public Collection<String> getMeasurementNames() {
        return Collections.unmodifiableCollection(measurements.keySet());
    }
    
    public Metrics addSubMetrics(Metrics metrics) {
        return submetrics.put(metrics.getName(), metrics);
    }
    
    public Collection<Metrics> getSubMetrics() {
        return Collections.unmodifiableCollection(submetrics.values());
    }

    public boolean isEmpty() {
        boolean result = true;

        Iterator<Measurement> i = measurements.values().iterator();
        while (result && i.hasNext()) {
            Measurement measurement = i.next();
            if (measurement.getDescriptor().isVisible()) {
                result = measurement.isEmpty();
            }
        }

        Iterator<Metrics> j = submetrics.values().iterator();
        while (result && j.hasNext()) {
            result = j.next().isEmpty();
        }
        
        return result;
    }

    public boolean isInRange() {
        boolean result = true;

        Iterator<Measurement> i = measurements.values().iterator();
        while (result && i.hasNext()) {
            result = i.next().isInRange();
        }
        
        return result;
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append(getClass().getName()).append(" ").append(getName()).append(" with [");

        Iterator<String> i = getMeasurementNames().iterator();
        while (i.hasNext()) {
            String name = i.next();
            Measurement measure = getMeasurement(name);

            result.append("\"").append(name).append("\"(").append(measure.getClass().getName()).append(")");
            if (i.hasNext()) {
                result.append(", ");
            }
        }

        result.append("]");

        return result.toString();
    }
}
