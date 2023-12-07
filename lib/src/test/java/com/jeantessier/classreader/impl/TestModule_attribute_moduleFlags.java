/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import com.jeantessier.classreader.Visitable;
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
public class TestModule_attribute_moduleFlags {
    @Parameters(name="Module with module flags {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"OPEN", 0x0020, true, false, false},
                {"SYNTHETIC", 0x1000, false, true, false},
                {"MANDATED", 0x8000, false, false, true},
                {"all of them", 0x9020, true, true, true},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int moduleFlags;

    @Parameter(2)
    public boolean isOpen;

    @Parameter(3)
    public boolean isSynthetic;

    @Parameter(4)
    public boolean isMandated;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private Module_attribute sut;

    @Before
    public void setUp() throws IOException {
        final int moduleNameIndex = 123;
        final String moduleName = "abc";
        final int moduleVersionIndex = 465;
        final String moduleVersion = "blah";

        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final Visitable mockOwner = context.mock(Visitable.class);
        final DataInput mockIn = context.mock(DataInput.class);
        final Module_info mockModule_info = context.mock(Module_info.class);
        final UTF8_info mockUtf8_info = context.mock(UTF8_info.class);

        context.checking(new Expectations() {{
            oneOf (mockIn).readInt();
                will(returnValue(16));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(moduleNameIndex));
            allowing (mockConstantPool).get(moduleNameIndex);
                will(returnValue(mockModule_info));
            allowing (mockModule_info).getName();
                will(returnValue(moduleName));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(moduleFlags));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(moduleVersionIndex));
            allowing (mockConstantPool).get(moduleVersionIndex);
                will(returnValue(mockUtf8_info));
            allowing (mockUtf8_info).getValue();
                will(returnValue(moduleVersion));

            exactly(5).of (mockIn).readUnsignedShort();
                will(returnValue(0));
        }});

        sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);
    }

    @Test
    public void testIsOpen() {
        assertEquals(label, isOpen, sut.isOpen());
    }

    @Test
    public void testIsSynthetic() {
        assertEquals(label, isSynthetic, sut.isSynthetic());
    }

    @Test
    public void testIsMandated() {
        assertEquals(label, isMandated, sut.isMandated());
    }
}
