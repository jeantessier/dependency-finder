/*
 *  Copyright (c) 2001-2004, Jean Tessier
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

package com.jeantessier.dependency;

import java.io.*;

import junit.framework.*;

public class TestTextPrinter extends TestCase {
	private NodeFactory  factory;
	private StringWriter out;
	private TextPrinter  visitor;

	protected void setUp() throws Exception {
		factory = new NodeFactory();
		out     = new StringWriter();
		visitor = new TextPrinter(new PrintWriter(out));
	}

	public void testShowInboundsPackageTrue() throws IOException {
		factory.CreatePackage("outbound").addDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		visitor.ShowInbounds(true);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    <-- outbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsPackageFalse() throws IOException {
		factory.CreatePackage("outbound").addDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsClassTrue() throws IOException {
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		visitor.ShowInbounds(true);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        <-- outbound.Outbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsClassFalse() throws IOException {
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsFeatureTrue() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		visitor.ShowInbounds(true);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "        empty()", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- outbound.Outbound.outbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowInboundsFeatureFalse() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "        empty()", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsPackageTrue() throws IOException {
		factory.CreatePackage("outbound").addDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(true);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    --> inbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsPackageFalse() throws IOException {
		factory.CreatePackage("outbound").addDependency(factory.CreatePackage("inbound"));
		factory.CreatePackage("empty");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsClassTrue() throws IOException {
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(true);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        --> inbound.Inbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsClassFalse() throws IOException {
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateClass("empty.Empty");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsFeatureTrue() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(true);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "        empty()", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());
		assertEquals("line " + ++line_number, "            --> inbound.Inbound.inbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowOutboundsFeatureFalse() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		visitor.ShowInbounds(false);
		visitor.ShowOutbounds(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "        empty()", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}
	
	public void testShowEmptyPackageTrue() throws IOException {
		factory.CreatePackage("outbound").addDependency(factory.CreatePackage("inbound"));
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreatePackage("empty");

		visitor.ShowEmptyNodes(true);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    <-- outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        <-- outbound.Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- outbound.Outbound.outbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    --> inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        --> inbound.Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());
		assertEquals("line " + ++line_number, "            --> inbound.Inbound.inbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyPackageFalse() throws IOException {
		factory.CreatePackage("outbound").addDependency(factory.CreatePackage("inbound"));
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreatePackage("empty");

		visitor.ShowEmptyNodes(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    <-- outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        <-- outbound.Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- outbound.Outbound.outbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    --> inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        --> inbound.Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());
		assertEquals("line " + ++line_number, "            --> inbound.Inbound.inbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyClassTrue() throws IOException {
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateClass("empty.Empty");

		visitor.ShowEmptyNodes(true);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        <-- outbound.Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- outbound.Outbound.outbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        --> inbound.Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());
		assertEquals("line " + ++line_number, "            --> inbound.Inbound.inbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyClassFalse() throws IOException {
		factory.CreateClass("outbound.Outbound").addDependency(factory.CreateClass("inbound.Inbound"));
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateClass("empty.Empty");

		visitor.ShowEmptyNodes(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        <-- outbound.Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- outbound.Outbound.outbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        --> inbound.Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());
		assertEquals("line " + ++line_number, "            --> inbound.Inbound.inbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyFeatureTrue() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		visitor.ShowEmptyNodes(true);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "empty", in.readLine());
		assertEquals("line " + ++line_number, "    Empty", in.readLine());
		assertEquals("line " + ++line_number, "        empty()", in.readLine());
		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- outbound.Outbound.outbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());
		assertEquals("line " + ++line_number, "            --> inbound.Inbound.inbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}

	public void testShowEmptyFeatureFalse() throws IOException {
		factory.CreateFeature("outbound.Outbound.outbound()").addDependency(factory.CreateFeature("inbound.Inbound.inbound()"));
		factory.CreateFeature("empty.Empty.empty()");

		visitor.ShowEmptyNodes(false);

		visitor.traverseNodes(factory.Packages().values());

		int            line_number = 0;
		BufferedReader in          = new BufferedReader(new StringReader(out.toString()));

		assertEquals("line " + ++line_number, "inbound", in.readLine());
		assertEquals("line " + ++line_number, "    Inbound", in.readLine());
		assertEquals("line " + ++line_number, "        inbound()", in.readLine());
		assertEquals("line " + ++line_number, "            <-- outbound.Outbound.outbound()", in.readLine());
		assertEquals("line " + ++line_number, "outbound", in.readLine());
		assertEquals("line " + ++line_number, "    Outbound", in.readLine());
		assertEquals("line " + ++line_number, "        outbound()", in.readLine());
		assertEquals("line " + ++line_number, "            --> inbound.Inbound.inbound()", in.readLine());

		assertEquals("End of file", null, in.readLine());
	}
}
