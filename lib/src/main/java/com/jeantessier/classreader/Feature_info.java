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

package com.jeantessier.classreader;

import java.util.*;

public interface Feature_info extends Deprecatable, Visitable {
    Classfile getClassfile();

    int getAccessFlags();

    boolean isPublic();
    boolean isProtected();
    boolean isPrivate();
    boolean isPackage();

    boolean isStatic();
    boolean isFinal();

    int getNameIndex();
    default UTF8_info getRawName() {
        return (UTF8_info) getClassfile().getConstantPool().get(getNameIndex());
    }
    default String getName() {
        return getRawName().getValue();
    }
    default String getFullName() {
        return getClassfile().getClassName() + "." + getName();
    }

    int getDescriptorIndex();
    default UTF8_info getRawDescriptor() {
        return (UTF8_info) getClassfile().getConstantPool().get(getDescriptorIndex());
    }
    default String getDescriptor() {
        return getRawDescriptor().getValue();
    }

    Collection<? extends Attribute_info> getAttributes();

    boolean isSynthetic();
    default boolean isDeprecated() {
        return getAttributes().stream().anyMatch(attribute -> attribute instanceof Deprecated_attribute);
    }
    default boolean isGeneric() {
        SignatureFinder finder = new SignatureFinder();
        accept(finder);
        return finder.getSignature() != null;
    }

    String getDeclaration();
    String getSignature();
    default String getFullSignature() {
        return getClassfile().getClassName() + "." + getSignature();
    }

    String getUniqueName();
    default String getFullUniqueName() {
        return getClassfile().getClassName() + "." + getUniqueName();
    }
}
