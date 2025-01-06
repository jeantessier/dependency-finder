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

package com.jeantessier.diff;

import java.util.*;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDifferencesFactoryWithIncompatibleDifferenceStrategy extends TestDifferencesFactoryBase {
    private final DifferencesFactory factory = new DifferencesFactory(new IncompatibleDifferenceStrategy(new NoDifferenceStrategy()));

    @Test
    void testFieldDeclarationDifference() {
        String className = "ModifiedPackage.ModifiedInterface";
        String fieldName = className + ".modifiedField";

        var fieldDifferences = findFeatureDifferences(factory, className, fieldName);

        assertEquals("public static final int modifiedField", fieldDifferences.getOldDeclaration());
        assertEquals("public static final float modifiedField", fieldDifferences.getNewDeclaration());
    }

    @Test
    void testFieldConstantValueDifference() {
        String className = "ModifiedPackage.ModifiedInterface";
        String fieldName = className + ".modifiedValueField";

        try {
            var fieldDifferences = findFeatureDifferences(factory, className, fieldName);
            fail("Should have thrown an exception, instead found " + fieldDifferences);
        } catch (NoSuchElementException e) {
            assertEquals(fieldName + " not found", e.getMessage(), "exception message");
        }
    }

    @Test
    void testConstructorDifference() {
        String className = "ModifiedPackage.ModifiedClass";
        String constructorName = className + ".ModifiedClass(int, int, int)";

        ConstructorDifferences constructorDifferences = findFeatureDifferences(factory, className, constructorName);

        assertFalse(constructorDifferences.isCodeDifference());
    }

    @Test
    void testConstructorCodeDifference() {
        String className = "ModifiedPackage.ModifiedClass";
        String constructorName = className + ".ModifiedClass(float)";

        try {
            var constructorDifferences = findFeatureDifferences(factory, className, constructorName);
            fail("Should have thrown an exception, instead found " + constructorDifferences);
        } catch (NoSuchElementException e) {
            assertEquals(constructorName + " not found", e.getMessage(), "exception message");
        }
    }

    @Test
    void testMethodDifference() {
        String className  = "ModifiedPackage.ModifiedClass";
        String methodName = className + ".modifiedMethod()";

        MethodDifferences methodDifferences = findFeatureDifferences(factory, className, methodName);

        assertNotNull(methodDifferences);
        assertTrue(methodDifferences.isModified());
        assertFalse(methodDifferences.isCodeDifference());
    }

    @Test
    void testMethodCodeDifference() {
        String className = "ModifiedPackage.ModifiedClass";
        String methodName = className + ".modifiedCodeMethod()";

        try {
            var methodDifferences = findFeatureDifferences(factory, className, methodName);
            fail("Should have thrown an exception, instead found " + methodDifferences);
        } catch (NoSuchElementException e) {
            assertEquals(methodName + " not found", e.getMessage(), "exception message");
        }
    }
}
