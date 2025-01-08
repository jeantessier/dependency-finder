/*
 *  Copyright (c) 2001-2024, Jean Tessier
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
import javax.xml.parsers.*;

import org.apache.oro.text.perl.*;
import org.junit.jupiter.api.*;
import org.xml.sax.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestListDiffPrinter {
    private static final String SPECIFIC_ENCODING   = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "../etc";

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
        ListDiffPrinter printer = new ListDiffPrinter(ListDiffPrinter.DEFAULT_COMPRESS, ListDiffPrinter.DEFAULT_INDENT_TEXT, ListDiffPrinter.DEFAULT_ENCODING, ListDiffPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = printer.toString();
        assertTrue(perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument), xmlDocument + "Missing DTD");
        assertTrue(perl.group(1).startsWith(ListDiffPrinter.DEFAULT_DTD_PREFIX), "DTD \"" + perl.group(1) + "\" does not have prefix \"" + ListDiffPrinter.DEFAULT_DTD_PREFIX + "\"");
        
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
        ListDiffPrinter printer = new ListDiffPrinter(ListDiffPrinter.DEFAULT_COMPRESS, ListDiffPrinter.DEFAULT_INDENT_TEXT, ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = printer.toString();
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
        ListDiffPrinter printer = new ListDiffPrinter(ListDiffPrinter.DEFAULT_COMPRESS, ListDiffPrinter.DEFAULT_INDENT_TEXT, ListDiffPrinter.DEFAULT_ENCODING, ListDiffPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = printer.toString();
        assertTrue(perl.match("/encoding=\"([^\"]*)\"/", xmlDocument), xmlDocument + "Missing encoding");
        assertEquals(ListDiffPrinter.DEFAULT_ENCODING, perl.group(1), "Encoding");
        
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
        ListDiffPrinter printer = new ListDiffPrinter(ListDiffPrinter.DEFAULT_COMPRESS, ListDiffPrinter.DEFAULT_INDENT_TEXT, SPECIFIC_ENCODING, ListDiffPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = printer.toString();
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
    void testDefault() {
        ListDiffPrinter printer = new ListDiffPrinter();

        printer.remove("java.lang.Object");
        printer.remove("java.lang.Object.Object()");
        printer.remove("java.lang.String");
        printer.remove("java.util");
        printer.remove("java.util.Collection.add(java.lang.Object)");
        printer.remove("java.util.Collection.addAll(java.util.Collection)");

        printer.add("java.lang.Thread");
        printer.add("java.lang.Thread.Thread()");
        printer.add("java.lang.System");
        printer.add("java.io");
        printer.add("java.io.PrintStream.println(java.lang.Object)");
        printer.add("java.io.PrintWriter.println(java.lang.Object)");

        String xmlDocument = printer.toString();
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        assertTrue(xmlDocument.contains("<line>java.lang.Object</line>"), "java.lang.Object not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.Object.Object()</line>"), "java.lang.Object.Object() not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.String</line>"), "java.lang.String not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util</line>"), "java.util not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.add(java.lang.Object) not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.addAll(java.util.Collection) not in " + xmlDocument);

        assertTrue(xmlDocument.contains("<line>java.lang.Thread</line>"), "java.lang.Thread not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.Thread.Thread()</line>"), "java.lang.Thread.Thread() not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.System</line>"), "java.lang.System not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io</line>"), "java.io not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintWriter.println(java.lang.Object) not in " + xmlDocument);
    }

    @Test
    void testFullList() {
        ListDiffPrinter printer = new ListDiffPrinter();

        printer.remove("java.lang.Object");
        printer.remove("java.lang.Object.Object()");
        printer.remove("java.lang.String");
        printer.remove("java.util");
        printer.remove("java.util.Collection.add(java.lang.Object)");
        printer.remove("java.util.Collection.addAll(java.util.Collection)");

        printer.add("java.lang.Thread");
        printer.add("java.lang.Thread.Thread()");
        printer.add("java.lang.System");
        printer.add("java.io");
        printer.add("java.io.PrintStream.println(java.lang.Object)");
        printer.add("java.io.PrintWriter.println(java.lang.Object)");

        String xmlDocument = printer.toString();
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        assertTrue(xmlDocument.contains("<line>java.lang.Object</line>"), "java.lang.Object not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.Object.Object()</line>"), "java.lang.Object.Object() not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.String</line>"), "java.lang.String not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util</line>"), "java.util not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.add(java.lang.Object) not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.addAll(java.util.Collection) not in " + xmlDocument);

        assertTrue(xmlDocument.contains("<line>java.lang.Thread</line>"), "java.lang.Thread not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.Thread.Thread()</line>"), "java.lang.Thread.Thread() not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.System</line>"), "java.lang.System not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io</line>"), "java.io not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintWriter.println(java.lang.Object) not in " + xmlDocument);
    }

    @Test
    void testCompressedList() {
        ListDiffPrinter printer = new ListDiffPrinter(true);

        printer.remove("java.lang.Object [C]");
        printer.remove("java.lang.Object.Object() [F]");
        printer.remove("java.lang.String [C]");
        printer.remove("java.util [P]");
        printer.remove("java.util.Collection.add(java.lang.Object) [F]");
        printer.remove("java.util.Collection.addAll(java.util.Collection) [F]");

        printer.add("java.lang.Thread [C]");
        printer.add("java.lang.Thread.Thread() [F]");
        printer.add("java.lang.System [C]");
        printer.add("java.io [P]");
        printer.add("java.io.PrintStream.println(java.lang.Object) [F]");
        printer.add("java.io.PrintWriter.println(java.lang.Object) [F]");

        String xmlDocument = printer.toString();
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        assertTrue(xmlDocument.contains("<line>java.lang.Object</line>"), "java.lang.Object not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>java.lang.Object.Object()</line>"), "java.lang.Object.Object() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.String</line>"), "java.lang.String not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util</line>"), "java.util not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.add(java.lang.Object) in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.addAll(java.util.Collection) in " + xmlDocument);

        assertTrue(xmlDocument.contains("<line>java.lang.Thread</line>"), "java.lang.Thread not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>java.lang.Thread.Thread()</line>"), "java.lang.Thread.Thread() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.System</line>"), "java.lang.System not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io</line>"), "java.io not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintWriter.println(java.lang.Object) in " + xmlDocument);
    }

    @Test
    void testCompressedListWithoutSuffixes() {
        ListDiffPrinter printer = new ListDiffPrinter(true);

        printer.remove("java.lang.Object");
        printer.remove("java.lang.Object.Object()");
        printer.remove("java.lang.String");
        printer.remove("java.util");
        printer.remove("java.util.Collection.add(java.lang.Object)");
        printer.remove("java.util.Collection.addAll(java.util.Collection)");

        printer.add("java.lang.Thread");
        printer.add("java.lang.Thread.Thread()");
        printer.add("java.lang.System");
        printer.add("java.io");
        printer.add("java.io.PrintStream.println(java.lang.Object)");
        printer.add("java.io.PrintWriter.println(java.lang.Object)");

        String xmlDocument = printer.toString();
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        assertTrue(xmlDocument.contains("<line>java.lang.Object</line>"), "java.lang.Object not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.Object.Object()</line>"), "java.lang.Object.Object() not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.String</line>"), "java.lang.String not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util</line>"), "java.util not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.add(java.lang.Object) not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.util.Collection.add(java.lang.Object)</line>"), "java.util.Collection.addAll(java.util.Collection) not in " + xmlDocument);

        assertTrue(xmlDocument.contains("<line>java.lang.Thread</line>"), "java.lang.Thread not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.Thread.Thread()</line>"), "java.lang.Thread.Thread() not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.lang.System</line>"), "java.lang.System not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io</line>"), "java.io not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>java.io.PrintStream.println(java.lang.Object)</line>"), "java.io.PrintWriter.println(java.lang.Object) not in " + xmlDocument);
    }

    @Test
    void testLegitimateSuffixes() {
        ListDiffPrinter printer = new ListDiffPrinter(true);

        printer.remove("removed [P]");                                    // not compressed
        printer.remove("removed.Removed [C]");                            //     compressed
        printer.remove("removed.Removed.Removed() [F]");                  //     compressed
        printer.remove("removed.Removed.removed [F]");                    //     compressed
        printer.remove("removed.OtherRemoved.OtherRemoved() [F]");        //     compressed
        printer.remove("removed.OtherRemoved.other_removed [F]");         //     compressed
        printer.remove("removedpackage [P]");                             // not compressed
        printer.remove("removed.package.internal [P]");                   // not compressed
        printer.remove("other.removed.Removed [C]");                      // not compressed
        printer.remove("other.removed.Removed.Removed() [F]");            //     compressed
        printer.remove("other.removed.OtherRemoved.OtherRemoved() [F]");  // not compressed

        printer.add("add [P]");                                           // not compressed
        printer.add("add.Add [C]");                                       //     compressed
        printer.add("add.Add.Add() [F]");                                 //     compressed
        printer.add("add.Add.add [F]");                                   //     compressed
        printer.add("add.OtherAdd.OtherAdd() [F]");                       //     compressed
        printer.add("add.OtherAdd.add [F]");                              //     compressed
        printer.add("addpackage [P]");                                    // not compressed
        printer.add("add.package.internal [P]");                          // not compressed
        printer.add("other.add.Add [C]");                                 // not compressed
        printer.add("other.add.Add.Add() [F]");                           //     compressed
        printer.add("other.add.OtherAdd.OtherAdd() [F]");                 // not compressed

        String xmlDocument = printer.toString();
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        assertTrue(xmlDocument.contains("<line>removed</line>"), "removed not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>removed.Removed</line>"), "removed.Removed in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>removed.Removed.Removed()</line>"), "removed.Removed.Removed() in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>removed.Removed.removed</line>"), "removed.Removed.removed in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>removed.OtherRemoved.OtherRemoved()</line>"), "removed.OtherRemoved.OtherRemoved() in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>removed.OtherRemoved.other_removed</line>"), "removed.OtherRemoved.other_removed in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removedpackage</line>"), "removedpackage not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removed.package.internal</line>"), "removed.package.internal not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.removed.Removed</line>"), "other.removed.Removed not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>other.removed.Removed.Removed()</line>"), "other.removed.Removed.Removed() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.removed.OtherRemoved.OtherRemoved()</line>"), "other.removed.OtherRemoved.OtherRemoved() not in " + xmlDocument);

        assertTrue(xmlDocument.contains("<line>add</line>"), "add not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>add.Add</line>"), "add.Add in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>add.Add.Add()</line>"), "add.Add.Add() in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>add.Add.add</line>"), "add.Add.add in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>add.OtherAdd.OtherAdd()</line>"), "add.OtherAdd.OtherAdd() in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>add.OtherAdd.other_add</line>"), "add.OtherAdd.other_add in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>addpackage</line>"), "addpackage not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>add.package.internal</line>"), "add.package.internal not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.add.Add</line>"), "other.add.Add not in " + xmlDocument);
        assertFalse(xmlDocument.contains("<line>other.add.Add.Add()</line>"), "other.add.Add.Add() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.add.OtherAdd.OtherAdd()</line>"), "other.add.OtherAdd.OtherAdd() not in " + xmlDocument);
    }

    @Test
    void testNoSuffixes() {
        ListDiffPrinter printer = new ListDiffPrinter(true);

        printer.remove("removed");                                    // not compressed
        printer.remove("removed.Removed");                            // not compressed
        printer.remove("removed.Removed.Removed()");                  // not compressed
        printer.remove("removed.Removed.removed");                    // not compressed
        printer.remove("removed.OtherRemoved.OtherRemoved()");        // not compressed
        printer.remove("removed.OtherRemoved.other_removed");         // not compressed
        printer.remove("removedpackage");                             // not compressed
        printer.remove("removed.package.internal");                   // not compressed
        printer.remove("other.removed.Removed");                      // not compressed
        printer.remove("other.removed.Removed.Removed()");            // not compressed
        printer.remove("other.removed.OtherRemoved.OtherRemoved()");  // not compressed

        printer.add("add");                                           // not compressed
        printer.add("add.Add");                                       // not compressed
        printer.add("add.Add.Add()");                                 // not compressed
        printer.add("add.Add.add");                                   // not compressed
        printer.add("add.OtherAdd.OtherAdd()");                       // not compressed
        printer.add("add.OtherAdd.other_add");                        // not compressed
        printer.add("addpackage");                                    // not compressed
        printer.add("add.package.internal");                          // not compressed
        printer.add("other.add.Add");                                 // not compressed
        printer.add("other.add.Add.Add()");                           // not compressed
        printer.add("other.add.OtherAdd.OtherAdd()");                 // not compressed

        String xmlDocument = printer.toString();
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }

        assertTrue(xmlDocument.contains("<line>removed</line>"), "removed not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removed.Removed</line>"), "removed.Removed in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removed.Removed.Removed()</line>"), "removed.Removed.Removed() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removed.Removed.removed</line>"), "removed.Removed.removed in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removed.OtherRemoved.OtherRemoved()</line>"), "removed.OtherRemoved.OtherRemoved() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removed.OtherRemoved.other_removed</line>"), "removed.OtherRemoved.other_removed in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removedpackage</line>"), "removedpackage not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>removed.package.internal</line>"), "removed.package.internal not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.removed.Removed</line>"), "other.removed.Removed not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.removed.Removed.Removed()</line>"), "other.removed.Removed.Removed() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.removed.OtherRemoved.OtherRemoved()</line>"), "other.removed.OtherRemoved.OtherRemoved() not in " + xmlDocument);

        assertTrue(xmlDocument.contains("<line>add</line>"), "add not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>add.Add</line>"), "add.Add in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>add.Add.Add()</line>"), "add.Add.Add() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>add.Add.add</line>"), "add.Add.add in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>add.OtherAdd.OtherAdd()</line>"), "add.OtherAdd.OtherAdd() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>add.OtherAdd.other_add</line>"), "add.OtherAdd.other_add in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>addpackage</line>"), "addpackage not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>add.package.internal</line>"), "add.package.internal not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.add.Add</line>"), "other.add.Add not in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.add.Add.Add()</line>"), "other.add.Add.Add() in " + xmlDocument);
        assertTrue(xmlDocument.contains("<line>other.add.OtherAdd.OtherAdd()</line>"), "other.add.OtherAdd.OtherAdd() not in " + xmlDocument);
    }
}
