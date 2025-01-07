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

public class TestSignatureAttribute {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    private static final String TEST_GENERIC_CLASS_CLASS = "testgenericclass";
    private static final String TEST_GENERIC_METHODS_CLASS = "testgenericmethods";
    private static final String TEST_GENERIC_CLASS_FILENAME = CLASSES_DIR.resolve(TEST_GENERIC_CLASS_CLASS + ".class").toString();
    private static final String TEST_GENERIC_METHODS_FILENAME = CLASSES_DIR.resolve(TEST_GENERIC_METHODS_CLASS + ".class").toString();

    private final ClassfileLoader loader = new AggregatingClassfileLoader();

    @BeforeEach
    void setUp() throws Exception {
        loader.load(Collections.singleton(TEST_GENERIC_CLASS_FILENAME));
        loader.load(Collections.singleton(TEST_GENERIC_METHODS_FILENAME));
    }

    @Test
    void testClassDoesNotHaveSignatureAttribute() {
        Classfile nonGenericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);

        assertThrows(Throwable.class, () -> findSingleSignatureAttribute(nonGenericClass.getAttributes()));
        assertFalse(nonGenericClass.isGeneric(), "Classfile without Signature attribute should not be generic");
    }

    @Test
    void testClassHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);

        Signature_attribute signatureAttribute = findSingleSignatureAttribute(genericClass.getAttributes());
        assertEquals("<T:Ljava/lang/Object;>Ljava/lang/Object;", signatureAttribute.getSignature(), "Signature");
        assertTrue(genericClass.isGeneric(), "Classfile with Signature attribute is not generic");
    }

    @Test
    void testConstructorHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals(TEST_GENERIC_CLASS_CLASS + "(java.lang.Object)"));

        Signature_attribute signatureAttribute = findSingleSignatureAttribute(method.getAttributes());
        assertEquals("(TT;)V", signatureAttribute.getSignature(), "Signature");
        assertTrue(method.isGeneric(), "Method with Signature attribute is not generic");
    }

    @Test
    void testGenericConstructorHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals(TEST_GENERIC_METHODS_CLASS + "(java.lang.Object)"));

        Signature_attribute signatureAttribute = findSingleSignatureAttribute(method.getAttributes());
        assertEquals("<T:Ljava/lang/Object;>(TT;)V", signatureAttribute.getSignature(), "Signature");
        assertTrue(method.isGeneric(), "Method with Signature attribute is not generic");
    }

    @Test
    void testMethodHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals("testmethod(java.lang.Object)"));

        Signature_attribute signatureAttribute = findSingleSignatureAttribute(method.getAttributes());
        assertEquals("(TT;)TT;", signatureAttribute.getSignature(), "Signature");
        assertTrue(method.isGeneric(), "Method with Signature attribute is not generic");
    }

    @Test
    void testGenericMethodHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod(m -> m.getSignature().equals("testmethod(java.lang.Object)"));

        Signature_attribute signatureAttribute = findSingleSignatureAttribute(method.getAttributes());
        assertEquals("<T:Ljava/lang/Object;>(TT;)TT;", signatureAttribute.getSignature(), "Signature");
        assertTrue(method.isGeneric(), "Method with Signature attribute is not generic");
    }

    @Test
    void testFieldHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Field_info field = genericClass.getField(f -> f.getName().equals("testfield"));

        Signature_attribute signatureAttribute = findSingleSignatureAttribute(field.getAttributes());
        assertEquals("TT;", signatureAttribute.getSignature(), "Signature");
        assertTrue(field.isGeneric(), "Field with Signature attribute is not generic");
    }

    private Signature_attribute findSingleSignatureAttribute(Collection<? extends Attribute_info> attributes) {
        assertEquals(1, attributes.stream().filter(attribute -> attribute instanceof Signature_attribute).count(), "There should be only one Signature attribute");

        return (Signature_attribute) attributes.stream()
                .filter(attribute -> attribute instanceof Signature_attribute)
                .findAny()
                .orElseThrow(() -> new RuntimeException("Signature attribute missing"));
    }
}
