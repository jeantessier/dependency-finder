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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestInterfaceMethodRef_info_NameAndType {
    private static final int CLASS_INDEX = 2;
    private static final String CLASS = "abc";
    private static final int NAME_AND_TYPE_INDEX = 3;
    private static final String NAME = "def";

    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("void method", "(I)V", NAME + "(int)", "void", NAME + "(int): void", CLASS + "." + NAME + "(int): void"),
                arguments("regular method", "(Ljava/lang/String;)Ljava/lang/Object;", NAME + "(java.lang.String)", "java.lang.Object", NAME + "(java.lang.String): java.lang.Object", CLASS + "." + NAME + "(java.lang.String): java.lang.Object")
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("InterfaceMethodRef_info")
    @ParameterizedTest(name="class for {0} should be " + CLASS)
    @MethodSource("dataProvider")
    void testGetClass(String label, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(type);
        assertEquals(CLASS, sut.getClassName());
    }

    @DisplayName("InterfaceMethodRef_info")
    @ParameterizedTest(name="signature for {0} should be {2}")
    @MethodSource("dataProvider")
    void testGetSignature(String label, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(type);
        assertEquals(expectedSignature, sut.getSignature());
    }

    @DisplayName("InterfaceMethodRef_info")
    @ParameterizedTest(name="full signature for {0} should be " + CLASS + ".{2}")
    @MethodSource("dataProvider")
    void testGetFullSignature(String label, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(type);
        assertEquals(CLASS + "." + expectedSignature, sut.getFullSignature());
    }

    @DisplayName("InterfaceMethodRef_info")
    @ParameterizedTest(name="return type for {0} should be {3}")
    @MethodSource("dataProvider")
    void testGetReturnType(String label, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(type);
        assertEquals(expectedReturnType, sut.getReturnType());
    }

    @DisplayName("InterfaceMethodRef_info")
    @ParameterizedTest(name="unique name for {0} should be {4}")
    @MethodSource("dataProvider")
    void testGetUniqueName(String label, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(type);
        assertEquals(expectedUniqueName, sut.getUniqueName());
    }

    @DisplayName("InterfaceMethodRef_info")
    @ParameterizedTest(name="full unique name for {0} should be {5}")
    @MethodSource("dataProvider")
    void testGetFullUniqueName(String label, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(type);
        assertEquals(expectedFullUniqueName, sut.getFullUniqueName());
    }

    private InterfaceMethodRef_info createSut(String type) throws IOException {
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

        return new InterfaceMethodRef_info(mockConstantPool, mockIn);
    }
}
