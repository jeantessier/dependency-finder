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

import com.jeantessier.classreader.Visitable;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestModule_attribute_moduleVersion {
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

    @DisplayName("Module_attribute")
    @ParameterizedTest(name="moduleVersionIndex for {0} should be {1}")
    @MethodSource("dataProvider")
    public void testGetModuleVersionIndex(String variation, int moduleVersionIndex, boolean hasModuleVersion, String moduleVersion) throws IOException {
        var sut = createSut(moduleVersionIndex, moduleVersion);
        assertEquals(moduleVersionIndex, sut.getModuleVersionIndex());
    }

    @DisplayName("Module_attribute")
    @ParameterizedTest(name="hasModuleVersion for {0} should be {2}")
    @MethodSource("dataProvider")
    public void testHasModuleVersion(String variation, int moduleVersionIndex, boolean hasModuleVersion, String moduleVersion) throws IOException {
        var sut = createSut(moduleVersionIndex, moduleVersion);
        assertEquals(hasModuleVersion, sut.hasModuleVersion());
    }

    @DisplayName("Module_attribute")
    @ParameterizedTest(name="raw moduleVersion for {0} should be present if {2}")
    @MethodSource("dataProvider")
    public void testGetRawModuleVersion(String variation, int moduleVersionIndex, boolean hasModuleVersion, String moduleVersion) throws IOException {
        var sut = createSut(moduleVersionIndex, moduleVersion);

        var actualRawModuleVersion = Optional.ofNullable(sut.getRawModuleVersion());
        assertEquals(hasModuleVersion, actualRawModuleVersion.isPresent());
    }

    @DisplayName("Module_attribute")
    @ParameterizedTest(name="moduleVersion for {0} should be {3}")
    @MethodSource("dataProvider")
    public void testGetModuleVersion(String variation, int moduleVersionIndex, boolean hasModuleVersion, String moduleVersion) throws IOException {
        var sut = createSut(moduleVersionIndex, moduleVersion);
        assertEquals(moduleVersion, sut.getModuleVersion());
    }

    private Module_attribute createSut(int moduleVersionIndex, String moduleVersion) throws IOException {
        final int moduleNameIndex = 123;
        final String moduleName = "abc";
        final int moduleFlags = 456;

        final ConstantPool mockConstantPool = context.mock(ConstantPool.class);
        final Visitable mockOwner = context.mock(Visitable.class);
        final DataInput mockIn = context.mock(DataInput.class);
        final Module_info mockModule_info = context.mock(Module_info.class);

        var mockUtf8_info = moduleVersionIndex != 0 ? context.mock(UTF8_info.class) : null;

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
            if (mockUtf8_info != null) {
                allowing (mockConstantPool).get(moduleVersionIndex);
                    will(returnValue(mockUtf8_info));
                allowing (mockUtf8_info).getValue();
                    will(returnValue(moduleVersion));
            }

            exactly(5).of (mockIn).readUnsignedShort();
                will(returnValue(0));
        }});

        return new Module_attribute(mockConstantPool, mockOwner, mockIn);
    }
}
