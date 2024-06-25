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

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import fitlibrary.*;
import org.xml.sax.*;

import com.jeantessier.classreader.*;

public class MetricsGathererFixture extends DoFixture {
    private MetricsConfiguration configuration;
    private ClassfileLoader loader;
    private MetricsFactory factory;

    public void loadConfigurationFrom(String configFilename) throws IOException, ParserConfigurationException, SAXException {
        MetricsConfigurationLoader configurationLoader = new MetricsConfigurationLoader();
        configuration = configurationLoader.load(configFilename);
    }

    public void loadClassesFrom(String classpath) {
        if (loader == null) {
            loader = new AggregatingClassfileLoader();
        }
        loader.load(Collections.singleton(classpath));
    }

    public void computeMetrics() {
        factory = new MetricsFactory("test", configuration);
        MetricsGatherer gatherer = new MetricsGatherer(factory);
        gatherer.visitClassfiles(loader.getAllClassfiles());
    }

    public Collection<SingleMeasurement> getMetricsForGroup(String groupname) {
        return getMetrics(groupname, factory.getGroupMetrics());
    }

    public Collection<SingleMeasurement> getMetricsForClass(String classname) {
        return getMetrics(classname, factory.getClassMetrics());
    }

    public Collection<SingleMeasurement> getMetricsForMethod(String methodname) {
        return getMetrics(methodname, factory.getMethodMetrics());
    }

    private Collection<SingleMeasurement> getMetrics(String name, Collection<Metrics> metricsCollection) {
        return metricsCollection.stream()
                .filter(metrics -> metrics.getName().equals(name))
                .map(this::convertMetricsToMeasurements)
                .findAny()
                .orElseThrow(() -> new RuntimeException("No metrics found for \"" + name + "\" in " + metricsCollection.stream().map(Metrics::getName).toList()));
    }

    private Collection<SingleMeasurement> convertMetricsToMeasurements(Metrics metrics) {
        return metrics.getMeasurementNames().stream()
                .map(metrics::getMeasurement)
                .map(measurement -> new SingleMeasurement(measurement.getShortName(), measurement.getLongName(), measurement.getValue().doubleValue(), measurement.isInRange()))
                .toList();
    }
}
