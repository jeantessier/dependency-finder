/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

public class TestDifferencesFactory extends TestCase {
    public static final String OLD_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "old";
    public static final String NEW_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

    private JarDifferences jarDifferences;

    protected void setUp() throws Exception {
        ClassfileLoader oldJar = new AggregatingClassfileLoader();
        oldJar.load(Collections.singleton(OLD_CLASSPATH));

        ClassfileLoader newJar = new AggregatingClassfileLoader();
        newJar.load(Collections.singleton(NEW_CLASSPATH));

        Validator oldValidator = new ListBasedValidator(new BufferedReader(new FileReader(OLD_CLASSPATH + ".txt")));
        Validator newValidator = new ListBasedValidator(new BufferedReader(new FileReader(NEW_CLASSPATH + ".txt")));

        DifferencesFactory factory = new DifferencesFactory(oldValidator, newValidator);
        jarDifferences = (JarDifferences) factory.createJarDifferences("test", "old", oldJar, "new", newJar);
    }

    public void testEmpty() throws IOException {
        Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("")));
        DifferencesFactory factory = new DifferencesFactory(validator, validator);
        JarDifferences emptyDifferences = (JarDifferences) factory.createJarDifferences("test", "old", new AggregatingClassfileLoader(), "new", new AggregatingClassfileLoader());

        assertEquals("name",        "test", emptyDifferences.getName());
        assertEquals("old version", "old",  emptyDifferences.getOldVersion());
        assertEquals("new version", "new",  emptyDifferences.getNewVersion());

        assertTrue("IsEmpty()", emptyDifferences.isEmpty());

        assertTrue("!IsEmpty()", !jarDifferences.isEmpty());
        assertEquals("NbPackageDifferences: " + jarDifferences.getPackageDifferences(), 5, jarDifferences.getPackageDifferences().size());
    }
    
    public void testDocumentedPackage() {
        String name = "DocumentedPackage";
        DocumentableDifferences documentableDifferences = (DocumentableDifferences) find(name, jarDifferences.getPackageDifferences());
        PackageDifferences differences = (PackageDifferences) documentableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDocumentation()",      documentableDifferences.isNewDocumentation());
        assertTrue(name + ".Remove Documentation()", !documentableDifferences.isRemovedDocumentation());
        assertTrue(name + ".IsEmpty()",              !documentableDifferences.isEmpty());

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 1, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
        
    public void testModifiedPackage() {
        String name = "ModifiedPackage";
        PackageDifferences differences = (PackageDifferences) find(name, jarDifferences.getPackageDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 14, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }

    public void testNewPackage() {
        String name = "NewPackage";
        PackageDifferences differences = (PackageDifferences) find(name, jarDifferences.getPackageDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 0, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }

    public void testRemovedPackage() {
        String name = "RemovedPackage";
        PackageDifferences differences = (PackageDifferences) find(name, jarDifferences.getPackageDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 0, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testUndocumentedPackage() {
        String name = "UndocumentedPackage";
        DocumentableDifferences documentableDifferences = (DocumentableDifferences) find(name, jarDifferences.getPackageDifferences());
        PackageDifferences differences = (PackageDifferences) documentableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDocumentation()",     !documentableDifferences.isNewDocumentation());
        assertTrue(name + ".Remove Documentation()",  documentableDifferences.isRemovedDocumentation());
        assertTrue(name + ".IsEmpty()",              !documentableDifferences.isEmpty());

        assertEquals(name, differences.getName());
        assertEquals(name + ".ClassDifferences: " + differences.getClassDifferences(), 1, differences.getClassDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testDeprecatedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".DeprecatedClass";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDeprecation()",        deprecatableDifferences.isNewDeprecation());
        assertTrue(name + ".RemovedDeprecation()",   !deprecatableDifferences.isRemovedDeprecation());
        assertTrue(name + ".IsEmpty()",              !deprecatableDifferences.isEmpty());
        
        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",     differences.isEmpty());
    }
        
    public void testUndocumentedPackagePublishedClass() {
        String packageName = "UndocumentedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) ((DecoratorDifferences) find(packageName, jarDifferences.getPackageDifferences())).getLeafComponent();

        String name = packageName + ".PublishedClass";
        DocumentableDifferences documentableDifferences = (DocumentableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) documentableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDocumentation()",     !documentableDifferences.isNewDocumentation());
        assertTrue(name + ".Remove Documentation()",  documentableDifferences.isRemovedDocumentation());
        assertTrue(name + ".IsEmpty()",              !documentableDifferences.isEmpty());

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",     differences.isEmpty());
    }
        
    public void testDocumentedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".DocumentedClass";
        DocumentableDifferences documentableDifferences = (DocumentableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) documentableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDocumentation()",      documentableDifferences.isNewDocumentation());
        assertTrue(name + ".Remove Documentation()", !documentableDifferences.isRemovedDocumentation());
        assertTrue(name + ".IsEmpty()",              !documentableDifferences.isEmpty());

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",     differences.isEmpty());
    }

    public void testModifiedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".ModifiedClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 21, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".ModifiedInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 14, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testNewClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".NewClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testNewInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".NewInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testRemovedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".RemovedClass";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testRemovedInterface() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".RemovedInterface";
        ClassDifferences differences = (ClassDifferences) find(name, packageDifferences.getClassDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testUndeprecatedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".UndeprecatedClass";
        DeprecatableDifferences deprecatableDifferences = (DeprecatableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) deprecatableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDeprecation()",       !deprecatableDifferences.isNewDeprecation());
        assertTrue(name + ".RemovedDeprecation()",    deprecatableDifferences.isRemovedDeprecation());
        assertTrue(name + ".IsEmpty()",              !deprecatableDifferences.isEmpty());

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",     differences.isEmpty());
    }
    
    public void testUndocumentedClass() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String name = packageName + ".UndocumentedClass";
        DocumentableDifferences documentableDifferences = (DocumentableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) documentableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDocumentation()",     !documentableDifferences.isNewDocumentation());
        assertTrue(name + ".Remove Documentation()",  documentableDifferences.isRemovedDocumentation());
        assertTrue(name + ".IsEmpty()",              !documentableDifferences.isEmpty());

        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",     differences.isEmpty());
    }
        
    public void testDocumentedPackagePublishedClass() {
        String packageName = "DocumentedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) ((DecoratorDifferences) find(packageName, jarDifferences.getPackageDifferences())).getLeafComponent();

        String name = packageName + ".PublishedClass";
        DocumentableDifferences documentableDifferences = (DocumentableDifferences) find(name, packageDifferences.getClassDifferences());
        ClassDifferences differences = (ClassDifferences) documentableDifferences.getComponent();
        assertNotNull(name, differences);

        assertTrue(name + ".NewDocumentation()",      documentableDifferences.isNewDocumentation());
        assertTrue(name + ".Remove Documentation()", !documentableDifferences.isRemovedDocumentation());
        assertTrue(name + ".IsEmpty()",              !documentableDifferences.isEmpty());
        
        assertEquals(name, differences.getName());
        assertEquals(name + ".FeatureDifferences", 0, differences.getFeatureDifferences().size());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",     differences.isEmpty());
    }
    
    public void testModifiedClassModifiedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassNewField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassRemovedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassModifiedConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(int, int, int)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name + " not in " + classDifferences.getFeatureDifferences(), differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassNewConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass(int, int, int, int, int, int)";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name + " not in " + classDifferences.getFeatureDifferences(), differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassRemovedConstructor() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".ModifiedClass()";
        ConstructorDifferences differences = (ConstructorDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name + " not in " + classDifferences.getFeatureDifferences(), differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassModifiedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassNewMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedClassRemovedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedClass";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedMethod()";
        MethodDifferences differences = (MethodDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedInterfaceModifiedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedInterfaceNewField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedInterfaceRemovedField() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedField";
        FieldDifferences differences = (FieldDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedInterfaceModifiedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".modifiedMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()",  differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedInterfaceNewMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".newMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",  !differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",       differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }
    
    public void testModifiedInterfaceRemovedMethod() {
        String packageName = "ModifiedPackage";
        PackageDifferences packageDifferences = (PackageDifferences) find(packageName, jarDifferences.getPackageDifferences());

        String className = packageName + ".ModifiedInterface";
        ClassDifferences classDifferences = (ClassDifferences) find(className, packageDifferences.getClassDifferences());

        String name = className + ".removedMethod()";
        FeatureDifferences differences = (FeatureDifferences) find(name, classDifferences.getFeatureDifferences());
        assertNotNull(name, differences);

        assertEquals(name, differences.getName());
        assertTrue(name + ".IsRemoved()",   differences.isRemoved());
        assertTrue(name + ".IsModified()", !differences.isModified());
        assertTrue(name + ".IsNew()",      !differences.isNew());
        assertTrue(name + ".IsEmpty()",    !differences.isEmpty());
    }

    private Differences find(String name, Collection differences) {
        Differences result = null;

        Iterator i = differences.iterator();
        while (result == null && i.hasNext()) {
            Differences candidate = (Differences) i.next();
            if (name.equals(candidate.getName())) {
                result = candidate;
            }
        }

        return result;
    }
}
