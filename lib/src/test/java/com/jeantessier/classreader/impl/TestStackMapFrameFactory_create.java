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
import java.util.*;

import static org.junit.Assert.*;
import static org.junit.runners.Parameterized.*;

@RunWith(Parameterized.class)
public class TestStackMapFrameFactory_create {
    @Parameters(name="Stack Map Frame from tag {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"SAME (min)", 0, null, null, Collections.emptyList(), null, Collections.emptyList(), SameFrame.class},
                {"SAME (max)", 63, null, null, Collections.emptyList(), null, Collections.emptyList(), SameFrame.class},
                {"SAME_LOCALS_1_STACK_ITEM (min)", 64, null, null, Collections.emptyList(), null, Collections.singleton("TOP"), SameLocals1StackItemFrame.class},
                {"SAME_LOCALS_1_STACK_ITEM (max)", 127, null, null, Collections.emptyList(), null, Collections.singleton("TOP"), SameLocals1StackItemFrame.class},
                {"SAME_LOCALS_1_STACK_ITEM_EXTENDED", 247, 456, null, Collections.emptyList(), null, Collections.singleton("TOP"), SameLocals1StackItemFrameExtended.class},
                {"CHOP (min)", 248, 456, null, Collections.emptyList(), null, Collections.emptyList(), ChopFrame.class},
                {"CHOP (min)", 250, 456, null, Collections.emptyList(), null, Collections.emptyList(), ChopFrame.class},
                {"SAME_FRAME_EXTENDED", 251, 456, null, Collections.emptyList(), null, Collections.emptyList(), SameFrameExtended.class},
                {"APPEND (252 - 251 = 1)", 252, 456, null, List.of("TOP1"), null, Collections.emptyList(), AppendFrame.class},
                {"APPEND (253 - 251 = 2)", 253, 456, null, List.of("TOP1", "TOP2"), null, Collections.emptyList(), AppendFrame.class},
                {"APPEND (253 - 251 = 3)", 254, 456, null, List.of("TOP1", "TOP2", "TOP3"), null, Collections.emptyList(), AppendFrame.class},
                {"FULL_FRAME (empties)", 255, 456, 0, Collections.emptyList(), 0, Collections.emptyList(), FullFrame.class},
                {"FULL_FRAME (locals only)", 255, 456, 2, List.of("TOP1", "TOP2"), 0, Collections.emptyList(), FullFrame.class},
                {"FULL_FRAME (stack only)", 255, 456, 0, Collections.emptyList(), 3, List.of("TOP3", "TOP4", "TOP5"), FullFrame.class},
                {"FULL_FRAME (locals and stack)", 255, 456, 2, List.of("TOP1", "TOP2"), 3, List.of("TOP3", "TOP4", "TOP5"), FullFrame.class},
        };
    }

    @Parameter(0)
    public String label;

    @Parameter(1)
    public int frameType;

    @Parameter(2)
    public Integer offsetDelta;

    @Parameter(3)
    public Integer numberOfLocals;

    @Parameter(4)
    public Collection<String> locals;

    @Parameter(5)
    public Integer numberOfStackItems;

    @Parameter(6)
    public Collection<String> stacks;

    @Parameter(7)
    public Class<? extends StackMapFrame> expectedClass;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery() {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    private VerificationTypeInfoFactory mockVerificationTypeInfoFactory;
    private ConstantPool mockConstantPool;
    private DataInput mockIn;

    private StackMapFrameFactory sut;

    @Before
    public void setUp() throws IOException {
        mockVerificationTypeInfoFactory = context.mock(VerificationTypeInfoFactory.class);
        mockConstantPool = context.mock(ConstantPool.class);
        mockIn = context.mock(DataInput.class);

        context.checking(new Expectations() {{
            oneOf (mockIn).readUnsignedByte();
                will(returnValue(frameType));
        }});

        if (offsetDelta != null) {
            context.checking(new Expectations() {{
                oneOf (mockIn).readUnsignedShort();
                    will(returnValue(offsetDelta));
            }});
        }

        if (numberOfLocals != null) {
            context.checking(new Expectations() {{
                oneOf (mockIn).readUnsignedShort();
                    will(returnValue(numberOfLocals));
            }});
        }

        for (String localName : locals) {
            final VerificationTypeInfo mockVerificationTypeInfo = context.mock(VerificationTypeInfo.class, localName);
            context.checking(new Expectations() {{
                oneOf (mockVerificationTypeInfoFactory).create(mockConstantPool, mockIn);
                    will(returnValue(mockVerificationTypeInfo));
            }});
        }

        if (numberOfStackItems != null) {
            context.checking(new Expectations() {{
                oneOf (mockIn).readUnsignedShort();
                    will(returnValue(numberOfStackItems));
            }});
        }

        for (String stackName : stacks) {
            final VerificationTypeInfo mockVerificationTypeInfo = context.mock(VerificationTypeInfo.class, stackName);
            context.checking(new Expectations() {{
                oneOf (mockVerificationTypeInfoFactory).create(mockConstantPool, mockIn);
                    will(returnValue(mockVerificationTypeInfo));
            }});
        }

        sut = new StackMapFrameFactory(mockVerificationTypeInfoFactory);
    }

    @Test
    public void testCreate() throws IOException {
        var actualStackMapFrame = sut.create(mockConstantPool, mockIn);

        assertEquals(label, expectedClass, actualStackMapFrame.getClass());
        assertEquals(label, frameType, actualStackMapFrame.getFrameType());
    }
}
