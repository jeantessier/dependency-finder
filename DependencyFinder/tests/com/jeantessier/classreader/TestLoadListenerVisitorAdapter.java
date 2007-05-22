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

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestLoadListenerVisitorAdapter extends TestCase implements Visitor {
    public static final String TEST_DIR      = "tests" + File.separator + "JarJarDiff";
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";

    private LinkedList<Classfile> classfiles;
    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        classfiles = new LinkedList<Classfile>();

        LoadListenerVisitorAdapter adapter = new LoadListenerVisitorAdapter(this);
        loader = new AggregatingClassfileLoader();
        loader.addLoadListener(adapter);
    }

    public void testOneFile() {
        loader.load(TEST_FILENAME);

        assertEquals("First class", TEST_CLASS, classfiles.getFirst().getClassName());
        assertEquals("Nb visited classes", 1, classfiles.size());
    }

    public void testManyFiles() {
        loader.load(TEST_DIR);

        assertEquals("Nb visited classes", 164, classfiles.size());
    }

    public void visitClassfiles(Collection<Classfile> classfiles) {
        this.classfiles.addAll(classfiles);
    }
    
    public void visitClassfile(Classfile classfile) {
        classfiles.add(classfile);
    }

    public void visitConstantPool(ConstantPool constantPool) {
        // Do nothing
    }
    
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
    
    public void visitField_info(Field_info entry) {
        // Do nothing
    }
    
    public void visitMethod_info(Method_info entry) {
        // Do nothing
    }
    
    public void visitConstantValue_attribute(ConstantValue_attribute attribute) {
        // Do nothing
    }
    
    public void visitCode_attribute(Code_attribute attribute) {
        // Do nothing
    }
    
    public void visitExceptions_attribute(Exceptions_attribute attribute) {
        // Do nothing
    }
    
    public void visitInnerClasses_attribute(InnerClasses_attribute attribute) {
        // Do nothing
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

    public void visitLineNumberTable_attribute(LineNumberTable_attribute attribute) {
        // Do nothing
    }
    
    public void visitLocalVariableTable_attribute(LocalVariableTable_attribute attribute) {
        // Do nothing
    }
    
    public void visitDeprecated_attribute(Deprecated_attribute attribute) {
        // Do nothing
    }
    
    public void visitCustom_attribute(Custom_attribute attribute) {
        // Do nothing
    }

    public void visitInstruction(Instruction instruction) {
        // Do nothing
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
}
