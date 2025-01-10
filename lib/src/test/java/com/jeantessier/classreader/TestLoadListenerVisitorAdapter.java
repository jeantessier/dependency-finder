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

public class TestLoadListenerVisitorAdapter extends MockObjectTestCase {
    @Test
    void testBeginSession() {
        final Visitor visitor = mock(Visitor.class);

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.beginSession(new LoadEvent(this, null, null, null));
    }

    @Test
    void testBeginGroup() {
        final Visitor visitor = mock(Visitor.class);

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.beginGroup(new LoadEvent(this, null, null, null));
    }

    @Test
    void testBeginFile() {
        final Visitor visitor = mock(Visitor.class);

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.beginFile(new LoadEvent(this, null, null, null));
    }

    @Test
    void testBeginClassfile() {
        final Visitor visitor = mock(Visitor.class);

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.beginClassfile(new LoadEvent(this, null, null, null));
    }

    @Test
    void testEndClassfile() {
        final Visitor visitor = mock(Visitor.class);
        final Classfile classfile = mock(Classfile.class);

        checking(new Expectations() {{
            oneOf (classfile).accept(visitor);
            ignoring (visitor);
        }});

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.endClassfile(new LoadEvent(this, null, null, classfile));
    }

    @Test
    void testEndFile() {
        final Visitor visitor = mock(Visitor.class);

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.endFile(new LoadEvent(this, null, null, null));
    }

    @Test
    void testEndGroup() {
        final Visitor visitor = mock(Visitor.class);

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.endGroup(new LoadEvent(this, null, null, null));
    }

    @Test
    void testEndSession() {
        final Visitor visitor = mock(Visitor.class);

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(visitor);
        adapter.endSession(new LoadEvent(this, null, null, null));
    }
}
