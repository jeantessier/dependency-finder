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

package com.jeantessier.classreader;

import junit.framework.TestCase;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestXMLPrinterEscaping extends TestCase {
    private XMLPrinter printer;

    protected void setUp() throws Exception {
        super.setUp();

        printer = new XMLPrinter(new PrintWriter(new StringWriter()));
    }

    public void testEscapeXMLCharacters_normalCharacters() {
        String testText = "abcdef";
        String expectedText = "abcdef";

        String actualText = printer.escapeXMLCharacters(testText);

        assertEquals("text", expectedText, actualText);
    }

    public void testEscapeXMLCharacters_entities() {
        String testText = "<abc>";
        String expectedText = "&lt;abc&gt;";

        String actualText = printer.escapeXMLCharacters(testText);

        assertEquals("text", expectedText, actualText);
    }

    public void testEscapeXMLCharacters_lowValueCharacters() {
        String testText = "\u0005";
        String expectedText = "<![CDATA[&#x5;]]>";

        String actualText = printer.escapeXMLCharacters(testText);

        assertEquals("text", expectedText, actualText);
    }

    public void testEscapeXMLCharacters_highValueCharacters() {
        String testText = "\u0080";
        String expectedText = "<![CDATA[&#x80;]]>";

        String actualText = printer.escapeXMLCharacters(testText);

        assertEquals("text", expectedText, actualText);
    }

    public void testEscapeXMLCharacters_veryHighValueCharacters() {
        String testText = "\u00ff";
        String expectedText = "<![CDATA[&#xFF;]]>";

        String actualText = printer.escapeXMLCharacters(testText);

        assertEquals("text", expectedText, actualText);
    }
}
