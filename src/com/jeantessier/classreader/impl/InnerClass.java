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

import org.apache.log4j.*;

import com.jeantessier.classreader.*;

public class InnerClass implements com.jeantessier.classreader.InnerClass {
    private static final int ACC_PUBLIC = 0x0001;
    private static final int ACC_PRIVATE = 0x0002;
    private static final int ACC_PROTECTED = 0x0004;
    private static final int ACC_STATIC = 0x0008;
    private static final int ACC_FINAL = 0x0010;
    private static final int ACC_INTERFACE = 0x0200;
    private static final int ACC_ABSTRACT = 0x0400;
    private static final int ACC_SYNTHETIC = 0x1000;
    private static final int ACC_ANNOTATION = 0x2000;
    private static final int ACC_ENUM = 0x4000;

    private InnerClasses_attribute innerClasses;
    private int innerClassInfoIndex;
    private int outerClassInfoIndex;
    private int innerNameIndex;
    private int accessFlag;

    public InnerClass(InnerClasses_attribute innerClasses, DataInput in) throws IOException {
        this.innerClasses = innerClasses;

        innerClassInfoIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Inner class info index: " + innerClassInfoIndex + " (" + getInnerClassInfo() + ")");

        outerClassInfoIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Outer class info index: " + outerClassInfoIndex + " (" + getOuterClassInfo() + ")");

        innerNameIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Inner name index: " + innerNameIndex + " (" + getInnerName() + ")");

        accessFlag = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Inner class access flag: " + accessFlag);
    }

    public InnerClasses_attribute getInnerClasses() {
        return innerClasses;
    }

    public int getInnerClassInfoIndex() {
        return innerClassInfoIndex;
    }

    public Class_info getRawInnerClassInfo() {
        return (Class_info) innerClasses.getConstantPool().get(getInnerClassInfoIndex());
    }

    public String getInnerClassInfo() {
        String result = "";

        if (getInnerClassInfoIndex() != 0) {
            result = getRawInnerClassInfo().getName();
        }

        return result;
    }

    public int getOuterClassInfoIndex() {
        return outerClassInfoIndex;
    }

    public Class_info getRawOuterClassInfo() {
        return (Class_info) innerClasses.getConstantPool().get(getOuterClassInfoIndex());
    }

    public String getOuterClassInfo() {
        String result = "";

        if (getOuterClassInfoIndex() != 0) {
            result = getRawOuterClassInfo().getName();
        }

        return result;
    }

    public int getInnerNameIndex() {
        return innerNameIndex;
    }

    public UTF8_info getRawInnerName() {
        return (UTF8_info) innerClasses.getConstantPool().get(getInnerNameIndex());
    }

    public String getInnerName() {
        String result = "";

        if (getInnerNameIndex() != 0) {
            result = getRawInnerName().getValue();
        }

        return result;
    }

    public int getAccessFlag() {
        return accessFlag;
    }

    public boolean isPublic() {
        return (getAccessFlag() & ACC_PUBLIC) != 0;
    }

    public boolean isProtected() {
        return (getAccessFlag() & ACC_PROTECTED) != 0;
    }

    public boolean isPrivate() {
        return (getAccessFlag() & ACC_PRIVATE) != 0;
    }

    public boolean isPackage() {
        return (getAccessFlag() & (ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE)) == 0;
    }

    public boolean isStatic() {
        return (getAccessFlag() & ACC_STATIC) != 0;
    }

    public boolean isFinal() {
        return (getAccessFlag() & ACC_FINAL) != 0;
    }

    public boolean isInterface() {
        return (getAccessFlag() & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (getAccessFlag() & ACC_ABSTRACT) != 0;
    }

    public boolean isSynthetic() {
        return (getAccessFlag() & ACC_SYNTHETIC) != 0;
    }

    public boolean isAnnotation() {
        return (getAccessFlag() & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (getAccessFlag() & ACC_ENUM) != 0;
    }

    public boolean isMemberClass() {
        return getOuterClassInfoIndex() != 0;
    }

    public boolean isAnonymousClass() {
        return getInnerNameIndex() == 0;
    }

    public String toString() {
        return getInnerClassInfo();
    }

    public void accept(Visitor visitor) {
        visitor.visitInnerClass(this);
    }
}
