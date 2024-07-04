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
import org.jmock.integration.junit4.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestMethod_info_NameAndType {
    private static final int ACCESS_FLAG = 0x0000;
    private static final int NAME_INDEX = 123;
    private static final int DESCRIPTOR_INDEX = 456;
    private static final int NB_ATTRIBUTES = 0;

    private static final String CLASS = "Abc";

    @Parameters(name="MethodRef_info to {0} " + CLASS + ".{1} with descriptor \"{2}\"")
    public static Object[][] data() {
        return new Object[][] {
                {"void method", "foo", "(I)V", "foo(int)", "void", CLASS + ".foo(int): void"},
                {"regular method", "foo", "(Ljava/lang/String;)Ljava/lang/Object;", "foo(java.lang.String)", "java.lang.Object", CLASS + ".foo(java.lang.String): java.lang.Object"},
                {"constructor", "<init>", "()V", CLASS + "()", "void", CLASS + "." + CLASS + "()"},
                {"static initializer", "<clinit>", "()V", "static {}", "void", CLASS + ".static {}"},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public String name;

    @Parameter(2)
    public String descriptor;

    @Parameter(3)
    public String expectedSignature;

    @Parameter(4)
    public String expectedReturnType;

    @Parameter(5)
    public String expectedFullUniqueName;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private Method_info sut;

    @Before
    public void setUp() throws IOException {
        final Classfile mockClassfile = context.mock(Classfile.class);
        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final DataInput mockIn = context.mock(DataInput.class);

        final UTF8_info mockName_info = context.mock(UTF8_info.class, "name_info");
        final UTF8_info mockDescriptor_info = context.mock(UTF8_info.class, "descriptor_info");

        final Sequence dataReads = context.sequence("dataReads");

        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(ACCESS_FLAG));
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(NAME_INDEX));
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(DESCRIPTOR_INDEX));
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(NB_ATTRIBUTES));

            allowing (mockClassfile).getConstantPool();
                will(returnValue(mockConstantPool));
            allowing (mockClassfile).getClassName();
                will(returnValue(CLASS));
            allowing (mockClassfile).getSimpleName();
                will(returnValue(CLASS));

            allowing (mockConstantPool).get(NAME_INDEX);
                will(returnValue(mockName_info));
            allowing (mockName_info).getValue();
                will(returnValue(name));

            allowing (mockConstantPool).get(DESCRIPTOR_INDEX);
                will(returnValue(mockDescriptor_info));
            allowing (mockDescriptor_info).getValue();
                will(returnValue(descriptor));
        }});

        sut = new Method_info(mockClassfile, mockIn);
    }

    @Test
    public void testGetSignature() {
        assertEquals(expectedSignature, sut.getSignature());
    }

    @Test
    public void testGetFullSignature() {
        assertEquals(CLASS + "." + expectedSignature, sut.getFullSignature());
    }

    @Test
    public void testGetReturnType() {
        assertEquals(expectedReturnType, sut.getReturnType());
    }

    @Test
    public void testGetFullUniqueName() {
        assertEquals(expectedFullUniqueName, sut.getFullUniqueName());
    }
}
