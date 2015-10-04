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

import junit.framework.*;
import org.apache.oro.text.perl.*;
import org.xml.sax.*;

public class TestXMLPrinter extends TestCase implements ErrorHandler {
    private static final String SPECIFIC_ENCODING   = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

    private XMLReader    reader;
    private Perl5Util    perl;
    private NodeFactory  factory;
    private StringWriter out;

    protected void setUp() throws Exception {
	boolean validate = Boolean.getBoolean("DEPENDENCYFINDER_TESTS_VALIDATE");

        reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        reader.setFeature("http://xml.org/sax/features/validation", validate);
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validate);
        reader.setErrorHandler(this);

        perl = new Perl5Util();

        factory = new NodeFactory();
        out     = new StringWriter();
    }

    public void testDefaultDTDPrefix() {
        XMLPrinter printer = new XMLPrinter(new PrintWriter(out));

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + XMLPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(XMLPrinter.DEFAULT_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }
    
    public void testSpecificDTDPrefix() {
        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testDefaultEncoding() {
        XMLPrinter printer = new XMLPrinter(new PrintWriter(out));

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", XMLPrinter.DEFAULT_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testSpecificEncoding() {
        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), SPECIFIC_ENCODING, XMLPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = out.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
            fail("Parsed non-existant document\n" + xmlDocument);
        } catch (SAXException ex) {
            // Ignore
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testShowInboundsPackageTrue() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(true);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <inbound type=\"package\" confirmed=\"no\">outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsPackageFalse() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsClassTrue() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(true);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <inbound type=\"class\" confirmed=\"no\">outbound.Outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsClassFalse() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsFeatureTrue() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(true);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>empty.Empty.empty()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"no\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowInboundsFeatureFalse() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>empty.Empty.empty()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsPackageTrue() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(true);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <outbound type=\"package\" confirmed=\"no\">inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsPackageFalse() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createPackage("empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsClassTrue() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(true);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <outbound type=\"class\" confirmed=\"no\">inbound.Inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsClassFalse() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createClass("empty.Empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsFeatureTrue() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(true);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>empty.Empty.empty()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"no\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowOutboundsFeatureFalse() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowInbounds(false);
        printer.setShowOutbounds(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>empty.Empty.empty()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }
    
    public void testShowEmptyPackageTrue() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createPackage("empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowEmptyNodes(true);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <inbound type=\"package\" confirmed=\"no\">outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <inbound type=\"class\" confirmed=\"no\">outbound.Outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"no\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <outbound type=\"package\" confirmed=\"no\">inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <outbound type=\"class\" confirmed=\"no\">inbound.Inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"no\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }
    
    public void testShowEmptyPackageTrueWithConfirmed() throws IOException {
        factory.createPackage("outbound", true).addDependency(factory.createPackage("inbound", true));
        factory.createClass("outbound.Outbound", true).addDependency(factory.createClass("inbound.Inbound", true));
        factory.createFeature("outbound.Outbound.outbound()", true).addDependency(factory.createFeature("inbound.Inbound.inbound()", true));
        factory.createPackage("empty", true);

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowEmptyNodes(true);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"yes\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"yes\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <inbound type=\"package\" confirmed=\"yes\">outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"yes\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <inbound type=\"class\" confirmed=\"yes\">outbound.Outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"yes\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"yes\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"yes\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <outbound type=\"package\" confirmed=\"yes\">inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"yes\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <outbound type=\"class\" confirmed=\"yes\">inbound.Inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"yes\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"yes\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyPackageFalse() throws IOException {
        factory.createPackage("outbound").addDependency(factory.createPackage("inbound"));
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createPackage("empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowEmptyNodes(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <inbound type=\"package\" confirmed=\"no\">outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <inbound type=\"class\" confirmed=\"no\">outbound.Outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"no\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <outbound type=\"package\" confirmed=\"no\">inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <outbound type=\"class\" confirmed=\"no\">inbound.Inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"no\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyClassTrue() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createClass("empty.Empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowEmptyNodes(true);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <inbound type=\"class\" confirmed=\"no\">outbound.Outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"no\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <outbound type=\"class\" confirmed=\"no\">inbound.Inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"no\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyClassFalse() throws IOException {
        factory.createClass("outbound.Outbound").addDependency(factory.createClass("inbound.Inbound"));
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createClass("empty.Empty");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowEmptyNodes(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <inbound type=\"class\" confirmed=\"no\">outbound.Outbound</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"no\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <outbound type=\"class\" confirmed=\"no\">inbound.Inbound</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"no\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyFeatureTrue() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowEmptyNodes(true);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>empty.Empty</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>empty.Empty.empty()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"no\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"no\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void testShowEmptyFeatureFalse() throws IOException {
        factory.createFeature("outbound.Outbound.outbound()").addDependency(factory.createFeature("inbound.Inbound.inbound()"));
        factory.createFeature("empty.Empty.empty()");

        XMLPrinter printer = new XMLPrinter(new PrintWriter(out), XMLPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);
        printer.setShowEmptyNodes(false);

        printer.traverseNodes(factory.getPackages().values());

        int            lineNumber = 0;
        BufferedReader in         = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<?xml version=\"1.0\" encoding=\"utf-8\" ?>", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<!DOCTYPE dependencies SYSTEM \"./etc/dependencies.dtd\">", in.readLine());
        assertEquals("line " + ++lineNumber, "", in.readLine());
        assertEquals("line " + ++lineNumber, "<dependencies>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>inbound.Inbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>inbound.Inbound.inbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <inbound type=\"feature\" confirmed=\"no\">outbound.Outbound.outbound()</inbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "    <package confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "        <name>outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "        <class confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "            <name>outbound.Outbound</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "            <feature confirmed=\"no\">", in.readLine());
        assertEquals("line " + ++lineNumber, "                <name>outbound.Outbound.outbound()</name>", in.readLine());
        assertEquals("line " + ++lineNumber, "                <outbound type=\"feature\" confirmed=\"no\">inbound.Inbound.inbound()</outbound>", in.readLine());
        assertEquals("line " + ++lineNumber, "            </feature>", in.readLine());
        assertEquals("line " + ++lineNumber, "        </class>", in.readLine());
        assertEquals("line " + ++lineNumber, "    </package>", in.readLine());
        assertEquals("line " + ++lineNumber, "</dependencies>", in.readLine());

        assertEquals("End of file", null, in.readLine());
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
