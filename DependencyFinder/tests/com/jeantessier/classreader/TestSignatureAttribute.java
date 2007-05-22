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

public class TestSignatureAttribute extends TestCase {
    public static final String TEST_GENERIC_CLASS_CLASS = "testgenericclass";
    public static final String TEST_GENERIC_METHODS_CLASS = "testgenericmethods";
    public static final String TEST_GENERIC_CLASS_FILENAME = "classes" + File.separator + TEST_GENERIC_CLASS_CLASS + ".class";
    public static final String TEST_GENERIC_METHODS_FILENAME = "classes" + File.separator + TEST_GENERIC_METHODS_CLASS + ".class";

    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        loader = new AggregatingClassfileLoader();

        loader.load(Collections.singleton(TEST_GENERIC_CLASS_FILENAME));
        loader.load(Collections.singleton(TEST_GENERIC_METHODS_FILENAME));
    }

    public void testClassDoesNotHaveSignatureAttribute() {
        Classfile nonGenericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);

        Signature_attribute signatureAttribute = getSingleSignatureAttribute(nonGenericClass.getAttributes());
        assertNull("Found Signature attribute: " + signatureAttribute, signatureAttribute);
    }

    public void testClassHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);

        Signature_attribute signatureAttribute = getSingleSignatureAttribute(genericClass.getAttributes());
        assertNotNull("Signature attribute missing", signatureAttribute);
        assertEquals("Signature", "<T:Ljava/lang/Object;>Ljava/lang/Object;", signatureAttribute.getSignature());
    }

    public void testConstructorHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod(TEST_GENERIC_CLASS_CLASS + "(java.lang.Object)");

        Signature_attribute signatureAttribute = getSingleSignatureAttribute(method.getAttributes());
        assertNotNull("Signature attribute missing", signatureAttribute);
        assertEquals("Signature", "(TT;)V", signatureAttribute.getSignature());
    }

    public void testGenericConstructorHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod(TEST_GENERIC_METHODS_CLASS + "(java.lang.Object)");

        Signature_attribute signatureAttribute = getSingleSignatureAttribute(method.getAttributes());
        assertNotNull("Signature attribute missing", signatureAttribute);
        assertEquals("Signature", "<T:Ljava/lang/Object;>(TT;)V", signatureAttribute.getSignature());
    }

    public void testMethodHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Method_info method = genericClass.getMethod("testmethod(java.lang.Object)");

        Signature_attribute signatureAttribute = getSingleSignatureAttribute(method.getAttributes());
        assertNotNull("Signature attribute missing", signatureAttribute);
        assertEquals("Signature", "(TT;)TT;", signatureAttribute.getSignature());
    }

    public void testGenericMethodHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_METHODS_CLASS);
        Method_info method = genericClass.getMethod("testmethod(java.lang.Object)");

        Signature_attribute signatureAttribute = getSingleSignatureAttribute(method.getAttributes());
        assertNotNull("Signature attribute missing", signatureAttribute);
        assertEquals("Signature", "<T:Ljava/lang/Object;>(TT;)TT;", signatureAttribute.getSignature());
    }

    public void testFieldHasSignatureAttribute() {
        Classfile genericClass = loader.getClassfile(TEST_GENERIC_CLASS_CLASS);
        Field_info field = genericClass.getField("testfield");

        Signature_attribute signatureAttribute = getSingleSignatureAttribute(field.getAttributes());
        assertNotNull("Signature attribute missing", signatureAttribute);
        assertEquals("Signature", "TT;", signatureAttribute.getSignature());
    }

    private Signature_attribute getSingleSignatureAttribute(Collection<Attribute_info> attributes) {
        Signature_attribute result = null;

        for (Attribute_info attribute : attributes) {
            if (attribute instanceof Signature_attribute) {
                if (result == null) {
                    result = (Signature_attribute) attribute;
                } else {
                    fail("Multiple Signature attributes");
                }
            }
        }

        return result;
    }
}
