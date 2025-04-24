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

package com.jeantessier.dependency;

import org.junit.jupiter.api.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestNodeHandler {
    private static final String MY_PACKAGE_NAME = "mypackage";
    private static final String MY_CLASS_NAME = "mypackage.MyClass";
    private static final String MY_FEATURE_NAME = "mypackage.MyClass.myFeature";
    private static final String OTHER_PACKAGE_NAME = "otherpackage";
    private static final String OTHER_CLASS_NAME = "otherpackage.OtherClass";
    private static final String OTHER_FEATURE_NAME = "otherpackage.OtherClass.otherFeature";
    
    private final NodeHandler handler = new NodeHandler();

    @Test
    void testConstructor() {
        assertEquals(0, handler.getFactory().getPackages().size(), "nb packages");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testEmptyDocument() throws SAXException {
        handler.startDocument();
        handler.endDocument();

        assertEquals(0, handler.getFactory().getPackages().size(), "nb packages");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testEmptyTopNode() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        
        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(0, handler.getFactory().getPackages().size(), "nb packages");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testUnspecifiedPackageNodeIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testInferredPackageNode() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo    = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testConfirmedPackageNode() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes   = new AttributesImpl();
        attsYes.addAttribute(null, null, "confirmed", null, "yes");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testUnspecifiedClassNodeIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "class", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_CLASS_NAME.toCharArray(), 0, MY_CLASS_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "class");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(MY_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(MY_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testInferredClassNode() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "class", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_CLASS_NAME.toCharArray(), 0, MY_CLASS_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "class");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(MY_CLASS_NAME), "missing class");
        assertFalse(handler.getFactory().getClasses().get(MY_CLASS_NAME).isConfirmed(), "class is confirmed");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testConfirmedClassNode() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "class", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_CLASS_NAME.toCharArray(), 0, MY_CLASS_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "class");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(MY_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(MY_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testUnspecifiedFeatureNodeIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "class", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_CLASS_NAME.toCharArray(), 0, MY_CLASS_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "feature", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_FEATURE_NAME.toCharArray(), 0, MY_FEATURE_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "feature");
        handler.endElement(null, null, "class");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(MY_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(MY_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(MY_FEATURE_NAME), "missing feature");
        assertTrue(handler.getFactory().getFeatures().get(MY_FEATURE_NAME).isConfirmed(), "feature is inferred");
    }

    @Test
    void testInferredFeatureNode() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "class", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_CLASS_NAME.toCharArray(), 0, MY_CLASS_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "feature", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_FEATURE_NAME.toCharArray(), 0, MY_FEATURE_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "feature");
        handler.endElement(null, null, "class");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(MY_CLASS_NAME), "missing class");
        assertFalse(handler.getFactory().getClasses().get(MY_CLASS_NAME).isConfirmed(), "class is confirmed");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(MY_FEATURE_NAME), "missing feature");
        assertFalse(handler.getFactory().getFeatures().get(MY_FEATURE_NAME).isConfirmed(), "feature is confirmed");
    }

    @Test
    void testConfirmedFeatureNode() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "class", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_CLASS_NAME.toCharArray(), 0, MY_CLASS_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "feature", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_FEATURE_NAME.toCharArray(), 0, MY_FEATURE_NAME.length());
        handler.endElement(null, null, "name");
        handler.endElement(null, null, "feature");
        handler.endElement(null, null, "class");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(1, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(MY_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(MY_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(MY_FEATURE_NAME), "missing feature");
        assertTrue(handler.getFactory().getFeatures().get(MY_FEATURE_NAME).isConfirmed(), "feature is inferred");
    }

    @Test
    void testUnspecifiedInboundPackageDependencyIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        AttributesImpl attsPackage = new AttributesImpl();
        attsPackage.addAttribute(null, null, "type", null, "package");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsPackage);
        handler.characters(OTHER_PACKAGE_NAME.toCharArray(), 0, OTHER_PACKAGE_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testInferredInboundPackageDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");
        AttributesImpl attsPackageNo = new AttributesImpl();
        attsPackageNo.addAttribute(null, null, "type", null, "package");
        attsPackageNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsPackageNo);
        handler.characters(OTHER_PACKAGE_NAME.toCharArray(), 0, OTHER_PACKAGE_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testConfirmedInboundPackageDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();
        attsYes.addAttribute(null, null, "confirmed", null, "yes");
        AttributesImpl attsPackageYes = new AttributesImpl();
        attsPackageYes.addAttribute(null, null, "type", null, "package");
        attsPackageYes.addAttribute(null, null, "confirmed", null, "yes");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsPackageYes);
        handler.characters(OTHER_PACKAGE_NAME.toCharArray(), 0, OTHER_PACKAGE_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testUnspecifiedOutboundPackageDependencyIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        AttributesImpl attsPackage = new AttributesImpl();
        attsPackage.addAttribute(null, null, "type", null, "package");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsPackage);
        handler.characters(OTHER_PACKAGE_NAME.toCharArray(), 0, OTHER_PACKAGE_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testInferredOutboundPackageDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");
        AttributesImpl attsPackageNo = new AttributesImpl();
        attsPackageNo.addAttribute(null, null, "type", null, "package");
        attsPackageNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsPackageNo);
        handler.characters(OTHER_PACKAGE_NAME.toCharArray(), 0, OTHER_PACKAGE_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testConfirmedOutboundPackageDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();
        attsYes.addAttribute(null, null, "confirmed", null, "yes");
        AttributesImpl attsPackageYes = new AttributesImpl();
        attsPackageYes.addAttribute(null, null, "type", null, "package");
        attsPackageYes.addAttribute(null, null, "confirmed", null, "yes");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsPackageYes);
        handler.characters(OTHER_PACKAGE_NAME.toCharArray(), 0, OTHER_PACKAGE_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(0, handler.getFactory().getClasses().size(), "nb classes");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testUnspecifiedInboundClassDependencyIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        AttributesImpl attsClass = new AttributesImpl();
        attsClass.addAttribute(null, null, "type", null, "class");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsClass);
        handler.characters(OTHER_CLASS_NAME.toCharArray(), 0, OTHER_CLASS_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testInferredInboundClassDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");
        AttributesImpl attsClassNo = new AttributesImpl();
        attsClassNo.addAttribute(null, null, "type", null, "class");
        attsClassNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsClassNo);
        handler.characters(OTHER_CLASS_NAME.toCharArray(), 0, OTHER_CLASS_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertFalse(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is confirmed");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testConfirmedInboundClassDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();
        attsYes.addAttribute(null, null, "confirmed", null, "yes");
        AttributesImpl attsClassYes = new AttributesImpl();
        attsClassYes.addAttribute(null, null, "type", null, "class");
        attsClassYes.addAttribute(null, null, "confirmed", null, "yes");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsClassYes);
        handler.characters(OTHER_CLASS_NAME.toCharArray(), 0, OTHER_CLASS_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testUnspecifiedOutboundClassDependencyIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        AttributesImpl attsClass = new AttributesImpl();
        attsClass.addAttribute(null, null, "type", null, "class");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsClass);
        handler.characters(OTHER_CLASS_NAME.toCharArray(), 0, OTHER_CLASS_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testInferredOutboundClassDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");
        AttributesImpl attsClassNo = new AttributesImpl();
        attsClassNo.addAttribute(null, null, "type", null, "class");
        attsClassNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsClassNo);
        handler.characters(OTHER_CLASS_NAME.toCharArray(), 0, OTHER_CLASS_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertFalse(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is confirmed");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

    @Test
    void testConfirmedOutboundClassDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();
        attsYes.addAttribute(null, null, "confirmed", null, "yes");
        AttributesImpl attsClassYes = new AttributesImpl();
        attsClassYes.addAttribute(null, null, "type", null, "class");
        attsClassYes.addAttribute(null, null, "confirmed", null, "yes");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsClassYes);
        handler.characters(OTHER_CLASS_NAME.toCharArray(), 0, OTHER_CLASS_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(0, handler.getFactory().getFeatures().size(), "nb features");
    }

// 123456

    
    @Test
    void testUnspecifiedInboundFeatureDependencyIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        AttributesImpl attsFeature = new AttributesImpl();
        attsFeature.addAttribute(null, null, "type", null, "feature");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsFeature);
        handler.characters(OTHER_FEATURE_NAME.toCharArray(), 0, OTHER_FEATURE_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME), "missing feature");
        assertTrue(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME).isConfirmed(), "feature is inferred");
    }

    @Test
    void testInferredInboundFeatureDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");
        AttributesImpl attsFeatureNo = new AttributesImpl();
        attsFeatureNo.addAttribute(null, null, "type", null, "feature");
        attsFeatureNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsFeatureNo);
        handler.characters(OTHER_FEATURE_NAME.toCharArray(), 0, OTHER_FEATURE_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertFalse(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is confirmed");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME), "missing feature");
        assertFalse(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME).isConfirmed(), "feature is confirmed");
    }

    @Test
    void testConfirmedInboundFeatureDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();
        attsYes.addAttribute(null, null, "confirmed", null, "yes");
        AttributesImpl attsFeatureYes = new AttributesImpl();
        attsFeatureYes.addAttribute(null, null, "type", null, "feature");
        attsFeatureYes.addAttribute(null, null, "confirmed", null, "yes");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "inbound", attsFeatureYes);
        handler.characters(OTHER_FEATURE_NAME.toCharArray(), 0, OTHER_FEATURE_NAME.length());
        handler.endElement(null, null, "inbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME), "missing feature");
        assertTrue(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME).isConfirmed(), "feature is inferred");
    }

    @Test
    void testUnspecifiedOutboundFeatureDependencyIsConfirmed() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        AttributesImpl attsFeature = new AttributesImpl();
        attsFeature.addAttribute(null, null, "type", null, "feature");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsEmpty);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsFeature);
        handler.characters(OTHER_FEATURE_NAME.toCharArray(), 0, OTHER_FEATURE_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME), "missing feature");
        assertTrue(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME).isConfirmed(), "feature is inferred");
    }

    @Test
    void testInferredOutboundFeatureDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsNo = new AttributesImpl();
        attsNo.addAttribute(null, null, "confirmed", null, "no");
        AttributesImpl attsFeatureNo = new AttributesImpl();
        attsFeatureNo.addAttribute(null, null, "type", null, "feature");
        attsFeatureNo.addAttribute(null, null, "confirmed", null, "no");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsNo);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsFeatureNo);
        handler.characters(OTHER_FEATURE_NAME.toCharArray(), 0, OTHER_FEATURE_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is confirmed");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertFalse(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is confirmed");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertFalse(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is confirmed");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME), "missing feature");
        assertFalse(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME).isConfirmed(), "feature is confirmed");
    }

    @Test
    void testConfirmedOutboundFeatureDependency() throws SAXException {
        AttributesImpl attsEmpty = new AttributesImpl();
        AttributesImpl attsYes = new AttributesImpl();
        attsYes.addAttribute(null, null, "confirmed", null, "yes");
        AttributesImpl attsFeatureYes = new AttributesImpl();
        attsFeatureYes.addAttribute(null, null, "type", null, "feature");
        attsFeatureYes.addAttribute(null, null, "confirmed", null, "yes");

        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.startElement(null, null, "package", attsYes);
        handler.startElement(null, null, "name", attsEmpty);
        handler.characters(MY_PACKAGE_NAME.toCharArray(), 0, MY_PACKAGE_NAME.length());
        handler.endElement(null, null, "name");
        handler.startElement(null, null, "outbound", attsFeatureYes);
        handler.characters(OTHER_FEATURE_NAME.toCharArray(), 0, OTHER_FEATURE_NAME.length());
        handler.endElement(null, null, "outbound");
        handler.endElement(null, null, "package");
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals(2, handler.getFactory().getPackages().size(), "nb packages");
        assertNotNull(handler.getFactory().getPackages().get(MY_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(MY_PACKAGE_NAME).isConfirmed(), "package is inferred");
        assertNotNull(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME), "missing package");
        assertTrue(handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME).isConfirmed(), "package is inferred");

        assertEquals(1, handler.getFactory().getClasses().size(), "nb classes");
        assertNotNull(handler.getFactory().getClasses().get(OTHER_CLASS_NAME), "missing class");
        assertTrue(handler.getFactory().getClasses().get(OTHER_CLASS_NAME).isConfirmed(), "class is inferred");

        assertEquals(1, handler.getFactory().getFeatures().size(), "nb features");
        assertNotNull(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME), "missing feature");
        assertTrue(handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME).isConfirmed(), "feature is inferred");
    }
}
