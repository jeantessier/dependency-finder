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

import org.junit.jupiter.api.*;

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalVariableTypeTableAttribute {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS = "test";
    public static final String TEST_GENERIC_CLASS_CLASS = "testgenericclass";
    public static final String TEST_GENERIC_METHODS_CLASS = "testgenericmethods";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    public static final String TEST_GENERIC_CLASS_FILENAME = CLASSES_DIR.resolve(TEST_GENERIC_CLASS_CLASS + ".class").toString();
    public static final String TEST_GENERIC_METHODS_FILENAME = CLASSES_DIR.resolve(TEST_GENERIC_METHODS_CLASS + ".class").toString();

    private final ClassfileLoader loader = new AggregatingClassfileLoader();

    @BeforeEach
    void setUp() {
        loader.load(Collections.singleton(TEST_FILENAME));
        loader.load(Collections.singleton(TEST_GENERIC_CLASS_FILENAME));
        loader.load(Collections.singleton(TEST_GENERIC_METHODS_FILENAME));
    }

    @Test
    void testConstructorHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals(TEST_GENERIC_CLASS_CLASS + "(java.lang.Object)"));
        Code_attribute code = method.getCode();

        assertDoesNotThrow(() -> findLastLocalVariableTypeTableAttribute(code.getAttributes()));
    }

    @Test
    void testGenericConstructorHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals(TEST_GENERIC_METHODS_CLASS + "(java.lang.Object)"));
        Code_attribute code = method.getCode();

        assertDoesNotThrow(() -> findLastLocalVariableTypeTableAttribute(code.getAttributes()));
    }

    @Test
    void testNonGenericMethodDoesNotHaveLocalVariableTypeTableAttribute() {
        Classfile nonGenericClass = loader.getClassfile(TEST_CLASS);
        Method_info method = nonGenericClass.getMethod(m -> m.getSignature().equals("main(java.lang.String[])"));
        Code_attribute code = method.getCode();

        assertThrows(RuntimeException.class, () -> findLastLocalVariableTypeTableAttribute(code.getAttributes()));
    }

    @Test
    void testNonGenericMethodUsingGenericsHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals("testregularmethod(java.lang.Class)"));
        Code_attribute code = method.getCode();

        assertDoesNotThrow(() -> findLastLocalVariableTypeTableAttribute(code.getAttributes()));
    }

    @Test
    void testMethodHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals("testmethod(java.lang.Object)"));
        Code_attribute code = method.getCode();

        assertDoesNotThrow(() -> findLastLocalVariableTypeTableAttribute(code.getAttributes()));
    }

    @Test
    void testGenericMethodHasLocalVariableTypeTableAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals("testmethod(java.lang.Object)"));
        Code_attribute code = method.getCode();

        assertDoesNotThrow(() -> findLastLocalVariableTypeTableAttribute(code.getAttributes()));
    }

    @Test
    void testLocalVariableType() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals(TEST_GENERIC_CLASS_CLASS + "(java.lang.Object)"));
        Code_attribute code = method.getCode();

        LocalVariableTypeTable_attribute localVariableTypeTableAttribute = findLastLocalVariableTypeTableAttribute(code.getAttributes());
        assertNotNull(localVariableTypeTableAttribute, "LocalVariableTypeTable attribute missing");
        assertEquals(2, localVariableTypeTableAttribute.getLocalVariableTypes().size(), "Nb LocalVariableType");

        LocalVariableType localVariableType = localVariableTypeTableAttribute.getLocalVariableTypes().iterator().next();
        assertEquals(0, localVariableType.getStartPC(), "start pc");
        assertEquals(5, localVariableType.getLength(), "length");
        assertEquals("this", localVariableType.getName(), "name");
        assertEquals("Ltestgenericclass<TT;>;", localVariableType.getSignature(), "signature");
        assertEquals(0, localVariableType.getIndex(), "index");
    }

    private LocalVariableTypeTable_attribute findLastLocalVariableTypeTableAttribute(Collection<? extends Attribute_info> attributes) {
        return (LocalVariableTypeTable_attribute) attributes.stream()
                .filter(attribute -> attribute instanceof LocalVariableTypeTable_attribute)
                .findAny()
                .orElseThrow(() -> new RuntimeException("LocalVariableTypeTable attribute missing"));
    }
}
