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

package com.jeantessier.text;

import java.util.*;

import junit.framework.*;

public class TestRegularExpressionParser extends TestCase {
    public void testParseRE() {
        List<String> expected = new ArrayList<String>();
        expected.add("/test/");

        List test = RegularExpressionParser.parseRE("/test/");

        assertEquals("size", expected.size(), test.size());
        assertEquals("/test/", expected.get(0), test.get(0));
    }

    public void testParseBrokenRE() {
        List<String> expected = new ArrayList<String>();
        expected.add("/test");

        List test = RegularExpressionParser.parseRE("/test");

        assertEquals("size", expected.size(), test.size());
        assertEquals("/test", expected.get(0), test.get(0));
    }

    public void testParseMultipleREs() {
        List<String> expected = new ArrayList<String>();
        expected.add("/test1/");
        expected.add("/test2/");

        List test = RegularExpressionParser.parseRE("/test1/,/test2/");

        assertEquals("size", expected.size(), test.size());
        assertEquals("/test1/", expected.get(0), test.get(0));
        assertEquals("/test2/", expected.get(1), test.get(1));
    }

    public void testParseComplexREs() {
        List<String> expected = new ArrayList<String>();
        expected.add("/test1\\/test2/");

        List test = RegularExpressionParser.parseRE("/test1\\/test2/");

        assertEquals("size", expected.size(), test.size());
        assertEquals("/test1\\/test2/", expected.get(0), test.get(0));
    }

    public void testParseReallyComplexREs() {
        List<String> expected = new ArrayList<String>();
        expected.add("m=test1\\=test2=i");

        List test = RegularExpressionParser.parseRE("m=test1\\=test2=i");

        assertEquals("size", expected.size(), test.size());
        assertEquals("m=test1\\=test2=i", expected.get(0), test.get(0));
    }
}
