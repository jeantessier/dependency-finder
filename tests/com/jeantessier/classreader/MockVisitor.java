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

import java.util.*;

public class MockVisitor extends VisitorBase {
    private List<Classfile> visitedClassfiles = new LinkedList<Classfile>();
    private List<ConstantValue_attribute> visitedConstantValue_attributes = new LinkedList<ConstantValue_attribute>();
    private List<Code_attribute> visitedCode_attributes = new LinkedList<Code_attribute>();
    private List<Exceptions_attribute> visitedExceptions_attributes = new LinkedList<Exceptions_attribute>();
    private List<InnerClasses_attribute> visitedInnerClasses_attributes = new LinkedList<InnerClasses_attribute>();
    private List<EnclosingMethod_attribute> visitedEnclosingMethod_attributes = new LinkedList<EnclosingMethod_attribute>();
    private List<Synthetic_attribute> visitedSynthetic_attributes = new LinkedList<Synthetic_attribute>();
    private List<Signature_attribute> visitedSignature_attributes = new LinkedList<Signature_attribute>();
    private List<SourceFile_attribute> visitedSourceFile_attributes = new LinkedList<SourceFile_attribute>();
    private List<SourceDebugExtension_attribute> visitedSourceDebugExtension_attributes = new LinkedList<SourceDebugExtension_attribute>();
    private List<LineNumberTable_attribute> visitedLineNumberTable_attributes = new LinkedList<LineNumberTable_attribute>();
    private List<LocalVariableTable_attribute> visitedLocalVariableTable_attributes = new LinkedList<LocalVariableTable_attribute>();
    private List<LocalVariableTypeTable_attribute> visitedLocalVariableTypeTable_attributes = new LinkedList<LocalVariableTypeTable_attribute>();
    private List<Deprecated_attribute> visitedDeprecated_attributes = new LinkedList<Deprecated_attribute>();
    private List<Custom_attribute> visitedCustom_attributes = new LinkedList<Custom_attribute>();
    private List<Instruction> visitedInstructions = new LinkedList<Instruction>();
    private List<ExceptionHandler> visitedExceptionHandlers = new LinkedList<ExceptionHandler>();
    private List<InnerClass> visitedInnerClasses = new LinkedList<InnerClass>();
    private List<LineNumber> visitedLineNumbers = new LinkedList<LineNumber>();
    private List<LocalVariable> visitedLocalVariables = new LinkedList<LocalVariable>();
    private List<LocalVariableType> visitedLocalVariableTypes = new LinkedList<LocalVariableType>();

    public List<Classfile> getVisitedClassfiles() {
        return visitedClassfiles;
    }

    public List<ConstantValue_attribute> getVisitedConstantValue_attributes() {
        return visitedConstantValue_attributes;
    }

    public List<Code_attribute> getVisitedCode_attributes() {
        return visitedCode_attributes;
    }

    public List<Exceptions_attribute> getVisitedExceptions_attributes() {
        return visitedExceptions_attributes;
    }

    public List<InnerClasses_attribute> getVisitedInnerClasses_attributes() {
        return visitedInnerClasses_attributes;
    }


    public List<EnclosingMethod_attribute> getVisitedEnclosingMethod_attributes() {
        return visitedEnclosingMethod_attributes;
    }

    public List<Synthetic_attribute> getVisitedSynthetic_attributes() {
        return visitedSynthetic_attributes;
    }

    public List<Signature_attribute> getVisitedSignature_attributes() {
        return visitedSignature_attributes;
    }

    public List<SourceFile_attribute> getVisitedSourceFile_attributes() {
        return visitedSourceFile_attributes;
    }

    public List<SourceDebugExtension_attribute> getVisitedSourceDebugExtension_attribute() {
        return visitedSourceDebugExtension_attributes;
    }

    public List<LineNumberTable_attribute> getVisitedLineNumberTable_attributes() {
        return visitedLineNumberTable_attributes;
    }

    public List<LocalVariableTable_attribute> getVisitedLocalVariableTable_attributes() {
        return visitedLocalVariableTable_attributes;
    }

    public List<LocalVariableTypeTable_attribute> getVisitedLocalVariableTypeTable_attributes() {
        return visitedLocalVariableTypeTable_attributes;
    }

    public List<Deprecated_attribute> getVisitedDeprecated_attributes() {
        return visitedDeprecated_attributes;
    }

    public List<Custom_attribute> getVisitedCustom_attributes() {
        return visitedCustom_attributes;
    }

    public List<Instruction> getVisitedInstructions() {
        return visitedInstructions;
    }

    public List<ExceptionHandler> getVisitedExceptionHandlers() {
        return visitedExceptionHandlers;
    }

    public List<InnerClass> getVisitedInnerClasses() {
        return visitedInnerClasses;
    }

    public List<LineNumber> getVisitedLineNumbers() {
        return visitedLineNumbers;
    }

    public List<LocalVariable> getVisitedLocalVariables() {
        return visitedLocalVariables;
    }

    public List<LocalVariableType> getVisitedLocalVariableTypes() {
        return visitedLocalVariableTypes;
    }

    public void visitClassfile(Classfile classfile) {
        visitedClassfiles.add(classfile);

        super.visitClassfile(classfile);
    }

    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        visitedConstantValue_attributes.add(attribute);

        super.visitConstantValue_attribute(attribute);
    }

    public void visitCode_attribute(Code_attribute attribute) {
        visitedCode_attributes.add(attribute);

        super.visitCode_attribute(attribute);
    }

    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        visitedExceptions_attributes.add(attribute);

        super.visitExceptions_attribute(attribute);
    }

    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        visitedInnerClasses_attributes.add(attribute);

        super.visitInnerClasses_attribute(attribute);
    }

    public void visitEnclosingMethod_attribute(EnclosingMethod_attribute attribute) {
        visitedEnclosingMethod_attributes.add(attribute);

        super.visitEnclosingMethod_attribute(attribute);
    }

    public void visitSynthetic_attribute(Synthetic_attribute attribute) {
        visitedSynthetic_attributes.add(attribute);

        super.visitSynthetic_attribute(attribute);
    }

    public void visitSignature_attribute(Signature_attribute attribute) {
        visitedSignature_attributes.add(attribute);

        super.visitSignature_attribute(attribute);
    }

    public void visitSourceFile_attribute(SourceFile_attribute attribute) {
        visitedSourceFile_attributes.add(attribute);

        super.visitSourceFile_attribute(attribute);
    }

    public void visitSourceDebugExtension_attribute(SourceDebugExtension_attribute attribute) {
        visitedSourceDebugExtension_attributes.add(attribute);

        super.visitSourceDebugExtension_attribute(attribute);
    }

    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        visitedLineNumberTable_attributes.add(attribute);

        super.visitLineNumberTable_attribute(attribute);
    }

    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        visitedLocalVariableTable_attributes.add(attribute);

        super.visitLocalVariableTable_attribute(attribute);
    }

    public void visitLocalVariableTypeTable_attribute(LocalVariableTypeTable_attribute attribute) {
        visitedLocalVariableTypeTable_attributes.add(attribute);

        super.visitLocalVariableTypeTable_attribute(attribute);
    }

    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        visitedDeprecated_attributes.add(attribute);

        super.visitDeprecated_attribute(attribute);
    }

    public void visitCustom_attribute(Custom_attribute attribute) {
        visitedCustom_attributes.add(attribute);

        super.visitCustom_attribute(attribute);
    }

    public void visitInstruction(Instruction helper) {
        visitedInstructions.add(helper);

        super.visitInstruction(helper);
    }

    public void visitExceptionHandler(ExceptionHandler helper) {
        visitedExceptionHandlers.add(helper);

        super.visitExceptionHandler(helper);
    }

    public void visitInnerClass(InnerClass helper) {
        visitedInnerClasses.add(helper);

        super.visitInnerClass(helper);
    }

    public void visitLineNumber(LineNumber helper) {
        visitedLineNumbers.add(helper);

        super.visitLineNumber(helper);
    }

    public void visitLocalVariable(LocalVariable helper) {
        visitedLocalVariables.add(helper);

        super.visitLocalVariable(helper);
    }

    public void visitLocalVariableType(LocalVariableType helper) {
        visitedLocalVariableTypes.add(helper);

        super.visitLocalVariableType(helper);
    }

    public void reset() {
        visitedClassfiles.clear();
        visitedConstantValue_attributes.clear();
        visitedCode_attributes.clear();
        visitedExceptions_attributes.clear();
        visitedInnerClasses_attributes.clear();
        visitedSynthetic_attributes.clear();
        visitedSourceFile_attributes.clear();
        visitedLineNumberTable_attributes.clear();
        visitedLocalVariableTable_attributes.clear();
        visitedDeprecated_attributes.clear();
        visitedCustom_attributes.clear();
        visitedInstructions.clear();
        visitedExceptionHandlers.clear();
        visitedInnerClasses.clear();
        visitedLineNumbers.clear();
        visitedLocalVariables.clear();
    }
}
