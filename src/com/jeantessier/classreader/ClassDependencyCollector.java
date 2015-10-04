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

package com.jeantessier.classreader;

public class ClassDependencyCollector extends CollectorBase {
    private Class_info thisClass;
    private boolean top = true;

    public void visitClassfile(Classfile classfile) {
        thisClass = classfile.getRawClass();

        classfile.getConstantPool().accept(this);

        classfile.getRawSuperclass().accept(this);

        for (Class_info class_info : classfile.getAllInterfaces()) {
            class_info.accept(this);
        }

        for (Field_info field : classfile.getAllFields()) {
            field.accept(this);
        }

        for (Method_info method : classfile.getAllMethods()) {
            method.accept(this);
        }
    }

    public void visitClass_info(Class_info entry) {
        String classname = entry.getName();
    
        if (entry != thisClass) {
            if (classname.startsWith("[") ) {
                top = false;
                entry.getRawName().accept(this);
                top = true;
            } else {
                add(classname);
            }
        }
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        if (top) {
            if (entry.getRawClass() == thisClass) {
                top = false;
                entry.getRawNameAndType().accept(this);
                top = true;
            }
        } else {
            entry.getRawNameAndType().accept(this);
        }
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        if (top) {
            if (entry.getRawClass() == thisClass) {
                top = false;
                entry.getRawNameAndType().accept(this);
                top = true;
            }
        } else {
            entry.getRawNameAndType().accept(this);
        }
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        if (top) {
            if (entry.getRawClass() == thisClass) {
                top = false;
                entry.getRawNameAndType().accept(this);
                top = true;
            }
        } else {
            entry.getRawNameAndType().accept(this);
        }
    }

    public void visitString_info(String_info entry) {
        if (!top) {
            entry.getRawValue().accept(this);
        }
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        if (!top) {
            entry.getRawType().accept(this);
        }
    }

    public void visitUTF8_info(UTF8_info entry) {
        if (!top) {
            processSignature(entry.getValue());
        }
    }

    public void visitField_info(Field_info entry) {
        processSignature(entry.getDescriptor());
    
        super.visitField_info(entry);
    }

    public void visitMethod_info(Method_info entry) {
        processSignature(entry.getDescriptor());
    
        super.visitMethod_info(entry);
    }

    public void visitLocalVariable(LocalVariable helper) {
        processSignature(helper.getDescriptor());

        super.visitLocalVariable(helper);
    }

    private void processSignature(String str) {
        int currentPos = 0;
        int startPos;
        int endPos;

        while ((startPos = str.indexOf('L', currentPos)) != -1) {
            if ((endPos = str.indexOf(';', startPos)) != -1) {
                String candidate = str.substring(startPos + 1, endPos);
                if (!thisClass.getName().equals(candidate)) {
                    add(ClassNameHelper.path2ClassName(candidate));
                }
                currentPos = endPos + 1;
            } else {
                currentPos = startPos + 1;
            }
        }
    }
}
