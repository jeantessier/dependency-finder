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

public class TestMethod_info_NameAndType {
    private static final int ACCESS_FLAG = 0x0000;
    private static final int NAME_INDEX = 123;
    private static final int DESCRIPTOR_INDEX = 456;
    private static final int NB_ATTRIBUTES = 0;

    private static final String CLASS = "Abc";

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

    @DisplayName("Method_info")
    @ParameterizedTest(name="signature for {0} should be {3}")
    @MethodSource("dataProvider")
    void testGetSignature(String variation, String name, String descriptor, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, descriptor);
        assertEquals(expectedSignature, sut.getSignature());
    }

    @DisplayName("Method_info")
    @ParameterizedTest(name="full signature for {0} should be " + CLASS + ".{3}")
    @MethodSource("dataProvider")
    void testGetFullSignature(String variation, String name, String descriptor, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, descriptor);
        assertEquals(CLASS + "." + expectedSignature, sut.getFullSignature());
    }

    @DisplayName("Method_info")
    @ParameterizedTest(name="return type for {0} should be {4}")
    @MethodSource("dataProvider")
    void testGetReturnType(String variation, String name, String descriptor, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, descriptor);
        assertEquals(expectedReturnType, sut.getReturnType());
    }

    @DisplayName("Method_info")
    @ParameterizedTest(name="unique name for {0} should be {5}")
    @MethodSource("dataProvider")
    void testGetUniqueName(String variation, String name, String descriptor, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, descriptor);
        assertEquals(expectedUniqueName, sut.getUniqueName());
    }

    @DisplayName("Method_info")
    @ParameterizedTest(name="full unique name for {0} should be {6}")
    @MethodSource("dataProvider")
    void testGetFullUniqueName(String variation, String name, String descriptor, String expectedSignature, String expectedReturnType, String expectedUniqueName, String expectedFullUniqueName) throws IOException {
        var sut = createSut(name, descriptor);
        assertEquals(expectedFullUniqueName, sut.getFullUniqueName());
    }

    private Method_info createSut(String name, String descriptor) throws IOException {
        var mockClassfile = context.mock(Classfile.class);
        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);

        var mockName_info = context.mock(UTF8_info.class, "name_info");
        var mockDescriptor_info = context.mock(UTF8_info.class, "descriptor_info");

        var dataReads = context.sequence("dataReads");

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

        return new Method_info(mockClassfile, mockIn);
    }
}
