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

public abstract class FeatureRef_info extends ConstantPoolEntry implements com.jeantessier.classreader.FeatureRef_info {
    private int classIndex;
    private int nameAndTypeIndex;

    public FeatureRef_info(ConstantPool constantPool, DataInput in) throws IOException {
        super(constantPool);

        classIndex = in.readUnsignedShort();
        nameAndTypeIndex = in.readUnsignedShort();
    }

    public int getClassIndex() {
        return classIndex;
    }

    public Class_info getRawClass() {
        return (Class_info) getConstantPool().get(getClassIndex());
    }

    public String getClassName() {
        return getRawClass().getName();
    }

    public String getClassSimpleName() {
        return getRawClass().getSimpleName();
    }

    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }

    public NameAndType_info getRawNameAndType() {
        return (NameAndType_info) getConstantPool().get(getNameAndTypeIndex());
    }

    public String getNameAndType() {
        NameAndType_info nat = getRawNameAndType();
        return nat.getName() + nat.getType();
    }

    public abstract String getName();

    public String getFullName() {
        return getClassName() + "." + getName();
    }

    public abstract String getSignature();

    public String getFullSignature() {
        return getClassName() + "." + getSignature();
    }

    public String toString() {
        StringBuffer result = new StringBuffer();

        Class_info       c   = getRawClass();
        NameAndType_info nat = getRawNameAndType();

        result.append(c).append(".").append(nat.getName()).append(nat.getType());

        return result.toString();
    }

    public int hashCode() {
        return getRawClass().hashCode() ^ getRawNameAndType().hashCode();
    }

    public boolean equals(Object object) {
        boolean result = false;

        if (this == object) {
            result = true;
        } else if (object != null && this.getClass().equals(object.getClass())) {
            FeatureRef_info other = (FeatureRef_info) object;
            result = this.getRawClass().equals(other.getRawClass()) && this.getRawNameAndType().equals(other.getRawNameAndType());
        }

        return result;
    }
}
