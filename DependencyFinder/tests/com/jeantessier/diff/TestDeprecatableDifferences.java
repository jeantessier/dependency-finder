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

public class TestDeprecatableDifferences extends TestCase {
	private DifferencesFactory factory;
	private ClassfileLoader    old_loader;
	private ClassfileLoader    new_loader;

	protected void setUp() throws Exception {
		Validator validator = new ListBasedValidator(new BufferedReader(new StringReader("")));
		factory = new DifferencesFactory(validator, validator);
		
		old_loader = new AggregatingClassfileLoader();
		old_loader.Load(Collections.singleton("tests\\JarJarDiff\\old"));

		new_loader = new AggregatingClassfileLoader();
		new_loader.Load(Collections.singleton("tests\\JarJarDiff\\new"));
	}

	public void testNotDeprecatedNotDeprecatedDifferent() {
		String name = "ModifiedPackage.ModifiedClass";
		Classfile old_classfile = old_loader.Classfile(name);
		assertNotNull(old_classfile);
		Classfile new_classfile = new_loader.Classfile(name);
		assertNotNull(new_classfile);
		Differences component_differences = factory.CreateClassDifferences(name, old_classfile, new_classfile);
		assertTrue("component IsEmpty()", !component_differences.IsEmpty());

		DeprecatableDifferences deprecated_differences = new DeprecatableDifferences(component_differences, old_classfile, new_classfile);
		
		assertTrue("deprecated NewDeprecation()",     !deprecated_differences.NewDeprecation());
		assertTrue("deprecated RemovedDeprecation()", !deprecated_differences.RemovedDeprecation());
		assertTrue("deprecated IsEmpty()",            !deprecated_differences.IsEmpty());
	}

	public void testNotDeprecatedNotDeprecatedSame() {
		String name = "ModifiedPackage.ModifiedClass";
		Classfile old_classfile = new_loader.Classfile(name);
		assertNotNull(old_classfile);
		Classfile new_classfile = new_loader.Classfile(name);
		assertNotNull(new_classfile);
		Differences component_differences = new ClassDifferences(name, old_classfile, new_classfile);
		assertTrue("component IsEmpty()", component_differences.IsEmpty());

		DeprecatableDifferences deprecated_differences = new DeprecatableDifferences(component_differences, old_classfile, new_classfile);
		
		assertTrue("deprecated NewDeprecation()",     !deprecated_differences.NewDeprecation());
		assertTrue("deprecated RemovedDeprecation()", !deprecated_differences.RemovedDeprecation());
		assertTrue("deprecated IsEmpty()",             deprecated_differences.IsEmpty());
	}

	public void testDeprecatedNotDeprecated() {
		String name = "ModifiedPackage.UndeprecatedClass";
		Classfile old_classfile = old_loader.Classfile(name);
		assertNotNull(old_classfile);
		Classfile new_classfile = new_loader.Classfile(name);
		assertNotNull(new_classfile);
		Differences component_differences = new ClassDifferences(name, old_classfile, new_classfile);
		assertTrue("component not empty", component_differences.IsEmpty());

		DeprecatableDifferences deprecated_differences = new DeprecatableDifferences(component_differences, old_classfile, new_classfile);
		
		assertTrue("deprecated NewDeprecation()",     !deprecated_differences.NewDeprecation());
		assertTrue("deprecated RemovedDeprecation()",  deprecated_differences.RemovedDeprecation());
		assertTrue("deprecated IsEmpty()",            !deprecated_differences.IsEmpty());
	}

	public void testNotDeprecatedDeprecated() {
		String name = "ModifiedPackage.DeprecatedClass";
		Classfile old_classfile = old_loader.Classfile(name);
		assertNotNull(old_classfile);
		Classfile new_classfile = new_loader.Classfile(name);
		assertNotNull(new_classfile);
		Differences component_differences = new ClassDifferences(name, old_classfile, new_classfile);
		assertTrue("component not empty", component_differences.IsEmpty());

		DeprecatableDifferences deprecated_differences = new DeprecatableDifferences(component_differences, old_classfile, new_classfile);
		
		assertTrue("deprecated NewDeprecation()",      deprecated_differences.NewDeprecation());
		assertTrue("deprecated RemovedDeprecation()", !deprecated_differences.RemovedDeprecation());
		assertTrue("deprecated IsEmpty()",            !deprecated_differences.IsEmpty());
	}

	public void testDeprecatedDeprecated() {
		String name = "ModifiedPackage.DeprecatedClass";
		Classfile old_classfile = new_loader.Classfile(name);
		assertNotNull(old_classfile);
		Classfile new_classfile = new_loader.Classfile(name);
		assertNotNull(new_classfile);
		Differences component_differences = new ClassDifferences(name, old_classfile, new_classfile);
		assertTrue("component not empty", component_differences.IsEmpty());

		DeprecatableDifferences deprecated_differences = new DeprecatableDifferences(component_differences, old_classfile, new_classfile);
		
		assertTrue("deprecated NewDeprecation()",     !deprecated_differences.NewDeprecation());
		assertTrue("deprecated RemovedDeprecation()", !deprecated_differences.RemovedDeprecation());
		assertTrue("deprecated IsEmpty()",             deprecated_differences.IsEmpty());
	}
}
