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
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestBootstrapMethodWithMultipleArguments extends TestAttributeBase {
    private static final int BOOTSTRAP_METHOD_REF = 123;
    private static final int FIRST_ARGUMENT_INDEX = 4;
    private static final int FIRST_ARGUMENT_VALUE = 1234;
    private static final int SECOND_ARGUMENT_INDEX = 12;
    private static final String SECOND_ARGUMENT_VALUE = "abc def";
    private static final int THIRD_ARGUMENT_INDEX = 42;
    private static final String THIRD_ARGUMENT_VALUE = "Abc";

    private Integer_info firstArgument;
    private String_info secondArgument;
    private Class_info thirdArgument;

    private BootstrapMethod sut;

    @BeforeEach
    void setUp() throws Exception {
        var mockBootstrapMethods = mock(BootstrapMethods_attribute.class);
        var mockBootstrapMethod = mock(MethodHandle_info.class);

        checking(new Expectations() {{
            allowing (mockBootstrapMethods).getConstantPool();
                will(returnValue(mockConstantPool));
            allowing (mockConstantPool).get(BOOTSTRAP_METHOD_REF);
                will(returnValue(mockBootstrapMethod));
        }});

        expectReadU2(BOOTSTRAP_METHOD_REF);
        expectReadNumArguments(3);

        firstArgument = mock(Integer_info.class, "first argument");
        expectReadU2(FIRST_ARGUMENT_INDEX);
        checking(new Expectations() {{
            atLeast (1).of (mockConstantPool).get(FIRST_ARGUMENT_INDEX);
                will(returnValue(firstArgument));
        }});

        secondArgument = mock(String_info.class, "second argument");
        expectReadU2(SECOND_ARGUMENT_INDEX);
        checking(new Expectations() {{
            atLeast (1).of (mockConstantPool).get(SECOND_ARGUMENT_INDEX);
                will(returnValue(secondArgument));
        }});

        thirdArgument = mock(Class_info.class, "third argument");
        expectReadU2(THIRD_ARGUMENT_INDEX);
        checking(new Expectations() {{
            atLeast (1).of (mockConstantPool).get(THIRD_ARGUMENT_INDEX);
                will(returnValue(thirdArgument));
        }});

        sut = new BootstrapMethod(mockBootstrapMethods, mockIn);
    }

    @Test
    void testNumArguments() {
        assertEquals(3, sut.getArguments().size(), "num arguments");
    }

    @Test
    void testIntegerArguments() {
        checking(new Expectations() {{
            oneOf (firstArgument).getValue();
                will(returnValue(FIRST_ARGUMENT_VALUE));
        }});

        int actualArgument = sut.getArguments().stream()
                .filter(argument -> argument instanceof Integer_info)
                .map(argument -> (Integer_info) argument)
                .map(Integer_info::getValue)
                .findFirst()
                .orElseThrow();
        assertEquals(FIRST_ARGUMENT_VALUE, actualArgument, "first argument should be an int");
    }

    @Test
    void testStringArguments() {
        checking(new Expectations() {{
            oneOf (secondArgument).getValue();
                will(returnValue(SECOND_ARGUMENT_VALUE));
        }});

        String actualArgument = sut.getArguments().stream()
                .filter(argument -> argument instanceof String_info)
                .map(argument -> (String_info) argument)
                .map(String_info::getValue)
                .findFirst()
                .orElseThrow();
        assertEquals(SECOND_ARGUMENT_VALUE, actualArgument, "second argument should be a String");
    }

    @Test
    void testClassArguments() {
        checking(new Expectations() {{
            oneOf (thirdArgument).getName();
                will(returnValue(THIRD_ARGUMENT_VALUE));
        }});

        String actualArgument = sut.getArguments().stream()
                .filter(argument -> argument instanceof Class_info)
                .map(argument -> (Class_info) argument)
                .map(Class_info::getName)
                .findFirst()
                .orElseThrow();
        assertEquals(THIRD_ARGUMENT_VALUE, actualArgument, "third argument should be a class");
    }
}
