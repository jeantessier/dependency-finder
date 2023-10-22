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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.runners.Parameterized.Parameters;
import static org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public class TestVerificationTypeInfoFactory_create {
    @Parameters(name="VerificationType from tag {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"ITEM_Top", 0, null, null, TopVariableInfo.class},
                {"ITEM_Integer", 1, null, null, IntegerVariableInfo.class},
                {"ITEM_Float", 2, null, null, FloatVariableInfo.class},
                {"ITEM_Null", 5, null, null, NullVariableInfo.class},
                {"ITEM_UninitializedThis", 6, null, null, UninitializedThisVariableInfo.class},
                {"ITEM_Object", 7, 123, Class_info.class, ObjectVariableInfo.class},
                {"ITEM_Uninitialized", 8, 456, null, UninitializedVariableInfo.class},
                {"ITEM_Long", 4, null, null, LongVariableInfo.class},
                {"ITEM_Double", 3, null, null, DoubleVariableInfo.class},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int tag;

    @Parameter(2)
    public Integer indexOrOffset;

    @Parameter(3)
    public Class<? extends ConstantPoolEntry> constantPoolEntryClass;

    @Parameter(4)
    public Class<? extends VerificationTypeInfo> expectedClass;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private ConstantPool mockConstantPool;
    private DataInput mockIn;

    private final VerificationTypeInfoFactory sut = new VerificationTypeInfoFactory();

    @Before
    public void setUp() throws IOException {
        context.setImposteriser(ByteBuddyClassImposteriser.INSTANCE);

        mockConstantPool = context.mock(ConstantPool.class);
        mockIn = context.mock(DataInput.class);

        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedByte();
                will(returnValue(tag));
        }});

        // for ObjectVariableInfo's cpool_index and UninitializedVariableInfo's offset
        if (indexOrOffset != null) {
            context.checking(new Expectations() {{
                allowing (mockIn).readUnsignedShort();
                    will(returnValue(indexOrOffset));
            }});
        }

        // for ObjectVariableInfo's cpool_index
        if (constantPoolEntryClass != null) {
            final ConstantPoolEntry mockConstantPoolEntry = context.mock(constantPoolEntryClass);
            context.checking(new Expectations() {{
                allowing (mockConstantPool).get(indexOrOffset);
                    will(returnValue(mockConstantPoolEntry));
            }});
        }
    }

    @Test
    public void testCreate() throws IOException {
        var actualVerificationTypeInfo = sut.create(mockConstantPool, mockIn);

        assertEquals(label, expectedClass, actualVerificationTypeInfo.getClass());
        assertEquals(label, tag, actualVerificationTypeInfo.getTag());
    }
}
