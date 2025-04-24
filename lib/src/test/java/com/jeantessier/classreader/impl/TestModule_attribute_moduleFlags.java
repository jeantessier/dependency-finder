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

import com.jeantessier.classreader.Visitable;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

public class TestModule_attribute_moduleFlags {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("OPEN", 0x0020, true, false, false),
                arguments("SYNTHETIC", 0x1000, false, true, false),
                arguments("MANDATED", 0x8000, false, false, true),
                arguments("all of them", 0x9020, true, true, true)
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("Module_attribute")
    @ParameterizedTest(name="isOpen for {0} should be {2}")
    @MethodSource("dataProvider")
    public void testIsOpen(String variation, int moduleFlags, boolean isOpen, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(moduleFlags);
        assertEquals(isOpen, sut.isOpen());
    }

    @DisplayName("Module_attribute")
    @ParameterizedTest(name="isSynthetic for {0} should be {3}")
    @MethodSource("dataProvider")
    public void testIsSynthetic(String variation, int moduleFlags, boolean isOpen, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(moduleFlags);
        assertEquals(isSynthetic, sut.isSynthetic());
    }

    @DisplayName("Module_attribute")
    @ParameterizedTest(name="isMandated for {0} should be {4}")
    @MethodSource("dataProvider")
    public void testIsMandated(String variation, int moduleFlags, boolean isOpen, boolean isSynthetic, boolean isMandated) throws IOException {
        var sut = createSut(moduleFlags);
        assertEquals(isMandated, sut.isMandated());
    }

    private Module_attribute createSut(int moduleFlags) throws IOException {
        var moduleNameIndex = 123;
        var moduleName = "abc";
        var moduleVersionIndex = 465;
        var moduleVersion = "blah";

        var mockConstantPool = context.mock(ConstantPool.class);
        var mockOwner = context.mock(Visitable.class);
        var mockIn = context.mock(DataInput.class);
        var mockModule_info = context.mock(Module_info.class);
        var mockUtf8_info = context.mock(UTF8_info.class);

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

        return new Module_attribute(mockConstantPool, mockOwner, mockIn);
    }
}
