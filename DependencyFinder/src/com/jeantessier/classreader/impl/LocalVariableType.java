package com.jeantessier.classreader.impl;

import java.io.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class LocalVariableType implements com.jeantessier.classreader.LocalVariableType {
    private LocalVariableTypeTable_attribute localVariableTypeTable;
    private int startPC;
    private int length;
    private int nameIndex;
    private int signatureIndex;
    private int index;

    public LocalVariableType(LocalVariableTypeTable_attribute localVariableTypeTable, DataInputStream in) throws IOException {
        this.localVariableTypeTable = localVariableTypeTable;

        startPC = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("start PC: " + startPC);

        length = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("length: " + length);

        nameIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("name: " + nameIndex + " (" + getName() + ")");

        signatureIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("signature: " + signatureIndex + " (" + getSignature() + ")");

        index = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("index: " + index);
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
        return (UTF8_info) getLocalVariableTypeTable().getClassfile().getConstantPool().get(getNameIndex());
    }

    public String getName() {
        return getRawName().toString();
    }

    public int getSignatureIndex() {
        return signatureIndex;
    }

    public UTF8_info getRawSignature() {
        return (UTF8_info) getLocalVariableTypeTable().getClassfile().getConstantPool().get(getSignatureIndex());
    }

    public String getSignature() {
        return getRawSignature().toString();
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
