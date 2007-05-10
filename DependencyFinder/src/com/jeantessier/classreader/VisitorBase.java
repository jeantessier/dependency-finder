/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

import org.apache.log4j.*;

public abstract class VisitorBase implements Visitor {
    private int currentCount;

    protected void resetCount() {
        currentCount = 0;
    }

    protected void incrementCount() {
        currentCount++;
    }

    protected int currentCount() {
        return currentCount;
    }

    public void visitConstantPool(ConstantPool constantPool) {
        for (ConstantPoolEntry entry : constantPool) {
            if (entry != null) {
                entry.accept(this);
            }
            incrementCount();
        }
    }

    // Classfile
    public void visitClassfiles(Collection<Classfile> classfiles) {
        for (Classfile classfile : classfiles) {
            classfile.accept(this);
        }
    }

    public void visitClassfile(Classfile classfile) {
        for (Attribute_info attribute : classfile.getAttributes()) {
            attribute.accept(this);
        }

        for (Field_info field : classfile.getAllFields()) {
            field.accept(this);
        }

        for (Method_info method : classfile.getAllMethods()) {
            method.accept(this);
        }
    }

    // ConstantPool entries
    public void visitClass_info(Class_info entry) {}
    public void visitFieldRef_info(FieldRef_info entry) {}
    public void visitMethodRef_info(MethodRef_info entry) {}
    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {}
    public void visitString_info(String_info entry) {}
    public void visitInteger_info(Integer_info entry) {}
    public void visitFloat_info(Float_info entry) {}
    public void visitLong_info(Long_info entry) {}
    public void visitDouble_info(Double_info entry) {}
    public void visitNameAndType_info(NameAndType_info entry) {}
    public void visitUTF8_info(UTF8_info entry) {}

    // Features
    public void visitField_info(Field_info entry) {
        for (Attribute_info attribute : entry.getAttributes()) {
            attribute.accept(this);
        }
    }

    public void visitMethod_info(Method_info entry) {
        for (Attribute_info attribute : entry.getAttributes()) {
            attribute.accept(this);
        }
    }

    // Attributes
    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        // Do nothing
    }

    public void visitCode_attribute(Code_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting instruction(s) ...");
        for (Instruction instruction : attribute) {
            instruction.accept(this);
        }

        Logger.getLogger(getClass()).debug("Visiting " + attribute.getExceptionHandlers().size() + " exception handler(s) ...");
        for (ExceptionHandler exceptionHandler : attribute.getExceptionHandlers()) {
            exceptionHandler.accept(this);
        }
        
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getAttributes().size() + " code attribute(s) ...");
        for (Attribute_info attribute_info : attribute.getAttributes()) {
            attribute_info.accept(this);
        }
    }

    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getExceptions().size() + " exception class(es) ...");

        for (Class_info exception : attribute.getExceptions()) {
            exception.accept(this);
        }
    }
    
    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getInnerClasses().size() + " inner class(es) ...");

        for (InnerClass innerClass : attribute.getInnerClasses()) {
            innerClass.accept(this);
        }
    }

    public void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute) {
        // Do nothing
    }

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        // Do nothing
    }
    
    public void visitSourceFile_attribute(SourceFile_attribute attribute) {
        // Do nothing
    }
    
    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getLineNumbers().size() + " line number(s) ...");

        for (LineNumber lineNumber : attribute.getLineNumbers()) {
            lineNumber.accept(this);
        }
    }
    
    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getLocalVariables().size() + " local variable(s) ...");

        for (LocalVariable localVariable : attribute.getLocalVariables()) {
            localVariable.accept(this);
        }
    }
    
    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        // Do nothing
    }
    
    public void visitCustom_attribute(Custom_attribute attribute) {
        // Do nothing
    }

    // Attribute helpers
    public void visitInstruction(Instruction helper) {}
    public void visitExceptionHandler(ExceptionHandler helper) {}
    public void visitInnerClass(InnerClass helper) {}
    public void visitLineNumber(LineNumber helper) {}
    public void visitLocalVariable(LocalVariable helper) {}
}
