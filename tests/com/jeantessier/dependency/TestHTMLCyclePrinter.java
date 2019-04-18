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
import java.util.*;

public class TestHTMLCyclePrinter extends TestHTMLPrinterBase {
    private HTMLCyclePrinter visitor;

    private Node a_package;
    private Node b_package;
    private Node c_package;

    protected void setUp() throws Exception {
        super.setUp();

        visitor = new HTMLCyclePrinter(new PrintWriter(out), FORMAT);

        a_package = factory.createPackage("a");
        b_package = factory.createPackage("b");
        c_package = factory.createPackage("c");
    }

    public void testEmptyCycles() throws IOException {
        visitor.visitCycles(Collections.<Cycle>emptyList());

        BufferedReader in = new BufferedReader(new StringReader(out.toString()));

        assertEquals("End of file", null, in.readLine());
    }

    public void test2NodesCycle() throws IOException {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(a_package);
        nodes.add(b_package);
        Cycle cycle = new Cycle(nodes);

        visitor.visitCycles(Collections.singletonList(cycle));

        int lineNumber = 0;
        BufferedReader in = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"a\">a</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    --&gt; <a href=\"" + PREFIX + "b" + SUFFIX + "\" id=\"a_to_b\">b</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"b_to_a\">a</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }

    public void test3NodesCycle() throws IOException {
        List<Node> nodes = new ArrayList<Node>();
        nodes.add(a_package);
        nodes.add(b_package);
        nodes.add(c_package);
        Cycle cycle = new Cycle(nodes);

        visitor.visitCycles(Collections.singletonList(cycle));

        int lineNumber = 0;
        BufferedReader in = new BufferedReader(new StringReader(out.toString()));

        assertEquals("line " + ++lineNumber, "<a class=\"scope\" href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"a\">a</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "    --&gt; <a href=\"" + PREFIX + "b" + SUFFIX + "\" id=\"a_to_b\">b</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "        --&gt; <a href=\"" + PREFIX + "c" + SUFFIX + "\" id=\"b_to_c\">c</a>", in.readLine());
        assertEquals("line " + ++lineNumber, "            --&gt; <a href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"c_to_a\">a</a>", in.readLine());

        assertEquals("End of file", null, in.readLine());
    }
}
