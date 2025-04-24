/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.junit.jupiter.api.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestMetricsConfigurationLoader {
    private final MetricsConfiguration configuration= new MetricsConfiguration();

    private final MetricsConfigurationLoader loader = new MetricsConfigurationLoader(configuration, Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE"));
    
    @Test
    void testEmptyFile() throws IOException, SAXException, ParserConfigurationException {
        Reader in = new StringReader("");

        try {
            loader.load(in);
            fail("Read empty file");
        } catch (SAXParseException ex) {
            // Ignore
        }

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");
    }

    @Test
    void testEmptyDocument() throws IOException, SAXException, ParserConfigurationException {
        Reader in = new StringReader("<metrics-configuration/>");

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");
    }

    @Test
    void testNonWellFormedDocument() throws IOException, SAXException, ParserConfigurationException {
        Reader in = new StringReader("<metrics-configuration>");

        try {
            loader.load(in);
            fail("Read non well formed file");
        } catch (SAXParseException ex) {
            // Ignore
        }

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");
    }

    @Test
    void testValidation() throws IOException, SAXException, ParserConfigurationException {
        StringBuilder document = new StringBuilder();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"https://jeantessier.github.io/dependency-finder/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements/>\n");
        document.append("</metrics-configuration>\n");

        Reader in = new StringReader(document.toString());

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");
    }

    @Test
    void testPackageMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuilder document = new StringBuilder();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"https://jeantessier.github.io/dependency-finder/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements>\n");
        document.append("        <measurement>\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.StatisticalMeasurement</class>\n");
        document.append("            <init>\n");
        document.append("                SLOC\n");
        document.append("                DISPOSE_SUM\n");
        document.append("            </init>\n");
        document.append("        </measurement>\n");
        document.append("    </project-measurements>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements/>\n");
        document.append("</metrics-configuration>\n");

        Reader in = new StringReader(document.toString());

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals(1, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");

        MeasurementDescriptor descriptor = configuration.getProjectMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC\n                DISPOSE_SUM", descriptor.getInitText());
        assertNull(descriptor.getLowerThreshold(), "descriptor.LowerThreshold()");
        assertNull(descriptor.getUpperThreshold(), "descriptor.UpperThreshold()");
    }

    @Test
    void testGroupMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuilder document = new StringBuilder();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"https://jeantessier.github.io/dependency-finder/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements>\n");
        document.append("        <measurement>\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.StatisticalMeasurement</class>\n");
        document.append("            <init>\n");
        document.append("                SLOC\n");
        document.append("                DISPOSE_SUM\n");
        document.append("            </init>\n");
        document.append("        </measurement>\n");
        document.append("    </group-measurements>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements/>\n");
        document.append("</metrics-configuration>\n");

        Reader in = new StringReader(document.toString());

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(1, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");

        MeasurementDescriptor descriptor = configuration.getGroupMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC\n                DISPOSE_SUM", descriptor.getInitText());
        assertNull(descriptor.getLowerThreshold(), "descriptor.LowerThreshold()");
        assertNull(descriptor.getUpperThreshold(), "descriptor.UpperThreshold()");
    }

    @Test
    void testClassMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuilder document = new StringBuilder();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"https://jeantessier.github.io/dependency-finder/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements>\n");
        document.append("        <measurement>\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.StatisticalMeasurement</class>\n");
        document.append("            <init>\n");
        document.append("                SLOC\n");
        document.append("            </init>\n");
        document.append("        </measurement>\n");
        document.append("    </class-measurements>\n");
        document.append("    <method-measurements/>\n");
        document.append("</metrics-configuration>\n");

        Reader in = new StringReader(document.toString());

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(1, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");

        MeasurementDescriptor descriptor = configuration.getClassMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC", descriptor.getInitText());
        assertNull(descriptor.getLowerThreshold(), "descriptor.LowerThreshold()");
        assertNull(descriptor.getUpperThreshold(), "descriptor.UpperThreshold()");
    }

    @Test
    void testMethodMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuilder document = new StringBuilder();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"https://jeantessier.github.io/dependency-finder/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements>\n");
        document.append("        <measurement>\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.CounterMeasurement</class>\n");
        document.append("            <upper-threshold>50</upper-threshold>\n");
        document.append("        </measurement>\n");
        document.append("    </method-measurements>\n");
        document.append("</metrics-configuration>\n");

        Reader in = new StringReader(document.toString());

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(1, configuration.getMethodMeasurements().size(), "MethodMeasurements");

        MeasurementDescriptor descriptor = configuration.getMethodMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.CounterMeasurement.class, descriptor.getClassFor());
        assertNull(descriptor.getInitText(), "descriptor.Init()");
        assertNull(descriptor.getLowerThreshold(), "descriptor.LowerThreshold()");
        assertEquals(50, descriptor.getUpperThreshold(), 0.01, "descriptor.UpperThreshold()");
    }

    @Test
    void testGroupDefinitions() throws IOException, SAXException, ParserConfigurationException {
        StringBuilder document = new StringBuilder();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"https://jeantessier.github.io/dependency-finder/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-definitions>\n");
        document.append("        <group-definition>\n");
        document.append("            <name>Jean Tessier</name>\n");
        document.append("            <pattern>/^com.jeantessier/</pattern>\n");
        document.append("        </group-definition>\n");
        document.append("    </group-definitions>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements/>\n");
        document.append("</metrics-configuration>\n");

        Reader in = new StringReader(document.toString());

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals(0, configuration.getProjectMeasurements().size(), "ProjectMeasurements");
        assertEquals(0, configuration.getGroupMeasurements().size(), "GroupMeasurements");
        assertEquals(0, configuration.getClassMeasurements().size(), "ClassMeasurements");
        assertEquals(0, configuration.getMethodMeasurements().size(), "MethodMeasurements");

        assertEquals(0, configuration.getGroups("foobar").size(), "groups for foobar");
        assertEquals(1, configuration.getGroups("com.jeantessier.metrics").size(), "groups for com.jeantessier.metrics");
        assertEquals("Jean Tessier", configuration.getGroups("com.jeantessier.metrics").iterator().next());
    }
}
