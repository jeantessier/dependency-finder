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

package com.jeantessier.metrics;

import junit.framework.*;

public class TestWordCounter extends TestCase {
    private WordCounter counter;

    protected void setUp() throws Exception {
        super.setUp();

        counter = new WordCounter();
    }

    public void testNullPackageName() {
        try {
            counter.countPackageName(null);
            fail("Computed word count on package name null");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testEmptyPackageName() {
        assertEquals("empty", 0, counter.countPackageName(""));
    }

    public void testOneCharPackageName() {
        assertEquals("p", 1, counter.countPackageName("p"));
        assertEquals("P", 1, counter.countPackageName("P"));
    }

    public void testNullIdentifier() {
        try {
            counter.countIdentifier(null);
            fail("Computed word count on identifier null");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    public void testEmptyIdentifier() {
        assertEquals("empty", 0, counter.countIdentifier(""));
    }

    public void testOneCharIdentifier() {
        assertEquals("p", 1, counter.countIdentifier("p"));
        assertEquals("P", 1, counter.countIdentifier("P"));
    }
}
