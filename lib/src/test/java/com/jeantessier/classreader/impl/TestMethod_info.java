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

package com.jeantessier.classreader.impl;

import org.jmock.*;
import org.jmock.imposters.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TestMethod_info {
    private static final int TEST_ACCESS_FLAG = 0x0008;
    private static final int TEST_NAME_INDEX = 123;
    private static final int TEST_SIGNATURE_INDEX = 456;
    private static final int TEST_NB_ATTRIBUTES = 0;

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private DataInput mockIn;
    private ConstantPool mockConstantPool;

    private Sequence dataReads;

    @BeforeEach
    void setUp() {
        mockIn = context.mock(DataInput.class);
        mockConstantPool = context.mock(ConstantPool.class);

        dataReads = context.sequence("dataReads");
    }

    @Test
    void testDeclarationForStaticInitializer() throws IOException {
        final Classfile mockClassfile = context.mock(Classfile.class);

        context.checking(new Expectations() {{
            allowing (mockClassfile).getConstantPool();
                will(returnValue(mockConstantPool));
        }});

        expectReadU2(TEST_ACCESS_FLAG);
        expectReadU2(TEST_NAME_INDEX);
        expectLookupUtf8(TEST_NAME_INDEX, "<clinit>", "name");
        expectReadU2(TEST_SIGNATURE_INDEX);
        expectLookupUtf8(TEST_SIGNATURE_INDEX, "()V", "signature");
        expectReadU2(TEST_NB_ATTRIBUTES);

        Method_info sut = new Method_info(mockClassfile, mockIn);
        assertThat("declaration", sut.getDeclaration(), is("static {}"));
    }

    @Test
    void testLocateMethodDeclarations_delegatesToClassfile() throws IOException {
        final Classfile mockClassfile = context.mock(Classfile.class);

        context.checking(new Expectations() {{
            allowing (mockClassfile).getConstantPool();
                will(returnValue(mockConstantPool));
        }});

        expectReadU2(TEST_ACCESS_FLAG);
        expectReadU2(TEST_NAME_INDEX);
        expectLookupUtf8(TEST_NAME_INDEX, "foo", "name");
        expectReadU2(TEST_SIGNATURE_INDEX);
        expectLookupUtf8(TEST_SIGNATURE_INDEX, "()V", "signature");
        expectReadU2(TEST_NB_ATTRIBUTES);

        Method_info sut = new Method_info(mockClassfile, mockIn);
        Collection<Method_info> expectedDeclarations = Collections.singleton(sut);

        context.checking(new Expectations() {{
            oneOf (mockClassfile).locateMethodDeclarations(with(any(Predicate.class)));
                will(returnValue(expectedDeclarations));
        }});

        assertThat("method expectedDeclarations", sut.locateMethodDeclarations(), is(expectedDeclarations));
    }

    protected void expectReadU2(final int i) throws IOException {
        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectLookupUtf8(int index, String value, String mockName) {
        expectLookupUtf8(index, value, context.mock(UTF8_info.class, mockName));
    }

    private void expectLookupUtf8(final int index, final String value, final UTF8_info mockUtf8_info) {
        context.checking(new Expectations() {{
            atLeast(1).of (mockConstantPool).get(index);
                will(returnValue(mockUtf8_info));
            atLeast(1).of (mockUtf8_info).getValue();
                will(returnValue(value));
        }});
    }
}
