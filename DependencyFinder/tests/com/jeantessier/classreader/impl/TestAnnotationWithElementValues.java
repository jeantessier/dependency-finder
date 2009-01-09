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

package com.jeantessier.classreader.impl;

import java.io.*;

import org.jmock.*;

public class TestAnnotationWithElementValues extends TestAnnotationsBase {
    private static final int TYPE_INDEX = 2;
    private static final String TYPE = "Labc;";

    public void testConstructorWithNoElementValuePairs() throws Exception {
        doTestConstructorWithElementValuePairs(0);
    }

    public void testConstructorWithASingleElementValuePair() throws Exception {
        doTestConstructorWithElementValuePairs(1);
    }

    public void testConstructorWithMultipleElementValuePairs() throws Exception {
        doTestConstructorWithElementValuePairs(2);
    }

    private void doTestConstructorWithElementValuePairs(final int numElementValuePairs) throws IOException {
        final UTF8_info mockUtf8_info = mock(UTF8_info.class, "element name");

        expectReadTypeIndex(TYPE_INDEX);
        expectReadNumElementValuePairs(numElementValuePairs);
        expectLookupUtf8(TYPE_INDEX, TYPE);

        checking(new Expectations() {{
            for (int i = 0; i < numElementValuePairs; i++) {
                one (mockIn).readUnsignedShort();
                    inSequence(dataReads);
                    will(returnValue(i + 1));
                one (mockConstantPool).get(i + 1);
                    will(returnValue(mockUtf8_info));
                one (mockElementValueFactory).create(mockConstantPool, mockIn);
                    inSequence(dataReads);
            }
            exactly(numElementValuePairs).of (mockUtf8_info).getValue();
        }});

        Annotation sut = new Annotation(mockConstantPool, mockIn, mockElementValueFactory);
        assertEquals("Num element value pairs", numElementValuePairs, sut.getElementValuePairs().size());
    }
}