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

import com.jeantessier.classreader.*;

public class TestDifferencesFactory extends TestDifferencesFactoryBase {
    private ProjectDifferences projectDifferences;

    protected void setUp() throws Exception {
        super.setUp();

        DifferencesFactory factory = new DifferencesFactory();
        projectDifferences = (ProjectDifferences) factory.createProjectDifferences("test", "old", getOldPackages(), "new", getNewPackages());
    }

    public void testEmptyJars() throws IOException {
        DifferencesFactory factory = new DifferencesFactory();
        ProjectDifferences emptyDifferences = (ProjectDifferences) factory.createProjectDifferences("test", "old", new PackageMapper(), "new", new PackageMapper());

        assertEquals("name",        "test", emptyDifferences.getName());
        assertEquals("old version", "old",  emptyDifferences.getOldVersion());
        assertEquals("new version", "new",  emptyDifferences.getNewVersion());

        assertEquals("NbPackageDifferences: " + projectDifferences.getPackageDifferences(), 3, projectDifferences.getPackageDifferences().size());
    }

    public void testModifiedPackage() {
        String name = "ModifiedPackage";
        PackageDifferences differences = (PackageDifferences) find(name, projectDifferences.getPackageDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 13, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testNewPackage() {
        String name = "NewPackage";
        PackageDifferences differences = (PackageDifferences) find(name, projectDifferences.getPackageDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 0, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }

    public void testRemovedPackage() {
        String name = "RemovedPackage";
        PackageDifferences differences = (PackageDifferences) find(name, projectDifferences.getPackageDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 0, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testDeprecatedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".DeprecatedClass";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDeprecation()",        deprecatableDifferences.isNewDeprecation());
        assertTrue(name + ".RemovedDeprecation()",   !deprecatableDifferences.isRemovedDeprecation());

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testModifiedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".ModifiedClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 35, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testModifiedInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".ModifiedInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 11, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testNewClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".NewClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }

    public void testNewInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".NewInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }

    public void testRemovedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".RemovedClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testRemovedInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".RemovedInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testUndeprecatedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String name = packageName + ".UndeprecatedClass";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDeprecation()",       !deprecatableDifferences.isNewDeprecation());
        assertTrue(name + ".RemovedDeprecation()",    deprecatableDifferences.isRemovedDeprecation());

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testModifiedClassModifiedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
    
    public void testModifiedClassNewField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }
    
    public void testModifiedClassRemovedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
    
    public void testModifiedClassModifiedConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(int, int, int)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name + " not in " + classDifferences.getFeatureDifferences(), differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testModifiedClassModifiedCodeConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(float)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name + " not in " + classDifferences.getFeatureDifferences(), differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testModifiedClassNewConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(int, int, int, int, int, int)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name + " not in " + classDifferences.getFeatureDifferences(), differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }
    
    public void testModifiedClassRemovedConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass()";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name + " not in " + classDifferences.getFeatureDifferences(), differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
    
    public void testModifiedClassModifiedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testModifiedClassModifiedCodeMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedCodeMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }

    public void testModifiedClassNewMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }
    
    public void testModifiedClassRemovedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
    
    public void testModifiedInterfaceModifiedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
    
    public void testModifiedInterfaceNewField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }
    
    public void testModifiedInterfaceRemovedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
    
    public void testModifiedInterfaceModifiedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
    
    public void testModifiedInterfaceNewMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
    }
    
    public void testModifiedInterfaceRemovedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, projectDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
    }
}
