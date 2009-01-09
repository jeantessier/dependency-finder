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

package com.jeantessier.dependency;

import java.io.*;

import javax.xml.parsers.*;

import org.xml.sax.*;

import junit.framework.*;

public class TestNodeLoader extends TestCase {
    private static final String MY_PACKAGE_NAME = "mypackage";
    private static final String MY_CLASS_NAME = "mypackage.MyClass";
    private static final String MY_FEATURE_NAME = "mypackage.MyClass.myFeature";
    private static final String OTHER_PACKAGE_NAME = "otherpackage";
    private static final String OTHER_CLASS_NAME = "otherpackage.OtherClass";
    private static final String OTHER_FEATURE_NAME = "otherpackage.OtherClass.otherFeature";
    
    public void testReadDocument() throws IOException, ParserConfigurationException, SAXException {
        StringBuffer xml = new StringBuffer();

        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
        xml.append("\n");
        xml.append("<!DOCTYPE dependencies SYSTEM \"http://depfind.sourceforge.net/dtd/dependencies.dtd\">\n");
        xml.append("\n");
        xml.append("<dependencies>\n");
        xml.append("    <package confirmed=\"yes\">\n");
        xml.append("        <name>").append(MY_PACKAGE_NAME).append("</name>\n");
        xml.append("        <outbound type=\"package\" confirmed=\"no\">").append(OTHER_PACKAGE_NAME).append("</outbound>\n");
        xml.append("        <class confirmed=\"yes\">\n");
        xml.append("            <name>").append(MY_CLASS_NAME).append("</name>\n");
        xml.append("            <outbound type=\"class\" confirmed=\"no\">").append(OTHER_CLASS_NAME).append("</outbound>\n");
        xml.append("            <feature confirmed=\"yes\">\n");
        xml.append("                <name>").append(MY_FEATURE_NAME).append("</name>\n");
        xml.append("                <outbound type=\"feature\" confirmed=\"no\">").append(OTHER_FEATURE_NAME).append("</outbound>\n");
        xml.append("            </feature>\n");
        xml.append("        </class>\n");
        xml.append("    </package>\n");
        xml.append("    <package confirmed=\"no\">\n");
        xml.append("        <name>").append(OTHER_PACKAGE_NAME).append("</name>\n");
        xml.append("        <inbound type=\"package\" confirmed=\"yes\">").append(MY_PACKAGE_NAME).append("</inbound>\n");
        xml.append("        <class confirmed=\"no\">\n");
        xml.append("            <name>").append(OTHER_CLASS_NAME).append("</name>\n");
        xml.append("            <inbound type=\"class\" confirmed=\"yes\">").append(MY_CLASS_NAME).append("</inbound>\n");
        xml.append("            <feature confirmed=\"no\">\n");
        xml.append("                <name>").append(OTHER_FEATURE_NAME).append("</name>\n");
        xml.append("                <inbound type=\"feature\" confirmed=\"yes\">").append(MY_FEATURE_NAME).append("</inbound>\n");
        xml.append("            </feature>\n");
        xml.append("        </class>\n");
        xml.append("    </package>\n");
        xml.append("</dependencies>\n");

        NodeLoader loader = new NodeLoader();
        NodeFactory factory = loader.load(new StringReader(xml.toString()));

        assertEquals("nb packages", 2, factory.getPackages().size());
        assertNotNull("missing package", factory.getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", factory.getPackages().get(MY_PACKAGE_NAME).isConfirmed());
        assertNotNull("missing package", factory.getPackages().get(OTHER_PACKAGE_NAME));
        assertFalse("package is confirmed", factory.getPackages().get(OTHER_PACKAGE_NAME).isConfirmed());

        assertEquals("nb classes", 2, factory.getClasses().size());
        assertNotNull("missing class", factory.getClasses().get(MY_CLASS_NAME));
        assertTrue("class is inferred", factory.getClasses().get(MY_CLASS_NAME).isConfirmed());
        assertNotNull("missing class", factory.getClasses().get(OTHER_CLASS_NAME));
        assertFalse("class is confirmed", factory.getClasses().get(OTHER_CLASS_NAME).isConfirmed());

        assertEquals("nb features", 2, factory.getFeatures().size());
        assertNotNull("missing feature", factory.getFeatures().get(MY_FEATURE_NAME));
        assertTrue("feature is inferred", factory.getFeatures().get(MY_FEATURE_NAME).isConfirmed());
        assertNotNull("missing feature", factory.getFeatures().get(OTHER_FEATURE_NAME));
        assertFalse("feature is confirmed", factory.getFeatures().get(OTHER_FEATURE_NAME).isConfirmed());
    }
}
