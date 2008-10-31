/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

package com.jeantessier.classreader.impl;

import java.io.*;

import org.jmock.*;
import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;

import com.jeantessier.classreader.*;

public class TestAttributeBase extends MockObjectTestCase {
    protected Classfile mockClassfile;

    protected Visitable mockOwner;

    protected DataInput mockIn;

    protected Sequence dataReads;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        mockClassfile = mock(Classfile.class);
        mockOwner = mock(Visitable.class);
        mockIn = mock(DataInput.class);

        dataReads = sequence("dataReads");

    }

    protected void expectReadAttributeLength(final int length) throws IOException {
        expectReadU4(length);
    }

    protected void expectReadAnnotation(int typeIndex, int numElementValuePairs) throws IOException {
        expectReadTypeIndex(typeIndex);
        expectReadNumElementValuePairs(numElementValuePairs);
    }

    protected void expectReadTypeIndex(int typeIndex) throws IOException {
        expectReadU2(typeIndex);
    }

    protected void expectReadNumElementValuePairs(int numElementValuePairs) throws IOException {
        expectReadU2(numElementValuePairs);
    }

    protected void expectReadU1(final int i) throws IOException {
        checking(new Expectations() {{
            one (mockIn).readUnsignedByte();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectReadU2(final int i) throws IOException {
        checking(new Expectations() {{
            one (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectReadU4(final int i) throws IOException {
        checking(new Expectations() {{
            one (mockIn).readInt();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectReadUtf(final String s) throws IOException {
        checking(new Expectations() {{
            one (mockIn).readUTF();
                inSequence(dataReads);
                will(returnValue(s));
        }});
    }
}
