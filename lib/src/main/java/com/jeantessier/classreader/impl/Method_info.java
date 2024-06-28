/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import com.jeantessier.classreader.*;

import static java.util.stream.Collectors.*;

public class Method_info extends Feature_info implements com.jeantessier.classreader.Method_info {
    private static final int ACC_SYNCHRONIZED = 0x0020;
    private static final int ACC_BRIDGE = 0x0040;
    private static final int ACC_VARARGS = 0x0080;
    private static final int ACC_NATIVE = 0x0100;
    private static final int ACC_ABSTRACT = 0x0400;
    private static final int ACC_STRICT = 0x0800;

    public Method_info(Classfile classfile, DataInput in) throws IOException {
        super(classfile, in);
    }

    public Method_info(Classfile classfile, DataInput in, AttributeFactory attributeFactory) throws IOException {
        super(classfile, in, attributeFactory);
    }

    public String getFeatureType() {
        return "method";
    }

    public boolean isSynchronized() {
        return (getAccessFlags() & ACC_SYNCHRONIZED) != 0;
    }

    public boolean isBridge() {
        return (getAccessFlags() & ACC_BRIDGE) != 0;
    }

    public boolean isVarargs() {
        return (getAccessFlags() & ACC_VARARGS) != 0;
    }

    public boolean isNative() {
        return (getAccessFlags() & ACC_NATIVE) != 0;
    }

    public boolean isAbstract() {
        return (getAccessFlags() & ACC_ABSTRACT) != 0;
    }

    public boolean isStrict() {
        return (getAccessFlags() & ACC_STRICT) != 0;
    }

    public boolean isConstructor() {
        return getName().equals("<init>");
    }

    public boolean isStaticInitializer() {
        return getName().equals("<clinit>");
    }

    public Collection<Class_info> getExceptions() {
        return getAttributes().stream()
                .filter(attribute -> attribute instanceof Exceptions_attribute)
                .map(attribute -> ((Exceptions_attribute) attribute).getExceptions())
                .findAny()
                .orElse(Collections.emptyList());
    }

    public String getSignature() {
        StringBuilder result = new StringBuilder();

        if (isConstructor()) {
            result.append(getClassfile().getSimpleName());
            result.append(DescriptorHelper.getSignature(getDescriptor()));
        } else if (isStaticInitializer()) {
            result.append("static {}");
        } else {
            result.append(getName());
            result.append(DescriptorHelper.getSignature(getDescriptor()));
        }

        return result.toString();
    }

    public String getReturnType() {
        return DescriptorHelper.getReturnType(getDescriptor());
    }

    public String getDeclaration() {
        StringBuilder result = new StringBuilder();

        if (isPublic()) result.append("public ");
        if (isProtected()) result.append("protected ");
        if (isPrivate()) result.append("private ");
        if (isStatic() && !isStaticInitializer()) result.append("static ");
        if (isFinal()) result.append("final ");
        if (isSynchronized()) result.append("synchronized ");
        if (isNative()) result.append("native ");
        if (isAbstract()) result.append("abstract ");

        if (!isConstructor() && !isStaticInitializer()) {
            result.append((getReturnType() != null) ? getReturnType() : "void").append(" ");
        }

        result.append(getSignature());

        if (!getExceptions().isEmpty()) {
            result.append(" throws ");
            result.append(
                    getExceptions().stream()
                            .map(Class_info::getName)
                            .collect(joining(", "))
            );
        }

        return result.toString();
    }

    public com.jeantessier.classreader.Code_attribute getCode() {
        CodeFinder finder = new CodeFinder();
        accept(finder);
        return finder.getCode();
    }

    public void accept(Visitor visitor) {
        visitor.visitMethod_info(this);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        return compareTo((Method_info) object) == 0;
    }

    public int hashCode() {
        return getSignature().hashCode();
    }

    public int compareTo(com.jeantessier.classreader.Method_info other) {
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

        int nameCompare = getName().compareTo(other.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }

        return getDescriptor().compareTo(other.getDescriptor());
    }
}
