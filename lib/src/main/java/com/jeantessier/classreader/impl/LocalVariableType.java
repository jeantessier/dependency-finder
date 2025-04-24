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

import java.io.*;

import org.apache.logging.log4j.*;

import com.jeantessier.classreader.*;

public class LocalVariableType implements com.jeantessier.classreader.LocalVariableType {
    private final LocalVariableTypeTable_attribute localVariableTypeTable;
    private final int startPC;
    private final int length;
    private final int nameIndex;
    private final int signatureIndex;
    private final int index;

    public LocalVariableType(LocalVariableTypeTable_attribute localVariableTypeTable, DataInput in) throws IOException {
        this.localVariableTypeTable = localVariableTypeTable;

        startPC = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("start PC: {}", startPC);

        length = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("length: {}", length);

        nameIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("name: {} ({})", nameIndex, getName());

        signatureIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("signature: {} ({})", signatureIndex, getSignature());

        index = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("index: {}", index);
    }

    public LocalVariableTypeTable_attribute getLocalVariableTypeTable() {
        return localVariableTypeTable;
    }

    public int getStartPC() {
        return startPC;
    }

    public int getLength() {
        return length;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public UTF8_info getRawName() {
        return (UTF8_info) getLocalVariableTypeTable().getConstantPool().get(getNameIndex());
    }

    public String getName() {
        return getRawName().getValue();
    }

    public int getSignatureIndex() {
        return signatureIndex;
    }

    public UTF8_info getRawSignature() {
        return (UTF8_info) getLocalVariableTypeTable().getConstantPool().get(getSignatureIndex());
    }

    public String getSignature() {
        return getRawSignature().getValue();
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return "Local variable type " + getSignature() + " " + getName();
    }

    public void accept(Visitor visitor) {
        visitor.visitLocalVariableType(this);
    }
}
