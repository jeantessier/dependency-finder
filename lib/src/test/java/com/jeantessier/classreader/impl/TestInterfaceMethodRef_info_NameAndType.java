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
public class TestInterfaceMethodRef_info_NameAndType {
    private static final int CLASS_INDEX = 2;
    private static final String CLASS = "abc";
    private static final int NAME_AND_TYPE_INDEX = 3;
    private static final String NAME = "def";

    @Parameters(name="MethodRef_info to {0} " + CLASS + ".{1} with type \"{2}\"")
    public static Object[][] data() {
        return new Object[][] {
                {"void method", "(I)V", NAME + "(int)", "void", CLASS + "." + NAME + "(int): void"},
                {"regular method", "(Ljava/lang/String;)Ljava/lang/Object;", NAME + "(java.lang.String)", "java.lang.Object", CLASS + "." + NAME + "(java.lang.String): java.lang.Object"},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public String type;

    @Parameter(2)
    public String expectedSignature;

    @Parameter(3)
    public String expectedReturnType;

    @Parameter(4)
    public String expectedFullUniqueName;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private InterfaceMethodRef_info sut;

    @Before
    public void setUp() throws IOException {
        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final DataInput mockIn = context.mock(DataInput.class);

        final Class_info mockClass_info = context.mock(Class_info.class);
        final NameAndType_info mockNameAndType_info = context.mock(NameAndType_info.class);

        final Sequence dataReads = context.sequence("dataReads");

        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(CLASS_INDEX));
            oneOf (mockIn).readUnsignedShort();
                inSequence(dataReads);
                will(returnValue(NAME_AND_TYPE_INDEX));

            allowing (mockConstantPool).get(CLASS_INDEX);
                will(returnValue(mockClass_info));
            allowing (mockClass_info).getName();
                will(returnValue(CLASS));
            allowing (mockClass_info).getSimpleName();
                will(returnValue(CLASS));

            allowing (mockConstantPool).get(NAME_AND_TYPE_INDEX);
                will(returnValue(mockNameAndType_info));
            allowing (mockNameAndType_info).getName();
                will(returnValue(NAME));
            allowing (mockNameAndType_info).getType();
                will(returnValue(type));
        }});

        sut = new InterfaceMethodRef_info(mockConstantPool, mockIn);
    }

    @Test
    public void testGetClass() {
        assertEquals(CLASS, sut.getClassName());
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
