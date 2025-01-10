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
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestModuleRequires_requiresFlags {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("TRANSTIVE", 0x0020, true, false, false, false),
                arguments("STATIC_PHASE", 0x0040, false, true, false, false),
                arguments("SYNTHETIC", 0x1000, false, false, true, false),
                arguments("MANDATED", 0x8000, false, false, false, true),
                arguments("all of them", 0x9060, true, true, true, true)
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("ModuleRequires")
    @ParameterizedTest(name="isTransitive for {0} should be {2}")
    @MethodSource("dataProvider")
    public void testIsTransitive(String variation, int requiresFlags, boolean isTransitive, boolean isStaticPhase, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(requiresFlags);
        assertEquals(isTransitive, sut.isTransitive());
    }

    @DisplayName("ModuleRequires")
    @ParameterizedTest(name="isStaticPhase for {0} should be {3}")
    @MethodSource("dataProvider")
    public void testIsStaticPhase(String variation, int requiresFlags, boolean isTransitive, boolean isStaticPhase, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(requiresFlags);
        assertEquals(isStaticPhase, sut.isStaticPhase());
    }

    @DisplayName("ModuleRequires")
    @ParameterizedTest(name="isSynthetic for {0} should be {4}")
    @MethodSource("dataProvider")
    public void testIsSynthetic(String variation, int requiresFlags, boolean isTransitive, boolean isStaticPhase, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(requiresFlags);
        assertEquals(isSynthetic, sut.isSynthetic());
    }

    @DisplayName("ModuleRequires")
    @ParameterizedTest(name="isMandated for {0} should be {5}")
    @MethodSource("dataProvider")
    public void testIsMandated(String variation, int requiresFlags, boolean isTransitive, boolean isStaticPhase, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(requiresFlags);
        assertEquals(isMandated, sut.isMandated());
    }

    private ModuleRequires createSut(int requiresFlags) throws IOException {
        var requiresIndex = 123;
        var requires = "abc";
        var requiresVersionIndex = 465;
        var requiresVersion = "blah";

        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);
        var mockModule_info = context.mock(Module_info.class);
        var mockUtf8_info = context.mock(UTF8_info.class);

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

        return new ModuleRequires(mockConstantPool, mockIn);
    }
}
