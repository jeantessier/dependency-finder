/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import org.junit.jupiter.api.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDifferencesFactory extends TestDifferencesFactoryBase {
    private ProjectDifferences projectDifferences;

    @BeforeEach
    void setUp() {
        DifferencesFactory factory = new DifferencesFactory();
        projectDifferences = (ProjectDifferences) factory.createProjectDifferences("test", "old", getOldPackages(), "new", getNewPackages());
    }

    @Test
    void testEmptyJars() {
        DifferencesFactory factory = new DifferencesFactory();
        ProjectDifferences emptyDifferences = (ProjectDifferences) factory.createProjectDifferences("test", "old", new PackageMapper(), "new", new PackageMapper());

        assertEquals("test", emptyDifferences.getName(), "name");
        assertEquals("old",  emptyDifferences.getOldVersion(), "old version");
        assertEquals("new",  emptyDifferences.getNewVersion(), "new version");

        assertEquals(3, projectDifferences.getPackageDifferences().size(), "NbPackageDifferences: " + projectDifferences.getPackageDifferences());
    }

    @Test
    void testModifiedPackage() {
        String name = "ModifiedPackage";
        PackageDifferences differences = (PackageDifferences) find(name, projectDifferences.getPackageDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(17, differences.getClassDifferences().size(), name + ".ClassDifferences: " + differences.getClassDifferences());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testNewPackage() {
        String name = "NewPackage";
        PackageDifferences differences = (PackageDifferences) find(name, projectDifferences.getPackageDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getClassDifferences().size(), name + ".ClassDifferences: " + differences.getClassDifferences());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testRemovedPackage() {
        String name = "RemovedPackage";
        PackageDifferences differences = (PackageDifferences) find(name, projectDifferences.getPackageDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getClassDifferences().size(), name + ".ClassDifferences: " + differences.getClassDifferences());
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testDeprecatedClassByAnnotation() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".DeprecatedClassByAnnotation";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(differences, name);

        assertTrue(deprecatableDifferences.isNewDeprecation(), name + ".NewDeprecation()");
        assertFalse(deprecatableDifferences.isRemovedDeprecation(), name + ".RemovedDeprecation()");

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testDeprecatedClassByJavadocTag() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".DeprecatedClassByJavadocTag";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(differences, name);

        assertTrue(deprecatableDifferences.isNewDeprecation(), name + ".NewDeprecation()");
        assertFalse(deprecatableDifferences.isRemovedDeprecation(), name + ".RemovedDeprecation()");

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testModifiedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".ModifiedClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(41, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testModifiedInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".ModifiedInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(15, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testNewClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".NewClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testNewInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".NewInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testRemovedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".RemovedClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testRemovedInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".RemovedInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testUndeprecatedClassByAnnotation() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".UndeprecatedClassByAnnotation";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(differences, name);

        assertFalse(deprecatableDifferences.isNewDeprecation(), name + ".NewDeprecation()");
        assertTrue(deprecatableDifferences.isRemovedDeprecation(), name + ".RemovedDeprecation()");

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testUndeprecatedClassByJavadocTag() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".UndeprecatedClassByJavadocTag";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(differences, name);

        assertFalse(deprecatableDifferences.isNewDeprecation(), name + ".NewDeprecation()");
        assertTrue(deprecatableDifferences.isRemovedDeprecation(), name + ".RemovedDeprecation()");

        assertEquals(name, differences.getName());
        assertEquals(0, differences.getFeatureDifferences().size(), name + ".FeatureDifferences");
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testModifiedClassModifiedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedClassNewField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedClassRemovedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedClassModifiedConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(int, int, int)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name + " not in " + classDifferences.getFeatureDifferences());

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testModifiedClassModifiedCodeConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(float)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name + " not in " + classDifferences.getFeatureDifferences());

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testModifiedClassNewConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(int, int, int, int, int, int)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name + " not in " + classDifferences.getFeatureDifferences());

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedClassRemovedConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass()";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name + " not in " + classDifferences.getFeatureDifferences());

        assertEquals(name, differences.getName());
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedClassModifiedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testModifiedClassModifiedCodeMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedCodeMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }

    @Test
    void testModifiedClassNewMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedClassRemovedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedInterfaceModifiedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedInterfaceNewField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedInterfaceRemovedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedInterfaceModifiedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertTrue(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedInterfaceNewMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertFalse(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertTrue(differences.isNew(), name + ".IsNew()");
    }
    
    @Test
    void testModifiedInterfaceRemovedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(differences, name);

        assertEquals(name, differences.getName());
        assertTrue(differences.isRemoved(), name + ".IsRemoved()");
        assertFalse(differences.isModified(), name + ".IsModified()");
        assertFalse(differences.isNew(), name + ".IsNew()");
    }
}
