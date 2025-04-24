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
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestModuleRequires_requiresVersion {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("without version", 0, false, null),
                arguments("with version", 789, true, "version information")
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("MethodRequires")
    @ParameterizedTest(name="requiresVersionIndex for {0} should be {1}")
    @MethodSource("dataProvider")
    public void testGetRequiresVersionIndex(String variation, int requiresVersionIndex, boolean hasRequiresVersion, String requiresVersion) throws IOException {
        var sut = createSut(requiresVersionIndex, requiresVersion);
        assertEquals(requiresVersionIndex, sut.getRequiresVersionIndex());
    }

    @DisplayName("MethodRequires")
    @ParameterizedTest(name="hasRequiresVersion for {0} should be {2}")
    @MethodSource("dataProvider")
    public void testHasRequiresVersion(String variation, int requiresVersionIndex, boolean hasRequiresVersion, String requiresVersion) throws IOException {
        var sut = createSut(requiresVersionIndex, requiresVersion);
        assertEquals(hasRequiresVersion, sut.hasRequiresVersion());
    }

    @DisplayName("MethodRequires")
    @ParameterizedTest(name="raw requiresVersion for {0} should be present if {2}")
    @MethodSource("dataProvider")
    public void testGetRawRequiresVersion(String variation, int requiresVersionIndex, boolean hasRequiresVersion, String requiresVersion) throws IOException {
        var sut = createSut(requiresVersionIndex, requiresVersion);

        var actualRawRequiresVersion = Optional.ofNullable(sut.getRawRequiresVersion());
        assertEquals(hasRequiresVersion, actualRawRequiresVersion.isPresent());
    }

    @DisplayName("MethodRequires")
    @ParameterizedTest(name="requiresVersion for {0} should be {3}")
    @MethodSource("dataProvider")
    public void testGetRequiresVersion(String variation, int requiresVersionIndex, boolean hasRequiresVersion, String requiresVersion) throws IOException {
        var sut = createSut(requiresVersionIndex, requiresVersion);
        assertEquals(requiresVersion, sut.getRequiresVersion());
    }

    private ModuleRequires createSut(int requiresVersionIndex, String requiresVersion) throws IOException {
        var requiresIndex = 123;
        var requires = "abc";
        var requiresFlags = 456;

        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);
        var mockModule_info = context.mock(Module_info.class);

        var mockUtf8_info = requiresVersionIndex != 0 ? context.mock(UTF8_info.class) : null;

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

        return new ModuleRequires(mockConstantPool, mockIn);
    }
}
