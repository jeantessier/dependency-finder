/*
 *  Copyright (c) 2001-2003, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *  	* Redistributions of source code must retain the above copyright
 *  	  notice, this list of conditions and the following disclaimer.
 *  
 *  	* Redistributions in binary form must reproduce the above copyright
 *  	  notice, this list of conditions and the following disclaimer in the
 *  	  documentation and/or other materials provided with the distribution.
 *  
 *  	* Neither the name of Jean Tessier nor the names of his contributors
 *  	  may be used to endorse or promote products derived from this software
 *  	  without specific prior written permission.
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

	private JarDifferences jar_differences;

	protected void setUp() throws Exception {
		ClassfileLoader old_jar = new AggregatingClassfileLoader();
		old_jar.Load(Collections.singleton(OLD_CLASSPATH));

		ClassfileLoader new_jar = new AggregatingClassfileLoader();
		new_jar.Load(Collections.singleton(NEW_CLASSPATH));

		Validator old_validator = new ListBasedValidator(new BufferedReader(new FileReader(OLD_CLASSPATH + ".txt")));
		Validator new_validator = new ListBasedValidator(new BufferedReader(new FileReader(NEW_CLASSPATH + ".txt")));

		DifferencesFactory factory = new DifferencesFactory(old_validator, new_validator);
		jar_differences = (JarDifferences) factory.CreateJarDifferences("test", "old", old_jar, "new", new_jar);
	}

	public void testEmpty() throws IOException {
		Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("")));
		DifferencesFactory factory = new DifferencesFactory(validator, validator);
		JarDifferences empty_differences = (JarDifferences) factory.CreateJarDifferences("test", "old", new AggregatingClassfileLoader(), "new", new AggregatingClassfileLoader());

		assertEquals("name",        "test", empty_differences.Name());
		assertEquals("old version", "old",  empty_differences.OldVersion());
		assertEquals("new version", "new",  empty_differences.NewVersion());

		assertTrue("IsEmpty()", empty_differences.IsEmpty());

		assertTrue("!IsEmpty()", !jar_differences.IsEmpty());
		assertEquals("NbPackageDifferences: " + jar_differences.PackageDifferences(), 5, jar_differences.PackageDifferences().size());
	}
	
	public void testDocumentedPackage() {
		String name = "DocumentedPackage";
		DocumentableDifferences documentable_differences = (DocumentableDifferences) Find(name, jar_differences.PackageDifferences());
		PackageDifferences differences = (PackageDifferences) documentable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDocumentation()",      documentable_differences.NewDocumentation());
		assertTrue(name + ".Remove Documentation()", !documentable_differences.RemovedDocumentation());
		assertTrue(name + ".IsEmpty()",              !documentable_differences.IsEmpty());

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 1, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
		
	public void testModifiedPackage() {
		String name = "ModifiedPackage";
		PackageDifferences differences = (PackageDifferences) Find(name, jar_differences.PackageDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 14, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}

	public void testNewPackage() {
		String name = "NewPackage";
		PackageDifferences differences = (PackageDifferences) Find(name, jar_differences.PackageDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 0, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}

	public void testRemovedPackage() {
		String name = "RemovedPackage";
		PackageDifferences differences = (PackageDifferences) Find(name, jar_differences.PackageDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 0, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testUndocumentedPackage() {
		String name = "UndocumentedPackage";
		DocumentableDifferences documentable_differences = (DocumentableDifferences) Find(name, jar_differences.PackageDifferences());
		PackageDifferences differences = (PackageDifferences) documentable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDocumentation()",     !documentable_differences.NewDocumentation());
		assertTrue(name + ".Remove Documentation()",  documentable_differences.RemovedDocumentation());
		assertTrue(name + ".IsEmpty()",              !documentable_differences.IsEmpty());

		assertEquals(name, differences.Name());
		assertEquals(name + ".ClassDifferences: " + differences.ClassDifferences(), 1, differences.ClassDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testDeprecatedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".DeprecatedClass";
		DeprecatableDifferences deprecatable_differences = (DeprecatableDifferences) Find(name, package_differences.ClassDifferences());
		ClassDifferences differences = (ClassDifferences) deprecatable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDeprecation()",        deprecatable_differences.NewDeprecation());
		assertTrue(name + ".RemovedDeprecation()",   !deprecatable_differences.RemovedDeprecation());
		assertTrue(name + ".IsEmpty()",              !deprecatable_differences.IsEmpty());
		
		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",     differences.IsEmpty());
	}
		
	public void testUndocumentedPackagePublishedClass() {
		String package_name = "UndocumentedPackage";
		PackageDifferences package_differences = (PackageDifferences) ((DecoratorDifferences) Find(package_name, jar_differences.PackageDifferences())).LeafComponent();

		String name = package_name + ".PublishedClass";
		DocumentableDifferences documentable_differences = (DocumentableDifferences) Find(name, package_differences.ClassDifferences());
		ClassDifferences differences = (ClassDifferences) documentable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDocumentation()",     !documentable_differences.NewDocumentation());
		assertTrue(name + ".Remove Documentation()",  documentable_differences.RemovedDocumentation());
		assertTrue(name + ".IsEmpty()",              !documentable_differences.IsEmpty());

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",     differences.IsEmpty());
	}
		
	public void testDocumentedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".DocumentedClass";
		DocumentableDifferences documentable_differences = (DocumentableDifferences) Find(name, package_differences.ClassDifferences());
		ClassDifferences differences = (ClassDifferences) documentable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDocumentation()",      documentable_differences.NewDocumentation());
		assertTrue(name + ".Remove Documentation()", !documentable_differences.RemovedDocumentation());
		assertTrue(name + ".IsEmpty()",              !documentable_differences.IsEmpty());

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",     differences.IsEmpty());
	}

	public void testModifiedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".ModifiedClass";
		ClassDifferences differences = (ClassDifferences) Find(name, package_differences.ClassDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 21, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterface() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".ModifiedInterface";
		ClassDifferences differences = (ClassDifferences) Find(name, package_differences.ClassDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 14, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testNewClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".NewClass";
		ClassDifferences differences = (ClassDifferences) Find(name, package_differences.ClassDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testNewInterface() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".NewInterface";
		ClassDifferences differences = (ClassDifferences) Find(name, package_differences.ClassDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testRemovedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".RemovedClass";
		ClassDifferences differences = (ClassDifferences) Find(name, package_differences.ClassDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testRemovedInterface() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".RemovedInterface";
		ClassDifferences differences = (ClassDifferences) Find(name, package_differences.ClassDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testUndeprecatedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".UndeprecatedClass";
		DeprecatableDifferences deprecatable_differences = (DeprecatableDifferences) Find(name, package_differences.ClassDifferences());
		ClassDifferences differences = (ClassDifferences) deprecatable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDeprecation()",       !deprecatable_differences.NewDeprecation());
		assertTrue(name + ".RemovedDeprecation()",    deprecatable_differences.RemovedDeprecation());
		assertTrue(name + ".IsEmpty()",              !deprecatable_differences.IsEmpty());

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",     differences.IsEmpty());
	}
	
	public void testUndocumentedClass() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String name = package_name + ".UndocumentedClass";
		DocumentableDifferences documentable_differences = (DocumentableDifferences) Find(name, package_differences.ClassDifferences());
		ClassDifferences differences = (ClassDifferences) documentable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDocumentation()",     !documentable_differences.NewDocumentation());
		assertTrue(name + ".Remove Documentation()",  documentable_differences.RemovedDocumentation());
		assertTrue(name + ".IsEmpty()",              !documentable_differences.IsEmpty());

		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",     differences.IsEmpty());
	}
		
	public void testDocumentedPackagePublishedClass() {
		String package_name = "DocumentedPackage";
		PackageDifferences package_differences = (PackageDifferences) ((DecoratorDifferences) Find(package_name, jar_differences.PackageDifferences())).LeafComponent();

		String name = package_name + ".PublishedClass";
		DocumentableDifferences documentable_differences = (DocumentableDifferences) Find(name, package_differences.ClassDifferences());
		ClassDifferences differences = (ClassDifferences) documentable_differences.Component();
		assertNotNull(name, differences);

		assertTrue(name + ".NewDocumentation()",      documentable_differences.NewDocumentation());
		assertTrue(name + ".Remove Documentation()", !documentable_differences.RemovedDocumentation());
		assertTrue(name + ".IsEmpty()",              !documentable_differences.IsEmpty());
		
		assertEquals(name, differences.Name());
		assertEquals(name + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",     differences.IsEmpty());
	}
	
	public void testModifiedClassModifiedField() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".modified_field";
		FieldDifferences differences = (FieldDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassNewField() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".new_field";
		FieldDifferences differences = (FieldDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassRemovedField() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".removed_field";
		FieldDifferences differences = (FieldDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassModifiedConstructor() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".ModifiedClass(int, int, int)";
		ConstructorDifferences differences = (ConstructorDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name + " not in " + class_differences.FeatureDifferences(), differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassNewConstructor() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".ModifiedClass(int, int, int, int, int, int)";
		ConstructorDifferences differences = (ConstructorDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name + " not in " + class_differences.FeatureDifferences(), differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassRemovedConstructor() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".ModifiedClass()";
		ConstructorDifferences differences = (ConstructorDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name + " not in " + class_differences.FeatureDifferences(), differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassModifiedMethod() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".ModifiedMethod()";
		MethodDifferences differences = (MethodDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassNewMethod() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".NewMethod()";
		MethodDifferences differences = (MethodDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedClassRemovedMethod() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedClass";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".RemovedMethod()";
		MethodDifferences differences = (MethodDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterfaceModifiedField() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedInterface";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".modified_field";
		FieldDifferences differences = (FieldDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterfaceNewField() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedInterface";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".new_field";
		FieldDifferences differences = (FieldDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterfaceRemovedField() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedInterface";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".removed_field";
		FieldDifferences differences = (FieldDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterfaceModifiedMethod() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedInterface";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".ModifiedMethod()";
		FeatureDifferences differences = (FeatureDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()",  differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterfaceNewMethod() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedInterface";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".NewMethod()";
		FeatureDifferences differences = (FeatureDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",       differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testModifiedInterfaceRemovedMethod() {
		String package_name = "ModifiedPackage";
		PackageDifferences package_differences = (PackageDifferences) Find(package_name, jar_differences.PackageDifferences());

		String class_name = package_name + ".ModifiedInterface";
		ClassDifferences class_differences = (ClassDifferences) Find(class_name, package_differences.ClassDifferences());

		String name = class_name + ".RemovedMethod()";
		FeatureDifferences differences = (FeatureDifferences) Find(name, class_differences.FeatureDifferences());
		assertNotNull(name, differences);

		assertEquals(name, differences.Name());
		assertTrue(name + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(name + ".IsModified()", !differences.IsModified());
		assertTrue(name + ".IsNew()",      !differences.IsNew());
		assertTrue(name + ".IsEmpty()",    !differences.IsEmpty());
	}

	private Differences Find(String name, Collection differences) {
		Differences result = null;

		Iterator i = differences.iterator();
		while (result == null && i.hasNext()) {
			Differences candidate = (Differences) i.next();
			if (name.equals(candidate.Name())) {
				result = candidate;
			}
		}

		return result;
	}
}
