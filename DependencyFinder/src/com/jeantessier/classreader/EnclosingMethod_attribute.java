/*
 *  Copyright (c) 2001-2007, Jean Tessier
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
