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

import java.io.*;
import java.util.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

public class TestIncompatibleDifferenceStrategyWithPublishedAPI extends TestCase {
    public static final String OLD_PUBLISHED_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "oldpublished";
    public static final String NEW_PUBLISHED_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "newpublished";

    private static PackageMapper oldPublishedPackages;
    private static PackageMapper newPublishedPackages;

    private static ClassfileLoader oldPublishedJar;
    private static ClassfileLoader newPublishedJar;

    public static PackageMapper getOldPublishedPackages() {
        if (oldPublishedPackages == null) {
            oldPublishedPackages = new PackageMapper();
        }

        return oldPublishedPackages;
    }

    public static PackageMapper getNewPublishedPackages() {
        if (newPublishedPackages == null) {
            newPublishedPackages = new PackageMapper();
        }

        return newPublishedPackages;
    }

    public static  ClassfileLoader getOldPublishedJar() {
        if (oldPublishedJar == null) {
            oldPublishedJar = new AggregatingClassfileLoader();
            oldPublishedJar.addLoadListener(getOldPublishedPackages());
            oldPublishedJar.load(Collections.singleton(OLD_PUBLISHED_CLASSPATH));
        }

        return oldPublishedJar;
    }

    public static ClassfileLoader getNewPublishedJar() {
        if (newPublishedJar == null) {
            newPublishedJar = new AggregatingClassfileLoader();
            newPublishedJar.addLoadListener(getNewPublishedPackages());
            newPublishedJar.load(Collections.singleton(NEW_PUBLISHED_CLASSPATH));
        }

        return newPublishedJar;
    }

    private MockDifferenceStrategy         mockStrategy;
    private IncompatibleDifferenceStrategy strategy;

    protected void setUp() throws Exception {
        super.setUp();

        mockStrategy = new MockDifferenceStrategy(new NoDifferenceStrategy());
        strategy     = new IncompatibleDifferenceStrategy(mockStrategy);
    }

    /*
     * Classes
     */

    public void testPublicToPublicClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PublicToPublicClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PublicToPublicClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPackageClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PublicToPackageClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PublicToPackageClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPublicClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PackageToPublicClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PackageToPublicClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPackageClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PackageToPackageClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PackageToPackageClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testClassToClassClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ClassToClassClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ClassToClassClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testClassToInterfaceClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ClassToInterfaceClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ClassToInterfaceClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testInterfaceToClassClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.InterfaceToClassClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.InterfaceToClassClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testInterfaceToInterfaceClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.InterfaceToInterfaceClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.InterfaceToInterfaceClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testConcreteToConcreteClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ConcreteToConcreteClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ConcreteToConcreteClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testConcreteToAbstractClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ConcreteToAbstractClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ConcreteToAbstractClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testAbstractToConcreteClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.AbstractToConcreteClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.AbstractToConcreteClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testAbstractToAbstractClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.AbstractToAbstractClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.AbstractToAbstractClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonFinalToNonFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.NonFinalToNonFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.NonFinalToNonFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonFinalToFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.NonFinalToFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.NonFinalToFinalClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testFinalToNonFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.FinalToNonFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.FinalToNonFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testFinalToFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.FinalToFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.FinalToFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    /*
     * Fields
     */

    public void testPrivateToPrivateField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToPrivateField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToPrivateField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToProtectedField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToProtectedField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToProtectedField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToPackageField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToPackagedField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToPackageField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToPublicField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToPublicField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("privateToPublicField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPrivateField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToPrivateField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToPrivateField");

        assertTrue(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToProtectedField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToProtectedField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToProtectedField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPackageField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToPackageField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToPackageField");

        assertTrue(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPublicField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToPublicField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("protectedToPublicField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPrivateField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToPrivateField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToPrivateField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToProtectedField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToProtectedField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToProtectedField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPackageField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToPackagedField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToPackageField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPublicField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToPublicField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("packageToPublicField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPrivateField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToPrivateField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToPrivateField");

        assertTrue(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToProtectedField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToProtectedField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToProtectedField");

        assertTrue(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPackageField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToPackageField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToPackageField");

        assertTrue(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPublicField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToPublicField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("publicToPublicField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonFinalToNonFinalField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("nonFinalToNonFinalField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("nonFinalToNonFinalField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonFinalToFinalField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("nonFinalToFinalField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("nonFinalToFinalField");

        assertTrue(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testFinalToNonFinalField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("finalToNonFinalField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("finalToNonFinalField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testFinalToFinalField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("finalToFinalField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("finalToFinalField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testTypeToSameTypeField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("typeToSameTypeField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("typeToSameTypeField");

        assertFalse(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testTypeToDifferentTypeField() {
        Field_info oldField = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("typeToDifferentTypeField");
        Field_info newField = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getField("typeToDifferentTypeField");

        assertTrue(strategy.isFieldDifferent(oldField, newField));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    /*
     * Constructors
     */

    public void testPrivateToPrivateConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToProtectedConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToPackageConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToPublicConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPrivateConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int)");

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToProtectedConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long, long)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long, long)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPackageConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float, float)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float, float)");

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPublicConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double, double)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double, double)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPrivateConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToProtectedConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long, long, long)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long, long, long)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPackageConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float, float, float)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float, float, float)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPublicConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double, double, double)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double, double, double)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPrivateConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int, int)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(int, int, int, int)");

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToProtectedConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long, long, long, long)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(long, long, long, long)");

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPackageConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float, float, float, float)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(float, float, float, float)");

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPublicConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double, double, double, double)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(double, double, double, double)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testThrowsToSameThrowsConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(java.lang.Object)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(java.lang.Object)");

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testThrowsToDifferentThrowsConstructor() {
        Method_info oldConstructor = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(java.lang.Object, java.lang.Object)");
        Method_info newConstructor = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("ModifiedClass(java.lang.Object, java.lang.Object)");

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    /*
    * Methods
    */

    public void testPrivateToPrivateMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToPrivateMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToPrivateMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToProtectedMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToProtectedMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToProtectedMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToPackageMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToPackagedMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToPackageMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPrivateToPublicMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToPublicMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("privateToPublicMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPrivateMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToPrivateMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToPrivateMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToProtectedMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToProtectedMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToProtectedMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPackageMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToPackageMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToPackageMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testProtectedToPublicMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToPublicMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("protectedToPublicMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPrivateMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToPrivateMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToPrivateMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToProtectedMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToProtectedMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToProtectedMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPackageMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToPackagedMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToPackageMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPackageToPublicMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToPublicMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("packageToPublicMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPrivateMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToPrivateMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToPrivateMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToProtectedMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToProtectedMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToProtectedMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPackageMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToPackageMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToPackageMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testPublicToPublicMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToPublicMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("publicToPublicMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testConcreteToConcreteMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("concreteToConcreteMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("concreteToConcreteMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testConcreteToAbstractMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("concreteToAbstractMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("concreteToAbstractMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testAbstractToConcreteMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("abstractToConcreteMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("abstractToConcreteMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testAbstractToAbstractMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("abstractToAbstractMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("abstractToAbstractMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonStaticToNonStaticMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonStaticToNonStaticMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonStaticToNonStaticMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonStaticToStaticMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonStaticToStaticMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonStaticToStaticMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testStaticToNonStaticMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("staticToNonStaticMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("staticToNonStaticMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testStaticToStaticMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("staticToStaticMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("staticToStaticMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonFinalToNonFinalMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonFinalToNonFinalMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonFinalToNonFinalMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testNonFinalToFinalMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonFinalToFinalMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("nonFinalToFinalMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testFinalToNonFinalMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("finalToNonFinalMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("finalToNonFinalMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testFinalToFinalMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("finalToFinalMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("finalToFinalMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testReturnTypeToSameReturnTypeMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("returnTypeToSameReturnTypeMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("returnTypeToSameReturnTypeMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testReturnTypeToDifferentReturnTypeMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("returnTypeToDifferentReturnTypeMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("returnTypeToDifferentReturnTypeMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getFieldDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testThrowsToSameThrowsMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("throwsToSameThrowsMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("throwsToSameThrowsMethod()");

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testThrowsToDifferentThrowsMethod() {
        Method_info oldMethod = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("throwsToDifferentThrowsMethod()");
        Method_info newMethod = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass").getMethod("throwsToDifferentThrowsMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }

    public void testCompatibleClassWithIncompatibleMethod() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.CompatibleClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.CompatibleClass");

        Method_info oldMethod = oldClass.getMethod("incompatibleMethod()");
        Method_info newMethod = newClass.getMethod("incompatibleMethod()");

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
        assertTrue(strategy.isClassDifferent(oldClass, newClass));

        assertEquals("package",  0, mockStrategy.getPackageDifferentCount());
        assertEquals("class",    0, mockStrategy.getClassDifferentCount());
        assertEquals("field",    0, mockStrategy.getMethodDifferentCount());
        assertEquals("constant", 0, mockStrategy.getConstantValueDifferentCount());
        assertEquals("method",   0, mockStrategy.getMethodDifferentCount());
        assertEquals("code",     0, mockStrategy.getCodeDifferentCount());
    }
}
