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
public class TestModuleRequires_requiresVersion {
    @Parameters(name="Module requires {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"without version", 0, false, null},
                {"with version", 789, true, "version information"},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int requiresVersionIndex;

    @Parameter(2)
    public boolean hasRequiresVersion;

    @Parameter(3)
    public String requiresVersion;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private UTF8_info mockUtf8_info;

    private ModuleRequires sut;

    @Before
    public void setUp() throws IOException {
        context.setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        final int requiresIndex = 123;
        final String requires = "abc";
        final int requiresFlags = 456;

        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final DataInput mockIn = context.mock(DataInput.class);
        final Module_info mockModule_info = context.mock(Module_info.class);

        mockUtf8_info = requiresVersionIndex != 0 ? context.mock(UTF8_info.class) : null;

        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(requiresIndex));
            allowing (mockConstantPool).get(requiresIndex);
                will(returnValue(mockModule_info));
            allowing (mockModule_info).getName();
                will(returnValue(requires));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(requiresFlags));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(requiresVersionIndex));
            if (requiresVersionIndex != 0) {
                allowing (mockConstantPool).get(requiresVersionIndex);
                    will(returnValue(mockUtf8_info));
                allowing (mockUtf8_info).getValue();
                    will(returnValue(requiresVersion));
            }
        }});

        sut = new ModuleRequires(mockConstantPool, mockIn);
    }

    @Test
    public void testGetRequiresVersionIndex() {
        assertEquals(label, requiresVersionIndex, sut.getRequiresVersionIndex());
    }

    @Test
    public void testHasRequiresVersion() {
        assertEquals(label, hasRequiresVersion, sut.hasRequiresVersion());
    }

    @Test
    public void testGetRawRequiresVersion() {
        assertEquals(label, mockUtf8_info, sut.getRawRequiresVersion());
    }

    @Test
    public void testGetRequiresVersion() {
        assertEquals(label, requiresVersion, sut.getRequiresVersion());
    }
}
