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

import static org.hamcrest.Matchers.*;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import org.jmock.lib.legacy.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;

@RunWith(JMock.class)
public class TestMethod_info {
    private static final int TEST_ACCESS_FLAG = 0x0008;
    private static final int TEST_NAME_INDEX = 123;
    private static final String TEST_NAME_VALUE = "testMethod";
    private static final int TEST_SIGNATURE_INDEX = 456;
    private static final String TEST_SIGNATURE_VALUE = "(ILjava/util/List;)I";
    private static final int NO_ATTRIBUTES = 0;
    private static final int ONE_ATTRIBUTE = 1;
    private static final int TEST_SIGNATURE_ATTRIBUTE_TYPE_INDEX = 789;
    private static final int TEST_SIGNATURE_ATTRIBUTE_LENGTH = 2;
    private static final int TEST_SIGNATURE_ATTRIBUTE_INDEX = 987;

    private Mockery context;

    private Classfile mockClassfile;
    private ConstantPool mockConstantPool;
    private DataInput mockIn;

    private Sequence dataReads;

    @Before
    public void setUp() {
        context = new Mockery();
        context.setImposteriser(ClassImposteriser.INSTANCE);

        mockClassfile = context.mock(Classfile.class);
        mockConstantPool = context.mock(ConstantPool.class);
        mockIn = context.mock(DataInput.class);

        dataReads = context.sequence("dataReads");

        context.checking(new Expectations() {{
            allowing (mockClassfile).getConstantPool();
                will(returnValue(mockConstantPool));
        }});
    }

    @Test
    public void testGetSignature_FromDescriptor() throws IOException {
        expectReadU2(TEST_ACCESS_FLAG);
        expectReadU2(TEST_NAME_INDEX);
        expectLookupUtf8(TEST_NAME_INDEX, TEST_NAME_VALUE, "name");
        expectReadU2(TEST_SIGNATURE_INDEX);
        expectLookupUtf8(TEST_SIGNATURE_INDEX, TEST_SIGNATURE_VALUE, "signature");
        expectReadU2(NO_ATTRIBUTES);

        Method_info sut = new Method_info(mockClassfile, mockIn);
        assertThat("signature", sut.getSignature(), is(TEST_NAME_VALUE + "(int, java.util.List)"));
    }

    @Test
    public void testGetSignature_FromSignatureAttribute() throws IOException {
        expectReadU2(TEST_ACCESS_FLAG);
        expectReadU2(TEST_NAME_INDEX);
        expectLookupUtf8(TEST_NAME_INDEX, TEST_NAME_VALUE, "name");
        expectReadU2(TEST_SIGNATURE_INDEX);
        expectLookupUtf8(TEST_SIGNATURE_INDEX, TEST_SIGNATURE_VALUE, "signature");
        expectReadU2(ONE_ATTRIBUTE);
        expectReadU2(TEST_SIGNATURE_ATTRIBUTE_TYPE_INDEX);
        expectLookupUtf8(TEST_SIGNATURE_ATTRIBUTE_TYPE_INDEX, "Signature", "Signature_attribute");
        expectReadU4(TEST_SIGNATURE_ATTRIBUTE_LENGTH);
        expectReadU2(TEST_SIGNATURE_ATTRIBUTE_INDEX);
        expectLookupUtf8(TEST_SIGNATURE_ATTRIBUTE_INDEX, "(ILjava/util/List<Ljava/lang/String;>;)I", "Generic signature");

        Method_info sut = new Method_info(mockClassfile, mockIn);
        assertThat("signature", sut.getSignature(), is(TEST_NAME_VALUE + "(int, java.util.List<java.lang.String>)"));
    }

    @Test
    public void testDeclarationForStaticInitializer() throws IOException {
        expectReadU2(TEST_ACCESS_FLAG);
        expectReadU2(TEST_NAME_INDEX);
        expectLookupUtf8(TEST_NAME_INDEX, "<clinit>", "name");
        expectReadU2(TEST_SIGNATURE_INDEX);
        expectLookupUtf8(TEST_SIGNATURE_INDEX, TEST_SIGNATURE_VALUE, "signature");
        expectReadU2(NO_ATTRIBUTES);

        Method_info sut = new Method_info(mockClassfile, mockIn);
        assertThat("declaration", sut.getDeclaration(), is("static {}"));
    }

    protected void expectReadU2(final int i) throws IOException {
        context.checking(new Expectations() {{
            one (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(i));
        }});
    }

    protected void expectReadU4(final int i) throws IOException {
        context.checking(new Expectations() {{
            one (mockIn).readInt();
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
