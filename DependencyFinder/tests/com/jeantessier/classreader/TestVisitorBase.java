/*
 *  Copyright (c) 2001-2008, Jean Tessier
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

import org.jmock.*;
import org.jmock.integration.junit3.*;

public class TestVisitorBase extends MockObjectTestCase {
    private VisitorBase sut;

    protected void setUp() throws Exception {
        sut = new VisitorBase() {};
    }

    public void testVisitConstantPool() {
        final ConstantPool mockConstantPool = mock(ConstantPool.class);
        final ConstantPoolEntry mockEntry = mock(ConstantPoolEntry.class);

        checking(new Expectations() {{
            one (mockConstantPool).iterator();
            will(returnIterator(mockEntry));
            one (mockEntry).accept(sut);
        }});

        sut.visitConstantPool(mockConstantPool);
        assertEquals("current count", 1, sut.currentCount());
    }

    public void testVisitClassfiles() {
        final Classfile mockClassfile1 = mock(Classfile.class, "classfile1");
        final Classfile mockClassfile2 = mock(Classfile.class, "classfile2");

        Collection<Classfile> classfiles = new ArrayList<Classfile>();
        classfiles.add(mockClassfile1);
        classfiles.add(mockClassfile2);

        checking(new Expectations() {{
            one (mockClassfile1).accept(sut);
            one (mockClassfile2).accept(sut);
        }});

        sut.visitClassfiles(classfiles);
    }

    public void testVisitClassfile() {
        final Classfile mockClassfile = mock(Classfile.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);
        final Field_info mockField = mock(Field_info.class);
        final Method_info mockMethod = mock(Method_info.class);

        checking(new Expectations() {{
            one (mockClassfile).getAttributes();
            will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
            one (mockClassfile).getAllFields();
            will(returnValue(Collections.singleton(mockField)));
            one (mockField).accept(sut);
            one (mockClassfile).getAllMethods();
            will(returnValue(Collections.singleton(mockMethod)));
            one (mockMethod).accept(sut);
        }});

        sut.visitClassfile(mockClassfile);
    }

    public void testVisitClass_info() {
        Class_info mockClass = mock(Class_info.class);
        sut.visitClass_info(mockClass);
    }

    public void testVisitFieldRef_info() {
        FieldRef_info mockFieldRef = mock(FieldRef_info.class);
        sut.visitFieldRef_info(mockFieldRef);
    }

    public void testVisitMethodRef_info() {
        MethodRef_info mockMethodRef = mock(MethodRef_info.class);
        sut.visitMethodRef_info(mockMethodRef);
    }

    public void testVisitInterfaceMethodRef_info() {
        InterfaceMethodRef_info mockInterfaceMethodRef = mock(InterfaceMethodRef_info.class);
        sut.visitInterfaceMethodRef_info(mockInterfaceMethodRef);
    }

    public void testVisitString_info() {
        String_info mockString = mock(String_info.class);
        sut.visitString_info(mockString);
    }

    public void testVisitInteger_info() {
        Integer_info mockInteger = mock(Integer_info.class);
        sut.visitInteger_info(mockInteger);
    }

    public void testVisitFloat_info() {
        Float_info mockFloat = mock(Float_info.class);
        sut.visitFloat_info(mockFloat);
    }

    public void testVisitLong_info() {
        Long_info mockLong = mock(Long_info.class);
        sut.visitLong_info(mockLong);
    }

    public void testVisitDouble_info() {
        Double_info mockDouble = mock(Double_info.class);
        sut.visitDouble_info(mockDouble);
    }

    public void testVisitNameAndType_info() {
        NameAndType_info mockNameAndType = mock(NameAndType_info.class);
        sut.visitNameAndType_info(mockNameAndType);
    }

    public void testVisitUTF8_info() {
        UTF8_info mockUTF8 = mock(UTF8_info.class);
        sut.visitUTF8_info(mockUTF8);
    }

    public void testVisitField_info() {
        final Field_info mockField = mock(Field_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            one (mockField).getAttributes();
            will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
        }});

        sut.visitField_info(mockField);
    }

    public void testVisitMethod_info() {
        final Method_info mockMethod = mock(Method_info.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            one (mockMethod).getAttributes();
            will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
        }});

        sut.visitMethod_info(mockMethod);
    }
    
    public void testVisitConstantValue_attribute() {
        final ConstantValue_attribute mockConstantValue = mock(ConstantValue_attribute.class);
        sut.visitConstantValue_attribute(mockConstantValue);
    }

    public void testVisitCode_attribute() {
        final Code_attribute mockCode = mock(Code_attribute.class);
        final Instruction mockInstruction = mock(Instruction.class);
        final ExceptionHandler mockExceptionHandler = mock(ExceptionHandler.class);
        final Attribute_info mockAttribute = mock(Attribute_info.class);

        checking(new Expectations() {{
            one (mockCode).iterator();
            will(returnIterator(mockInstruction));
            one (mockInstruction).accept(sut);
            exactly(2).of (mockCode).getExceptionHandlers();
            will(returnValue(Collections.singleton(mockExceptionHandler)));
            one (mockExceptionHandler).accept(sut);
            exactly(2).of (mockCode).getAttributes();
            will(returnValue(Collections.singleton(mockAttribute)));
            one (mockAttribute).accept(sut);
        }});

        sut.visitCode_attribute(mockCode);
    }

    public void testVisitInstruction() {
        Instruction mockInstruction = mock(Instruction.class);
        sut.visitInstruction(mockInstruction);
    }
}
