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

import junit.framework.*;

import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

public class TestMetricsConfigurationLoader extends TestCase {
    private MetricsConfiguration       configuration;
    private MetricsConfigurationLoader loader;
    
    protected void setUp() throws Exception {
        configuration = new MetricsConfiguration();
        loader        = new MetricsConfigurationLoader(configuration, Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE"));
    }
    
    public void testEmptyFile() throws IOException, SAXException, ParserConfigurationException {
        Reader in = new StringReader("");

        try {
            loader.load(in);
            fail("Read empty file");
        } catch (SAXParseException ex) {
            // Ignore
        }

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());
    }

    public void testEmptyDocument() throws IOException, SAXException, ParserConfigurationException {
        Reader in = new StringReader("<metrics-configuration/>");

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());
    }

    public void testNonWellFormedDocument() throws IOException, SAXException, ParserConfigurationException {
        Reader in = new StringReader("<metrics-configuration>");

        try {
            loader.load(in);
            fail("Read non well formed file");
        } catch (SAXParseException ex) {
            // Ignore
        }

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());
    }

    public void testValidation() throws IOException, SAXException, ParserConfigurationException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements/>\n");
        document.append("</metrics-configuration>\n");

        Reader in = new StringReader(document.toString());

        MetricsConfiguration configuration2 = loader.load(in);
        assertSame(configuration, configuration2);

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());
    }

    public void testPackageMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
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

        assertEquals("ProjectMeasurements", 1, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) configuration.getProjectMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC\n                DISPOSE_SUM", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertNull("descriptor.UpperThreshold()", descriptor.getUpperThreshold());
    }

    public void testGroupMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
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

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   1, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) configuration.getGroupMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC\n                DISPOSE_SUM", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertNull("descriptor.UpperThreshold()", descriptor.getUpperThreshold());
    }

    public void testClassMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
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

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   1, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) configuration.getClassMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertNull("descriptor.UpperThreshold()", descriptor.getUpperThreshold());
    }

    public void testMethodMeasurement() throws IOException, SAXException, ParserConfigurationException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
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

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  1, configuration.getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) configuration.getMethodMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.CounterMeasurement.class, descriptor.getClassFor());
        assertNull("descriptor.Init()", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertEquals("descriptor.UpperThreshold()", "50", descriptor.getUpperThreshold());
    }

    public void testGroupDefinitions() throws IOException, SAXException, ParserConfigurationException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
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

        assertEquals("ProjectMeasurements", 0, configuration.getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, configuration.getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, configuration.getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, configuration.getMethodMeasurements().size());

        assertEquals("groups for foobar",                  0, configuration.getGroups("foobar").size());
        assertEquals("groups for com.jeantessier.metrics", 1, configuration.getGroups("com.jeantessier.metrics").size());
        assertEquals("Jean Tessier", configuration.getGroups("com.jeantessier.metrics").iterator().next());
    }
}
