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

public class TestBootstrapMethodWithOneArgument {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("an integer", 123, 1, com.jeantessier.classreader.Integer_info.class),
                arguments("a float", 234, 2, com.jeantessier.classreader.Float_info.class),
                arguments("a long", 345, 3, com.jeantessier.classreader.Long_info.class),
                arguments("a double", 456, 4, com.jeantessier.classreader.Double_info.class),
                arguments("a Class", 567, 5, com.jeantessier.classreader.Class_info.class),
                arguments("a String", 678, 6, com.jeantessier.classreader.String_info.class),
                arguments("a MethodHandle", 789, 7, com.jeantessier.classreader.MethodHandle_info.class),
                arguments("a MethodType", 890, 8, com.jeantessier.classreader.MethodType_info.class),
                arguments("a Dynamic", 909, 9, com.jeantessier.classreader.Dynamic_info.class)
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("getArguments")
    @ParameterizedTest(name="with {0} argument")
    @MethodSource("dataProvider")
    void testGetArguments(String variation, int bootstrapMethodRef, int argumentIndex, Class<? extends ConstantPoolEntry> argumentClass) throws Exception {
        // Given
        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);
        com.jeantessier.classreader.ConstantPoolEntry mockArgument = context.mock(argumentClass);

        // and
        var mockBootstrapMethods = context.mock(BootstrapMethods_attribute.class);
        var mockBootstrapMethod = context.mock(MethodHandle_info.class, "bootstrap method");

        // and
        context.checking(new Expectations() {{
            allowing (mockBootstrapMethods).getConstantPool();
                will(returnValue(mockConstantPool));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(bootstrapMethodRef));
            // num arguments
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(1));
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(argumentIndex));
            // Lookup during construction
            oneOf (mockConstantPool).get(bootstrapMethodRef);
                will(returnValue(mockBootstrapMethod));
            atLeast(1).of (mockConstantPool).get(argumentIndex);
                will(returnValue(mockArgument));
        }});

        // When
        var sut = new BootstrapMethod(mockBootstrapMethods, mockIn);

        // Then
        assertEquals(1, sut.getArguments().size(),"num arguments");
        assertSame(mockArgument, sut.getArguments().stream().findFirst().orElseThrow(), "argument");
    }
}
