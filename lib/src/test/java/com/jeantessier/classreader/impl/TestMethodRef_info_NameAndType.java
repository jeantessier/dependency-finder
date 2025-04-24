/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

public class TestMethodRef_info_NameAndType {
    private static final int CLASS_INDEX = 2;
    private static final String CLASS = "Abc";
    private static final int NAME_AND_TYPE_INDEX = 3;

    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("void method", "foo", "(I)V", "foo(int)", "void", "foo(int): void", CLASS + ".foo(int): void"),
                arguments("regular method", "foo", "(Ljava/lang/String;)Ljava/lang/Object;", "foo(java.lang.String)", "java.lang.Object", "foo(java.lang.String): java.lang.Object", CLASS + ".foo(java.lang.String): java.lang.Object"),
                arguments("constructor", "<init>", "()V", CLASS + "()", "void", CLASS + "()", CLASS + "." + CLASS + "()"),
                arguments("static initializer", "<clinit>", "()V", "static {}", "void", "static {}", CLASS + ".static {}")
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("MethodRef_info")
    @ParameterizedTest(name="class for {0} should be " + CLASS)
    @MethodSource("dataProvider")
    void testGetClass(String variation, String name, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, type);
        assertEquals(CLASS, sut.getClassName());
    }

    @DisplayName("MethodRef_info")
    @ParameterizedTest(name="signature for {0} should be {3}")
    @MethodSource("dataProvider")
    void testGetSignature(String variation, String name, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, type);
        assertEquals(expectedSignature, sut.getSignature());
    }

    @DisplayName("MethodRef_info")
    @ParameterizedTest(name="full signature for {0} should be " + CLASS + ".{3}")
    @MethodSource("dataProvider")
    void testGetFullSignature(String variation, String name, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, type);
        assertEquals(CLASS + "." + expectedSignature, sut.getFullSignature());
    }

    @DisplayName("MethodRef_info")
    @ParameterizedTest(name="return type for {0} should be {4}")
    @MethodSource("dataProvider")
    void testGetReturnType(String variation, String name, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, type);
        assertEquals(expectedReturnType, sut.getReturnType());
    }

    @DisplayName("MethodRef_info")
    @ParameterizedTest(name="unique name for {0} should be {5}")
    @MethodSource("dataProvider")
    void testGetUniqueName(String variation, String name, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, type);
        assertEquals(expectedUniqueName, sut.getUniqueName());
    }

    @DisplayName("MethodRef_info")
    @ParameterizedTest(name="full unique name for {0} should be {6}")
    @MethodSource("dataProvider")
    void testGetFullUniqueName(String variation, String name, String type, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, type);
        assertEquals(expectedFullUniqueName, sut.getFullUniqueName());
    }

    private MethodRef_info createSut(String name, String type) throws IOException {
        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);

        var mockClass_info = context.mock(Class_info.class);
        var mockNameAndType_info = context.mock(NameAndType_info.class);

        var dataReads = context.sequence("dataReads");

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
                will(returnValue(name));
            allowing (mockNameAndType_info).getType();
                will(returnValue(type));
        }});

        return new MethodRef_info(mockConstantPool, mockIn);
    }
}
