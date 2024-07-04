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

import java.nio.file.*;
import java.util.*;

import com.jeantessier.classreader.*;
import org.jmock.integration.junit3.*;

public class TestIncompatibleDifferenceStrategyWithPublishedAPI extends MockObjectTestCase {
    public static final String OLD_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/old-published/build/libs/old-published.jar").toString();
    public static final String NEW_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/new-published/build/libs/new-published.jar").toString();

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

    private IncompatibleDifferenceStrategy strategy;

    protected void setUp() throws Exception {
        super.setUp();

        var mockStrategy = mock(DifferenceStrategy.class);
        strategy = new IncompatibleDifferenceStrategy(mockStrategy);
    }

    /*
     * Classes
     */

    public void testPublicToPublicClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PublicToPublicClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PublicToPublicClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testPublicToPackageClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PublicToPackageClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PublicToPackageClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testPackageToPublicClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PackageToPublicClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PackageToPublicClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testPackageToPackageClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.PackageToPackageClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.PackageToPackageClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testClassToClassClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ClassToClassClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ClassToClassClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testClassToInterfaceClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ClassToInterfaceClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ClassToInterfaceClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testInterfaceToClassClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.InterfaceToClassClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.InterfaceToClassClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testInterfaceToInterfaceClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.InterfaceToInterfaceClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.InterfaceToInterfaceClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testConcreteToConcreteClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ConcreteToConcreteClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ConcreteToConcreteClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testConcreteToAbstractClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.ConcreteToAbstractClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.ConcreteToAbstractClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testAbstractToConcreteClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.AbstractToConcreteClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.AbstractToConcreteClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testAbstractToAbstractClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.AbstractToAbstractClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.AbstractToAbstractClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testNonFinalToNonFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.NonFinalToNonFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.NonFinalToNonFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testNonFinalToFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.NonFinalToFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.NonFinalToFinalClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testFinalToNonFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.FinalToNonFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.FinalToNonFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    public void testFinalToFinalClass() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.FinalToFinalClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.FinalToFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    /*
     * Fields
     */

    public void testPrivateToPrivateField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToPrivateField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToPrivateField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPrivateToProtectedField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToProtectedField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToProtectedField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPrivateToPackageField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToPackagedField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToPackageField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPrivateToPublicField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToPublicField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testProtectedToPrivateField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToPrivateField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToPrivateField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    public void testProtectedToProtectedField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToProtectedField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToProtectedField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testProtectedToPackageField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToPackageField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToPackageField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    public void testProtectedToPublicField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToPublicField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPackageToPrivateField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToPrivateField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToPrivateField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPackageToProtectedField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToProtectedField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToProtectedField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPackageToPackageField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToPackagedField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToPackageField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPackageToPublicField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToPublicField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPublicToPrivateField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToPrivateField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToPrivateField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPublicToProtectedField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToProtectedField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToProtectedField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPublicToPackageField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToPackageField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToPackageField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    public void testPublicToPublicField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToPublicField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testNonFinalToNonFinalField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("nonFinalToNonFinalField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("nonFinalToNonFinalField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testNonFinalToFinalField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("nonFinalToFinalField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("nonFinalToFinalField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    public void testFinalToNonFinalField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("finalToNonFinalField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("finalToNonFinalField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testFinalToFinalField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("finalToFinalField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("finalToFinalField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testTypeToSameTypeField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("typeToSameTypeField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("typeToSameTypeField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    public void testTypeToDifferentTypeField() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("typeToDifferentTypeField"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("typeToDifferentTypeField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    /*
     * Constructors
     */

    public void testPrivateToPrivateConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPrivateToProtectedConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPrivateToPackageConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPrivateToPublicConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testProtectedToPrivateConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testProtectedToProtectedConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testProtectedToPackageConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testProtectedToPublicConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPackageToPrivateConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPackageToProtectedConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPackageToPackageConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPackageToPublicConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPublicToPrivateConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int, int)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int, int)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPublicToProtectedConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long, long)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long, long)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPublicToPackageConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float, float)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float, float)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testPublicToPublicConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double, double)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double, double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testThrowsToSameThrowsConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    public void testThrowsToDifferentThrowsConstructor() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object, java.lang.Object)"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object, java.lang.Object)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    /*
    * Methods
    */

    public void testPrivateToPrivateMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToPrivateMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToPrivateMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPrivateToProtectedMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToProtectedMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToProtectedMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPrivateToPackageMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToPackagedMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToPackageMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPrivateToPublicMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToPublicMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testProtectedToPrivateMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToPrivateMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToPrivateMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testProtectedToProtectedMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToProtectedMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToProtectedMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testProtectedToPackageMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToPackageMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToPackageMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testProtectedToPublicMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToPublicMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPackageToPrivateMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToPrivateMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToPrivateMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPackageToProtectedMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToProtectedMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToProtectedMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPackageToPackageMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToPackagedMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToPackageMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPackageToPublicMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToPublicMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPublicToPrivateMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToPrivateMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToPrivateMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPublicToProtectedMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToProtectedMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToProtectedMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPublicToPackageMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToPackageMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToPackageMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testPublicToPublicMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToPublicMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testConcreteToConcreteMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("concreteToConcreteMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("concreteToConcreteMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testConcreteToAbstractMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("concreteToAbstractMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("concreteToAbstractMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testAbstractToConcreteMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("abstractToConcreteMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("abstractToConcreteMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testAbstractToAbstractMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("abstractToAbstractMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("abstractToAbstractMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testNonStaticToNonStaticMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonStaticToNonStaticMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonStaticToNonStaticMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testNonStaticToStaticMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonStaticToStaticMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonStaticToStaticMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testStaticToNonStaticMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("staticToNonStaticMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("staticToNonStaticMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testStaticToStaticMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("staticToStaticMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("staticToStaticMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testNonFinalToNonFinalMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonFinalToNonFinalMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonFinalToNonFinalMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testNonFinalToFinalMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonFinalToFinalMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonFinalToFinalMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testFinalToNonFinalMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("finalToNonFinalMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("finalToNonFinalMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testFinalToFinalMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("finalToFinalMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("finalToFinalMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testReturnTypeToSameReturnTypeMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("returnTypeToSameReturnTypeMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("returnTypeToSameReturnTypeMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testReturnTypeToDifferentReturnTypeMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("returnTypeToDifferentReturnTypeMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("returnTypeToDifferentReturnTypeMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testThrowsToSameThrowsMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("throwsToSameThrowsMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("throwsToSameThrowsMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testThrowsToDifferentThrowsMethod() {
        Classfile classfile1 = getOldPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("throwsToDifferentThrowsMethod()"));
        Classfile classfile = getNewPublishedJar().getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("throwsToDifferentThrowsMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    public void testCompatibleClassWithIncompatibleMethod() {
        Classfile oldClass = getOldPublishedJar().getClassfile("ModifiedPackage.CompatibleClass");
        Classfile newClass = getNewPublishedJar().getClassfile("ModifiedPackage.CompatibleClass");

        Method_info oldMethod = oldClass.getMethod(m -> m.getSignature().equals("incompatibleMethod()"));
        Method_info newMethod = newClass.getMethod(m -> m.getSignature().equals("incompatibleMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }
}
