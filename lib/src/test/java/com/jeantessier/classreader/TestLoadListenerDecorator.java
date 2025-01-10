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

import org.jmock.*;
import org.junit.jupiter.api.*;

import com.jeantessier.MockObjectTestCase;

import static org.junit.jupiter.api.Assertions.*;

public class TestLoadListenerDecorator extends MockObjectTestCase {
    private final LoadEvent testEvent = new LoadEvent(this, null, 0);
    private final LoadListener mockDelegate = mock(LoadListener.class);

    private final LoadListener sut = new LoadListenerDecorator(mockDelegate);

    @Test
    void testBeginSession() {
        checking(new Expectations() {{
            oneOf (mockDelegate).beginSession(testEvent);
        }});

        sut.beginSession(testEvent);
    }

    @Test
    void testBeginGroup() {
        checking(new Expectations() {{
            oneOf (mockDelegate).beginGroup(testEvent);
        }});

        sut.beginGroup(testEvent);
    }

    @Test
    void testBeginFile() {
        checking(new Expectations() {{
            oneOf (mockDelegate).beginFile(testEvent);
        }});

        sut.beginFile(testEvent);
    }

    @Test
    void testBeginClassfile() {
        checking(new Expectations() {{
            oneOf (mockDelegate).beginClassfile(testEvent);
        }});

        sut.beginClassfile(testEvent);
    }

    @Test
    void testEndClassfile() {
        checking(new Expectations() {{
            oneOf (mockDelegate).endClassfile(testEvent);
        }});

        sut.endClassfile(testEvent);
    }

    @Test
    void testEndFile() {
        checking(new Expectations() {{
            oneOf (mockDelegate).endFile(testEvent);
        }});

        sut.endFile(testEvent);
    }

    @Test
    void testEndGroup() {
        checking(new Expectations() {{
            oneOf (mockDelegate).endGroup(testEvent);
        }});

        sut.endGroup(testEvent);
    }

    @Test
    void testEndSession() {
        checking(new Expectations() {{
            oneOf (mockDelegate).endSession(testEvent);
        }});

        sut.endSession(testEvent);
    }
}
