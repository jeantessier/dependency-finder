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
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestIncompatibleDifferenceStrategyWithPublishedAPI {
    public static final String OLD_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/old-published/build/libs/old-published.jar").toString();
    public static final String NEW_PUBLISHED_CLASSPATH = Paths.get("jarjardiff/new-published/build/libs/new-published.jar").toString();

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final IncompatibleDifferenceStrategy strategy = new IncompatibleDifferenceStrategy(context.mock(DifferenceStrategy.class));
    private final ClassfileLoader oldLoader = new AggregatingClassfileLoader();
    private final ClassfileLoader newLoader = new AggregatingClassfileLoader();

    @BeforeEach
    void setUp() throws Exception {
        oldLoader.load(Collections.singleton(OLD_PUBLISHED_CLASSPATH));
        newLoader.load(Collections.singleton(NEW_PUBLISHED_CLASSPATH));
    }

    /*
     * Classes
     */

    @Test
    void testPublicToPublicClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.PublicToPublicClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.PublicToPublicClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testPublicToPackageClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.PublicToPackageClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.PublicToPackageClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testPackageToPublicClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.PackageToPublicClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.PackageToPublicClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testPackageToPackageClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.PackageToPackageClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.PackageToPackageClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testClassToClassClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.ClassToClassClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.ClassToClassClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testClassToInterfaceClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.ClassToInterfaceClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.ClassToInterfaceClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testInterfaceToClassClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.InterfaceToClassClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.InterfaceToClassClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testInterfaceToInterfaceClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.InterfaceToInterfaceClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.InterfaceToInterfaceClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testConcreteToConcreteClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.ConcreteToConcreteClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.ConcreteToConcreteClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testConcreteToAbstractClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.ConcreteToAbstractClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.ConcreteToAbstractClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testAbstractToConcreteClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.AbstractToConcreteClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.AbstractToConcreteClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testAbstractToAbstractClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.AbstractToAbstractClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.AbstractToAbstractClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testNonFinalToNonFinalClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.NonFinalToNonFinalClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.NonFinalToNonFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testNonFinalToFinalClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.NonFinalToFinalClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.NonFinalToFinalClass");

        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testFinalToNonFinalClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.FinalToNonFinalClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.FinalToNonFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    @Test
    void testFinalToFinalClass() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.FinalToFinalClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.FinalToFinalClass");

        assertFalse(strategy.isClassDifferent(oldClass, newClass));
    }

    /*
     * Fields
     */

    @Test
    void testPrivateToPrivateField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToPrivateField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToPrivateField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPrivateToProtectedField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToProtectedField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToProtectedField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPrivateToPackageField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToPackagedField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToPackageField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPrivateToPublicField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("privateToPublicField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("privateToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testProtectedToPrivateField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToPrivateField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToPrivateField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testProtectedToProtectedField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToProtectedField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToProtectedField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testProtectedToPackageField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToPackageField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToPackageField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testProtectedToPublicField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("protectedToPublicField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("protectedToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPackageToPrivateField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToPrivateField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToPrivateField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPackageToProtectedField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToProtectedField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToProtectedField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPackageToPackageField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToPackagedField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToPackageField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPackageToPublicField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("packageToPublicField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("packageToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPublicToPrivateField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToPrivateField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToPrivateField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPublicToProtectedField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToProtectedField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToProtectedField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPublicToPackageField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToPackageField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToPackageField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testPublicToPublicField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("publicToPublicField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("publicToPublicField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testNonFinalToNonFinalField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("nonFinalToNonFinalField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("nonFinalToNonFinalField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testNonFinalToFinalField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("nonFinalToFinalField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("nonFinalToFinalField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testFinalToNonFinalField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("finalToNonFinalField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("finalToNonFinalField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testFinalToFinalField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("finalToFinalField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("finalToFinalField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testTypeToSameTypeField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("typeToSameTypeField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("typeToSameTypeField"));

        assertFalse(strategy.isFieldDifferent(oldField, newField));
    }

    @Test
    void testTypeToDifferentTypeField() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info oldField = classfile1.getField(f -> f.getName().equals("typeToDifferentTypeField"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Field_info newField = classfile.getField(f -> f.getName().equals("typeToDifferentTypeField"));

        assertTrue(strategy.isFieldDifferent(oldField, newField));
    }

    /*
     * Constructors
     */

    @Test
    void testPrivateToPrivateConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPrivateToProtectedConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPrivateToPackageConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPrivateToPublicConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testProtectedToPrivateConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testProtectedToProtectedConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testProtectedToPackageConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testProtectedToPublicConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPackageToPrivateConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPackageToProtectedConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPackageToPackageConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPackageToPublicConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPublicToPrivateConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int, int)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(int, int, int, int)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPublicToProtectedConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long, long)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(long, long, long, long)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPublicToPackageConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float, float)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(float, float, float, float)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testPublicToPublicConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double, double)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(double, double, double, double)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testThrowsToSameThrowsConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object)"));

        assertFalse(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    @Test
    void testThrowsToDifferentThrowsConstructor() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldConstructor = classfile1.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object, java.lang.Object)"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newConstructor = classfile.getMethod(m -> m.getSignature().equals("ModifiedClass(java.lang.Object, java.lang.Object)"));

        assertTrue(strategy.isMethodDifferent(oldConstructor, newConstructor));
    }

    /*
    * Methods
    */

    @Test
    void testPrivateToPrivateMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToPrivateMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToPrivateMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPrivateToProtectedMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToProtectedMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToProtectedMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPrivateToPackageMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToPackagedMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToPackageMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPrivateToPublicMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("privateToPublicMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("privateToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testProtectedToPrivateMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToPrivateMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToPrivateMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testProtectedToProtectedMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToProtectedMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToProtectedMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testProtectedToPackageMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToPackageMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToPackageMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testProtectedToPublicMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("protectedToPublicMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("protectedToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPackageToPrivateMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToPrivateMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToPrivateMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPackageToProtectedMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToProtectedMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToProtectedMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPackageToPackageMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToPackagedMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToPackageMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPackageToPublicMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("packageToPublicMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("packageToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPublicToPrivateMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToPrivateMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToPrivateMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPublicToProtectedMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToProtectedMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToProtectedMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPublicToPackageMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToPackageMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToPackageMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testPublicToPublicMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("publicToPublicMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("publicToPublicMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testConcreteToConcreteMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("concreteToConcreteMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("concreteToConcreteMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testConcreteToAbstractMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("concreteToAbstractMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("concreteToAbstractMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testAbstractToConcreteMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("abstractToConcreteMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("abstractToConcreteMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testAbstractToAbstractMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("abstractToAbstractMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("abstractToAbstractMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testNonStaticToNonStaticMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonStaticToNonStaticMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonStaticToNonStaticMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testNonStaticToStaticMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonStaticToStaticMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonStaticToStaticMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testStaticToNonStaticMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("staticToNonStaticMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("staticToNonStaticMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testStaticToStaticMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("staticToStaticMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("staticToStaticMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testNonFinalToNonFinalMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonFinalToNonFinalMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonFinalToNonFinalMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testNonFinalToFinalMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("nonFinalToFinalMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("nonFinalToFinalMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testFinalToNonFinalMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("finalToNonFinalMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("finalToNonFinalMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testFinalToFinalMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("finalToFinalMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("finalToFinalMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testReturnTypeToSameReturnTypeMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("returnTypeToSameReturnTypeMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("returnTypeToSameReturnTypeMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testReturnTypeToDifferentReturnTypeMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("returnTypeToDifferentReturnTypeMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("returnTypeToDifferentReturnTypeMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testThrowsToSameThrowsMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("throwsToSameThrowsMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("throwsToSameThrowsMethod()"));

        assertFalse(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testThrowsToDifferentThrowsMethod() {
        Classfile classfile1 = oldLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info oldMethod = classfile1.getMethod(m -> m.getSignature().equals("throwsToDifferentThrowsMethod()"));
        Classfile classfile = newLoader.getClassfile("ModifiedPackage.ModifiedClass");
        Method_info newMethod = classfile.getMethod(m -> m.getSignature().equals("throwsToDifferentThrowsMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
    }

    @Test
    void testCompatibleClassWithIncompatibleMethod() {
        Classfile oldClass = oldLoader.getClassfile("ModifiedPackage.CompatibleClass");
        Classfile newClass = newLoader.getClassfile("ModifiedPackage.CompatibleClass");

        Method_info oldMethod = oldClass.getMethod(m -> m.getSignature().equals("incompatibleMethod()"));
        Method_info newMethod = newClass.getMethod(m -> m.getSignature().equals("incompatibleMethod()"));

        assertTrue(strategy.isMethodDifferent(oldMethod, newMethod));
        assertTrue(strategy.isClassDifferent(oldClass, newClass));
    }
}
