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
public class TestModuleOpens_opensFlags {
    @Parameters(name="Module opens with opens flags {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"SYNTHETIC", 0x1000, true, false},
                {"MANDATED", 0x8000, false, true},
                {"all of them", 0x9060, true, true},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int opensFlags;

    @Parameter(2)
    public boolean isSynthetic;

    @Parameter(3)
    public boolean isMandated;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private ModuleOpens sut;

    @Before
    public void setUp() throws IOException {
        final int opensIndex = 123;
        final String opens = "abc";

        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final DataInput mockIn = context.mock(DataInput.class);
        final Package_info mockPackage_info = context.mock(Package_info.class);

        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedShort();
                will(returnValue(opensIndex));
            allowing (mockConstantPool).get(opensIndex);
                will(returnValue(mockPackage_info));
            allowing (mockPackage_info).getName();
                will(returnValue(opens));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(opensFlags));

            oneOf (mockIn).readUnsignedShort();
                will(returnValue(0));
        }});

        sut = new ModuleOpens(mockConstantPool, mockIn);
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
