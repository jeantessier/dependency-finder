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

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import junit.framework.*;

public class TestNodeHandler extends TestCase {
    private static final String MY_PACKAGE_NAME    = "mypackage";
    private static final String MY_CLASS_NAME      = "mypackage.MyClass";
    private static final String MY_FEATURE_NAME    = "mypackage.MyClass.myFeature";
    private static final String OTHER_PACKAGE_NAME = "otherpackage";
    private static final String OTHER_CLASS_NAME   = "otherpackage.OtherClass";
    private static final String OTHER_FEATURE_NAME = "otherpackage.OtherClass.otherFeature";
    
    private NodeHandler handler;

    protected void setUp() throws Exception {
        super.setUp();

        handler = new NodeHandler();
    }

    public void testConstructor() {
        assertEquals("nb packages", 0, handler.getFactory().getPackages().size());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testEmptyDocument() throws SAXException {
        handler.startDocument();
        handler.endDocument();

        assertEquals("nb packages", 0, handler.getFactory().getPackages().size());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testEmptyTopNode() throws SAXException {
        Attributes attsEmpty = new AttributesImpl();
        
        handler.startDocument();
        handler.startElement(null, null, "dependencies", attsEmpty);
        handler.endElement(null, null, "dependencies");
        handler.endDocument();

        assertEquals("nb packages", 0, handler.getFactory().getPackages().size());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testUnspecifiedPackageNodeIsConfirmed() throws SAXException {
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

        assertEquals("nb packages",  1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testInferredPackageNode() throws SAXException {
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

        assertEquals("nb packages",  1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testConfirmedPackageNode() throws SAXException {
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

        assertEquals("nb packages",  1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testUnspecifiedClassNodeIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(MY_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(MY_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testInferredClassNode() throws SAXException {
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

        assertEquals("nb packages", 1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(MY_CLASS_NAME));
        assertFalse("class is confirmed", ((Node) handler.getFactory().getClasses().get(MY_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testConfirmedClassNode() throws SAXException {
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

        assertEquals("nb packages", 1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(MY_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(MY_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testUnspecifiedFeatureNodeIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(MY_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(MY_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(MY_FEATURE_NAME));
        assertTrue("feature is inferred", ((Node) handler.getFactory().getFeatures().get(MY_FEATURE_NAME)).isConfirmed());
    }

    public void testInferredFeatureNode() throws SAXException {
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

        assertEquals("nb packages", 1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(MY_CLASS_NAME));
        assertFalse("class is confirmed", ((Node) handler.getFactory().getClasses().get(MY_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(MY_FEATURE_NAME));
        assertFalse("feature is confirmed", ((Node) handler.getFactory().getFeatures().get(MY_FEATURE_NAME)).isConfirmed());
    }

    public void testConfirmedFeatureNode() throws SAXException {
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

        assertEquals("nb packages", 1, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(MY_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(MY_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(MY_FEATURE_NAME));
        assertTrue("feature is inferred", ((Node) handler.getFactory().getFeatures().get(MY_FEATURE_NAME)).isConfirmed());
    }

    public void testUnspecifiedInboundPackageDependencyIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testInferredInboundPackageDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testConfirmedInboundPackageDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testUnspecifiedOutboundPackageDependencyIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testInferredOutboundPackageDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testConfirmedOutboundPackageDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 0, handler.getFactory().getClasses().size());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testUnspecifiedInboundClassDependencyIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testInferredInboundClassDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertFalse("class is confirmed", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testConfirmedInboundClassDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testUnspecifiedOutboundClassDependencyIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testInferredOutboundClassDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertFalse("class is confirmed", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

    public void testConfirmedOutboundClassDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 0, handler.getFactory().getFeatures().size());
    }

// 123456

    
    public void testUnspecifiedInboundFeatureDependencyIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME));
        assertTrue("feature is inferred", ((Node) handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME)).isConfirmed());
    }

    public void testInferredInboundFeatureDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertFalse("class is confirmed", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME));
        assertFalse("feature is confirmed", ((Node) handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME)).isConfirmed());
    }

    public void testConfirmedInboundFeatureDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME));
        assertTrue("feature is inferred", ((Node) handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME)).isConfirmed());
    }

    public void testUnspecifiedOutboundFeatureDependencyIsConfirmed() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME));
        assertTrue("feature is inferred", ((Node) handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME)).isConfirmed());
    }

    public void testInferredOutboundFeatureDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertFalse("package is confirmed", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertFalse("class is confirmed", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME));
        assertFalse("feature is confirmed", ((Node) handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME)).isConfirmed());
    }

    public void testConfirmedOutboundFeatureDependency() throws SAXException {
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

        assertEquals("nb packages", 2, handler.getFactory().getPackages().size());
        assertNotNull("missing package", handler.getFactory().getPackages().get(MY_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(MY_PACKAGE_NAME)).isConfirmed());
        assertNotNull("missing package", handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME));
        assertTrue("package is inferred", ((Node) handler.getFactory().getPackages().get(OTHER_PACKAGE_NAME)).isConfirmed());

        assertEquals("nb classes", 1, handler.getFactory().getClasses().size());
        assertNotNull("missing class", handler.getFactory().getClasses().get(OTHER_CLASS_NAME));
        assertTrue("class is inferred", ((Node) handler.getFactory().getClasses().get(OTHER_CLASS_NAME)).isConfirmed());

        assertEquals("nb features", 1, handler.getFactory().getFeatures().size());
        assertNotNull("missing feature", handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME));
        assertTrue("feature is inferred", ((Node) handler.getFactory().getFeatures().get(OTHER_FEATURE_NAME)).isConfirmed());
    }
}
