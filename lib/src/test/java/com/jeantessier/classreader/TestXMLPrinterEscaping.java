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

package com.jeantessier.classreader;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestXMLPrinterEscaping {
    @Parameters(name="Escaping {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"normal characters", "abcdef", "abcdef"},
                {"entities", "<abc>", "&lt;abc&gt;"},
                {"low value characters", "\u0005", "<![CDATA[&#x5;]]>"},
                {"high value characters", "\u0080", "<![CDATA[&#x80;]]>"},
                {"very high value characters", "\u00ff", "<![CDATA[&#xFF;]]>"},
                {"multi-byte characters", "\u1aff", "<![CDATA[&#x1AFF;]]>"},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public String text;

    @Parameter(2)
    public String expectedResult;

    private XMLPrinter sut;

    @Before
    public void setUp() {
        sut = new XMLPrinter(new PrintWriter(new StringWriter()));
    }

    @Test
    public void testEscapeXMLCharacters() {
        assertEquals("text", expectedResult, sut.escapeXMLCharacters(text));
    }
}
