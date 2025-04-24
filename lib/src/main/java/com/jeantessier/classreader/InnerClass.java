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

public interface InnerClass extends Visitable, Comparable<InnerClass> {
    InnerClasses_attribute getInnerClasses();

    int getInnerClassInfoIndex();
    Class_info getRawInnerClassInfo();
    String getInnerClassInfo();

    boolean hasOuterClassInfo();
    int getOuterClassInfoIndex();
    Class_info getRawOuterClassInfo();
    String getOuterClassInfo();

    boolean hasInnerName();
    int getInnerNameIndex();
    UTF8_info getRawInnerName();
    String getInnerName();

    int getAccessFlags();

    boolean isPublic();
    boolean isProtected();
    boolean isPrivate();
    boolean isPackage();

    boolean isStatic();
    boolean isFinal();
    boolean isInterface();
    boolean isAbstract();
    boolean isSynthetic();
    boolean isAnnotation();
    boolean isEnum();
    
    boolean isMemberClass();
    boolean isAnonymousClass();

    default Classfile getClassfile() {
        class ClassfileLocator implements Visitor {
            private Classfile classfile;

            public Classfile getClassfile() {
                return classfile;
            }

            public void visitClassfile(Classfile classfile) {
                this.classfile = classfile;
            }

            public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
                attribute.getOwner().accept(this);
            }

            public void visitInnerClass(InnerClass helper) {
                helper.getInnerClasses().accept(this);
            }
        }

        var classfileLocator = new ClassfileLocator();
        accept(classfileLocator);
        return classfileLocator.getClassfile();
    }
}
