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
import javax.xml.parsers.*;

import junit.framework.*;
import org.apache.oro.text.perl.*;
import org.xml.sax.*;

public class TestListDiffPrinter extends TestCase implements ErrorHandler {
    private static final String SPECIFIC_ENCODING   = "iso-latin-1";
    private static final String SPECIFIC_DTD_PREFIX = "./etc";

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
        ListDiffPrinter printer = new ListDiffPrinter();

        String xmlDocument = printer.toString();
        assertTrue(xmlDocument + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xmlDocument));
        assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + ListDiffPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(ListDiffPrinter.DEFAULT_DTD_PREFIX));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }
    
    public void testSpecificDTDPrefix() {
        ListDiffPrinter printer = new ListDiffPrinter(ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

        String xmlDocument = printer.toString();
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
        ListDiffPrinter printer = new ListDiffPrinter();

        String xmlDocument = printer.toString();
        assertTrue(xmlDocument + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xmlDocument));
        assertEquals("Encoding", ListDiffPrinter.DEFAULT_ENCODING, perl.group(1));
        
        try {
            reader.parse(new InputSource(new StringReader(xmlDocument)));
        } catch (SAXException ex) {
            fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        } catch (IOException ex) {
            fail("Could not read XML Document: " + ex.getMessage() + "\n" + xmlDocument);
        }
    }

    public void testSpecificEncoding() {
        ListDiffPrinter printer = new ListDiffPrinter(SPECIFIC_ENCODING, ListDiffPrinter.DEFAULT_DTD_PREFIX);

        String xmlDocument = printer.toString();
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

    public void testDefault() {
        ListDiffPrinter printer = new ListDiffPrinter(ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

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

        assertTrue("java.lang.Object not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object</line>") != -1);
        assertTrue("java.lang.Object.Object() not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object.Object()</line>") != -1);
        assertTrue("java.lang.String not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.String</line>") != -1);
        assertTrue("java.util not in " + xmlDocument, xmlDocument.indexOf("<line>java.util</line>") != -1);
        assertTrue("java.util.Collection.add(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
        assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

        assertTrue("java.lang.Thread not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread</line>") != -1);
        assertTrue("java.lang.Thread.Thread() not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
        assertTrue("java.lang.System not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.System</line>") != -1);
        assertTrue("java.io not in " + xmlDocument, xmlDocument.indexOf("<line>java.io</line>") != -1);
        assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
        assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
    }

    public void testFullList() {
        ListDiffPrinter printer = new ListDiffPrinter(false, ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

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

        assertTrue("java.lang.Object not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object</line>") != -1);
        assertTrue("java.lang.Object.Object() not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object.Object()</line>") != -1);
        assertTrue("java.lang.String not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.String</line>") != -1);
        assertTrue("java.util not in " + xmlDocument, xmlDocument.indexOf("<line>java.util</line>") != -1);
        assertTrue("java.util.Collection.add(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
        assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

        assertTrue("java.lang.Thread not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread</line>") != -1);
        assertTrue("java.lang.Thread.Thread() not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
        assertTrue("java.lang.System not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.System</line>") != -1);
        assertTrue("java.io not in " + xmlDocument, xmlDocument.indexOf("<line>java.io</line>") != -1);
        assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
        assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
    }

    public void testCompressedList() {
        ListDiffPrinter printer = new ListDiffPrinter(true, ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

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

        assertTrue("java.lang.Object not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object</line>") != -1);
        assertTrue("java.lang.Object.Object() in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object.Object()</line>") == -1);
        assertTrue("java.lang.String not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.String</line>") != -1);
        assertTrue("java.util not in " + xmlDocument, xmlDocument.indexOf("<line>java.util</line>") != -1);
        assertTrue("java.util.Collection.add(java.lang.Object) in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") == -1);
        assertTrue("java.util.Collection.addAll(java.util.Collection) in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") == -1);

        assertTrue("java.lang.Thread not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread</line>") != -1);
        assertTrue("java.lang.Thread.Thread() in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread.Thread()</line>") == -1);
        assertTrue("java.lang.System not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.System</line>") != -1);
        assertTrue("java.io not in " + xmlDocument, xmlDocument.indexOf("<line>java.io</line>") != -1);
        assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") == -1);
        assertTrue("java.io.PrintWriter.println(java.lang.Object) in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") == -1);
    }

    public void testCompressedListWithoutSuffixes() {
        ListDiffPrinter printer = new ListDiffPrinter(true, ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

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

        assertTrue("java.lang.Object not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object</line>") != -1);
        assertTrue("java.lang.Object.Object() not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Object.Object()</line>") != -1);
        assertTrue("java.lang.String not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.String</line>") != -1);
        assertTrue("java.util not in " + xmlDocument, xmlDocument.indexOf("<line>java.util</line>") != -1);
        assertTrue("java.util.Collection.add(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
        assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + xmlDocument, xmlDocument.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

        assertTrue("java.lang.Thread not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread</line>") != -1);
        assertTrue("java.lang.Thread.Thread() not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
        assertTrue("java.lang.System not in " + xmlDocument, xmlDocument.indexOf("<line>java.lang.System</line>") != -1);
        assertTrue("java.io not in " + xmlDocument, xmlDocument.indexOf("<line>java.io</line>") != -1);
        assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
        assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + xmlDocument, xmlDocument.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
    }

    public void testLegitimateSuffixes() {
        ListDiffPrinter printer = new ListDiffPrinter(true, ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

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

        assertTrue("removed not in " + xmlDocument, xmlDocument.indexOf("<line>removed</line>") != -1);
        assertTrue("removed.Removed in " + xmlDocument, xmlDocument.indexOf("<line>removed.Removed</line>") == -1);
        assertTrue("removed.Removed.Removed() in " + xmlDocument, xmlDocument.indexOf("<line>removed.Removed.Removed()</line>") == -1);
        assertTrue("removed.Removed.removed in " + xmlDocument, xmlDocument.indexOf("<line>removed.Removed.removed</line>") == -1);
        assertTrue("removed.OtherRemoved.OtherRemoved() in " + xmlDocument, xmlDocument.indexOf("<line>removed.OtherRemoved.OtherRemoved()</line>") == -1);
        assertTrue("removed.OtherRemoved.other_removed in " + xmlDocument, xmlDocument.indexOf("<line>removed.OtherRemoved.other_removed</line>") == -1);
        assertTrue("removedpackage not in " + xmlDocument, xmlDocument.indexOf("<line>removedpackage</line>") != -1);
        assertTrue("removed.package.internal not in " + xmlDocument, xmlDocument.indexOf("<line>removed.package.internal</line>") != -1);
        assertTrue("other.removed.Removed not in " + xmlDocument, xmlDocument.indexOf("<line>other.removed.Removed</line>") != -1);
        assertTrue("other.removed.Removed.Removed() in " + xmlDocument, xmlDocument.indexOf("<line>other.removed.Removed.Removed()</line>") == -1);
        assertTrue("other.removed.OtherRemoved.OtherRemoved() not in " + xmlDocument, xmlDocument.indexOf("<line>other.removed.OtherRemoved.OtherRemoved()</line>") != -1);

        assertTrue("add not in " + xmlDocument, xmlDocument.indexOf("<line>add</line>") != -1);
        assertTrue("add.Add in " + xmlDocument, xmlDocument.indexOf("<line>add.Add</line>") == -1);
        assertTrue("add.Add.Add() in " + xmlDocument, xmlDocument.indexOf("<line>add.Add.Add()</line>") == -1);
        assertTrue("add.Add.add in " + xmlDocument, xmlDocument.indexOf("<line>add.Add.add</line>") == -1);
        assertTrue("add.OtherAdd.OtherAdd() in " + xmlDocument, xmlDocument.indexOf("<line>add.OtherAdd.OtherAdd()</line>") == -1);
        assertTrue("add.OtherAdd.other_add in " + xmlDocument, xmlDocument.indexOf("<line>add.OtherAdd.other_add</line>") == -1);
        assertTrue("addpackage not in " + xmlDocument, xmlDocument.indexOf("<line>addpackage</line>") != -1);
        assertTrue("add.package.internal not in " + xmlDocument, xmlDocument.indexOf("<line>add.package.internal</line>") != -1);
        assertTrue("other.add.Add not in " + xmlDocument, xmlDocument.indexOf("<line>other.add.Add</line>") != -1);
        assertTrue("other.add.Add.Add() in " + xmlDocument, xmlDocument.indexOf("<line>other.add.Add.Add()</line>") == -1);
        assertTrue("other.add.OtherAdd.OtherAdd() not in " + xmlDocument, xmlDocument.indexOf("<line>other.add.OtherAdd.OtherAdd()</line>") != -1);
    }

    public void testNoSuffixes() {
        ListDiffPrinter printer = new ListDiffPrinter(true, ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

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

        assertTrue("removed not in " + xmlDocument, xmlDocument.indexOf("<line>removed</line>") != -1);
        assertTrue("removed.Removed in " + xmlDocument, xmlDocument.indexOf("<line>removed.Removed</line>") != -1);
        assertTrue("removed.Removed.Removed() in " + xmlDocument, xmlDocument.indexOf("<line>removed.Removed.Removed()</line>") != -1);
        assertTrue("removed.Removed.removed in " + xmlDocument, xmlDocument.indexOf("<line>removed.Removed.removed</line>") != -1);
        assertTrue("removed.OtherRemoved.OtherRemoved() in " + xmlDocument, xmlDocument.indexOf("<line>removed.OtherRemoved.OtherRemoved()</line>") != -1);
        assertTrue("removed.OtherRemoved.other_removed in " + xmlDocument, xmlDocument.indexOf("<line>removed.OtherRemoved.other_removed</line>") != -1);
        assertTrue("removedpackage not in " + xmlDocument, xmlDocument.indexOf("<line>removedpackage</line>") != -1);
        assertTrue("removed.package.internal not in " + xmlDocument, xmlDocument.indexOf("<line>removed.package.internal</line>") != -1);
        assertTrue("other.removed.Removed not in " + xmlDocument, xmlDocument.indexOf("<line>other.removed.Removed</line>") != -1);
        assertTrue("other.removed.Removed.Removed() in " + xmlDocument, xmlDocument.indexOf("<line>other.removed.Removed.Removed()</line>") != -1);
        assertTrue("other.removed.OtherRemoved.OtherRemoved() not in " + xmlDocument, xmlDocument.indexOf("<line>other.removed.OtherRemoved.OtherRemoved()</line>") != -1);

        assertTrue("add not in " + xmlDocument, xmlDocument.indexOf("<line>add</line>") != -1);
        assertTrue("add.Add in " + xmlDocument, xmlDocument.indexOf("<line>add.Add</line>") != -1);
        assertTrue("add.Add.Add() in " + xmlDocument, xmlDocument.indexOf("<line>add.Add.Add()</line>") != -1);
        assertTrue("add.Add.add in " + xmlDocument, xmlDocument.indexOf("<line>add.Add.add</line>") != -1);
        assertTrue("add.OtherAdd.OtherAdd() in " + xmlDocument, xmlDocument.indexOf("<line>add.OtherAdd.OtherAdd()</line>") != -1);
        assertTrue("add.OtherAdd.other_add in " + xmlDocument, xmlDocument.indexOf("<line>add.OtherAdd.other_add</line>") != -1);
        assertTrue("addpackage not in " + xmlDocument, xmlDocument.indexOf("<line>addpackage</line>") != -1);
        assertTrue("add.package.internal not in " + xmlDocument, xmlDocument.indexOf("<line>add.package.internal</line>") != -1);
        assertTrue("other.add.Add not in " + xmlDocument, xmlDocument.indexOf("<line>other.add.Add</line>") != -1);
        assertTrue("other.add.Add.Add() in " + xmlDocument, xmlDocument.indexOf("<line>other.add.Add.Add()</line>") != -1);
        assertTrue("other.add.OtherAdd.OtherAdd() not in " + xmlDocument, xmlDocument.indexOf("<line>other.add.OtherAdd.OtherAdd()</line>") != -1);
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
