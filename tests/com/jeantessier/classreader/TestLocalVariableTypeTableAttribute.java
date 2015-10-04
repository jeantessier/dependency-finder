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

import java.io.*;
import java.util.*;

import junit.framework.*;

public class TestLocalVariableTypeTableAttribute extends TestCase {
    public static final String TEST_CLASS = "test";
    public static final String TEST_GENERIC_CLASS_CLASS = "testgenericclass";
    public static final String TEST_GENERIC_METHODS_CLASS = "testgenericmethods";
    public static final String TEST_FILENAME = "classes" + File.separator + TEST_CLASS + ".class";
    public static final String TEST_GENERIC_CLASS_FILENAME = "classes" + File.separator + TEST_GENERIC_CLASS_CLASS + ".class";
    public static final String TEST_GENERIC_METHODS_FILENAME = "classes" + File.separator + TEST_GENERIC_METHODS_CLASS + ".class";

    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        loader = new AggregatingClassfileLoader();

        loader.load(Collections.singleton(TEST_FILENAME));
        loader.load(Collections.singleton(TEST_GENERIC_CLASS_FILENAME));
        loader.load(Collections.singleton(TEST_GENERIC_METHODS_FILENAME));
    }

    public void testConstructorHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(TEST_GENERIC_CLASS_CLASS + "(java.lang.Object)");
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNotNull("LocalVariableTypeTable attribute missing", localVariableTypeTableAttribute);
    }

    public void testGenericConstructorHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod(TEST_GENERIC_METHODS_CLASS + "(java.lang.Object)");
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNotNull("LocalVariableTypeTable attribute missing", localVariableTypeTableAttribute);
    }

    public void testNonGenericMethodDoesNotHaveLocalVariableTypeTableAttribute() {
        Classfile nonGenericClass = loader.getClassfile(TEST_CLASS);
        Method_info method = nonGenericClass.getMethod("main(java.lang.String[])");
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNull("LocalVariableTypeTable attribute missing", localVariableTypeTableAttribute);
    }

    public void testNonGenericMethodUsingGenericsHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod("testregularmethod(java.lang.Class)");
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNotNull("LocalVariableTypeTable attribute missing", localVariableTypeTableAttribute);
    }

    public void testMethodHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod("testmethod(java.lang.Object)");
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNotNull("LocalVariableTypeTable attribute missing", localVariableTypeTableAttribute);
    }

    public void testGenericMethodHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod("testmethod(java.lang.Object)");
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNotNull("LocalVariableTypeTable attribute missing", localVariableTypeTableAttribute);
    }

    public void testLocalVariableType() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(TEST_GENERIC_CLASS_CLASS + "(java.lang.Object)");
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNotNull("LocalVariableTypeTable attribute missing", localVariableTypeTableAttribute);
        assertEquals("Nb LocalVariableType", 2, localVariableTypeTableAttribute.getLocalVariableTypes().size());

        LocalVariableType localVariableType = localVariableTypeTableAttribute.getLocalVariableTypes().iterator().next();
        assertEquals("start pc", 0, localVariableType.getStartPC());
        assertEquals("length", 5, localVariableType.getLength());
        assertEquals("name", "this", localVariableType.getName());
        assertEquals("signature", "Ltestgenericclass<TT;>;", localVariableType.getSignature());
        assertEquals("index", 0, localVariableType.getIndex());
    }

    private LocalVariableTypeTable_attribute findLastLocalVariableTypeTableAttribute(Collection<? extends Attribute_info> attributes) {
        LocalVariableTypeTable_attribute result = null;

        for (Attribute_info attribute : attributes) {
            if (attribute instanceof LocalVariableTypeTable_attribute) {
                result = (LocalVariableTypeTable_attribute) attribute;
            }
        }

        return result;
    }
}
