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

package com.jeantessier.diff;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import junit.framework.*;

import org.apache.oro.text.perl.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import com.jeantessier.classreader.*;

public class TestReport extends TestCase implements ErrorHandler {
    private static final String SPECIFIC_ENCODING   = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

    private static final String OLD_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "old";
    private static final String NEW_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

    private static final String OLD_PUBLISHED_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "oldpublished";
    private static final String NEW_PUBLISHED_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "newpublished";

    private XMLReader reader;
    private Perl5Util perl;

    protected void setUp() throws Exception {
	boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);
        reader.setErrorHandler(this);

        perl = new Perl5Util();
    }

    public void testDefaultDTDPrefix() {
        Report report = new Report();

        String xmlDocument = report.render();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + Report.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(Report.DEFAULT_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }
    
    public void testSpecificDTDPrefix() {
        Report report = new Report(Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = report.render();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testDefaultEncoding() {
        Report report = new Report();

        String xmlDocument = report.render();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", Report.DEFAULT_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testSpecificEncoding() {
        Report report = new Report(SPECIFIC_ENCODING, Report.DEFAULT_DTD_PREFIX);

        String xmlDocument = report.render();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testContent() throws Exception {
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

        Report report = new Report(Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
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

        assertNotNull("//differences", xPath.evaluate("//differences", doc));
        assertNotNull("*/old[text()='old']", xPath.evaluate("*/old[text()='old']", doc));
        assertEquals("*/modified-classes/class", 3, ((NodeList) xPath.evaluate("*/modified-classes/class", doc, XPathConstants.NODESET)).getLength());
    }

    public void testIncompatibleContent() throws Exception {
        PackageMapper oldPackages = new PackageMapper();
        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.addLoadListener(oldPackages);
        oldJar.load(Collections.singleton(OLD_PUBLISHED_CLASSPATH + File.separator + "ModifiedPackage" + File.separator + "CompatibleClass.class"));

        PackageMapper newPackages = new PackageMapper();
        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.addLoadListener(newPackages);
        newJar.load(Collections.singleton(NEW_PUBLISHED_CLASSPATH + File.separator + "ModifiedPackage" + File.separator + "CompatibleClass.class"));

        DifferencesFactory factory = new DifferencesFactory(new IncompatibleDifferenceStrategy(new NoDifferenceStrategy()));
        ProjectDifferences projectDifferences = (ProjectDifferences) factory.createProjectDifferences("test", "old", oldPackages, "new", newPackages);

        Report report = new Report(Report.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
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

        assertNotNull("//differences", xPath.evaluate("//differences", doc));
        assertNotNull("*/old[text()='old']", xPath.evaluate("*/old[text()='old']", doc));
        assertEquals("*/modified-classes/class", 1, ((NodeList) xPath.evaluate("*/modified-classes/class", doc, XPathConstants.NODESET)).getLength());
        assertEquals("*/modified-classes/class/modified-declaration", "", xPath.evaluate("*/modified-classes/class/modified-declaration", doc));
        assertEquals("*/modified-classes/class/modified-methods/feature", 1, ((NodeList) xPath.evaluate("*/modified-classes/class/modified-methods/feature", doc, XPathConstants.NODESET)).getLength());
        assertNotNull("*/modified-classes/class/modified-methods/feature/modified-declaration", xPath.evaluate("*/modified-classes/class/modified-methods/feature/modified-declaration", doc));
    }

    public void error(SAXParseException ex) {
        // Ignore
    }

    public void fatalError(SAXParseException ex) {
        // Ignore
    }

    public void warning(SAXParseException ex) {
        // Ignore
    }
}
