/*
 *  Copyright (c) 2001-2003, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import org.apache.oro.text.perl.*;

import junit.framework.*;

public class TestListDiffPrinter extends TestCase implements ErrorHandler {
	private static final String READER_CLASSNAME = "org.apache.xerces.parsers.SAXParser";

	private static final String SPECIFIC_ENCODING   = "iso-latin-1";
	private static final String SPECIFIC_DTD_PREFIX = "./etc";

	private ListDiffPrinter printer;
	private XMLReader       reader;

	private Perl5Util perl;

	protected void setUp() throws Exception {
		reader = XMLReaderFactory.createXMLReader(READER_CLASSNAME);
		reader.setFeature("http://xml.org/sax/features/validation", true);
		reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);
		reader.setErrorHandler(this);

		perl = new Perl5Util();
	}

	public void testDefaultDTDPrefix() {
		printer = new ListDiffPrinter();

		String xml_document = printer.toString();
		assertTrue(xml_document + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xml_document));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"" + ListDiffPrinter.DEFAULT_DTD_PREFIX + "\"", perl.group(1).startsWith(ListDiffPrinter.DEFAULT_DTD_PREFIX));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}
	
	public void testSpecificDTDPrefix() {
		printer = new ListDiffPrinter(ListDiffPrinter.DEFAULT_ENCODING, SPECIFIC_DTD_PREFIX);

		String xml_document = printer.toString();
		assertTrue(xml_document + "Missing DTD", perl.match("/DOCTYPE \\S+ SYSTEM \"(.*)\"/", xml_document));
		assertTrue("DTD \"" + perl.group(1) + "\" does not have prefix \"./etc\"", perl.group(1).startsWith(SPECIFIC_DTD_PREFIX));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}

	public void testDefaultEncoding() {
		printer = new ListDiffPrinter();

		String xml_document = printer.toString();
		assertTrue(xml_document + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xml_document));
		assertEquals("Encoding", ListDiffPrinter.DEFAULT_ENCODING, perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}

	public void testSpecificEncoding() {
		printer = new ListDiffPrinter(SPECIFIC_ENCODING, ListDiffPrinter.DEFAULT_DTD_PREFIX);

		String xml_document = printer.toString();
		assertTrue(xml_document + "Missing encoding", perl.match("/encoding=\"([^\"]*)\"/", xml_document));
		assertEquals("Encoding", SPECIFIC_ENCODING, perl.group(1));
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}
	}

	public void testDefault() {
		ListDiffPrinter printer = new ListDiffPrinter();

		printer.Remove("java.lang.Object");
		printer.Remove("java.lang.Object.Object()");
		printer.Remove("java.lang.String");
		printer.Remove("java.util");
		printer.Remove("java.util.Collection.add(java.lang.Object)");
		printer.Remove("java.util.Collection.addAll(java.util.Collection)");

		printer.Add("java.lang.Thread");
		printer.Add("java.lang.Thread.Thread()");
		printer.Add("java.lang.System");
		printer.Add("java.io");
		printer.Add("java.io.PrintStream.println(java.lang.Object)");
		printer.Add("java.io.PrintWriter.println(java.lang.Object)");

		String xml_document = printer.toString();
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}

		assertTrue("java.lang.Object not in " + xml_document, xml_document.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() not in " + xml_document, xml_document.indexOf("<line>java.lang.Object.Object()</line>") != -1);
		assertTrue("java.lang.String not in " + xml_document, xml_document.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + xml_document, xml_document.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

		assertTrue("java.lang.Thread not in " + xml_document, xml_document.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + xml_document, xml_document.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
		assertTrue("java.lang.System not in " + xml_document, xml_document.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + xml_document, xml_document.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
	}

	public void testFullList() {
		ListDiffPrinter printer = new ListDiffPrinter(false);

		printer.Remove("java.lang.Object");
		printer.Remove("java.lang.Object.Object()");
		printer.Remove("java.lang.String");
		printer.Remove("java.util");
		printer.Remove("java.util.Collection.add(java.lang.Object)");
		printer.Remove("java.util.Collection.addAll(java.util.Collection)");

		printer.Add("java.lang.Thread");
		printer.Add("java.lang.Thread.Thread()");
		printer.Add("java.lang.System");
		printer.Add("java.io");
		printer.Add("java.io.PrintStream.println(java.lang.Object)");
		printer.Add("java.io.PrintWriter.println(java.lang.Object)");

		String xml_document = printer.toString();
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}

		assertTrue("java.lang.Object not in " + xml_document, xml_document.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() not in " + xml_document, xml_document.indexOf("<line>java.lang.Object.Object()</line>") != -1);
		assertTrue("java.lang.String not in " + xml_document, xml_document.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + xml_document, xml_document.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

		assertTrue("java.lang.Thread not in " + xml_document, xml_document.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + xml_document, xml_document.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
		assertTrue("java.lang.System not in " + xml_document, xml_document.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + xml_document, xml_document.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
	}

	public void testCompressedList() {
		ListDiffPrinter printer = new ListDiffPrinter(true);

		printer.Remove("java.lang.Object [C]");
		printer.Remove("java.lang.Object.Object() [F]");
		printer.Remove("java.lang.String [C]");
		printer.Remove("java.util [P]");
		printer.Remove("java.util.Collection.add(java.lang.Object) [F]");
		printer.Remove("java.util.Collection.addAll(java.util.Collection) [F]");

		printer.Add("java.lang.Thread [C]");
		printer.Add("java.lang.Thread.Thread() [F]");
		printer.Add("java.lang.System [C]");
		printer.Add("java.io [P]");
		printer.Add("java.io.PrintStream.println(java.lang.Object) [F]");
		printer.Add("java.io.PrintWriter.println(java.lang.Object) [F]");

		String xml_document = printer.toString();
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}

		assertTrue("java.lang.Object not in " + xml_document, xml_document.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() in " + xml_document, xml_document.indexOf("<line>java.lang.Object.Object()</line>") == -1);
		assertTrue("java.lang.String not in " + xml_document, xml_document.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + xml_document, xml_document.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") == -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") == -1);

		assertTrue("java.lang.Thread not in " + xml_document, xml_document.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() in " + xml_document, xml_document.indexOf("<line>java.lang.Thread.Thread()</line>") == -1);
		assertTrue("java.lang.System not in " + xml_document, xml_document.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + xml_document, xml_document.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") == -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") == -1);
	}

	public void testCompressedListWithoutSuffixes() {
		ListDiffPrinter printer = new ListDiffPrinter(true);

		printer.Remove("java.lang.Object");
		printer.Remove("java.lang.Object.Object()");
		printer.Remove("java.lang.String");
		printer.Remove("java.util");
		printer.Remove("java.util.Collection.add(java.lang.Object)");
		printer.Remove("java.util.Collection.addAll(java.util.Collection)");

		printer.Add("java.lang.Thread");
		printer.Add("java.lang.Thread.Thread()");
		printer.Add("java.lang.System");
		printer.Add("java.io");
		printer.Add("java.io.PrintStream.println(java.lang.Object)");
		printer.Add("java.io.PrintWriter.println(java.lang.Object)");

		String xml_document = printer.toString();
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}

		assertTrue("java.lang.Object not in " + xml_document, xml_document.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() not in " + xml_document, xml_document.indexOf("<line>java.lang.Object.Object()</line>") != -1);
		assertTrue("java.lang.String not in " + xml_document, xml_document.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + xml_document, xml_document.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + xml_document, xml_document.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

		assertTrue("java.lang.Thread not in " + xml_document, xml_document.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + xml_document, xml_document.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
		assertTrue("java.lang.System not in " + xml_document, xml_document.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + xml_document, xml_document.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + xml_document, xml_document.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
	}

	public void testLegitimateSuffixes() {
		ListDiffPrinter printer = new ListDiffPrinter(true);

		printer.Remove("removed [P]");                                    // not compressed
		printer.Remove("removed.Removed [C]");                            //     compressed
		printer.Remove("removed.Removed.Removed() [F]");                  //     compressed
		printer.Remove("removed.Removed.removed [F]");                    //     compressed
		printer.Remove("removed.OtherRemoved.OtherRemoved() [F]");        //     compressed
		printer.Remove("removed.OtherRemoved.other_removed [F]");         //     compressed
		printer.Remove("removedpackage [P]");                             // not compressed
		printer.Remove("removed.package.internal [P]");                   // not compressed
		printer.Remove("other.removed.Removed [C]");                      // not compressed
		printer.Remove("other.removed.Removed.Removed() [F]");            //     compressed
		printer.Remove("other.removed.OtherRemoved.OtherRemoved() [F]");  // not compressed

		printer.Add("add [P]");                                           // not compressed
		printer.Add("add.Add [C]");                                       //     compressed
		printer.Add("add.Add.Add() [F]");                                 //     compressed
		printer.Add("add.Add.add [F]");                                   //     compressed
		printer.Add("add.OtherAdd.OtherAdd() [F]");                       //     compressed
		printer.Add("add.OtherAdd.add [F]");                              //     compressed
		printer.Add("addpackage [P]");                                    // not compressed
		printer.Add("add.package.internal [P]");                          // not compressed
		printer.Add("other.add.Add [C]");                                 // not compressed
		printer.Add("other.add.Add.Add() [F]");                           //     compressed
		printer.Add("other.add.OtherAdd.OtherAdd() [F]");                 // not compressed

		String xml_document = printer.toString();
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}

		assertTrue("removed not in " + xml_document, xml_document.indexOf("<line>removed</line>") != -1);
		assertTrue("removed.Removed in " + xml_document, xml_document.indexOf("<line>removed.Removed</line>") == -1);
		assertTrue("removed.Removed.Removed() in " + xml_document, xml_document.indexOf("<line>removed.Removed.Removed()</line>") == -1);
		assertTrue("removed.Removed.removed in " + xml_document, xml_document.indexOf("<line>removed.Removed.removed</line>") == -1);
		assertTrue("removed.OtherRemoved.OtherRemoved() in " + xml_document, xml_document.indexOf("<line>removed.OtherRemoved.OtherRemoved()</line>") == -1);
		assertTrue("removed.OtherRemoved.other_removed in " + xml_document, xml_document.indexOf("<line>removed.OtherRemoved.other_removed</line>") == -1);
		assertTrue("removedpackage not in " + xml_document, xml_document.indexOf("<line>removedpackage</line>") != -1);
		assertTrue("removed.package.internal not in " + xml_document, xml_document.indexOf("<line>removed.package.internal</line>") != -1);
		assertTrue("other.removed.Removed not in " + xml_document, xml_document.indexOf("<line>other.removed.Removed</line>") != -1);
		assertTrue("other.removed.Removed.Removed() in " + xml_document, xml_document.indexOf("<line>other.removed.Removed.Removed()</line>") == -1);
		assertTrue("other.removed.OtherRemoved.OtherRemoved() not in " + xml_document, xml_document.indexOf("<line>other.removed.OtherRemoved.OtherRemoved()</line>") != -1);

		assertTrue("add not in " + xml_document, xml_document.indexOf("<line>add</line>") != -1);
		assertTrue("add.Add in " + xml_document, xml_document.indexOf("<line>add.Add</line>") == -1);
		assertTrue("add.Add.Add() in " + xml_document, xml_document.indexOf("<line>add.Add.Add()</line>") == -1);
		assertTrue("add.Add.add in " + xml_document, xml_document.indexOf("<line>add.Add.add</line>") == -1);
		assertTrue("add.OtherAdd.OtherAdd() in " + xml_document, xml_document.indexOf("<line>add.OtherAdd.OtherAdd()</line>") == -1);
		assertTrue("add.OtherAdd.other_add in " + xml_document, xml_document.indexOf("<line>add.OtherAdd.other_add</line>") == -1);
		assertTrue("addpackage not in " + xml_document, xml_document.indexOf("<line>addpackage</line>") != -1);
		assertTrue("add.package.internal not in " + xml_document, xml_document.indexOf("<line>add.package.internal</line>") != -1);
		assertTrue("other.add.Add not in " + xml_document, xml_document.indexOf("<line>other.add.Add</line>") != -1);
		assertTrue("other.add.Add.Add() in " + xml_document, xml_document.indexOf("<line>other.add.Add.Add()</line>") == -1);
		assertTrue("other.add.OtherAdd.OtherAdd() not in " + xml_document, xml_document.indexOf("<line>other.add.OtherAdd.OtherAdd()</line>") != -1);
	}

	public void testNoSuffixes() {
		ListDiffPrinter printer = new ListDiffPrinter(true);

		printer.Remove("removed");                                    // not compressed
		printer.Remove("removed.Removed");                            // not compressed
		printer.Remove("removed.Removed.Removed()");                  // not compressed
		printer.Remove("removed.Removed.removed");                    // not compressed
		printer.Remove("removed.OtherRemoved.OtherRemoved()");        // not compressed
		printer.Remove("removed.OtherRemoved.other_removed");         // not compressed
		printer.Remove("removedpackage");                             // not compressed
		printer.Remove("removed.package.internal");                   // not compressed
		printer.Remove("other.removed.Removed");                      // not compressed
		printer.Remove("other.removed.Removed.Removed()");            // not compressed
		printer.Remove("other.removed.OtherRemoved.OtherRemoved()");  // not compressed

		printer.Add("add");                                           // not compressed
		printer.Add("add.Add");                                       // not compressed
		printer.Add("add.Add.Add()");                                 // not compressed
		printer.Add("add.Add.add");                                   // not compressed
		printer.Add("add.OtherAdd.OtherAdd()");                       // not compressed
		printer.Add("add.OtherAdd.other_add");                        // not compressed
		printer.Add("addpackage");                                    // not compressed
		printer.Add("add.package.internal");                          // not compressed
		printer.Add("other.add.Add");                                 // not compressed
		printer.Add("other.add.Add.Add()");                           // not compressed
		printer.Add("other.add.OtherAdd.OtherAdd()");                 // not compressed

		String xml_document = printer.toString();
		
		try {
			reader.parse(new InputSource(new StringReader(xml_document)));
		} catch (SAXException ex) {
			fail("Could not parse XML Document: " + ex.getMessage() + "\n" + xml_document);
		} catch (IOException ex) {
			fail("Could not read XML Document: " + ex.getMessage() + "\n" + xml_document);
		}

		assertTrue("removed not in " + xml_document, xml_document.indexOf("<line>removed</line>") != -1);
		assertTrue("removed.Removed in " + xml_document, xml_document.indexOf("<line>removed.Removed</line>") != -1);
		assertTrue("removed.Removed.Removed() in " + xml_document, xml_document.indexOf("<line>removed.Removed.Removed()</line>") != -1);
		assertTrue("removed.Removed.removed in " + xml_document, xml_document.indexOf("<line>removed.Removed.removed</line>") != -1);
		assertTrue("removed.OtherRemoved.OtherRemoved() in " + xml_document, xml_document.indexOf("<line>removed.OtherRemoved.OtherRemoved()</line>") != -1);
		assertTrue("removed.OtherRemoved.other_removed in " + xml_document, xml_document.indexOf("<line>removed.OtherRemoved.other_removed</line>") != -1);
		assertTrue("removedpackage not in " + xml_document, xml_document.indexOf("<line>removedpackage</line>") != -1);
		assertTrue("removed.package.internal not in " + xml_document, xml_document.indexOf("<line>removed.package.internal</line>") != -1);
		assertTrue("other.removed.Removed not in " + xml_document, xml_document.indexOf("<line>other.removed.Removed</line>") != -1);
		assertTrue("other.removed.Removed.Removed() in " + xml_document, xml_document.indexOf("<line>other.removed.Removed.Removed()</line>") != -1);
		assertTrue("other.removed.OtherRemoved.OtherRemoved() not in " + xml_document, xml_document.indexOf("<line>other.removed.OtherRemoved.OtherRemoved()</line>") != -1);

		assertTrue("add not in " + xml_document, xml_document.indexOf("<line>add</line>") != -1);
		assertTrue("add.Add in " + xml_document, xml_document.indexOf("<line>add.Add</line>") != -1);
		assertTrue("add.Add.Add() in " + xml_document, xml_document.indexOf("<line>add.Add.Add()</line>") != -1);
		assertTrue("add.Add.add in " + xml_document, xml_document.indexOf("<line>add.Add.add</line>") != -1);
		assertTrue("add.OtherAdd.OtherAdd() in " + xml_document, xml_document.indexOf("<line>add.OtherAdd.OtherAdd()</line>") != -1);
		assertTrue("add.OtherAdd.other_add in " + xml_document, xml_document.indexOf("<line>add.OtherAdd.other_add</line>") != -1);
		assertTrue("addpackage not in " + xml_document, xml_document.indexOf("<line>addpackage</line>") != -1);
		assertTrue("add.package.internal not in " + xml_document, xml_document.indexOf("<line>add.package.internal</line>") != -1);
		assertTrue("other.add.Add not in " + xml_document, xml_document.indexOf("<line>other.add.Add</line>") != -1);
		assertTrue("other.add.Add.Add() in " + xml_document, xml_document.indexOf("<line>other.add.Add.Add()</line>") != -1);
		assertTrue("other.add.OtherAdd.OtherAdd() not in " + xml_document, xml_document.indexOf("<line>other.add.OtherAdd.OtherAdd()</line>") != -1);
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
