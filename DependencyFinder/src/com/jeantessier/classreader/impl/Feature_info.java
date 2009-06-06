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

import com.jeantessier.classreader.*;

public abstract class Feature_info implements com.jeantessier.classreader.Feature_info {
    private static final int ACC_PUBLIC = 0x0001;
    private static final int ACC_PRIVATE = 0x0002;
    private static final int ACC_PROTECTED = 0x0004;
    private static final int ACC_STATIC = 0x0008;
    private static final int ACC_FINAL = 0x0010;
    private static final int ACC_SYNTHETIC = 0x1000;

    private Classfile classfile;
    private int accessFlag;
    private int nameIndex;
    private int descriptorIndex;
    private Collection<Attribute_info> attributes = new LinkedList<Attribute_info>();

    public Feature_info(Classfile classfile, DataInput in) throws IOException {
        this(classfile, in, new AttributeFactory());
    }

    public Feature_info(Classfile classfile, DataInput in, AttributeFactory attributeFactory) throws IOException {
        this.classfile = classfile;

        accessFlag = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug(getFeatureType() + " access flag: " + accessFlag);

        nameIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug(getFeatureType() + " name: " + nameIndex + " (" + getName() + ")");

        descriptorIndex = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug(getFeatureType() + " Descriptor: " + descriptorIndex + " (" + getDescriptor() + ")");

        int attributeCount = in.readUnsignedShort();
        Logger.getLogger(getClass()).debug("Reading " + attributeCount + " " + getFeatureType() + " attribute(s)");
        for (int i=0; i<attributeCount; i++) {
            Logger.getLogger(getClass()).debug(getFeatureType() + " attribute " + i + ":");
            attributes.add(attributeFactory.create(getClassfile().getConstantPool(), this, in));
        }
    }

    public Classfile getClassfile() {
        return classfile;
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

    public int getNameIndex() {
        return nameIndex;
    }

    public UTF8_info getRawName() {
        return (UTF8_info) getClassfile().getConstantPool().get(nameIndex);
    }

    public String getName() {
        return getRawName().getValue();
    }

    public String getFullName() {
        return getClassfile().getClassName() + "." + getName();
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public UTF8_info getRawDescriptor() {
        return (UTF8_info) getClassfile().getConstantPool().get(descriptorIndex);
    }

    public String getDescriptor() {
        return getRawDescriptor().getValue();
    }

    public Collection<Attribute_info> getAttributes() {
        return attributes;
    }

    public boolean isSynthetic() {
        return isSyntheticFromAccessFlag() || isSyntheticFromAttribute();
    }

    private boolean isSyntheticFromAccessFlag() {
        return (getAccessFlag() & ACC_SYNTHETIC) != 0;
    }

    private boolean isSyntheticFromAttribute() {
        boolean result = false;

        Iterator i = getAttributes().iterator();
        while (!result && i.hasNext()) {
            result = i.next() instanceof Synthetic_attribute;
        }

        return result;
    }

    public boolean isDeprecated() {
        boolean result = false;

        Iterator i = getAttributes().iterator();
        while (!result && i.hasNext()) {
            result = i.next() instanceof Deprecated_attribute;
        }

        return result;
    }

    public boolean isGeneric() {
        SignatureFinder finder = new SignatureFinder();
        accept(finder);
        return finder.getSignature() != null;
    }

    public String getFullSignature() {
        return getClassfile().getClassName() + "." + getSignature();
    }

    /**
     * Only used for pretty logging in constructor.
     * @return a printable string as to whether this is a field or a method
     */
    protected abstract String getFeatureType();

    public String toString() {
        return getFullName();
    }
}
