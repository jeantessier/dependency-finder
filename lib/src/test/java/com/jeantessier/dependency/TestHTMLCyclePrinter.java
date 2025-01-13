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

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestHTMLCyclePrinter extends TestHTMLPrinterBase {
    private final HTMLCyclePrinter visitor = new HTMLCyclePrinter(new PrintWriter(writer), FORMAT);

    private final Node a_package = factory.createPackage("a");
    private final Node b_package = factory.createPackage("b");
    private final Node c_package = factory.createPackage("c");

    @Test
    void testEmptyCycles() {
        visitor.visitCycles(Collections.emptyList());

        var expectedLines = Stream.<String>empty();

        assertLinesMatch(expectedLines, writer.toString().lines());
    }

    @Test
    void test2NodesCycle() {
        var cycle = new Cycle(List.of(a_package, b_package));

        visitor.visitCycles(Collections.singletonList(cycle));

        var expectedLines = Stream.of(
                "<a class=\"scope\" href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"a\">a</a>",
                "    --&gt; <a href=\"" + PREFIX + "b" + SUFFIX + "\" id=\"a_to_b\">b</a>",
                "        --&gt; <a href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"b_to_a\">a</a>"
        );

        assertLinesMatch(expectedLines, writer.toString().lines());
    }

    @Test
    void test3NodesCycle() {
        var cycle = new Cycle(List.of(a_package, b_package, c_package));

        visitor.visitCycles(Collections.singletonList(cycle));

        var expectedLines = Stream.of(
                "<a class=\"scope\" href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"a\">a</a>",
                "    --&gt; <a href=\"" + PREFIX + "b" + SUFFIX + "\" id=\"a_to_b\">b</a>",
                "        --&gt; <a href=\"" + PREFIX + "c" + SUFFIX + "\" id=\"b_to_c\">c</a>",
                "            --&gt; <a href=\"" + PREFIX + "a" + SUFFIX + "\" id=\"c_to_a\">a</a>"
        );

        assertLinesMatch(expectedLines, writer.toString().lines());
    }
}
