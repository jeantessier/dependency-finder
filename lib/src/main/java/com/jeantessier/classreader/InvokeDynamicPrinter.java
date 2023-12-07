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

import java.io.PrintWriter;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

public class InvokeDynamicPrinter extends Printer {
    private Classfile currentClassfile;
    private Method_info currentMethod;
    private int currentConstantPoolIndex;
    private int currentBootstrapMethodIndex;

    public InvokeDynamicPrinter(PrintWriter out) {
        super(out);
    }

    public void visitClassfile(Classfile classfile) {
        Logger.getLogger(getClass()).debug("visitClassfile(" + classfile.getClassName() + ")");
        currentClassfile = classfile;
        classfile.getAllMethods().forEach(method -> method.accept(this));
    }

    // ConstantPool
    public void visitClass_info(Class_info entry) {
        Logger.getLogger(getClass()).debug("visitClass_info(" + entry.getName() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getName()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getNameIndex();
        entry.getRawName().accept(this);
        lowerIndent();
    }

    public void visitFieldRef_info(FieldRef_info entry) {
        Logger.getLogger(getClass()).debug("visitFieldRef_info(" + entry.getFullSignature() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getFullSignature()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getClassIndex();
        entry.getRawClass().accept(this);
        currentConstantPoolIndex = entry.getNameAndTypeIndex();
        entry.getRawNameAndType().accept(this);
        lowerIndent();
    }

    public void visitMethodRef_info(MethodRef_info entry) {
        Logger.getLogger(getClass()).debug("visitMethodRef_info(" + entry.getFullSignature() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getFullSignature()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getClassIndex();
        entry.getRawClass().accept(this);
        currentConstantPoolIndex = entry.getNameAndTypeIndex();
        entry.getRawNameAndType().accept(this);
        lowerIndent();
    }

    public void visitInterfaceMethodRef_info(InterfaceMethodRef_info entry) {
        Logger.getLogger(getClass()).debug("visitInterfaceMethodRef_info(" + entry.getFullSignature() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getFullSignature()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getClassIndex();
        entry.getRawClass().accept(this);
        currentConstantPoolIndex = entry.getNameAndTypeIndex();
        entry.getRawNameAndType().accept(this);
        lowerIndent();
    }

    public void visitString_info(String_info entry) {
        Logger.getLogger(getClass()).debug("visitString_info(" + entry.getValue() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" \"").append(entry.getValue()).append("\"").eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getValueIndex();
        entry.getRawValue().accept(this);
        lowerIndent();
    }

    public void visitInteger_info(Integer_info entry) {
        Logger.getLogger(getClass()).debug("visitInteger_info(" + entry.getValue() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getValue()).eol();
    }

    public void visitFloat_info(Float_info entry) {
        Logger.getLogger(getClass()).debug("visitFloat_info(" + entry.getValue() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getValue()).eol();
    }

    public void visitLong_info(Long_info entry) {
        Logger.getLogger(getClass()).debug("visitLong_info(" + entry.getValue() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getValue()).eol();
    }

    public void visitDouble_info(Double_info entry) {
        Logger.getLogger(getClass()).debug("visitDouble_info(" + entry.getValue() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getValue()).eol();
    }

    public void visitNameAndType_info(NameAndType_info entry) {
        Logger.getLogger(getClass()).debug("visitNameAndType_info(" + entry.getName() + " + " + entry.getType() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getNameIndex();
        entry.getRawName().accept(this);
        currentConstantPoolIndex = entry.getTypeIndex();
        entry.getRawType().accept(this);
        lowerIndent();
    }

    public void visitUTF8_info(UTF8_info entry) {
        Logger.getLogger(getClass()).debug("visitUTF8_info(" + entry.getValue() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" \"").append(entry.getValue()).append("\"").eol();
    }

    public void visitMethodHandle_info(MethodHandle_info entry) {
        Logger.getLogger(getClass()).debug("visitMethodHandle_info(" + entry.getReferenceKind().getDescription() + " " + entry.getReference().getFullSignature() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry.getReferenceKind().getDescription()).append("(").append(entry.getRawReferenceKind()).append(") ").append(entry.getReference().getFullSignature()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getReferenceIndex();
        entry.getReference().accept(this);
        lowerIndent();
    }

    public void visitMethodType_info(MethodType_info entry) {
        Logger.getLogger(getClass()).debug("visitMethodType_info(" + entry.getDescriptor() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getDescriptorIndex();
        entry.getRawDescriptor().accept(this);
        lowerIndent();
    }

    public void visitDynamic_info(Dynamic_info entry) {
        Logger.getLogger(getClass()).debug("visitDynamic_info(bootstrap method #" + entry.getBootstrapMethodAttrIndex() + " + " + entry.getSignature() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getNameAndTypeIndex();
        entry.getRawNameAndType().accept(this);

        currentBootstrapMethodIndex = entry.getBootstrapMethodAttrIndex();
        var finder = new BootstrapMethodFinder(entry.getBootstrapMethodAttrIndex());
        currentClassfile.accept(finder);
        finder.getBootstrapMethod().accept(this);
        lowerIndent();
    }

    public void visitInvokeDynamic_info(InvokeDynamic_info entry) {
        Logger.getLogger(getClass()).debug("visitInvokeDynamic_info(bootstrap method #" + entry.getBootstrapMethodAttrIndex() + " + " + entry.getSignature() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).append(" ").append(entry).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getNameAndTypeIndex();
        entry.getRawNameAndType().accept(this);

        currentBootstrapMethodIndex = entry.getBootstrapMethodAttrIndex();
        var finder = new BootstrapMethodFinder(entry.getBootstrapMethodAttrIndex());
        currentClassfile.accept(finder);
        finder.getBootstrapMethod().accept(this);
        lowerIndent();
    }

    public void visitModule_info(Module_info entry) {
        Logger.getLogger(getClass()).debug("visitModule_info(" + entry.getName() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getNameIndex();
        entry.getRawName().accept(this);
        lowerIndent();
    }

    public void visitPackage_info(Package_info entry) {
        Logger.getLogger(getClass()).debug("visitPackage_info(" + entry.getName() + ")");
        indent().append(currentConstantPoolIndex).append(" : ").append(entry.getClass().getSimpleName()).eol();

        raiseIndent();
        currentConstantPoolIndex = entry.getNameIndex();
        entry.getRawName().accept(this);
        lowerIndent();
    }

    // Features
    public void visitMethod_info(Method_info entry) {
        Logger.getLogger(getClass()).debug("visitMethod_info(" + entry.getFullSignature() + ")");
        currentMethod = entry;
        super.visitMethod_info(entry);
    }

    // Attributes
    public void visitCode_attribute(Code_attribute attribute) {
        attribute.forEach(instruction -> instruction.accept(this));
    }

    public void visitBootstrapMethods_attribute(BootstrapMethods_attribute attribute) {
        Logger.getLogger(getClass()).debug("visitBootstrapMethods_attribute(w/ " + attribute.getBootstrapMethods().size() + " method(s))");
        // Do not traverse the BootstrapMethods from the attribute.
        // Only from the invokedynamic instructions.
    }

    // Attribute helpers
    public void visitInstruction(Instruction helper) {
        Logger.getLogger(getClass()).debug("visitInstruction(" + helper.getStart() + " : " + helper.getMnemonic() + ")");
        if (helper.getOpcode() == 0xba /* invokedynamic */) {
            indent().append(currentMethod.getFullSignature()).eol();

            raiseIndent();
            indent().append("pc=").append(helper.getStart()).append(" : ").append(helper.getMnemonic());
            var indexedEntry = helper.getIndexedConstantPoolEntry();
            if (indexedEntry instanceof Dynamic_info dynamic_info) {
                append(" ").append(dynamic_info.getName());
            } else if (indexedEntry instanceof InvokeDynamic_info invokeDynamic_info) {
                append(" ").append(invokeDynamic_info.getName());
            }
            // TODO: Replace with type pattern matching in switch expression in Java 21
            // switch (helper.getIndexedConstantPoolEntry()) {
            //     case Dynamic_info entry -> append(" ").append(entry.getName());
            //     case InvokeDynamic_info entry -> append(" ").append(entry.getName());
            //     default -> append("");
            // }
            eol();

            raiseIndent();
            currentConstantPoolIndex = helper.getIndex();
            helper.getIndexedConstantPoolEntry().accept(this);
            lowerIndent();
            lowerIndent();

            eol();
        }
    }

    public void visitBootstrapMethod(BootstrapMethod helper) {
        Logger.getLogger(getClass()).debug("visitBootstrapMethod(" + helper.getBootstrapMethod().getReference().getFullSignature() + " w/ [" + helper.getArgumentIndices().stream().map(String::valueOf).collect(joining(" ,")) + "])");
        indent().append(currentBootstrapMethodIndex).append(" : BootstrapMethod").eol();

        raiseIndent();
        currentConstantPoolIndex = helper.getBootstrapMethodRef();
        helper.getBootstrapMethod().accept(this);

        var argumentIndices = helper.getArgumentIndices().toArray();
        IntStream.range(0, argumentIndices.length).forEach(i -> {
            indent().append("argument " + i).eol();
            raiseIndent();
            var argumentIndex = (int) argumentIndices[i];
            currentConstantPoolIndex = argumentIndex;
            currentClassfile.getConstantPool().get(argumentIndex).accept(this);
            lowerIndent();
        });

        lowerIndent();
    }
}
