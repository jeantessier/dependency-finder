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

import com.jeantessier.classreader.TextPrinter;
import com.jeantessier.classreader.Visitor;

import org.apache.logging.log4j.*;

import java.io.*;
import java.util.*;

public class ConstantPool extends ArrayList<com.jeantessier.classreader.ConstantPoolEntry> implements com.jeantessier.classreader.ConstantPool {
    private final Classfile classfile;

    // Visible for testing
    public ConstantPool() {
        this.classfile = null;
    }

    public ConstantPool(Classfile classfile, DataInput in) throws IOException {
        this.classfile = classfile;

        int count = in.readUnsignedShort();

        ensureCapacity(count);

        // Entry 0 is null
        add(null);

        for (int i=1; i<count; i++) {
            byte tag = in.readByte();

            LogManager.getLogger(getClass()).info("Entry {} has tag {} ({})", i, tag, ConstantPoolEntry.stringValueOf(tag));

            switch(tag) {
                case ConstantPoolEntry.CONSTANT_Class:
                    add(new Class_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Fieldref:
                    add(new FieldRef_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Methodref:
                    add(new MethodRef_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_InterfaceMethodref:
                    add(new InterfaceMethodRef_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_String:
                    add(new String_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Integer:
                    add(new Integer_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Float:
                    add(new Float_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Long:
                    add(new Long_info(this, in));
                    i++;
                    LogManager.getLogger(getClass()).info("Entry {} is unusable.", i);
                    add(new UnusableEntry(this, in, "previous entry is tagged CONSTANT_Long_info"));
                    break;
                case ConstantPoolEntry.CONSTANT_Double:
                    add(new Double_info(this, in));
                    i++;
                    LogManager.getLogger(getClass()).info("Entry {} is unusable.", i);
                    add(new UnusableEntry(this, in, "previous entry is tagged CONSTANT_Double_info"));
                    break;
                case ConstantPoolEntry.CONSTANT_NameAndType:
                    add(new NameAndType_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Utf8:
                    add(new UTF8_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_MethodHandle:
                    add(new MethodHandle_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_MethodType:
                    add(new MethodType_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Dynamic:
                    add(new Dynamic_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_InvokeDynamic:
                    add(new InvokeDynamic_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Module:
                    add(new Module_info(this, in));
                    break;
                case ConstantPoolEntry.CONSTANT_Package:
                    add(new Package_info(this, in));
                    break;
                default:
                    LogManager.getLogger(getClass()).info("Unknown Tag {}", tag);
                    break;
            }
        }
    }

    public Classfile getClassfile() {
        return classfile;
    }

    public void accept(Visitor visitor) {
        visitor.visitConstantPool(this);
    }

    public String toString() {
        var out = new StringWriter();

        try (var writer = new PrintWriter(out)) {
            writer.println("Constant Pool:");
            var printer = new TextPrinter(writer);
            accept(printer);
        }

        return out.toString();
    }
}
