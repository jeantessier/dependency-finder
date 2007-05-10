package com.jeantessier.classreader;

import java.io.*;

import org.apache.log4j.*;

public class EnclosingMethod_attribute extends Attribute_info {
    private int classIndex;
    private int methodIndex;

    public EnclosingMethod_attribute(Classfile classfile, Visitable owner, DataInputStream in) throws IOException {
        super(classfile, owner);

        int byteCount = in.readInt();
        Logger.getLogger(getClass()).debug("Attribute length: " + byteCount);

        classIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Class index: " + classIndex + " (" + getClassInfo() + ")");

        methodIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Method index: " + methodIndex + " (" + getMethod() + ")");
    }

    public int getClassIndex() {
        return classIndex;
    }

    public Class_info getRawClassInfo() {
        return (Class_info) getClassfile().getConstantPool().get(getClassIndex());
    }

    public String getClassInfo() {
        String result = "";

        if (getClassIndex() != 0) {
            result = getRawClassInfo().toString();
        }

        return result;
    }

    public int getMethodIndex() {
        return methodIndex;
    }

    public NameAndType_info getRawMethod() {
        return (NameAndType_info) getClassfile().getConstantPool().get(getMethodIndex());
    }

    public String getMethod() {
        String result = "";

        if (getMethodIndex() != 0) {
            result = getRawMethod().toString();
        }

        return result;
    }

    public String toString() {
        return "Enclosing method \"" + getClassInfo() + "." + getMethod() + "\"";
    }

    public void accept(Visitor visitor) {
        visitor.visitEnclosingMethod_attribute(this);
    }
}
