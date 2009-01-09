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

package com.jeantessier.diff;

import java.util.*;

import com.jeantessier.classreader.*;

public class TestDifferencesFactoryWithAPIDifferenceStrategy extends TestDifferencesFactoryBase {
    private DifferencesFactory factory;

    protected void setUp() throws Exception {
        super.setUp();

        factory = new DifferencesFactory(new APIDifferenceStrategy(new NoDifferenceStrategy()));
    }

    public void testFieldDeclarationDifference() {
        String className = "ModifiedPackage.ModifiedInterface";
        Classfile oldClass = getOldJar().getClassfile(className);
        Classfile newClass = getNewJar().getClassfile(className);

        ClassDifferences classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);

        FieldDifferences fieldDifferences = null;
        Iterator i = classDifferences.getFeatureDifferences().iterator();
        while (i.hasNext()) {
            Differences differences = (Differences) i.next();
            if (differences.getName().equals(className + ".modifiedField")) {
                fieldDifferences = (FieldDifferences) differences;
            }
        }

        assertEquals("public static final int modifiedField", fieldDifferences.getOldDeclaration());
        assertEquals("public static final float modifiedField", fieldDifferences.getNewDeclaration());
    }

    public void testFieldConstantValueDifference() {
        String className = "ModifiedPackage.ModifiedInterface";
        Classfile oldClass = getOldJar().getClassfile(className);
        Classfile newClass = getNewJar().getClassfile(className);

        ClassDifferences classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);

        FieldDifferences fieldDifferences = null;
        Iterator i = classDifferences.getFeatureDifferences().iterator();
        while (i.hasNext()) {
            Differences differences = (Differences) i.next();
            if (differences.getName().equals(className + ".modifiedValueField")) {
                fieldDifferences = (FieldDifferences) differences;
            }
        }

        assertNull(fieldDifferences);
    }

    public void testConstructorDifference() {
        String className = "ModifiedPackage.ModifiedClass";
        Classfile oldClass = getOldJar().getClassfile(className);
        Classfile newClass = getNewJar().getClassfile(className);

        ClassDifferences classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);

        ConstructorDifferences constructorDifferences = null;
        Iterator i = classDifferences.getFeatureDifferences().iterator();
        while (i.hasNext()) {
            Differences differences = (Differences) i.next();
            if (differences.getName().equals(className + ".ModifiedClass(int, int, int)")) {
                constructorDifferences = (ConstructorDifferences) differences;
            }
        }

        assertFalse(constructorDifferences.isCodeDifference());
    }

    public void testConstructorCodeDifference() {
        String className = "ModifiedPackage.ModifiedClass";
        Classfile oldClass = getOldJar().getClassfile(className);
        Classfile newClass = getNewJar().getClassfile(className);

        ClassDifferences classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);

        ConstructorDifferences constructorDifferences = null;
        Iterator i = classDifferences.getFeatureDifferences().iterator();
        while (i.hasNext()) {
            Differences differences = (Differences) i.next();
            if (differences.getName().equals(className + ".ModifiedClass(float)")) {
                constructorDifferences = (ConstructorDifferences) differences;
            }
        }

        assertNull(constructorDifferences);
    }

    public void testMethodDifference() {
        String className  = "ModifiedPackage.ModifiedClass";
        Classfile oldClass = getOldJar().getClassfile(className);
        Classfile newClass = getNewJar().getClassfile(className);

        ClassDifferences classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);

        MethodDifferences methodDifferences = null;
        Iterator i = classDifferences.getFeatureDifferences().iterator();
        while (i.hasNext()) {
            Differences differences = (Differences) i.next();
            if (differences.getName().equals(className + ".modifiedMethod()")) {
                methodDifferences = (MethodDifferences) differences;
            }
        }

        assertNotNull(methodDifferences);
        assertTrue(methodDifferences.isModified());
        assertFalse(methodDifferences.isCodeDifference());
    }

    public void testMethodCodeDifference() {
        String className = "ModifiedPackage.ModifiedClass";
        Classfile oldClass = getOldJar().getClassfile(className);
        Classfile newClass = getNewJar().getClassfile(className);

        ClassDifferences classDifferences = (ClassDifferences) factory.createClassDifferences(className, oldClass, newClass);

        MethodDifferences methodDifferences = null;
        Iterator i = classDifferences.getFeatureDifferences().iterator();
        while (i.hasNext()) {
            Differences differences = (Differences) i.next();
            if (differences.getName().equals(className + ".modifiedCodeMethod()")) {
                methodDifferences = (MethodDifferences) differences;
            }
        }

        assertNull(methodDifferences);
    }
}
