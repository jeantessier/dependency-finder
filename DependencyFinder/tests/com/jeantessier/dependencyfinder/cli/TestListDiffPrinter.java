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
	public TestListDiffPrinter(String name) {
		super(name);
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

		String text = printer.toString();

		assertTrue("java.lang.Object not in " + text, text.indexOf("java.lang.Object") != -1);
		assertTrue("java.lang.Object.Object() not in " + text, text.indexOf("java.lang.Object.Object()") != -1);
		assertTrue("java.lang.String not in " + text, text.indexOf("java.lang.String") != -1);
		assertTrue("java.util not in " + text, text.indexOf("java.util") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + text, text.indexOf("java.util.Collection.add(java.lang.Object)") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + text, text.indexOf("java.util.Collection.add(java.lang.Object)") != -1);

		assertTrue("java.lang.Thread not in " + text, text.indexOf("java.lang.Thread") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + text, text.indexOf("java.lang.Thread.Thread()") != -1);
		assertTrue("java.lang.System not in " + text, text.indexOf("java.lang.System") != -1);
		assertTrue("java.io not in " + text, text.indexOf("java.io") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + text, text.indexOf("java.io.PrintStream.println(java.lang.Object)") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + text, text.indexOf("java.io.PrintStream.println(java.lang.Object)") != -1);
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

		assertTrue("java.lang.Object not in " + text, text.indexOf("java.lang.Object") != -1);
		assertTrue("java.lang.Object.Object() not in " + text, text.indexOf("java.lang.Object.Object()") != -1);
		assertTrue("java.lang.String not in " + text, text.indexOf("java.lang.String") != -1);
		assertTrue("java.util not in " + text, text.indexOf("java.util") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) not in " + text, text.indexOf("java.util.Collection.add(java.lang.Object)") != -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) not in " + text, text.indexOf("java.util.Collection.add(java.lang.Object)") != -1);

		assertTrue("java.lang.Thread not in " + text, text.indexOf("java.lang.Thread") != -1);
		assertTrue("java.lang.Thread.Thread() not in " + text, text.indexOf("java.lang.Thread.Thread()") != -1);
		assertTrue("java.lang.System not in " + text, text.indexOf("java.lang.System") != -1);
		assertTrue("java.io not in " + text, text.indexOf("java.io") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + text, text.indexOf("java.io.PrintStream.println(java.lang.Object)") != -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) not in " + text, text.indexOf("java.io.PrintStream.println(java.lang.Object)") != -1);
	}

	public void testCompressedList() {
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

		assertTrue("java.lang.Object not in " + text, text.indexOf("java.lang.Object") != -1);
		assertTrue("java.lang.Object.Object() in " + text, text.indexOf("java.lang.Object.Object()") == -1);
		assertTrue("java.lang.String not in " + text, text.indexOf("java.lang.String") != -1);
		assertTrue("java.util not in " + text, text.indexOf("java.util") != -1);
		assertTrue("java.util.Collection.add(java.lang.Object) in " + text, text.indexOf("java.util.Collection.add(java.lang.Object)") == -1);
		assertTrue("java.util.Collection.addAll(java.util.Collection) in " + text, text.indexOf("java.util.Collection.add(java.lang.Object)") == -1);

		assertTrue("java.lang.Thread not in " + text, text.indexOf("java.lang.Thread") != -1);
		assertTrue("java.lang.Thread.Thread() in " + text, text.indexOf("java.lang.Thread.Thread()") == -1);
		assertTrue("java.lang.System not in " + text, text.indexOf("java.lang.System") != -1);
		assertTrue("java.io not in " + text, text.indexOf("java.io") != -1);
		assertTrue("java.io.PrintStream.println(java.lang.Object) not in " + text, text.indexOf("java.io.PrintStream.println(java.lang.Object)") == -1);
		assertTrue("java.io.PrintWriter.println(java.lang.Object) in " + text, text.indexOf("java.io.PrintStream.println(java.lang.Object)") == -1);
	}
}
