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

import java.io.*;
import javax.xml.parsers.*;

import junit.framework.*;
import org.xml.sax.*;

public class TestMetricsConfigurationHandler extends TestCase {
    private MetricsConfigurationHandler handler;
    private XMLReader                   reader;
    
    protected void setUp() throws Exception {
        handler = new MetricsConfigurationHandler();
        
        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setDTDHandler(handler);
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        if (Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE")) {
            reader.setFeature("http://xml.org/sax/features/validation", true);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
        } else {
            reader.setFeature("http://xml.org/sax/features/validation", false);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        }
    }
    
    public void testEmptyFile() throws IOException, SAXException {
        InputSource in = new InputSource(new StringReader(""));

        try {
            reader.parse(in);
            fail("Read empty file");
        } catch (SAXParseException ex) {
            // Ignore
        }

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());
    }

    public void testEmptyDocument() throws IOException, SAXException {
        InputSource in = new InputSource(new StringReader("<metrics-configuration/>"));

        reader.parse(in);

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());
    }

    public void testNonWellFormedDocument() throws IOException, SAXException {
        InputSource in = new InputSource(new StringReader("<metrics-configuration>"));

        try {
            reader.parse(in);
            fail("Read non well formed file");
        } catch (SAXParseException ex) {
            // Ignore
        }

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());
    }

    public void testValidation() throws IOException, SAXException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements/>\n");
        document.append("</metrics-configuration>\n");

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());
    }

    public void testPackageMeasurement() throws IOException, SAXException {
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

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        assertEquals("ProjectMeasurements", 1, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getProjectMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC\n                DISPOSE_SUM", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertNull("descriptor.UpperThreshold()", descriptor.getUpperThreshold());
    }

    public void testGroupMeasurement() throws IOException, SAXException {
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

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   1, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getGroupMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC\n                DISPOSE_SUM", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertNull("descriptor.UpperThreshold()", descriptor.getUpperThreshold());
    }

    public void testClassMeasurement() throws IOException, SAXException {
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

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   1, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getClassMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.StatisticalMeasurement.class, descriptor.getClassFor());
        assertEquals("SLOC", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertNull("descriptor.UpperThreshold()", descriptor.getUpperThreshold());
    }

    public void testMethodMeasurement() throws IOException, SAXException {
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

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  1, handler.getMetricsConfiguration().getMethodMeasurements().size());

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getMethodMeasurements().get(0);
        assertEquals("SLOC", descriptor.getShortName());
        assertEquals("Single Lines of Code", descriptor.getLongName());
        assertEquals(com.jeantessier.metrics.CounterMeasurement.class, descriptor.getClassFor());
        assertNull("descriptor.Init()", descriptor.getInitText());
        assertNull("descriptor.LowerThreshold()", descriptor.getLowerThreshold());
        assertEquals("descriptor.UpperThreshold()", "50", descriptor.getUpperThreshold());
    }

    public void testTrueVisible() throws IOException, SAXException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements>\n");
        document.append("        <measurement visible=\"true\">\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.CounterMeasurement</class>\n");
        document.append("        </measurement>\n");
        document.append("    </method-measurements>\n");
        document.append("</metrics-configuration>\n");

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getMethodMeasurements().get(0);
        assertTrue("Not visible", descriptor.isVisible());
    }

    public void testYesVisible() throws IOException, SAXException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements>\n");
        document.append("        <measurement visible=\"yes\">\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.CounterMeasurement</class>\n");
        document.append("        </measurement>\n");
        document.append("    </method-measurements>\n");
        document.append("</metrics-configuration>\n");

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getMethodMeasurements().get(0);
        assertTrue("Not visible", descriptor.isVisible());
    }

    public void testOnVisible() throws IOException, SAXException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements>\n");
        document.append("        <measurement visible=\"on\">\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.CounterMeasurement</class>\n");
        document.append("        </measurement>\n");
        document.append("    </method-measurements>\n");
        document.append("</metrics-configuration>\n");

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getMethodMeasurements().get(0);
        assertTrue("Not visible", descriptor.isVisible());
    }

    public void testDefaultVisible() throws IOException, SAXException {
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
        document.append("        </measurement>\n");
        document.append("    </method-measurements>\n");
        document.append("</metrics-configuration>\n");

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getMethodMeasurements().get(0);
        assertTrue("Not visible", descriptor.isVisible());
    }

    public void testNotVisible() throws IOException, SAXException {
        StringBuffer document = new StringBuffer();

        document.append("<!DOCTYPE metrics-configuration SYSTEM \"http://depfind.sourceforge.net/dtd/metrics-configuration.dtd\">\n");
        document.append("<metrics-configuration>\n");
        document.append("    <project-measurements/>\n");
        document.append("    <group-measurements/>\n");
        document.append("    <class-measurements/>\n");
        document.append("    <method-measurements>\n");
        document.append("        <measurement visible=\"no\">\n");
        document.append("            <short-name>SLOC</short-name>\n");
        document.append("            <long-name>Single Lines of Code</long-name>\n");
        document.append("            <class>com.jeantessier.metrics.CounterMeasurement</class>\n");
        document.append("        </measurement>\n");
        document.append("    </method-measurements>\n");
        document.append("</metrics-configuration>\n");

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        MeasurementDescriptor descriptor = (MeasurementDescriptor) handler.getMetricsConfiguration().getMethodMeasurements().get(0);
        assertTrue("Visible", !descriptor.isVisible());
    }

    public void testGroupDefinitions() throws IOException, SAXException {
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

        InputSource in = new InputSource(new StringReader(document.toString()));

        reader.parse(in);

        assertEquals("ProjectMeasurements", 0, handler.getMetricsConfiguration().getProjectMeasurements().size());
        assertEquals("GroupMeasurements",   0, handler.getMetricsConfiguration().getGroupMeasurements().size());
        assertEquals("ClassMeasurements",   0, handler.getMetricsConfiguration().getClassMeasurements().size());
        assertEquals("MethodMeasurements",  0, handler.getMetricsConfiguration().getMethodMeasurements().size());

        assertEquals("groups for foobar",                  0, handler.getMetricsConfiguration().getGroups("foobar").size());
        assertEquals("groups for com.jeantessier.metrics", 1, handler.getMetricsConfiguration().getGroups("com.jeantessier.metrics").size());
        assertEquals("Jean Tessier", handler.getMetricsConfiguration().getGroups("com.jeantessier.metrics").iterator().next());
    }
}
