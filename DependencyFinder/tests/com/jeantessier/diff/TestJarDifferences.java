/*
 *  Copyright (c) 2001-2002, Jean Tessier
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

public class TestJarDifferences extends TestCase {
	public static final String OLD_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "old";
	public static final String NEW_CLASSPATH = "tests" + File.separator + "JarJarDiff" + File.separator + "new";

	private JarDifferences jar_differences;

	public TestJarDifferences(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		DirectoryClassfileLoader loader;

		ClassfileLoader old_jar = new AggregatingClassfileLoader();
		loader = new DirectoryClassfileLoader(old_jar);
		loader.Load(new DirectoryExplorer(OLD_CLASSPATH));

		ClassfileLoader new_jar = new AggregatingClassfileLoader();
		loader = new DirectoryClassfileLoader(new_jar);
		loader.Load(new DirectoryExplorer(NEW_CLASSPATH));

		Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("")));

		jar_differences = new JarDifferences("test", "old", validator, old_jar, "new", validator, new_jar);
	}

	public void testEmpty() throws IOException {
		Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("")));
		JarDifferences empty_differences = new JarDifferences("test", "old", validator, new AggregatingClassfileLoader(), "new", validator, new AggregatingClassfileLoader());

		assertEquals("name",        "test", empty_differences.Name());
		assertEquals("old version", "old",  empty_differences.OldVersion());
		assertEquals("new version", "new",  empty_differences.NewVersion());

		assertTrue("IsEmpty()", empty_differences.IsEmpty());

		assertTrue("!IsEmpty()", !jar_differences.IsEmpty());
		assertEquals("NbPackageDifferences: " + jar_differences.PackageDifferences(), 3, jar_differences.PackageDifferences().size());
	}
	
	public void testPackages() {
		Object[] package_differences = jar_differences.PackageDifferences().toArray();

		PackageDifferences differences;

		differences = (PackageDifferences) package_differences[0];
		assertEquals("[0]", "ModifiedPackage", differences.Name());
		assertEquals(differences + ".ClassDifferences: " + differences.ClassDifferences(), 6, differences.ClassDifferences().size());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()",  differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (PackageDifferences) package_differences[1];
		assertEquals("[1]", "NewPackage", differences.Name());
		assertEquals(differences + ".ClassDifferences", 0, differences.ClassDifferences().size());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",       differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (PackageDifferences) package_differences[2];
		assertEquals("[2]", "RemovedPackage", differences.Name());
		assertEquals(differences + ".ClassDifferences", 0, differences.ClassDifferences().size());
		assertTrue(differences + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());
	}
	
	public void testClasses() {
		PackageDifferences package_differences = (PackageDifferences) jar_differences.PackageDifferences().iterator().next();
		DecoratorDifferences[] class_differences = (DecoratorDifferences[]) package_differences.ClassDifferences().toArray(new DecoratorDifferences[0]);

		ClassDifferences differences;

		differences = (ClassDifferences) class_differences[0].Component();
		assertEquals("[0][0]", "ModifiedPackage.ModifiedClass", differences.Name());
		assertEquals(differences + ".FeatureDifferences", 3, differences.FeatureDifferences().size());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()",  differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (ClassDifferences) class_differences[1].Component();
		assertEquals("[0][1]", "ModifiedPackage.ModifiedInterface", differences.Name());
		assertEquals(differences + ".FeatureDifferences", 3, differences.FeatureDifferences().size());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()",  differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (ClassDifferences) class_differences[2].Component();
		assertEquals("[0][2]", "ModifiedPackage.NewClass", differences.Name());
		assertEquals(differences + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",       differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (ClassDifferences) class_differences[3].Component();
		assertEquals("[0][3]", "ModifiedPackage.NewInterface", differences.Name());
		assertEquals(differences + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",       differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (ClassDifferences) class_differences[4].Component();
		assertEquals("[0][4]", "ModifiedPackage.RemovedClass", differences.Name());
		assertEquals(differences + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(differences + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (ClassDifferences) class_differences[5].Component();
		assertEquals("[0][5]", "ModifiedPackage.RemovedInterface", differences.Name());
		assertEquals(differences + ".FeatureDifferences", 0, differences.FeatureDifferences().size());
		assertTrue(differences + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());
	}		
	
	public void testClassFeatures() {
		PackageDifferences package_differences = (PackageDifferences) jar_differences.PackageDifferences().iterator().next();

		Iterator i = package_differences.ClassDifferences().iterator();
		ClassDifferences class_differences = (ClassDifferences) ((DecoratorDifferences) i.next()).Component();
		
		DecoratorDifferences[] feature_differences = (DecoratorDifferences[]) class_differences.FeatureDifferences().toArray(new DecoratorDifferences[0]);
		
		FeatureDifferences differences;

		differences = (FeatureDifferences) feature_differences[0].Component();
		assertEquals("[0][0][0]", "ModifiedPackage.ModifiedClass.ModifiedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()",  differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[1].Component();
		assertEquals("[0][0][1]", "ModifiedPackage.ModifiedClass.NewMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",       differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[2].Component();
		assertEquals("[0][0][2]", "ModifiedPackage.ModifiedClass.RemovedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());
	}		
	
	public void testInterfaceFeatures() {
		PackageDifferences package_differences = (PackageDifferences) jar_differences.PackageDifferences().iterator().next();

		Iterator i = package_differences.ClassDifferences().iterator();
		ClassDifferences class_differences = (ClassDifferences) ((DecoratorDifferences) i.next()).Component();
		ClassDifferences interface_differences = (ClassDifferences) ((DecoratorDifferences) i.next()).Component();
		
		DecoratorDifferences[] feature_differences = (DecoratorDifferences[]) interface_differences.FeatureDifferences().toArray(new DecoratorDifferences[0]);

		FeatureDifferences differences;

		differences = (FeatureDifferences) feature_differences[0].Component();
		assertEquals("[0][1][0]", "ModifiedPackage.ModifiedInterface.ModifiedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()",  differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[1].Component();
		assertEquals("[0][1][1]", "ModifiedPackage.ModifiedInterface.NewMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",  !differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",       differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());

		differences = (FeatureDifferences) feature_differences[2].Component();
		assertEquals("[0][1][2]", "ModifiedPackage.ModifiedInterface.RemovedMethod()", differences.Name());
		assertTrue(differences + ".IsRemoved()",   differences.IsRemoved());
		assertTrue(differences + ".IsModified()", !differences.IsModified());
		assertTrue(differences + ".IsNew()",      !differences.IsNew());
		assertTrue(differences + ".IsEmpty()",    !differences.IsEmpty());
	}		
}
