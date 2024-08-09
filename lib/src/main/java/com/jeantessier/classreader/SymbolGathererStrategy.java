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

package com.jeantessier.classreader;

public interface SymbolGathererStrategy {
    boolean isMatching(Classfile classfile);
    boolean isMatching(Field_info field);
    boolean isMatching(Method_info method);
    boolean isMatching(LocalVariable localVariable);
    boolean isMatching(InnerClass innerClass);

    class MethodLocator implements Visitor {
        private Method_info method;

        public Method_info getMethod() {
            return method;
        }

        public void visitMethod_info(Method_info entry) {
            method = entry;
        }

        public void visitCode_attribute(Code_attribute attribute) {
            attribute.getOwner().accept(this);
        }

        public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
            attribute.getOwner().accept(this);
        }

        public void visitLocalVariable(LocalVariable helper) {
            helper.getLocalVariableTable().accept(this);
        }
    }

    default Method_info locateMethodFor(LocalVariable localVariable) {
        var methodLocator = new MethodLocator();
        localVariable.accept(methodLocator);
        return methodLocator.getMethod();
    }

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

    default Classfile locateClassfileFor(InnerClass innerClass) {
        var classfileLocator = new ClassfileLocator();
        innerClass.accept(classfileLocator);
        return classfileLocator.getClassfile();
    }
}
