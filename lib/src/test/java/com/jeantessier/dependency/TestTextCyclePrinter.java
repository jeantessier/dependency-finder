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

package com.jeantessier.dependency;

import java.util.*;
import java.io.*;

import junit.framework.*;

public class TestTextCyclePrinter extends TestCase {
    private Node a_package;
    private Node b_package;
    private Node c_package;

    protected void setUp() throws Exception {
        super.setUp();

        var factory = new NodeFactory();

        a_package = factory.createPackage("a");
        b_package = factory.createPackage("b");
        c_package = factory.createPackage("c");
    }

    public void testVisitCycleWith2NodeCycle() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(a_package);
        nodes.add(b_package);
        Cycle cycle = new Cycle(nodes);

        var expected = new StringWriter();
        try (var pw = new PrintWriter(expected)) {
            pw.println(a_package);
            pw.println("    --> " + b_package);
            pw.println("        --> " + a_package);
        }

        var buffer = new StringWriter();
        try (var out = new PrintWriter(buffer)) {
            var printer = new TextCyclePrinter(out);
            printer.visitCycle(cycle);
        }

        assertEquals(expected.toString(), buffer.toString());
    }

    public void testVisitCycleWith2NodeCycleWithIndentText() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(a_package);
        nodes.add(b_package);
        Cycle cycle = new Cycle(nodes);

        var expected = new StringWriter();
        try (var pw = new PrintWriter(expected)) {
            pw.println(a_package);
            pw.println("*--> " + b_package);
            pw.println("**--> " + a_package);
        }

        var buffer = new StringWriter();
        try (var out = new PrintWriter(buffer)) {
            var printer = new TextCyclePrinter(out);
            printer.setIndentText("*");
            printer.visitCycle(cycle);
        }

        assertEquals(expected.toString(), buffer.toString());
    }

    public void testVisitCycleWith3NodeCycle() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(a_package);
        nodes.add(b_package);
        nodes.add(c_package);
        Cycle cycle = new Cycle(nodes);

        var expected = new StringWriter();
        try (var pw = new PrintWriter(expected)) {
            pw.println(a_package);
            pw.println("    --> " + b_package);
            pw.println("        --> " + c_package);
            pw.println("            --> " + a_package);
        }

        var buffer = new StringWriter();
        try (var out = new PrintWriter(buffer)) {
            var printer = new TextCyclePrinter(out);
            printer.visitCycle(cycle);
        }

        assertEquals(expected.toString(), buffer.toString());
    }

    public void testVisitCyclesWith2NodeCycle() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(a_package);
        nodes.add(b_package);
        Cycle cycle = new Cycle(nodes);

        var expected = new StringWriter();
        try (var pw = new PrintWriter(expected)) {
            pw.println(a_package);
            pw.println("    --> " + b_package);
            pw.println("        --> " + a_package);
        }

        var buffer = new StringWriter();
        try (var out = new PrintWriter(buffer)) {
            var printer = new TextCyclePrinter(out);
            printer.visitCycles(Collections.singletonList(cycle));
        }

        assertEquals(expected.toString(), buffer.toString());
    }

    public void testVisitCyclesWith3NodeCycle() {
        List<Node> nodes = new ArrayList<>();
        nodes.add(a_package);
        nodes.add(b_package);
        nodes.add(c_package);
        Cycle cycle = new Cycle(nodes);

        var expected = new StringWriter();
        try (var pw = new PrintWriter(expected)) {
            pw.println(a_package);
            pw.println("    --> " + b_package);
            pw.println("        --> " + c_package);
            pw.println("            --> " + a_package);
        }

        var buffer = new StringWriter();
        try (var out = new PrintWriter(buffer)) {
            var printer = new TextCyclePrinter(out);
            printer.visitCycles(Collections.singletonList(cycle));
        }

        assertEquals(expected.toString(), buffer.toString());
    }

    public void testVisitCyclesWith2Cycles() {
        List<Cycle> cycles = new ArrayList<>();

        List<Node> nodes1 = new ArrayList<>();
        nodes1.add(a_package);
        nodes1.add(b_package);
        cycles.add(new Cycle(nodes1));

        List<Node> nodes2 = new ArrayList<>();
        nodes2.add(a_package);
        nodes2.add(b_package);
        nodes2.add(c_package);
        cycles.add(new Cycle(nodes2));

        var expected = new StringWriter();
        try (var pw = new PrintWriter(expected)) {
            pw.println(a_package);
            pw.println("    --> " + b_package);
            pw.println("        --> " + a_package);
            pw.println(a_package);
            pw.println("    --> " + b_package);
            pw.println("        --> " + c_package);
            pw.println("            --> " + a_package);
        }

        var buffer = new StringWriter();
        try (var out = new PrintWriter(buffer)) {
            var printer = new TextCyclePrinter(out);
            printer.visitCycles(cycles);
        }

        assertEquals(expected.toString(), buffer.toString());
    }
}
