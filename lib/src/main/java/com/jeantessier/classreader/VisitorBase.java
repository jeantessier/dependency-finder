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

import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.*;

public abstract class VisitorBase implements Visitor {
    protected static final int STARTING_INDEX = 1;

    private int currentIndex = STARTING_INDEX;

    protected void resetIndex() {
        currentIndex = STARTING_INDEX;
    }

    protected void incrementIndex() {
        currentIndex++;
    }

    protected int currentIndex() {
        return currentIndex;
    }

    public void visitConstantPool(ConstantPool constantPool) {
        resetIndex();

        constantPool.stream()
                .skip(1) // Constant pool indices start at 1
                .forEach(entry -> {
                    Logger.getLogger(getClass()).debug("Visiting constant pool entry " + currentIndex() + ": " + entry);
                    entry.accept(this);
                    incrementIndex();
                });
    }

    /*
     * Classfile
     */

    public void visitClassfile(Classfile classfile) {
        visitClassfileFields(classfile);
        visitClassfileMethods(classfile);
        visitClassfileAttributes(classfile);
    }

    protected void visitClassfileFields(Classfile classfile) {
        classfile.getAllFields().forEach(field -> field.accept(this));
    }

    protected void visitClassfileMethods(Classfile classfile) {
        classfile.getAllMethods().forEach(method -> method.accept(this));
    }

    protected void visitClassfileAttributes(Classfile classfile) {
        visitAttributes(classfile.getAttributes());
    }

    /*
     * ConstantPool entries
     */

    /*
     * Features
     */

    public void visitField_info(Field_info entry) {
        visitAttributes(entry.getAttributes());
    }

    public void visitMethod_info(Method_info entry) {
        visitAttributes(entry.getAttributes());
    }

    /*
     * Attributes
     */

    protected void visitAttributes(Collection<? extends Attribute_info> attributes) {
        attributes.forEach(attributeInfo -> attributeInfo.accept(this));
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

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getLocalVariableTypes().size() + " local variable type(s) ...");
        attribute.getLocalVariableTypes().forEach(localVariableType -> localVariableType.accept(this));
    }

    public void visitRuntimeVisibleAnnotations_attribute(RuntimeVisibleAnnotations_attribute attribute) {
        visitRuntimeAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleAnnotations_attribute(RuntimeInvisibleAnnotations_attribute attribute) {
        visitRuntimeAnnotations_attribute(attribute);
    }

    protected void visitRuntimeAnnotations_attribute(RuntimeAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getAnnotations().size() + " annotation(s) ...");
        attribute.getAnnotations().forEach(annotation -> annotation.accept(this));
    }

    public void visitRuntimeVisibleTypeAnnotations_attribute(RuntimeVisibleTypeAnnotations_attribute attribute) {
        visitRuntimeTypeAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleTypeAnnotations_attribute(RuntimeInvisibleTypeAnnotations_attribute attribute) {
        visitRuntimeTypeAnnotations_attribute(attribute);
    }

    protected void visitRuntimeTypeAnnotations_attribute(RuntimeTypeAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getTypeAnnotations().size() + " type annotation(s) ...");
        attribute.getTypeAnnotations().forEach(parameterAnnotation -> parameterAnnotation.accept(this));
    }

    public void visitRuntimeVisibleParameterAnnotations_attribute(RuntimeVisibleParameterAnnotations_attribute attribute) {
        visitRuntimeParameterAnnotations_attribute(attribute);
    }

    public void visitRuntimeInvisibleParameterAnnotations_attribute(RuntimeInvisibleParameterAnnotations_attribute attribute) {
        visitRuntimeParameterAnnotations_attribute(attribute);
    }

    protected void visitRuntimeParameterAnnotations_attribute(RuntimeParameterAnnotations_attribute attribute) {
        Logger.getLogger(getClass()).debug("Visiting " + attribute.getParameterAnnotations().size() + " parameter annotation(s) ...");
        attribute.getParameterAnnotations().forEach(parameterAnnotation -> parameterAnnotation.accept(this));
    }

    /*
     * Attribute helpers
     */

    protected void visitInstructions(Code_attribute attribute) {
        attribute.forEach(instruction -> instruction.accept(this));
    }

    protected void visitExceptionHandlers(Collection<? extends ExceptionHandler> exceptionHandlers) {
        exceptionHandlers.forEach(exceptionHandler -> exceptionHandler.accept(this));
    }

    public void visitSameLocals1StackItemFrame(SameLocals1StackItemFrame helper) {
        visitVerificationTypeInfos(Stream.of(helper.getStack()));
    }

    public void visitSameLocals1StackItemFrameExtended(SameLocals1StackItemFrameExtended helper) {
        visitVerificationTypeInfos(Stream.of(helper.getStack()));
    }

    public void visitAppendFrame(AppendFrame helper) {
        visitVerificationTypeInfos(helper.getLocals().stream());
    }

    public void visitFullFrame(FullFrame helper) {
        visitVerificationTypeInfos(Stream.concat(helper.getLocals().stream(), helper.getStack().stream()));
    }

    protected void visitVerificationTypeInfos(Stream<? extends VerificationTypeInfo> stacks) {
        stacks.forEach(stack -> stack.accept(this));
    }
}