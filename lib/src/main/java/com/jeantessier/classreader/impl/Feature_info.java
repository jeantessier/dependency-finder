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
import java.util.*;

import org.apache.logging.log4j.*;

public abstract class Feature_info implements com.jeantessier.classreader.Feature_info {
    private static final int ACC_PUBLIC = 0x0001;
    private static final int ACC_PRIVATE = 0x0002;
    private static final int ACC_PROTECTED = 0x0004;
    private static final int ACC_STATIC = 0x0008;
    private static final int ACC_FINAL = 0x0010;
    private static final int ACC_SYNTHETIC = 0x1000;

    private final Classfile classfile;
    private final int accessFlags;
    private final int nameIndex;
    private final int descriptorIndex;
    private final Collection<Attribute_info> attributes = new LinkedList<>();

    public Feature_info(Classfile classfile, DataInput in) throws IOException {
        this(classfile, in, new AttributeFactory());
    }

    public Feature_info(Classfile classfile, DataInput in, AttributeFactory attributeFactory) throws IOException {
        this.classfile = classfile;

        accessFlags = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("{} access flags: {}", getFeatureType(), accessFlags);

        nameIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("{} name: {} ({})", getFeatureType(), nameIndex, getName());

        descriptorIndex = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("{} descriptor: {} ({})", getFeatureType(), descriptorIndex, getDescriptor());

        int attributeCount = in.readUnsignedShort();
        LogManager.getLogger(getClass()).debug("Reading {} {} attribute(s)", attributeCount, getFeatureType());
        for (int i=0; i<attributeCount; i++) {
            LogManager.getLogger(getClass()).debug("{} attribute {}:", getFeatureType(), i);
            attributes.add(attributeFactory.create(getClassfile().getConstantPool(), this, in));
        }
    }

    public Classfile getClassfile() {
        return classfile;
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

    public int getNameIndex() {
        return nameIndex;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public Collection<Attribute_info> getAttributes() {
        return attributes;
    }

    public boolean isSynthetic() {
        return isSyntheticFromAccessFlag() || isSyntheticFromAttribute();
    }

    private boolean isSyntheticFromAccessFlag() {
        return (getAccessFlags() & ACC_SYNTHETIC) != 0;
    }

    private boolean isSyntheticFromAttribute() {
        return getAttributes().stream().anyMatch(attribute -> attribute instanceof Synthetic_attribute);
    }

    public String getUniqueName() {
        return getSignature();
    }

    /**
     * Only used for pretty logging in constructor.
     * @return a printable string as to whether this is a field or a method
     */
    protected abstract String getFeatureType();

    public String toString() {
        return getFullUniqueName();
    }
}
