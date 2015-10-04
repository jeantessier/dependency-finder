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

import junit.framework.*;

import org.apache.oro.text.*;
import org.apache.oro.text.regex.*;

public class TestMaximumCapacityPatternCache extends TestCase {
    private PatternCache cache;

    protected void setUp() throws Exception {
        cache = new MaximumCapacityPatternCache();
    }

    public void testCapacity() {
        assertEquals(20, cache.capacity());
    }

    public void testSize() throws MalformedPatternException {
        assertEquals("empty", 0, cache.size());

        cache.addPattern("/foo/");

        assertEquals("add one", 1, cache.size());

        cache.addPattern("/foo/");

        assertEquals("add same again", 1, cache.size());

        cache.addPattern("/bar/");

        assertEquals("add another", 2, cache.size());
    }

    public void testAddPattern() throws MalformedPatternException {
        Object pattern1 = cache.addPattern("/foo/");
        assertNotNull("add returns null", pattern1);

        Object pattern2 = cache.addPattern("/foo/");
        assertSame("add twice returns different", pattern1, pattern2);
    }

    public void testAddPatternWithOption() throws MalformedPatternException {
        Object pattern = cache.addPattern("/foo/", Perl5Compiler.CASE_INSENSITIVE_MASK);
        assertNotNull("add returns null", pattern);
    }

    public void testAddMalformedPattern() throws MalformedPatternException {
        try {
            cache.addPattern("foo(");
            fail("Added a malformed pattern");
        } catch (MalformedPatternException e) {
            // expected
        }
    }

    public void testAddMalformedPatternWithOption() throws MalformedPatternException {
        try {
            cache.addPattern("foo(", Perl5Compiler.CASE_INSENSITIVE_MASK);
            fail("Added a malformed pattern");
        } catch (MalformedPatternException e) {
            // expected
        }
    }

    public void testGetPattern() throws MalformedCachePatternException {
        Object pattern1 = cache.getPattern("/foo/");
        assertNotNull("get returns null", pattern1);

        Object pattern2 = cache.getPattern("/foo/");
        assertSame("get twice returns different", pattern1, pattern2);
    }

    public void testGetPatternWithOption() throws MalformedCachePatternException {
        Object pattern = cache.getPattern("/foo/", Perl5Compiler.CASE_INSENSITIVE_MASK);
        assertNotNull("get returns null", pattern);
    }

    public void testGetMalformedPattern() throws MalformedCachePatternException {
        try {
            cache.getPattern("foo(");
            fail("Got malformed pattern");
        } catch (MalformedCachePatternException e) {
            // Expected
        }
    }

    public void testGetMalformedPatternWithOption() throws MalformedCachePatternException {
        try {
            cache.getPattern("foo(", Perl5Compiler.CASE_INSENSITIVE_MASK);
            fail("Got malformed pattern");
        } catch (MalformedCachePatternException e) {
            // Expected
        }
    }
}
