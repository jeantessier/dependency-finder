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

package com.jeantessier.classreader.impl;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.Instruction;
import com.jeantessier.classreader.LocalVariable;
import com.jeantessier.classreader.*;
import com.jeantessier.text.*;

public class Code_attribute extends Attribute_info implements Iterable<Instruction>, com.jeantessier.classreader.Code_attribute {
    private int maxStack;
    private int maxLocals;
    private byte[] code;
    private Collection<ExceptionHandler> exceptionHandlers = new LinkedList<ExceptionHandler>();
    private Collection<Attribute_info> attributes = new LinkedList<Attribute_info>();

    public Code_attribute(ConstantPool constantPool, Visitable owner, DataInput in) throws IOException {
        this(constantPool, owner, in, new AttributeFactory());
    }

    public Code_attribute(ConstantPool constantPool, Visitable owner, DataInput in, AttributeFactory attributeFactory) throws IOException {
        super(constantPool, owner);

        int byteCount = in.readInt();
        Logger.getLogger(getClass()).debug("Attribute length: " + byteCount);

        maxStack = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Code max stack: " + maxStack);

        maxLocals = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Code max locals: " + maxLocals);

        int codeLength = in.readInt();
        Logger.getLogger(getClass()).debug("Code length: " + codeLength);
        
        code = new byte[codeLength];
        in.readFully(code);
        Logger.getLogger(getClass()).debug("Read " + codeLength + " byte(s): " + Hex.toString(code));

        int exceptionTableLength = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + exceptionTableLength + " exception handler(s) ...");
        for (int i=0; i<exceptionTableLength; i++) {
            Logger.getLogger(getClass()).debug("Exception handler " + i + ":");
            exceptionHandlers.add(new ExceptionHandler(this, in));
        }

        int attributeCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + attributeCount + " code attribute(s)");
        for (int i=0; i<attributeCount; i++) {
            Logger.getLogger(getClass()).debug("code attribute " + i + ":");
            attributes.add(attributeFactory.create(getConstantPool(), this, in));
        }

        if (Logger.getLogger(getClass()).isDebugEnabled()) {
            Logger.getLogger(getClass()).debug("Read instructions(s):");

            for (Instruction instr : this) {
                logInstruction(instr);
            }
        }
    }

    public int getMaxStack() {
        return maxStack;
    }

    public int getMaxLocals() {
        return maxLocals;
    }

    public byte[] getCode() {
        return code;
    }

    public Iterator<Instruction> iterator() {
        return new CodeIterator(this, code);
    }

    public Collection<ExceptionHandler> getExceptionHandlers() {
        return exceptionHandlers;
    }

    public Collection<Attribute_info> getAttributes() {
        return attributes;
    }

    public String toString() {
        return "Code";
    }

    public String getAttributeName() {
        return AttributeType.CODE.getAttributeName();
    }

    public void accept(Visitor visitor) {
        visitor.visitCode_attribute(this);
    }

    private void logInstruction(Instruction instruction) {
        StringBuilder message = new StringBuilder();
        message.append("    ").append(instruction.getStart()).append(": ").append(instruction);
        appendIndexedConstantPoolEntry(message, instruction);
        appendIndexedLocalVariable(message, instruction);
        appendOffset(message, instruction);
        appendValue(message, instruction);

        Logger.getLogger(getClass()).debug(message);
    }

    private void appendIndexedConstantPoolEntry(StringBuilder message, Instruction instruction) {
        switch (instruction.getOpcode()) {
            case 0x12: // ldc
            case 0x13: // ldc_w
            case 0x14: // ldc2_w
            case 0xb2: // getstatic
            case 0xb3: // putstatic
            case 0xb4: // getfield
            case 0xb5: // putfield
            case 0xb6: // invokevirtual
            case 0xb7: // invokespecial
            case 0xb8: // invokestatic
            case 0xb9: // invokeinterface
            case 0xbb: // new
            case 0xbd: // anewarray
            case 0xc0: // checkcast
            case 0xc1: // instanceof
            case 0xc5: // multianewarray
                message.append(" ").append(instruction.getIndex()).append(" (").append(instruction.getIndexedConstantPoolEntry()).append(")");
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void appendIndexedLocalVariable(StringBuilder message, Instruction instruction) {
        switch (instruction.getOpcode()) {
            case 0x1a: // iload_0
            case 0x1e: // lload_0
            case 0x22: // fload_0
            case 0x26: // dload_0
            case 0x2a: // aload_0
            case 0x3b: // istore_0
            case 0x3f: // lstore_0
            case 0x43: // fstore_0
            case 0x47: // dstore_0
            case 0x4b: // astore_0
            case 0x1b: // iload_1
            case 0x1f: // lload_1
            case 0x23: // fload_1
            case 0x27: // dload_1
            case 0x2b: // aload_1
            case 0x3c: // istore_1
            case 0x40: // lstore_1
            case 0x44: // fstore_1
            case 0x48: // dstore_1
            case 0x4c: // astore_1
            case 0x1c: // iload_2
            case 0x20: // lload_2
            case 0x24: // fload_2
            case 0x28: // dload_2
            case 0x2c: // aload_2
            case 0x3d: // istore_2
            case 0x41: // lstore_2
            case 0x45: // fstore_2
            case 0x49: // dstore_2
            case 0x4d: // astore_2
            case 0x1d: // iload_3
            case 0x21: // lload_3
            case 0x25: // fload_3
            case 0x29: // dload_3
            case 0x2d: // aload_3
            case 0x3e: // istore_3
            case 0x42: // lstore_3
            case 0x46: // fstore_3
            case 0x4a: // dstore_3
            case 0x4e: // astore_3
                appendLocalVariable(message, instruction.getIndexedLocalVariable());
                break;
            case 0x15: // iload
            case 0x16: // llload
            case 0x17: // fload
            case 0x18: // dload
            case 0x19: // aload
            case 0x36: // istore
            case 0x37: // lstore
            case 0x38: // fstore
            case 0x39: // dstore
            case 0x3a: // astore
            case 0xa9: // ret
            case 0x84: // iinc
            case 0xc4: // wide
                message.append(" ").append(instruction.getIndex());
                appendLocalVariable(message, instruction.getIndexedLocalVariable());
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void appendLocalVariable(StringBuilder message, LocalVariable localVariable) {
        String name = "n/a";

        if (localVariable != null) {
            name = localVariable.toString();
        }

        message.append(" (").append(name).append(")");
    }

    private void appendOffset(StringBuilder message, Instruction instruction) {
        switch (instruction.getOpcode()) {
            case 0x99: // ifeq
            case 0x9a: // ifne
            case 0x9b: // iflt
            case 0x9c: // ifge
            case 0x9d: // ifgt
            case 0x9e: // ifle
            case 0x9f: // if_icmpeq
            case 0xa0: // if_icmpne
            case 0xa1: // if_icmplt
            case 0xa2: // if_icmpge
            case 0xa3: // if_icmpgt
            case 0xa4: // if_icmple
            case 0xa5: // if_acmpeq
            case 0xa6: // if_acmpne
            case 0xa7: // goto
            case 0xa8: // jsr
            case 0xc6: // ifnull
            case 0xc7: // ifnonnull
            case 0xc8: // goto_w
            case 0xc9: // jsr_w
                message.append(" ");
                if (instruction.getOffset() >= 0) {
                    message.append("+");
                }
                message.append(instruction.getOffset()).append(" (to ").append(instruction.getStart() + instruction.getOffset()).append(")");
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void appendValue(StringBuilder message, Instruction helper) {
        switch (helper.getOpcode()) {
            case 0x10: // bipush
            case 0x11: // sipush
                message.append(" ").append(helper.getValue());
                break;
            case 0x84: // iinc
                message.append(" by ").append(helper.getValue());
                break;
            case 0xc4: // wide
                if (helper.getByte(1) == 0x84 /* iinc */) {
                    message.append(" by ").append(helper.getValue());
                }
                break;
            default:
                // Do nothing
                break;
        }
    }
}
