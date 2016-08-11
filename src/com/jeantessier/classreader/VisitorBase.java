/*
 *  Copyright (c) 2001-2016, Jean Tessier
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

import org.apache.log4j.Logger;

import java.util.Collection;

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
        resetCount();
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
        visitClassfileFields(classfile);
        visitClassfileMethods(classfile);
        visitClassfileAttributes(classfile);
    }

    protected void visitClassfileFields(Classfile classfile) {
        for (Field_info field : classfile.getAllFields()) {
            field.accept(this);
        }
    }

    protected void visitClassfileMethods(Classfile classfile) {
        for (Method_info method : classfile.getAllMethods()) {
            method.accept(this);
        }
    }

    protected void visitClassfileAttributes(Classfile classfile) {
        visitAttributes(classfile.getAttributes());
    }

    // ConstantPool entries

    public void visitClass_info(Class_info entry) {
        // Do nothing
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        // Do nothing
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        // Do nothing
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        // Do nothing
    }

    public void visitString_info(String_info entry) {
        // Do nothing
    }

    public void visitInteger_info(Integer_info entry) {
        // Do nothing
    }

    public void visitFloat_info(Float_info entry) {
        // Do nothing
    }

    public void visitLong_info(Long_info entry) {
        // Do nothing
    }

    public void visitDouble_info(Double_info entry) {
        // Do nothing
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        // Do nothing
    }

    public void visitUTF8_info(UTF8_info entry) {
        // Do nothing
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        // Do nothing
    }

    public void visitMethodType_info(MethodType_info entry) {
        // Do nothing
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        // Do nothing
    }

    // Features

    public void visitField_info(Field_info entry) {
        visitAttributes(entry.getAttributes());
    }

    public void visitMethod_info(Method_info entry) {
        visitAttributes(entry.getAttributes());
    }

    // Attributes

    protected void visitAttributes(Collection<? extends Attribute_info> attributes) {
        for (Attribute_info attribute_info : attributes) {
            attribute_info.accept(this);
        }
    }

    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        // Do nothing
    }

    public void visitCode_attribute(Code_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting instruction(s) ...");
        visitInstructions(attribute);

        Collection<? extends ExceptionHandler> exceptionHandlers = attribute.getExceptionHandlers();
        Logger.getLogger(getClass()).debug("Visiting " + exceptionHandlers.size() + " exception handler(s) ...");
        visitExceptionHandlers(exceptionHandlers);

        Collection<? extends Attribute_info> attributes = attribute.getAttributes();
        Logger.getLogger(getClass()).debug("Visiting " + attributes.size() + " code attribute(s) ...");
        visitAttributes(attributes);
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

    public void visitSignature_attribute(Signature_attribute attribute) {
        // Do nothing
    }

    public void visitSourceFile_attribute(SourceFile_attribute attribute) {
        // Do nothing
    }

    public void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute) {
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

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getLocalVariableTypes().size() + " local variable type(s) ...");

        for (LocalVariableType localVariableType : attribute.getLocalVariableTypes()) {
            localVariableType.accept(this);
        }
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        // Do nothing
    }

    public void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute) {
        visitRuntimeAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute) {
        visitRuntimeAnnotations_attribute(attribute);
    }

    protected void visitRuntimeAnnotations_attribute(RuntimeAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getAnnotations().size() + " annotation(s) ...");

        for (Annotation annotation : attribute.getAnnotations()) {
            annotation.accept(this);
        }
    }

    public void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute) {
        visitRuntimeParameterAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute) {
        visitRuntimeParameterAnnotations_attribute(attribute);
    }

    protected void visitRuntimeParameterAnnotations_attribute(RuntimeParameterAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getParameterAnnotations().size() + " parameter annotation(s) ...");

        for (Parameter parameter : attribute.getParameterAnnotations()) {
            parameter.accept(this);
        }
    }

    public void visitAnnotationDefault_attribute(AnnotationDefault_attribute attribute) {
        attribute.getElemementValue().accept(this);
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        // Do nothing
    }

    // Attribute helpers

    protected void visitInstructions(Code_attribute attribute) {
        for (Instruction instruction : attribute) {
            instruction.accept(this);
        }
    }

    public void visitInstruction(Instruction helper) {
        // Do nothing
    }

    protected void visitExceptionHandlers(Collection<? extends ExceptionHandler> exceptionHandlers) {
        for (ExceptionHandler exceptionHandler : exceptionHandlers) {
            exceptionHandler.accept(this);
        }
    }

    public void visitExceptionHandler(ExceptionHandler helper) {
        // Do nothing
    }

    public void visitInnerClass(InnerClass helper) {
        // Do nothing
    }

    public void visitLineNumber(LineNumber helper) {
        // Do nothing
    }

    public void visitLocalVariable(LocalVariable helper) {
        // Do nothing
    }

    public void visitLocalVariableType(LocalVariableType helper) {
        // Do nothing
    }

    public void visitParameter(Parameter helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getAnnotations().size() + " annotation(s) ...");

        for (Annotation annotation : helper.getAnnotations()) {
            annotation.accept(this);
        }
    }

    public void visitAnnotation(Annotation helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getElementValuePairs().size() + " element value pair(s) ...");

        for (ElementValuePair elementValuePair : helper.getElementValuePairs()) {
            elementValuePair.accept(this);
        }
    }

    public void visitElementValuePair(ElementValuePair helper) {
        helper.getElementValue().accept(this);
    }

    public void visitByteConstantElementValue(ByteConstantElementValue helper) {
        // Do nothing
    }

    public void visitCharConstantElementValue(CharConstantElementValue helper) {
        // Do nothing
    }

    public void visitDoubleConstantElementValue(DoubleConstantElementValue helper) {
        // Do nothing
    }

    public void visitFloatConstantElementValue(FloatConstantElementValue helper) {
        // Do nothing
    }

    public void visitIntegerConstantElementValue(IntegerConstantElementValue helper) {
        // Do nothing
    }

    public void visitLongConstantElementValue(LongConstantElementValue helper) {
        // Do nothing
    }

    public void visitShortConstantElementValue(ShortConstantElementValue helper) {
        // Do nothing
    }

    public void visitBooleanConstantElementValue(BooleanConstantElementValue helper) {
        // Do nothing
    }

    public void visitStringConstantElementValue(StringConstantElementValue helper) {
        // Do nothing
    }

    public void visitEnumElementValue(EnumElementValue helper) {
        // Do nothing
    }

    public void visitClassElementValue(ClassElementValue helper) {
        // Do nothing
    }

    public void visitAnnotationElementValue(AnnotationElementValue helper) {
        helper.getAnnotation().accept(this);
    }

    public void visitArrayElementValue(ArrayElementValue helper) {
        Logger.getLogger(getClass()).debug("Visiting " + helper.getValues().size() + " value(s) ...");

        for (ElementValue elementValue : helper.getValues()) {
            elementValue.accept(this);
        }
    }
}
