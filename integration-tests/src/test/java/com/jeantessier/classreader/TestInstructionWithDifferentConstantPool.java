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

package com.jeantessier.classreader;

import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestInstructionWithDifferentConstantPool {
    private final ClassfileLoader oldLoader = new AggregatingClassfileLoader();
    private final ClassfileLoader newLoader = new AggregatingClassfileLoader();

    @BeforeEach
    void setUp() {
        oldLoader.load(Paths.get("jarjardiff/old/build/classes/java/main/ModifiedPackage/DifferentConstantPool.class").toString());
        newLoader.load(Paths.get("jarjardiff/new/build/classes/java/main/ModifiedPackage/DifferentConstantPool.class").toString());
    }

    @Test
    void testSamePositionInChangedConstantPool() {
        Classfile oldClassfile = oldLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info oldMethod = oldClassfile.getMethod(m -> m.getSignature().equals("DifferentConstantPool()"));
        Code_attribute oldCode = oldMethod.getCode();
        byte[] oldBytecode = oldCode.getCode();

        Classfile newClassfile = newLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info newMethod = newClassfile.getMethod(m -> m.getSignature().equals("DifferentConstantPool()"));
        Code_attribute newCode = newMethod.getCode();
        byte[] newBytecode = newCode.getCode();

        assertArrayEquals(oldBytecode, newBytecode, "Bytecode");

        Iterator<Instruction> oldIterator = oldCode.iterator();
        Iterator<Instruction> newIterator = newCode.iterator();

        Instruction oldInstruction;
        Instruction newInstruction;

        // Instruction 0: aload_0
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals(oldInstruction.getOpcode(), newInstruction.getOpcode(), "aload_0");
        assertEquals(oldInstruction, newInstruction, "aload_0");
        assertEquals(oldInstruction.hashCode(), newInstruction.hashCode(), "aload_0");

        // Instruction 1: invokespecial
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals(oldInstruction.getOpcode(), newInstruction.getOpcode(), "invokespecial");
        assertEquals(oldInstruction, newInstruction, "invokespecial");
        assertEquals(oldInstruction.hashCode(), newInstruction.hashCode(), "invokespecial");

        // Instruction 2: return
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals(oldInstruction.getOpcode(), newInstruction.getOpcode(), "return");
        assertEquals(oldInstruction, newInstruction, "return");
        assertEquals(oldInstruction.hashCode(), newInstruction.hashCode(), "return");

        // The end
        assertFalse(oldIterator.hasNext(), "Extra instructions");
        assertFalse(newIterator.hasNext(), "Extra instructions");
    }

    @Test
    void testIndependantOfConstantPool() {
        Classfile oldClassfile = oldLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info oldMethod = oldClassfile.getMethod(m -> m.getSignature().equals("movedMethodRefInfo()"));
        Code_attribute oldCode = oldMethod.getCode();
        byte[] oldBytecode = oldCode.getCode();

        Classfile newClassfile = newLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info newMethod = newClassfile.getMethod(m -> m.getSignature().equals("movedMethodRefInfo()"));
        Code_attribute newCode = newMethod.getCode();
        byte[] newBytecode = newCode.getCode();

        assertArrayEquals(oldBytecode, newBytecode, "Bytecode");

        Iterator<Instruction> oldIterator = oldCode.iterator();
        Iterator<Instruction> newIterator = newCode.iterator();

        Instruction oldInstruction;
        Instruction newInstruction;

        // Instruction 0: return
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals(oldInstruction.getOpcode(), newInstruction.getOpcode(), "return");
        assertEquals(oldInstruction, newInstruction, "return");
        assertEquals(oldInstruction.hashCode(), newInstruction.hashCode(), "return");

        // The end
        assertFalse(oldIterator.hasNext(), "Extra instructions");
        assertFalse(newIterator.hasNext(), "Extra instructions");
    }

    @Test
    void testShiftInChangedConstantPool() {
        Classfile oldClassfile = oldLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info oldMethod = oldClassfile.getMethod(m -> m.getSignature().equals("callingMovedMethodRefInfo()"));
        Code_attribute oldCode = oldMethod.getCode();
        byte[] oldBytecode = oldCode.getCode();

        Classfile newClassfile = newLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info newMethod = newClassfile.getMethod(m -> m.getSignature().equals("callingMovedMethodRefInfo()"));
        Code_attribute newCode = newMethod.getCode();
        byte[] newBytecode = newCode.getCode();

        assertEquals(oldBytecode.length, newBytecode.length, "Bytecode length");

        boolean same = oldBytecode.length == newBytecode.length;
        for (int i=0; same && i<oldBytecode.length; i++) {
            same = oldBytecode[i] == newBytecode[i];
        }
        assertFalse(same, "Bytes are identical");

        Iterator<Instruction> oldIterator = oldCode.iterator();
        Iterator<Instruction> newIterator = newCode.iterator();

        Instruction oldInstruction;
        Instruction newInstruction;

        // Instruction 0: aload_0
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals(oldInstruction.getOpcode(), newInstruction.getOpcode(), "aload_0");
        assertEquals(oldInstruction, newInstruction, "aload_0");
        assertEquals(oldInstruction.hashCode(), newInstruction.hashCode(), "aload_0");

        // Instruction 1: invokevirtual
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals(oldInstruction.getOpcode(), newInstruction.getOpcode(), "invokevirtual");
        assertEquals(oldInstruction, newInstruction, "invokevirtual");
        assertEquals(oldInstruction.hashCode(), newInstruction.hashCode(), "invokevirtual");

        // Instruction 2: return
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals(oldInstruction.getOpcode(), newInstruction.getOpcode(), "return");
        assertEquals(oldInstruction, newInstruction, "return");
        assertEquals(oldInstruction.hashCode(), newInstruction.hashCode(), "return");

        // The end
        assertFalse(oldIterator.hasNext(), "Extra instructions");
        assertFalse(newIterator.hasNext(), "Extra instructions");
    }
}
