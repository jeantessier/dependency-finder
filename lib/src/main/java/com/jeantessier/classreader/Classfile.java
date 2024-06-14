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

package com.jeantessier.classreader;

import java.util.*;

public interface Classfile extends Deprecatable, Visitable, Comparable<Classfile> {
    ClassfileLoader getLoader();

    int getMagicNumber();
    int getMinorVersion();
    int getMajorVersion();

    ConstantPool getConstantPool();

    int getAccessFlags();

    int getClassIndex();
    Class_info getRawClass();
    String getClassName();
    String getPackageName();
    String getSimpleName();

    boolean hasSuperclass();
    int getSuperclassIndex();
    Class_info getRawSuperclass();
    String getSuperclassName();

    Collection<? extends Class_info> getAllInterfaces();
    Class_info getInterface(String name);

    Collection<? extends Field_info> getAllFields();
    Field_info getField(String name);
    Field_info locateField(String name);

    Collection<? extends Method_info> getAllMethods();
    Method_info getMethod(String signature);
    Method_info locateMethod(String signature);

    Collection<? extends Attribute_info> getAttributes();

    boolean isPublic();
    boolean isPackage();
    boolean isFinal();
    boolean isSuper();
    boolean isInterface();
    boolean isAbstract();
    boolean isSynthetic();
    boolean isAnnotation();
    boolean isEnum();
    boolean isModule();
    boolean isDeprecated();
    boolean isGeneric();

    String getDeclaration();

    boolean isInnerClass();
    boolean isMemberClass();
    boolean isLocalClass();
    boolean isAnonymousClass();
}
