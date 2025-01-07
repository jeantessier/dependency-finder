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
import org.jmock.api.*;
import org.jmock.lib.action.*;
import org.junit.jupiter.api.*;

import java.util.*;

import com.jeantessier.MockObjectTestCase;
import com.jeantessier.classreader.BootstrapMethodFinder;
import com.jeantessier.classreader.LocalVariableFinder;
import com.jeantessier.classreader.Visitor;

import static org.junit.jupiter.api.Assertions.*;

public class TestInstruction extends MockObjectTestCase {
    private static final byte ICONST_0_INSTRUCTION = (byte) 0x03;
    private static final byte LDC_INSTRUCTION = (byte) 0x12;
    private static final byte ILOAD_INSTRUCTION = (byte) 0x15;
    private static final byte ISTORE_INSTRUCTION = (byte) 0x36;
    private static final byte INVOKEDYNAMIC_INSTRUCTION = (byte) 0xba;
    private static final byte WIDE_INSTRUCTION = (byte) 0xc4;
    private static final byte INDEX = (byte) 0x02;
    private static final int START_PC = 0;
    private static final int LENGTH = 10;

    private Code_attribute mockCode_attribute;

    @BeforeEach
    void setUp() {
        mockCode_attribute = mock(Code_attribute.class);
    }

    @Test
    void testEquals_Same() {
        Instruction sut = new Instruction(mockCode_attribute, null, 0);
        assertEquals(sut, sut);
    }

    @Test
    void testEquals_DifferentClasses() {
        Instruction sut = new Instruction(mockCode_attribute, null, 0);
        Object other = new Object();

        assertNotEquals(sut, other);
        assertNotEquals(other, sut);
    }

    @Test
    void testEquals_Identical() {
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

        assertEquals(sut, other);
        assertEquals(other, sut);
    }

    @Test
    void testEquals_DifferentOpCode() {
        byte[] code1 = new byte[] {(byte) 0xAC};
        byte[] code2 = new byte[] {(byte) 0xAD};

        Instruction sut = new Instruction(mockCode_attribute, code1, 0);
        Instruction other = new Instruction(mockCode_attribute, code2, 0);

        assertNotEquals(sut, other, "different opcode");
        assertNotEquals(other, sut, "different opcode");
    }

    @Test
    void testEquals_Offset() {
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

        assertEquals(sut, other);
        assertEquals(other, sut);
    }

    @Test
    void testEquals_DifferentIndex_SameValue() {
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

        assertEquals(sut, other);
        assertEquals(other, sut);
    }

    @Test
    void testEquals_DifferentIndex_DifferentValue() {
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

        assertNotEquals(sut, other);
        assertNotEquals(other, sut);
    }

    @Test
    void testEquals_DifferentCode_attribute() {
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

        assertNotEquals(sut, other);
        assertNotEquals(other, sut);
    }

    @Test
    void testGetIndexedConstantPoolEntry() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking (new Expectations() {{
            oneOf (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            oneOf (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry));
        }});

        byte[] bytecode = new byte[] {LDC_INSTRUCTION, INDEX};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);
        ConstantPoolEntry actualEntry = (ConstantPoolEntry) sut.getIndexedConstantPoolEntry();
        assertSame(mockEntry, actualEntry);
    }

    @Test
    void testGetIndexedLocalVariable_NotMatchingIndex() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            oneOf (mockLocalVariable).getIndex();
                will(returnValue(INDEX + 1));
        }});

        byte[] bytecode = {ILOAD_INSTRUCTION, INDEX};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertNull(actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_LoadInstructionInsideMatchingIndexMatchingPcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {ILOAD_INSTRUCTION, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            oneOf (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertSame(mockLocalVariable, actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_LoadInstructionImmediatelyBeforePcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {ILOAD_INSTRUCTION, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            oneOf (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            oneOf (mockLocalVariable).getStartPC();
                will(returnValue(START_PC + bytecode.length));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertNull(actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_StoreInstructionInsidePcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {ISTORE_INSTRUCTION, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            oneOf (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertSame(mockLocalVariable, actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_StoreInstructionImmediatelyBeforePcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {ISTORE_INSTRUCTION, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            atLeast(1).of (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC + bytecode.length));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertSame(mockLocalVariable, actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_WideLoadInstructionInsideMatchingIndexMatchingPcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {WIDE_INSTRUCTION, ILOAD_INSTRUCTION, 0x00, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            oneOf (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertSame(mockLocalVariable, actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_WideLoadInstructionImmediatelyBeforePcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {WIDE_INSTRUCTION, ILOAD_INSTRUCTION, 0x00, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            oneOf (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            oneOf (mockLocalVariable).getStartPC();
                will(returnValue(START_PC + bytecode.length));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertNull(actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_WideStoreInstructionInsidePcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {WIDE_INSTRUCTION, ISTORE_INSTRUCTION, 0x00, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            oneOf (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertSame(mockLocalVariable, actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_WideStoreInstructionImmediatelyBeforePcRange() {
        final LocalVariable mockLocalVariable = mock(LocalVariable.class);

        final byte[] bytecode = {WIDE_INSTRUCTION, ISTORE_INSTRUCTION, 0x00, INDEX};

        checking(new Expectations() {{
            oneOf (mockCode_attribute).accept(with(any(LocalVariableFinder.class)));
                will(visitLocalVariable(mockLocalVariable));
            atLeast(1).of (mockLocalVariable).getIndex();
                will(returnValue((int) INDEX));
            atLeast(1).of (mockLocalVariable).getStartPC();
                will(returnValue(START_PC + bytecode.length));
            atLeast(1).of (mockLocalVariable).getLength();
                will(returnValue(LENGTH));
        }});

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertSame(mockLocalVariable, actualLocalVariable);
    }

    @Test
    void testGetIndexedLocalVariable_WrongOpCode() {
        byte[] bytecode = {ICONST_0_INSTRUCTION};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        LocalVariable actualLocalVariable = (LocalVariable) sut.getIndexedLocalVariable();
        assertNull(actualLocalVariable);
    }

    @Test
    void testGetDynamicConstantPoolEntries_withNonDynamicInstruction() {
        byte[] bytecode = new byte[] {ICONST_0_INSTRUCTION};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        var entries = sut.getDynamicConstantPoolEntries();
        assertTrue(entries.isEmpty(), "empty");
    }

    @Test
    void testGetDynamicConstantPoolEntries_withNonDynamicConstantPoolEntry() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking (new Expectations() {{
            atLeast(1).of (mockCode_attribute).getConstantPool();
            will(returnValue(mockConstantPool));
            atLeast(1).of (mockConstantPool).get(INDEX);
            will(returnValue(mockEntry));
        }});

        byte[] bytecode = new byte[] {INVOKEDYNAMIC_INSTRUCTION, 0, INDEX, 0, 0};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        var entries = sut.getDynamicConstantPoolEntries();
        assertTrue(entries.isEmpty(), "empty");
    }

    @Test
    void testGetDynamicConstantPoolEntries_withDynamic_info() {
        final Classfile mockClassfile = mock(Classfile.class);
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final Dynamic_info mockEntry = mock(Dynamic_info.class);
        final int bootstrapMethodAttrIndex = 123;
        final BootstrapMethod mockBootstrapMethod = mock(BootstrapMethod.class);
        final MethodHandle_info mockMethodHandle = mock(MethodHandle_info.class);
        final FeatureRef_info mockReference = mock(FeatureRef_info.class);

        checking (new Expectations() {{
            atLeast(1).of (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            atLeast(1).of (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry));
            oneOf (mockEntry).getBootstrapMethodAttrIndex();
                will(returnValue(bootstrapMethodAttrIndex));
            oneOf (mockConstantPool).getClassfile();
                will(returnValue(mockClassfile));
            oneOf (mockClassfile).accept(with(any(BootstrapMethodFinder.class)));
                will(new CustomAction("direct the visitor to the correct BootstrapMethod structure") {
                    public Object invoke(Invocation invocation) {
                        ((com.jeantessier.classreader.Visitor) invocation.getParameter(0)).visitBootstrapMethod(mockBootstrapMethod);
                        return null;
                    }
                });
            oneOf (mockBootstrapMethod).getArguments();
                will(returnValue(Collections.singleton(mockMethodHandle)));
            oneOf (mockMethodHandle).getReference();
                will(returnValue(mockReference));
        }});

        byte[] bytecode = new byte[] {INVOKEDYNAMIC_INSTRUCTION, 0, INDEX, 0, 0};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        var entries = sut.getDynamicConstantPoolEntries();
        assertEquals(1, entries.size(), "size");
        assertSame(mockReference, entries.stream().findFirst().orElseThrow(), "reference entry");
    }

    @Test
    void testGetDynamicConstantPoolEntries_withInvokeDynamic_info() {
        final Classfile mockClassfile = mock(Classfile.class);
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final InvokeDynamic_info mockEntry = mock(InvokeDynamic_info.class);
        final int bootstrapMethodAttrIndex = 123;
        final BootstrapMethod mockBootstrapMethod = mock(BootstrapMethod.class);
        final MethodHandle_info mockMethodHandle = mock(MethodHandle_info.class);
        final FeatureRef_info mockReference = mock(FeatureRef_info.class);

        checking (new Expectations() {{
            atLeast(1).of (mockCode_attribute).getConstantPool();
                will(returnValue(mockConstantPool));
            atLeast(1).of (mockConstantPool).get(INDEX);
                will(returnValue(mockEntry));
            oneOf (mockEntry).getBootstrapMethodAttrIndex();
                will(returnValue(bootstrapMethodAttrIndex));
            oneOf (mockConstantPool).getClassfile();
                will(returnValue(mockClassfile));
            oneOf (mockClassfile).accept(with(any(BootstrapMethodFinder.class)));
                will(new CustomAction("direct the visitor to the correct BootstrapMethod structure") {
                    public Object invoke(Invocation invocation) {
                        ((com.jeantessier.classreader.Visitor) invocation.getParameter(0)).visitBootstrapMethod(mockBootstrapMethod);
                        return null;
                    }
                });
            oneOf (mockBootstrapMethod).getArguments();
                will(returnValue(Collections.singleton(mockMethodHandle)));
            oneOf (mockMethodHandle).getReference();
                will(returnValue(mockReference));
        }});

        byte[] bytecode = new byte[] {INVOKEDYNAMIC_INSTRUCTION, 0, INDEX, 0, 0};

        Instruction sut = new Instruction(mockCode_attribute, bytecode, 0);

        var entries = sut.getDynamicConstantPoolEntries();
        assertEquals(1, entries.size(), "size");
        assertSame(mockReference, entries.stream().findFirst().orElseThrow(), "reference entry");
    }

    private Action visitLocalVariable(final LocalVariable localVariable) {
        return new CustomAction("Visit local variable") {
            public Object invoke(Invocation invocation) {
                ((Visitor) invocation.getParameter(0)).visitLocalVariable(localVariable);
                return null;
            }
        };
    }
}
