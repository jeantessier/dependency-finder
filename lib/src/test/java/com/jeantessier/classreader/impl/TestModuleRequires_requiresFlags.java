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
public class TestModuleRequires_requiresFlags {
    @Parameters(name="Module requires with requires flags {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"TRANSTIVE", 0x0020, true, false, false, false},
                {"STATIC_PHASE", 0x0040, false, true, false, false},
                {"SYNTHETIC", 0x1000, false, false, true, false},
                {"MANDATED", 0x8000, false, false, false, true},
                {"all of them", 0x9060, true, true, true, true},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int requiresFlags;

    @Parameter(2)
    public boolean isTransitive;

    @Parameter(3)
    public boolean isStaticPhase;

    @Parameter(4)
    public boolean isSynthetic;

    @Parameter(5)
    public boolean isMandated;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private ModuleRequires sut;

    @Before
    public void setUp() throws IOException {
        final int requiresIndex = 123;
        final String requires = "abc";
        final int requiresVersionIndex = 465;
        final String requiresVersion = "blah";

        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final DataInput mockIn = context.mock(DataInput.class);
        final Module_info mockModule_info = context.mock(Module_info.class);
        final UTF8_info mockUtf8_info = context.mock(UTF8_info.class);

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
            allowing (mockConstantPool).get(requiresVersionIndex);
                will(returnValue(mockUtf8_info));
            allowing (mockUtf8_info).getValue();
                will(returnValue(requiresVersion));
        }});

        sut = new ModuleRequires(mockConstantPool, mockIn);
    }

    @Test
    public void testIsTransitive() {
        assertEquals(label, isTransitive, sut.isTransitive());
    }

    @Test
    public void testIsStaticPhase() {
        assertEquals(label, isStaticPhase, sut.isStaticPhase());
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
