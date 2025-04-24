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

public class TestFrameType_create {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("SAME (min)", 0, null, null, Collections.emptyList(), null, Collections.emptyList(), SameFrame.class),
                arguments("SAME (max)", 63, null, null, Collections.emptyList(), null, Collections.emptyList(), SameFrame.class),
                arguments("SAME_LOCALS_1_STACK_ITEM (min)", 64, null, null, Collections.emptyList(), null, Collections.singleton("TOP"), SameLocals1StackItemFrame.class),
                arguments("SAME_LOCALS_1_STACK_ITEM (max)", 127, null, null, Collections.emptyList(), null, Collections.singleton("TOP"), SameLocals1StackItemFrame.class),
                arguments("SAME_LOCALS_1_STACK_ITEM_EXTENDED", 247, 456, null, Collections.emptyList(), null, Collections.singleton("TOP"), SameLocals1StackItemFrameExtended.class),
                arguments("CHOP (min)", 248, 456, null, Collections.emptyList(), null, Collections.emptyList(), ChopFrame.class),
                arguments("CHOP (min)", 250, 456, null, Collections.emptyList(), null, Collections.emptyList(), ChopFrame.class),
                arguments("SAME_FRAME_EXTENDED", 251, 456, null, Collections.emptyList(), null, Collections.emptyList(), SameFrameExtended.class),
                arguments("APPEND (252 - 251 = 1)", 252, 456, null, List.of("TOP1"), null, Collections.emptyList(), AppendFrame.class),
                arguments("APPEND (253 - 251 = 2)", 253, 456, null, List.of("TOP1", "TOP2"), null, Collections.emptyList(), AppendFrame.class),
                arguments("APPEND (253 - 251 = 3)", 254, 456, null, List.of("TOP1", "TOP2", "TOP3"), null, Collections.emptyList(), AppendFrame.class),
                arguments("FULL_FRAME (empties)", 255, 456, 0, Collections.emptyList(), 0, Collections.emptyList(), FullFrame.class),
                arguments("FULL_FRAME (locals only)", 255, 456, 2, List.of("TOP1", "TOP2"), 0, Collections.emptyList(), FullFrame.class),
                arguments("FULL_FRAME (stack only)", 255, 456, 0, Collections.emptyList(), 3, List.of("TOP3", "TOP4", "TOP5"), FullFrame.class),
                arguments("FULL_FRAME (locals and stack)", 255, 456, 2, List.of("TOP1", "TOP2"), 3, List.of("TOP3", "TOP4", "TOP5"), FullFrame.class)
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("create")
    @ParameterizedTest(name="from tag {0}")
    @MethodSource("dataProvider")
    public void testCreate(String variation, int frameType, Integer offsetDelta, Integer numberOfLocals, Collection<String> locals, Integer numberOfStackItems, Collection<String> stacks, Class<? extends StackMapFrame> expectedClass) throws IOException {
        // Given
        var mockVerificationTypeInfoFactory = context.mock(VerificationTypeInfoFactory.class);
        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);

        // and
        if (offsetDelta != null) {
            context.checking(new Expectations() {{
                oneOf (mockIn).readUnsignedShort();
                    will(returnValue(offsetDelta));
            }});
        }

        // and
        if (numberOfLocals != null) {
            context.checking(new Expectations() {{
                oneOf (mockIn).readUnsignedShort();
                    will(returnValue(numberOfLocals));
            }});
        }

        // and
        for (String localName : locals) {
            var mockVerificationTypeInfo = context.mock(VerificationTypeInfo.class, localName);
            context.checking(new Expectations() {{
                oneOf (mockVerificationTypeInfoFactory).create(mockConstantPool, mockIn);
                    will(returnValue(mockVerificationTypeInfo));
            }});
        }

        // and
        if (numberOfStackItems != null) {
            context.checking(new Expectations() {{
                oneOf (mockIn).readUnsignedShort();
                    will(returnValue(numberOfStackItems));
            }});
        }

        // and
        for (String stackName : stacks) {
            var mockVerificationTypeInfo = context.mock(VerificationTypeInfo.class, stackName);
            context.checking(new Expectations() {{
                oneOf (mockVerificationTypeInfoFactory).create(mockConstantPool, mockIn);
                    will(returnValue(mockVerificationTypeInfo));
            }});
        }

        // and
        var sut = FrameType.forTag(frameType);

        // When
        var actualStackMapFrame = sut.create(frameType, mockVerificationTypeInfoFactory, mockConstantPool, mockIn);

        // Then
        assertEquals(expectedClass, actualStackMapFrame.getClass(), variation);
        assertEquals(frameType, actualStackMapFrame.getFrameType(), variation);
    }
}
