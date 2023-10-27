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
import org.jmock.Expectations;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Parameterized;

import java.io.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestModule_attribute_moduleVersion {
    @Parameters(name="Module {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"without version", 0, null},
                {"with version", 789, "version information"},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int moduleVersionIndex;

    @Parameter(2)
    public String moduleVersion;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private UTF8_info mockUtf8_info;

    private Module_attribute sut;

    @Before
    public void setUp() throws IOException {
        context.setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        final int moduleNameIndex = 123;
        final String moduleName = "abc";
        final int moduleFlags = 456;

        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final Visitable mockOwner = context.mock(Visitable.class);
        final DataInput mockIn = context.mock(DataInput.class);
        final Module_info mockModule_info = context.mock(Module_info.class);

        mockUtf8_info = moduleVersionIndex != 0 ? context.mock(UTF8_info.class) : null;

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
            if (moduleVersionIndex != 0) {
                allowing (mockConstantPool).get(moduleVersionIndex);
                    will(returnValue(mockUtf8_info));
                allowing (mockUtf8_info).getValue();
                    will(returnValue(moduleVersion));
            }

            exactly(5).of (mockIn).readUnsignedShort();
                will(returnValue(0));
        }});

        sut = new Module_attribute(mockConstantPool, mockOwner, mockIn);
    }

    @Test
    public void testGetModuleVersionIndex() {
        assertEquals(label, moduleVersionIndex, sut.getModuleVersionIndex());
    }

    @Test
    public void testGetRawModuleVersion() {
        assertEquals(label, mockUtf8_info, sut.getRawModuleVersion());
    }

    @Test
    public void testGetModuleVersion() {
        assertEquals(label, moduleVersion, sut.getModuleVersion());
    }
}
