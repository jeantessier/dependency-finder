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

import com.jeantessier.classreader.DescriptorHelper;
import com.jeantessier.classreader.Visitor;

import java.io.DataInput;
import java.io.IOException;

public class Field_info extends Feature_info implements com.jeantessier.classreader.Field_info {
    private static final int ACC_VOLATILE  = 0x0040;
    private static final int ACC_TRANSIENT = 0x0080;
    private static final int ACC_ENUM = 0x4000;

    public Field_info(Classfile classfile, DataInput in) throws IOException {
        super(classfile, in);
    }

    public Field_info(Classfile classfile, DataInput in, AttributeFactory attributeFactory) throws IOException {
        super(classfile, in, attributeFactory);
    }

    public String getFeatureType() {
        return "field";
    }

    public boolean isVolatile() {
        return (getAccessFlags() & ACC_VOLATILE) != 0;
    }

    public boolean isTransient() {
        return (getAccessFlags() & ACC_TRANSIENT) != 0;
    }

    public boolean isEnum() {
        return (getAccessFlags() & ACC_ENUM) != 0;
    }

    public String getType() {
        return DescriptorHelper.getType(getDescriptor());
    }

    public String getDeclaration() {
        StringBuilder result = new StringBuilder();

        if (isPublic()) result.append("public ");
        if (isProtected()) result.append("protected ");
        if (isPrivate()) result.append("private ");
        if (isStatic()) result.append("static ");
        if (isFinal()) result.append("final ");
        if (isVolatile()) result.append("volatile ");
        if (isTransient()) result.append("transient ");
    
        result.append(getType()).append(" ");
        result.append(getName());

        return result.toString();
    }

    public String getFullDeclaration() {
        String result = getDeclaration();

        if (getConstantValue() != null) {
            if (getConstantValue().getRawValue() instanceof String_info) {
                result += " = \"" + getConstantValue().getRawValue() + "\"";
            } else {
                result += " = " + getConstantValue().getRawValue();
            }
        }

        return result;
    }

    public String getSignature() {
        return getName();
    }

    public ConstantValue_attribute getConstantValue() {
        return (ConstantValue_attribute) getAttributes().stream()
                .filter(attribute -> attribute instanceof ConstantValue_attribute)
                .findAny()
                .orElse(null);
    }

    public void accept(Visitor visitor) {
        visitor.visitField_info(this);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return compareTo((Field_info) object) == 0;
    }

    public int hashCode() {
        return getSignature().hashCode();
    }

    public int compareTo(com.jeantessier.classreader.Field_info other) {
        if (this == other) {
            return 0;
        }

        if (other == null) {
            throw new ClassCastException("compareTo: expected a " + getClass().getName() + " but got null");
        }

        int classCompare = getClassfile().compareTo(other.getClassfile());
        if (classCompare != 0) {
            return classCompare;
        }

        return getName().compareTo(other.getName());
    }
}
