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

import org.jmock.integration.junit3.*;
import org.jmock.*;

public class TestLoadListenerDecorator extends MockObjectTestCase {
    public LoadEvent testEvent;
    public LoadListener mockDelegate;

    public LoadListener sut;

    protected void setUp() throws Exception {
        super.setUp();

        testEvent = new LoadEvent(this, null, 0);
        mockDelegate = mock(LoadListener.class);

        sut = new LoadListenerDecorator(mockDelegate);
    }

    public void testBeginSession() {
        checking(new Expectations() {{
            one (mockDelegate).beginSession(testEvent);
        }});

        sut.beginSession(testEvent);
    }

    public void testBeginGroup() {
        checking(new Expectations() {{
            one (mockDelegate).beginGroup(testEvent);
        }});

        sut.beginGroup(testEvent);
    }

    public void testBeginFile() {
        checking(new Expectations() {{
            one (mockDelegate).beginFile(testEvent);
        }});

        sut.beginFile(testEvent);
    }

    public void testBeginClassfile() {
        checking(new Expectations() {{
            one (mockDelegate).beginClassfile(testEvent);
        }});

        sut.beginClassfile(testEvent);
    }

    public void testEndClassfile() {
        checking(new Expectations() {{
            one (mockDelegate).endClassfile(testEvent);
        }});

        sut.endClassfile(testEvent);
    }

    public void testEndFile() {
        checking(new Expectations() {{
            one (mockDelegate).endFile(testEvent);
        }});

        sut.endFile(testEvent);
    }

    public void testEndGroup() {
        checking(new Expectations() {{
            one (mockDelegate).endGroup(testEvent);
        }});

        sut.endGroup(testEvent);
    }

    public void testEndSession() {
        checking(new Expectations() {{
            one (mockDelegate).endSession(testEvent);
        }});

        sut.endSession(testEvent);
    }
}
