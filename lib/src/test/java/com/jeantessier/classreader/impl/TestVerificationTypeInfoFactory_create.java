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

public class TestVerificationTypeInfoFactory_create {
    static Stream<Arguments> dataProvider() {
        return Stream.of(
                arguments("ITEM_Top", 0, null, null, TopVariableInfo.class),
                arguments("ITEM_Integer", 1, null, null, IntegerVariableInfo.class),
                arguments("ITEM_Float", 2, null, null, FloatVariableInfo.class),
                arguments("ITEM_Null", 5, null, null, NullVariableInfo.class),
                arguments("ITEM_UninitializedThis", 6, null, null, UninitializedThisVariableInfo.class),
                arguments("ITEM_Object", 7, 123, Class_info.class, ObjectVariableInfo.class),
                arguments("ITEM_Uninitialized", 8, 456, null, UninitializedVariableInfo.class),
                arguments("ITEM_Long", 4, null, null, LongVariableInfo.class),
                arguments("ITEM_Double", 3, null, null, DoubleVariableInfo.class)
        );
    }

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @DisplayName("VerificationTypeInfoFactory")
    @ParameterizedTest(name="for {0} should be {4} with tag {1}")
    @MethodSource("dataProvider")
    public void testCreate(String variation, int tag, Integer indexOrOffset, Class<? extends ConstantPoolEntry> constantPoolEntryClass, Class<? extends VerificationTypeInfo> expectedClass) throws IOException {
        // Given
        var mockConstantPool = context.mock(ConstantPool.class);
        var mockIn = context.mock(DataInput.class);

        // and
        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedByte();
                will(returnValue(tag));
        }});

        // and
        // for ObjectVariableInfo's cpool_index and UninitializedVariableInfo's offset
        if (indexOrOffset != null) {
            context.checking(new Expectations() {{
                allowing (mockIn).readUnsignedShort();
                    will(returnValue(indexOrOffset));
            }});
        }

        // and
        // for ObjectVariableInfo's cpool_index
        if (constantPoolEntryClass != null) {
            final ConstantPoolEntry mockConstantPoolEntry = context.mock(constantPoolEntryClass);
            context.checking(new Expectations() {{
                allowing (mockConstantPool).get(indexOrOffset);
                    will(returnValue(mockConstantPoolEntry));
            }});
        }

        // and
        var sut = new VerificationTypeInfoFactory();

        // When
        var actualVerificationTypeInfo = sut.create(mockConstantPool, mockIn);

        // Then
        assertEquals(expectedClass, actualVerificationTypeInfo.getClass());
        assertEquals(tag, actualVerificationTypeInfo.getTag());
    }
}
