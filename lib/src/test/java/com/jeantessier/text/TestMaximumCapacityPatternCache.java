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

package com.jeantessier.text;

import org.apache.oro.text.*;
import org.apache.oro.text.regex.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestMaximumCapacityPatternCache {
    private final PatternCache cache = new MaximumCapacityPatternCache();

    @Test
    void testCapacity() {
        assertEquals(20, cache.capacity());
    }

    @Test
    void testSize() throws MalformedPatternException {
        assertEquals(0, cache.size(), "empty");

        cache.addPattern("/foo/");

        assertEquals(1, cache.size(), "add one");

        cache.addPattern("/foo/");

        assertEquals(1, cache.size(), "add same again");

        cache.addPattern("/bar/");

        assertEquals(2, cache.size(), "add another");
    }

    @Test
    void testAddPattern() throws MalformedPatternException {
        Object pattern1 = cache.addPattern("/foo/");
        assertNotNull(pattern1, "add returns null");

        Object pattern2 = cache.addPattern("/foo/");
        assertSame(pattern1, pattern2, "add twice returns different");
    }

    @Test
    void testAddPatternWithOption() throws MalformedPatternException {
        Object pattern = cache.addPattern("/foo/", Perl5Compiler.CASE_INSENSITIVE_MASK);
        assertNotNull(pattern, "add returns null");
    }

    @Test
    void testAddMalformedPattern() {
        assertThrows(MalformedPatternException.class, () -> cache.addPattern("foo("));
    }

    @Test
    void testAddMalformedPatternWithOption() {
        assertThrows(MalformedPatternException.class, () -> cache.addPattern("foo(", Perl5Compiler.CASE_INSENSITIVE_MASK));
    }

    @Test
    void testGetPattern() throws MalformedCachePatternException {
        Object pattern1 = cache.getPattern("/foo/");
        assertNotNull(pattern1, "get returns null");

        Object pattern2 = cache.getPattern("/foo/");
        assertSame(pattern1, pattern2, "get twice returns different");
    }

    @Test
    void testGetPatternWithOption() throws MalformedCachePatternException {
        Object pattern = cache.getPattern("/foo/", Perl5Compiler.CASE_INSENSITIVE_MASK);
        assertNotNull(pattern, "get returns null");
    }

    @Test
    void testGetMalformedPattern() {
        assertThrows(MalformedCachePatternException.class, () -> cache.getPattern("foo("));
    }

    @Test
    void testGetMalformedPatternWithOption() {
        assertThrows(MalformedCachePatternException.class, () -> cache.getPattern("foo(", Perl5Compiler.CASE_INSENSITIVE_MASK));
    }
}
