/*
 *  Copyright (c) 2001-2008, Jean Tessier
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
import org.jmock.integration.junit3.*;
import org.jmock.lib.legacy.*;

public class TestInstruction extends MockObjectTestCase {
    private static final byte LDC_INSTRUCTION = (byte) 0x12;
    private static final byte INDEX = (byte) 0x02;

    private Code_attribute mockCode_attribute;

    protected void setUp() throws Exception {
        super.setUp();

        setImposteriser(ClassImposteriser.INSTANCE);

        mockCode_attribute = mock(Code_attribute.class);
    }

    public void testEquals_Same() {
        Instruction sut = new Instruction(mockCode_attribute, null, 0);
        assertTrue(sut.equals(sut));
    }

    public void testEquals_DifferentClasses() {
        Instruction sut = new Instruction(mockCode_attribute, null, 0);
        Object other = new Object();

        assertFalse(sut.equals(other));
        assertFalse(other.equals(sut));
    }

    public void testEquals_Identical() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        byte[] code = new byte[] {LDC_INSTRUCTION, INDEX};

        Instruction sut = new Instruction(mockCode_attribute, code, 0);
        Instruction other = new Instruction(mockCode_attribute, code, 0);

        checking(new Expectations() {{
            atLeast(2).of (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            atLeast(2).of (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry));
        }});

        assertTrue(sut.equals(other));
        assertTrue(other.equals(sut));
    }

    public void testEquals_DifferentOpCode() {
        byte[] code1 = new byte[] {(byte) 0xAC};
        byte[] code2 = new byte[] {(byte) 0xAD};

        Instruction sut = new Instruction(mockCode_attribute, code1, 0);
        Instruction other = new Instruction(mockCode_attribute, code2, 0);

        assertFalse("different opcode", sut.equals(other));
        assertFalse("different opcode", other.equals(sut));
    }

    public void testEquals_Offset() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        byte[] code2 = new byte[] {LDC_INSTRUCTION, INDEX};
        byte[] code3 = new byte[] {(byte) 0xB6, (byte) 0x00, (byte) 0xFF, LDC_INSTRUCTION, INDEX};


        Instruction sut = new Instruction(mockCode_attribute, code2, 0);
        Instruction other = new Instruction(mockCode_attribute, code3, 3);

        checking(new Expectations() {{
            atLeast(2).of (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            atLeast(2).of (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry));
        }});

        assertTrue(sut.equals(other));
        assertTrue(other.equals(sut));
    }

    public void testEquals_DifferentIndex_SameValue() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        byte[] code2 = new byte[] {LDC_INSTRUCTION, INDEX};
        byte[] code5 = new byte[] {LDC_INSTRUCTION, INDEX + 1};

        Instruction sut = new Instruction(mockCode_attribute, code2, 0);
        Instruction other = new Instruction(mockCode_attribute, code5, 0);

        checking(new Expectations() {{
            atLeast(2).of (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            atLeast(1).of (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry));
            atLeast(1).of (mockConstantPool).get(INDEX + 1);
                will(returnValue(mockEntry));
        }});

        assertTrue(sut.equals(other));
        assertTrue(other.equals(sut));
    }

    public void testEquals_DifferentIndex_DifferentValue() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry1 = mock(ConstantPoolEntry.class, "SUT entry");
        final ConstantPoolEntry mockEntry2 = mock(ConstantPoolEntry.class, "other entry");

        byte[] code2 = new byte[] {LDC_INSTRUCTION, INDEX};
        byte[] code5 = new byte[] {LDC_INSTRUCTION, INDEX + 1};

        Instruction sut = new Instruction(mockCode_attribute, code2, 0);
        Instruction other = new Instruction(mockCode_attribute, code5, 0);

        checking(new Expectations() {{
            atLeast(2).of (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            atLeast(1).of (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry1));
            atLeast(1).of (mockConstantPool).get(INDEX + 1);
                will(returnValue(mockEntry2));
        }});

        assertFalse(sut.equals(other));
        assertFalse(other.equals(sut));
    }

    public void testEquals_DifferentCode_attribute() {
        final ConstantPool mockConstantPool1 = mock(ConstantPool.class, "SUT constant pool");
        final ConstantPool mockConstantPool2 = mock(ConstantPool.class, "other constant pool");
        final ConstantPoolEntry mockEntry1 = mock(ConstantPoolEntry.class, "SUT entry");
        final ConstantPoolEntry mockEntry2 = mock(ConstantPoolEntry.class, "other entry");
        final Code_attribute otherCode_attribute = mock(Code_attribute.class, "other");

        byte[] code = new byte[] {LDC_INSTRUCTION, INDEX};

        checking(new Expectations() {{
            atLeast(1).of (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool1));
            atLeast(1).of (mockConstantPool1).get(INDEX);
                will(returnValue(mockEntry1));
            atLeast(1).of (otherCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool2));
            atLeast(1).of (mockConstantPool2).get(INDEX);
                will(returnValue(mockEntry2));
        }});

        Instruction sut = new Instruction(mockCode_attribute, code, 0);
        Instruction other = new Instruction(otherCode_attribute, code, 0);

        assertFalse(sut.equals(other));
        assertFalse(other.equals(sut));
    }

    public void testGetIndexedConstantPoolEntry() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking (new Expectations() {{
            one (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            one (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry));
        }});

        byte[] bytecode = new byte[] {LDC_INSTRUCTION, INDEX};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);
        ConstantPoolEntry actualEntry = (ConstantPoolEntry) sut.getIndexedConstantPoolEntry();
        assertSame(mockEntry, actualEntry);
    }
}
