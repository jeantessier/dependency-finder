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

package com.jeantessier.diff;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.junit.jupiter.api.*;

import org.apache.oro.text.perl.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestReport {
    private static final String SPECIFIC_ENCODING   = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "../etc";

    private static final String OLD_CLASSPATH = Paths.get("jarjardiff/old/build/libs/old.jar").toString();
    private static final String NEW_CLASSPATH = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    private static final Path OLD_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/old-published/build/classes/java/main");
    private static final Path NEW_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/new-published/build/classes/java/main");

    private XMLReader reader;
    private final Perl5Util perl = new Perl5Util();

    @BeforeEach
    void setUp() throws Exception {
        boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);
    }

    @Test
    void testDefaultDTDPrefix() {
        Report report = new Report();

        String xmlDocument = report.render();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(Report.DEFAULT_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"" + Report.DEFAULT_DTD_PREFIX + "\"");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    @Test
    void testSpecificDTDPrefix() {
        Report report = new Report(Report.DEFAULT_INDENT_TEXT, Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = report.render();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(SPECIFIC_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    @Test
    void testDefaultEncoding() {
        Report report = new Report();

        String xmlDocument = report.render();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(Report.DEFAULT_ENCODING, perl.group(1), "Encoding");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    @Test
    void testSpecificEncoding() {
        Report report = new Report(Report.DEFAULT_INDENT_TEXT, SPECIFIC_ENCODING, Report.DEFAULT_DTD_PREFIX);

        String xmlDocument = report.render();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(SPECIFIC_ENCODING, perl.group(1), "Encoding");
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    @Test
    void testContent() throws Exception {
        PackageMapper oldPackages = new PackageMapper();
        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.addLoadListener(oldPackages);
        oldJar.load(Collections.singleton(OLD_CLASSPATH));

        PackageMapper newPackages = new PackageMapper();
        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.addLoadListener(newPackages);
        newJar.load(Collections.singleton(NEW_CLASSPATH));

        DifferencesFactory factory = new DifferencesFactory();
        ProjectDifferences projectDifferences = (ProjectDifferences) factory.createProjectDifferences("test", "old", oldPackages, "new", newPackages);

        Report report = new Report(Report.DEFAULT_INDENT_TEXT, Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        projectDifferences.accept(report);

        String xmlDocument = report.render();

        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in = new InputSource(new StringReader(xmlDocument));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

        assertNotNull(xPath.evaluate("//differences", doc), "//differences");
        assertNotNull(xPath.evaluate("*/old[text()='old']", doc), "*/old[text()='old']");
        assertEquals(3, ((NodeList) xPath.evaluate("*/modified-classes/class", doc, XPathConstants.NODESET)).getLength(), "*/modified-classes/class");
    }

    @Test
    void testIncompatibleContent() throws Exception {
        PackageMapper oldPackages = new PackageMapper();
        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.addLoadListener(oldPackages);
        oldJar.load(Collections.singleton(OLD_PUBLISHED_CLASSPATH.resolve("ModifiedPackage/CompatibleClass.class").toString()));

        PackageMapper newPackages = new PackageMapper();
        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.addLoadListener(newPackages);
        newJar.load(Collections.singleton(NEW_PUBLISHED_CLASSPATH.resolve("ModifiedPackage/CompatibleClass.class").toString()));

        DifferencesFactory factory = new DifferencesFactory(new IncompatibleDifferenceStrategy(new NoDifferenceStrategy()));
        ProjectDifferences projectDifferences = (ProjectDifferences) factory.createProjectDifferences("test", "old", oldPackages, "new", newPackages);

        Report report = new Report(Report.DEFAULT_INDENT_TEXT, Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        projectDifferences.accept(report);

        String xmlDocument = report.render();

        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        XPath xPath = XPathFactory.newInstance().newXPath();
        InputSource in  = new InputSource(new StringReader(xmlDocument));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

        assertNotNull(xPath.evaluate("//differences", doc), "//differences");
        assertNotNull(xPath.evaluate("*/old[text()='old']", doc), "*/old[text()='old']");
        assertEquals(1, ((NodeList) xPath.evaluate("*/modified-classes/class", doc, XPathConstants.NODESET)).getLength(), "*/modified-classes/class");
        assertEquals("", xPath.evaluate("*/modified-classes/class/modified-declaration", doc), "*/modified-classes/class/modified-declaration");
        assertEquals(1, ((NodeList) xPath.evaluate("*/modified-classes/class/modified-methods/feature", doc, XPathConstants.NODESET)).getLength(), "*/modified-classes/class/modified-methods/feature");
        assertNotNull(xPath.evaluate("*/modified-classes/class/modified-methods/feature/modified-declaration", doc), "*/modified-classes/class/modified-methods/feature/modified-declaration");
    }
}
