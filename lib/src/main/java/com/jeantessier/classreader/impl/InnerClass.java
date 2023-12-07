/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

    private final InnerClasses_attribute innerClasses;
    private final int innerClassInfoIndex;
    private final int outerClassInfoIndex;
    private final int innerNameIndex;
    private final int accessFlags;

    public InnerClass(InnerClasses_attribute innerClasses, DataInput in) throws IOException {
        this.innerClasses = innerClasses;

        innerClassInfoIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Inner class info index: " + innerClassInfoIndex + " (" + getInnerClassInfo() + ")");

        outerClassInfoIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Outer class info index: " + outerClassInfoIndex + " (" + getOuterClassInfo() + ")");

        innerNameIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Inner name index: " + innerNameIndex + " (" + getInnerName() + ")");

        accessFlags = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Inner class access flags: " + accessFlags);
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

    public int getAccessFlags() {
        return accessFlags;
    }

    public boolean isPublic() {
        return (getAccessFlags() & ACC_PUBLIC) != 0;
    }

    public boolean isProtected() {
        return (getAccessFlags() & ACC_PROTECTED) != 0;
    }

    public boolean isPrivate() {
        return (getAccessFlags() & ACC_PRIVATE) != 0;
    }

    public boolean isPackage() {
        return (getAccessFlags() & (ACC_PUBLIC | ACC_PROTECTED | ACC_PRIVATE)) == 0;
    }

    public boolean isStatic() {
        return (getAccessFlags() & ACC_STATIC) != 0;
    }

    public boolean isFinal() {
        return (getAccessFlags() & ACC_FINAL) != 0;
    }

    public boolean isInterface() {
        return (getAccessFlags() & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (getAccessFlags() & ACC_ABSTRACT) != 0;
    }

    public boolean isSynthetic() {
        return (getAccessFlags() & ACC_SYNTHETIC) != 0;
    }

    public boolean isAnnotation() {
        return (getAccessFlags() & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (getAccessFlags() & ACC_ENUM) != 0;
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

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return compareTo((InnerClass) object) == 0;
    }

    public int hashCode() {
        return getInnerClassInfo().hashCode();
    }

    public int compareTo(com.jeantessier.classreader.InnerClass other) {
        if (this == other) {
            return 0;
        }

        if (other == null) {
            throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got null");
        }

        int outerClassCompare = getOuterClassInfo().compareTo(other.getOuterClassInfo());
        if (outerClassCompare != 0) {
            return outerClassCompare;
        }

        return getInnerClassInfo().compareTo(other.getInnerClassInfo());
    }
}
