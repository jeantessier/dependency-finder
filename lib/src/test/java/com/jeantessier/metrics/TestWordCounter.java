/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

package com.jeantessier.metrics;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestWordCounter {
    private final WordCounter counter = new WordCounter();

    @Test
    void testNullPackageName() {
        assertThrows(NullPointerException.class, () -> counter.countPackageName(null));
    }

    @Test
    void testEmptyPackageName() {
        assertEquals(0, counter.countPackageName(""), "empty");
    }

    @Test
    void testOneCharPackageName() {
        assertEquals(1, counter.countPackageName("p"), "p");
        assertEquals(1, counter.countPackageName("P"), "P");
    }

    @Test
    void testNullIdentifier() {
        assertThrows(NullPointerException.class, () -> counter.countIdentifier(null));
    }

    @Test
    void testEmptyIdentifier() {
        assertEquals(0, counter.countIdentifier(""), "empty");
    }

    @Test
    void testOneCharIdentifier() {
        assertEquals(1, counter.countIdentifier("p"), "p");
        assertEquals(1, counter.countIdentifier("P"), "P");
    }
}
