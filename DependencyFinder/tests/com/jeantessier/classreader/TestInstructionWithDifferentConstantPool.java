/*
 *  Copyright (c) 2001-2009, Jean Tessier
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

import java.util.*;

import junit.framework.*;

public class TestInstructionWithDifferentConstantPool extends TestCase {
    private ClassfileLoader oldLoader;
    private ClassfileLoader newLoader;

    protected void setUp() throws Exception {
        super.setUp();

        oldLoader = new AggregatingClassfileLoader();
        oldLoader.load("tests/JarJarDiff/old/ModifiedPackage/DifferentConstantPool.class");

        newLoader = new AggregatingClassfileLoader();
        newLoader.load("tests/JarJarDiff/new/ModifiedPackage/DifferentConstantPool.class");
    }

    public void testSamePositionInChangedConstantPool() {
        Classfile oldClassfile = oldLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info oldMethod = oldClassfile.getMethod("DifferentConstantPool()");
        Code_attribute oldCode = oldMethod.getCode();
        byte[] oldBytecode = oldCode.getCode();

        Classfile newClassfile = newLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info newMethod = newClassfile.getMethod("DifferentConstantPool()");
        Code_attribute newCode = newMethod.getCode();
        byte[] newBytecode = newCode.getCode();

        assertEquals("Bytecode length", oldBytecode.length, newBytecode.length);

        for (int i=0; i<oldBytecode.length; i++) {
            assertEquals("byte " + i, oldBytecode[i], newBytecode[i]);
        }

        Iterator<Instruction> oldIterator = oldCode.iterator();
        Iterator<Instruction> newIterator = newCode.iterator();

        Instruction oldInstruction;
        Instruction newInstruction;

        // Instruction 0: aload_0
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals("aload_0", oldInstruction.getOpcode(), newInstruction.getOpcode());
        assertEquals("aload_0", oldInstruction, newInstruction);
        assertEquals("aload_0", oldInstruction.hashCode(), newInstruction.hashCode());

        // Instruction 1: invokespecial
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals("invokespecial", oldInstruction.getOpcode(), newInstruction.getOpcode());
        assertEquals("invokespecial", oldInstruction, newInstruction);
        assertEquals("invokespecial", oldInstruction.hashCode(), newInstruction.hashCode());

        // Instruction 2: return
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals("return", oldInstruction.getOpcode(), newInstruction.getOpcode());
        assertEquals("return", oldInstruction, newInstruction);
        assertEquals("return", oldInstruction.hashCode(), newInstruction.hashCode());

        // The end
        assertFalse("Extra instructions", oldIterator.hasNext());
        assertFalse("Extra instructions", newIterator.hasNext());
    }

    public void testIndependantOfConstantPool() {
        Classfile oldClassfile = oldLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info oldMethod = oldClassfile.getMethod("movedMethodRefInfo()");
        Code_attribute oldCode = oldMethod.getCode();
        byte[] oldBytecode = oldCode.getCode();

        Classfile newClassfile = newLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info newMethod = newClassfile.getMethod("movedMethodRefInfo()");
        Code_attribute newCode = newMethod.getCode();
        byte[] newBytecode = newCode.getCode();

        assertEquals("Bytecode length", oldBytecode.length, newBytecode.length);

        for (int i=0; i<oldBytecode.length; i++) {
            assertEquals("byte " + i, oldBytecode[i], newBytecode[i]);
        }

        Iterator<Instruction> oldIterator = oldCode.iterator();
        Iterator<Instruction> newIterator = newCode.iterator();

        Instruction oldInstruction;
        Instruction newInstruction;

        // Instruction 0: return
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals("return", oldInstruction.getOpcode(), newInstruction.getOpcode());
        assertEquals("return", oldInstruction, newInstruction);
        assertEquals("return", oldInstruction.hashCode(), newInstruction.hashCode());

        // The end
        assertFalse("Extra instructions", oldIterator.hasNext());
        assertFalse("Extra instructions", newIterator.hasNext());
    }

    public void testShiftInChangedConstantPool() {
        Classfile oldClassfile = oldLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info oldMethod = oldClassfile.getMethod("callingMovedMethodRefInfo()");
        Code_attribute oldCode = oldMethod.getCode();
        byte[] oldBytecode = oldCode.getCode();

        Classfile newClassfile = newLoader.getClassfile("ModifiedPackage.DifferentConstantPool");
        Method_info newMethod = newClassfile.getMethod("callingMovedMethodRefInfo()");
        Code_attribute newCode = newMethod.getCode();
        byte[] newBytecode = newCode.getCode();

        assertEquals("Bytecode length", oldBytecode.length, newBytecode.length);

        boolean same = oldBytecode.length == newBytecode.length;
        for (int i=0; same && i<oldBytecode.length; i++) {
            same = oldBytecode[i] == newBytecode[i];
        }
        assertFalse("Bytes are identical", same);

        Iterator<Instruction> oldIterator = oldCode.iterator();
        Iterator<Instruction> newIterator = newCode.iterator();

        Instruction oldInstruction;
        Instruction newInstruction;

        // Instruction 0: aload_0
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals("aload_0", oldInstruction.getOpcode(), newInstruction.getOpcode());
        assertEquals("aload_0", oldInstruction, newInstruction);
        assertEquals("aload_0", oldInstruction.hashCode(), newInstruction.hashCode());

        // Instruction 1: invokevirtual
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals("invokevirtual", oldInstruction.getOpcode(), newInstruction.getOpcode());
        assertEquals("invokevirtual", oldInstruction, newInstruction);
        assertEquals("invokevirtual", oldInstruction.hashCode(), newInstruction.hashCode());

        // Instruction 2: return
        oldInstruction = oldIterator.next();
        newInstruction = newIterator.next();
        assertEquals("return", oldInstruction.getOpcode(), newInstruction.getOpcode());
        assertEquals("return", oldInstruction, newInstruction);
        assertEquals("return", oldInstruction.hashCode(), newInstruction.hashCode());

        // The end
        assertFalse("Extra instructions", oldIterator.hasNext());
        assertFalse("Extra instructions", newIterator.hasNext());
    }
}
