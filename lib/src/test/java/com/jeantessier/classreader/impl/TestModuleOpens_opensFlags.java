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

public class TestModuleOpens_opensFlags {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("SYNTHETIC", 0x1000, true, false),
                arguments("MANDATED", 0x8000, false, true),
                arguments("all of them", 0x9060, true, true)
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("ModuleOpens")
    @ParameterizedTest(name="isSynthetic for {0} should be {2}")
    @MethodSource("dataProvider")
    public void testIsSynthetic(String variation, int opensFlags, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(opensFlags);
        assertEquals(isSynthetic, sut.isSynthetic());
    }

    @DisplayName("ModuleOpens")
    @ParameterizedTest(name="isMandated for {0} should be {3}")
    @MethodSource("dataProvider")
    public void testIsMandated(String variation, int opensFlags, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(opensFlags);
        assertEquals(isMandated, sut.isMandated());
    }

    private ModuleOpens createSut(int opensFlags) throws IOException {
        var opensIndex = 123;
        var opens = "abc";

        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);
        var mockPackage_info = context.mock(Package_info.class);

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

        return new ModuleOpens(mockConstantPool, mockIn);
    }
}
