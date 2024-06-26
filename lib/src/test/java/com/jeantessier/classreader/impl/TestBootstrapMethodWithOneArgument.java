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
public class TestBootstrapMethodWithOneArgument {
    @Parameters(name="BootstrapMethod with {0} argument")
    public static Object[][] data() {
        return new Object[][] {
                {"an integer", 123, 1, com.jeantessier.classreader.Integer_info.class},
                {"a float", 234, 2, com.jeantessier.classreader.Float_info.class},
                {"a long", 345, 3, com.jeantessier.classreader.Long_info.class},
                {"a double", 456, 4, com.jeantessier.classreader.Double_info.class},
                {"a Class", 567, 5, com.jeantessier.classreader.Class_info.class},
                {"a String", 678, 6, com.jeantessier.classreader.String_info.class},
                {"a MethodHandle", 789, 7, com.jeantessier.classreader.MethodHandle_info.class},
                {"a MethodType", 890, 8, com.jeantessier.classreader.MethodType_info.class},
                {"a Dynamic", 909, 9, com.jeantessier.classreader.Dynamic_info.class},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int bootstrapMethodRef;

    @Parameter(2)
    public int argumentIndex;

    @Parameter(3)
    public Class<? extends ConstantPoolEntry> argumentClass;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private ConstantPool mockConstantPool;
    private DataInput mockIn;
    private com.jeantessier.classreader.ConstantPoolEntry mockArgument;

    private BootstrapMethods_attribute mockBootstrapMethods;

    private BootstrapMethod sut;

    @Before
    public void setUp() throws IOException {
        mockConstantPool = context.mock(ConstantPool.class);
        mockIn = context.mock(DataInput.class);
        mockArgument = context.mock(argumentClass);

        var mockBootstrapMethods = context.mock(BootstrapMethods_attribute.class);
        var mockBootstrapMethod = context.mock(MethodHandle_info.class, "bootstrap method");

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

        sut = new BootstrapMethod(mockBootstrapMethods, mockIn);
    }

    @Test
    public void testGetArguments() {
        assertEquals("num arguments", 1, sut.getArguments().size());
        assertSame("argument", mockArgument, sut.getArguments().stream().findFirst().orElseThrow());
    }
}
