/*
 *  Copyright (c) 2001-2002, Jean Tessier
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

package com.jeantessier.dependencyfinder.cli;

import junit.framework.*;

public class TestListDiffPrinter extends TestCase {
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

		String text = printer.toString();

		assertTrue("java.lang.Object not in " + text, text.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() not in " + text, text.indexOf("<line>java.lang.Object.Object()</line>") != -1);
		assertTrue("java.lang.String not in " + text, text.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + text, text.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

		assertTrue("java.lang.Thread not in " + text, text.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + text, text.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
		assertTrue("java.lang.System not in " + text, text.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + text, text.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
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

		String text = printer.toString();

		assertTrue("java.lang.Object not in " + text, text.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() not in " + text, text.indexOf("<line>java.lang.Object.Object()</line>") != -1);
		assertTrue("java.lang.String not in " + text, text.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + text, text.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

		assertTrue("java.lang.Thread not in " + text, text.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + text, text.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
		assertTrue("java.lang.System not in " + text, text.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + text, text.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
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

		String text = printer.toString();

		assertTrue("java.lang.Object not in " + text, text.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() in " + text, text.indexOf("<line>java.lang.Object.Object()</line>") == -1);
		assertTrue("java.lang.String not in " + text, text.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + text, text.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") == -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") == -1);

		assertTrue("java.lang.Thread not in " + text, text.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() in " + text, text.indexOf("<line>java.lang.Thread.Thread()</line>") == -1);
		assertTrue("java.lang.System not in " + text, text.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + text, text.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") == -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") == -1);
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

		String text = printer.toString();

		assertTrue("java.lang.Object not in " + text, text.indexOf("<line>java.lang.Object</line>") != -1);
		assertTrue("java.lang.Object.Object() not in " + text, text.indexOf("<line>java.lang.Object.Object()</line>") != -1);
		assertTrue("java.lang.String not in " + text, text.indexOf("<line>java.lang.String</line>") != -1);
		assertTrue("java.util not in " + text, text.indexOf("<line>java.util</line>") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + text, text.indexOf("<line>java.util.Collection.add(java.lang.Object)</line>") != -1);

		assertTrue("java.lang.Thread not in " + text, text.indexOf("<line>java.lang.Thread</line>") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + text, text.indexOf("<line>java.lang.Thread.Thread()</line>") != -1);
		assertTrue("java.lang.System not in " + text, text.indexOf("<line>java.lang.System</line>") != -1);
		assertTrue("java.io not in " + text, text.indexOf("<line>java.io</line>") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + text, text.indexOf("<line>java.io.PrintStream.println(java.lang.Object)</line>") != -1);
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

		String text = printer.toString();

		assertTrue("removed not in " + text, text.indexOf("<line>removed</line>") != -1);
		assertTrue("removed.Removed in " + text, text.indexOf("<line>removed.Removed</line>") == -1);
		assertTrue("removed.Removed.Removed() in " + text, text.indexOf("<line>removed.Removed.Removed()</line>") == -1);
		assertTrue("removed.Removed.removed in " + text, text.indexOf("<line>removed.Removed.removed</line>") == -1);
		assertTrue("removed.OtherRemoved.OtherRemoved() in " + text, text.indexOf("<line>removed.OtherRemoved.OtherRemoved()</line>") == -1);
		assertTrue("removed.OtherRemoved.other_removed in " + text, text.indexOf("<line>removed.OtherRemoved.other_removed</line>") == -1);
		assertTrue("removedpackage not in " + text, text.indexOf("<line>removedpackage</line>") != -1);
		assertTrue("removed.package.internal not in " + text, text.indexOf("<line>removed.package.internal</line>") != -1);
		assertTrue("other.removed.Removed not in " + text, text.indexOf("<line>other.removed.Removed</line>") != -1);
		assertTrue("other.removed.Removed.Removed() in " + text, text.indexOf("<line>other.removed.Removed.Removed()</line>") == -1);
		assertTrue("other.removed.OtherRemoved.OtherRemoved() not in " + text, text.indexOf("<line>other.removed.OtherRemoved.OtherRemoved()</line>") != -1);

		assertTrue("add not in " + text, text.indexOf("<line>add</line>") != -1);
		assertTrue("add.Add in " + text, text.indexOf("<line>add.Add</line>") == -1);
		assertTrue("add.Add.Add() in " + text, text.indexOf("<line>add.Add.Add()</line>") == -1);
		assertTrue("add.Add.add in " + text, text.indexOf("<line>add.Add.add</line>") == -1);
		assertTrue("add.OtherAdd.OtherAdd() in " + text, text.indexOf("<line>add.OtherAdd.OtherAdd()</line>") == -1);
		assertTrue("add.OtherAdd.other_add in " + text, text.indexOf("<line>add.OtherAdd.other_add</line>") == -1);
		assertTrue("addpackage not in " + text, text.indexOf("<line>addpackage</line>") != -1);
		assertTrue("add.package.internal not in " + text, text.indexOf("<line>add.package.internal</line>") != -1);
		assertTrue("other.add.Add not in " + text, text.indexOf("<line>other.add.Add</line>") != -1);
		assertTrue("other.add.Add.Add() in " + text, text.indexOf("<line>other.add.Add.Add()</line>") == -1);
		assertTrue("other.add.OtherAdd.OtherAdd() not in " + text, text.indexOf("<line>other.add.OtherAdd.OtherAdd()</line>") != -1);
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

		String text = printer.toString();

		assertTrue("removed not in " + text, text.indexOf("<line>removed</line>") != -1);
		assertTrue("removed.Removed in " + text, text.indexOf("<line>removed.Removed</line>") != -1);
		assertTrue("removed.Removed.Removed() in " + text, text.indexOf("<line>removed.Removed.Removed()</line>") != -1);
		assertTrue("removed.Removed.removed in " + text, text.indexOf("<line>removed.Removed.removed</line>") != -1);
		assertTrue("removed.OtherRemoved.OtherRemoved() in " + text, text.indexOf("<line>removed.OtherRemoved.OtherRemoved()</line>") != -1);
		assertTrue("removed.OtherRemoved.other_removed in " + text, text.indexOf("<line>removed.OtherRemoved.other_removed</line>") != -1);
		assertTrue("removedpackage not in " + text, text.indexOf("<line>removedpackage</line>") != -1);
		assertTrue("removed.package.internal not in " + text, text.indexOf("<line>removed.package.internal</line>") != -1);
		assertTrue("other.removed.Removed not in " + text, text.indexOf("<line>other.removed.Removed</line>") != -1);
		assertTrue("other.removed.Removed.Removed() in " + text, text.indexOf("<line>other.removed.Removed.Removed()</line>") != -1);
		assertTrue("other.removed.OtherRemoved.OtherRemoved() not in " + text, text.indexOf("<line>other.removed.OtherRemoved.OtherRemoved()</line>") != -1);

		assertTrue("add not in " + text, text.indexOf("<line>add</line>") != -1);
		assertTrue("add.Add in " + text, text.indexOf("<line>add.Add</line>") != -1);
		assertTrue("add.Add.Add() in " + text, text.indexOf("<line>add.Add.Add()</line>") != -1);
		assertTrue("add.Add.add in " + text, text.indexOf("<line>add.Add.add</line>") != -1);
		assertTrue("add.OtherAdd.OtherAdd() in " + text, text.indexOf("<line>add.OtherAdd.OtherAdd()</line>") != -1);
		assertTrue("add.OtherAdd.other_add in " + text, text.indexOf("<line>add.OtherAdd.other_add</line>") != -1);
		assertTrue("addpackage not in " + text, text.indexOf("<line>addpackage</line>") != -1);
		assertTrue("add.package.internal not in " + text, text.indexOf("<line>add.package.internal</line>") != -1);
		assertTrue("other.add.Add not in " + text, text.indexOf("<line>other.add.Add</line>") != -1);
		assertTrue("other.add.Add.Add() in " + text, text.indexOf("<line>other.add.Add.Add()</line>") != -1);
		assertTrue("other.add.OtherAdd.OtherAdd() not in " + text, text.indexOf("<line>other.add.OtherAdd.OtherAdd()</line>") != -1);
	}
}
