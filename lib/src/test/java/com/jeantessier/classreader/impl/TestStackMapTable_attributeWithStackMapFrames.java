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
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestStackMapTable_attributeWithStackMapFrames extends TestAttributeBase {
    private StackMapFrameFactory mockStackMapFrameFactory;

    @BeforeEach
    void setUp() throws Exception {
        mockStackMapFrameFactory = mock(StackMapFrameFactory.class);

        expectReadAttributeLength(2);
    }

    @Test
    void testOneEntry() throws Exception {
        // Given
        final StackMapFrame mockStackMapFrame = mock(StackMapFrame.class);
        expectReadU2(1);
        checking(new Expectations() {{
            oneOf (mockStackMapFrameFactory).create(mockConstantPool, mockIn);
                will(returnValue(mockStackMapFrame));
        }});

        // When
        var sut = new StackMapTable_attribute(mockConstantPool, mockOwner, mockIn, mockStackMapFrameFactory);

        // Then
        assertEquals(1, sut.getEntries().size(), "entries");
        assertSame(mockStackMapFrame, sut.getEntries().stream().findFirst().orElseThrow());
    }

    @Test
    void testMultipleEntries() throws Exception {
        // Given
        final StackMapFrame mockStackMapFrame1 = mock(StackMapFrame.class, "first frame");
        final StackMapFrame mockStackMapFrame2 = mock(StackMapFrame.class, "second frame");
        expectReadU2(2);
        checking(new Expectations() {{
            oneOf (mockStackMapFrameFactory).create(mockConstantPool, mockIn);
                will(returnValue(mockStackMapFrame1));
            oneOf (mockStackMapFrameFactory).create(mockConstantPool, mockIn);
                will(returnValue(mockStackMapFrame2));
        }});

        // When
        var sut = new StackMapTable_attribute(mockConstantPool, mockOwner, mockIn, mockStackMapFrameFactory);

        // Then
        assertEquals(2, sut.getEntries().size(), "entries");
        assertSame(mockStackMapFrame1, sut.getEntries().stream().findFirst().orElseThrow());
        assertSame(mockStackMapFrame2, sut.getEntries().stream().skip(1).findFirst().orElseThrow());
    }
}
