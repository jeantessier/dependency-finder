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

import java.util.*;

import org.apache.log4j.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

public class DifferencesFactory {
	private Validator old_validator;
	private Validator new_validator;

	private ClassfileLoader old_jar;
	private ClassfileLoader new_jar;
	private Classfile old_class;
	private Classfile new_class;

	public DifferencesFactory(Validator old_validator, Validator new_validator) {
		this.old_validator = old_validator;
		this.new_validator = new_validator;
	}

	public Differences CreateJarDifferences(String name, String old_version, ClassfileLoader old_jar, String new_version, ClassfileLoader new_jar) {
		Logger.getLogger(getClass()).debug("Begin " + name + " (" + old_version + " -> " + new_version + ")");

		JarDifferences jar_differences = new JarDifferences(name, old_version, new_version);
		Differences    result = jar_differences;
		
		this.old_jar = old_jar;
		this.new_jar = new_jar;
		
		Logger.getLogger(getClass()).debug("      Collecting packages ...");
		
		Iterator   i;

		NodeFactory old_factory = new NodeFactory();
		i = old_jar.Classfiles().iterator();
		while (i.hasNext()) {
			old_factory.CreateClass(i.next().toString());
		}

		NodeFactory new_factory = new NodeFactory();
		i = new_jar.Classfiles().iterator();
		while (i.hasNext()) {
			new_factory.CreateClass(i.next().toString());
		}
		
		Collection package_level = new TreeSet();
		package_level.addAll(old_factory.Packages().keySet());
		package_level.addAll(new_factory.Packages().keySet());
		
		Logger.getLogger(getClass()).debug("      Diff'ing packages ...");
		
		i = package_level.iterator();
		while (i.hasNext()) {
			String package_name = (String) i.next();
			
			PackageNode old_package = (PackageNode) old_factory.Packages().get(package_name);
			PackageNode new_package = (PackageNode) new_factory.Packages().get(package_name);
			
			Differences differences = CreatePackageDifferences(package_name, old_package, new_package);
			if (!differences.IsEmpty()) {
				jar_differences.PackageDifferences().add(differences);
			}
		}
		
		Logger.getLogger(getClass()).debug("End   " + name + " (" + old_version + " -> " + new_version + "): " + (result.IsEmpty() ? "empty" : "not empty"));
		
		return result;
	}
	
	public Differences CreatePackageDifferences(String name, PackageNode old_package, PackageNode new_package) {
		Logger.getLogger(getClass()).debug("Begin " + name);
		
		PackageDifferences package_differences = new PackageDifferences(name, old_package, new_package);
		Differences        result = package_differences;
		
		if (old_package != null && new_package != null) {
			
			Collection class_level = new TreeSet();
			Iterator   i;
			
			i = old_package.Classes().iterator();
			while (i.hasNext()) {
				class_level.add(i.next().toString());
			}
			
			i = new_package.Classes().iterator();
			while (i.hasNext()) {
				class_level.add(i.next().toString());
			}
			
			Logger.getLogger(getClass()).debug("      Diff'ing classes ...");
			
			i = class_level.iterator();
			while (i.hasNext()) {
				String class_name = (String) i.next();
				
				Classfile old_class = old_jar.Classfile(class_name);
				Classfile new_class = new_jar.Classfile(class_name);
				
				Differences differences = CreateClassDifferences(class_name, old_class, new_class);
				if (!differences.IsEmpty()) {
					package_differences.ClassDifferences().add(differences);
				}
			}
			
			Logger.getLogger(getClass()).debug("      " + name + " has " + package_differences.ClassDifferences().size() + " class(es) that changed.");
			
			if (old_validator.IsAllowed(name) != new_validator.IsAllowed(name)) {
				result = new DocumentableDifferences(result, old_validator, new_validator);
			}
		}
		
		Logger.getLogger(getClass()).debug("End   " + name + ": " + (result.IsEmpty() ? "empty" : "not empty"));
		
		return result;
	}
	
	public Differences CreateClassDifferences(String name, Classfile old_class, Classfile new_class) {
		Logger.getLogger(getClass()).debug("Begin " + name);

		ClassDifferences class_differences;
		if (((old_class != null) && old_class.IsInterface()) || ((new_class != null) && new_class.IsInterface())) {
			class_differences = new InterfaceDifferences(name, old_class, new_class);
		} else {
			class_differences = new ClassDifferences(name, old_class, new_class);
		}
		Differences result = class_differences;

		this.old_class = old_class;
		this.new_class = new_class;
		
		if (old_class != null && new_class != null) {
			Logger.getLogger(getClass()).debug("      Collecting fields ...");
			
			Map field_level = new TreeMap();
			Iterator i;
			
			i = old_class.Fields().iterator();
			while (i.hasNext()) {
				Field_info field = (Field_info) i.next();
				field_level.put(field.Name(), field.FullSignature());
			}
			
			i = new_class.Fields().iterator();
			while (i.hasNext()) {
				Field_info field = (Field_info) i.next();
				field_level.put(field.Name(), field.FullSignature());
			}
			
			Logger.getLogger(getClass()).debug("      Diff'ing fields ...");
			
			i = field_level.keySet().iterator();
			while (i.hasNext()) {
				String field_name     = (String) i.next();
				String field_fullname = (String) field_level.get(field_name);
				
				Field_info old_field = old_class.Field(field_name);
				Field_info new_field = new_class.Field(field_name);
				
				Differences differences = CreateFeatureDifferences(field_fullname, old_field, new_field);
				if (!differences.IsEmpty()) {
					class_differences.FeatureDifferences().add(differences);
				}
			}
			
			Logger.getLogger(getClass()).debug("      Collecting methods ...");
			
			Map method_level = new TreeMap();
			
			i = old_class.Methods().iterator();
			while (i.hasNext()) {
				Method_info method = (Method_info) i.next();
				method_level.put(method.Signature(), method.FullSignature());
			}
			
			i = new_class.Methods().iterator();
			while (i.hasNext()) {
				Method_info method = (Method_info) i.next();
				method_level.put(method.Signature(), method.FullSignature());
			}
			
			Logger.getLogger(getClass()).debug("      Diff'ing methods ...");
			
			i = method_level.keySet().iterator();
			while (i.hasNext()) {
				String method_name     = (String) i.next();
				String method_fullname = (String) method_level.get(method_name);
				
				Method_info old_method = old_class.Method(method_name);
				Method_info new_method = new_class.Method(method_name);
				
				Differences differences = CreateFeatureDifferences(method_fullname, old_method, new_method);
				if (!differences.IsEmpty()) {
					class_differences.FeatureDifferences().add(differences);
				}
			}
			
			Logger.getLogger(getClass()).debug(name + " has " + class_differences.FeatureDifferences().size() + " feature(s) that changed.");

			if (old_class.IsDeprecated() != new_class.IsDeprecated()) {
				result = new DeprecatableDifferences(result, old_class, new_class);
			}
			
			if (old_validator.IsAllowed(name) != new_validator.IsAllowed(name)) {
				result = new DocumentableDifferences(result, old_validator, new_validator);
			}
		}

		Logger.getLogger(getClass()).debug("End   " + name + ": " + (result.IsEmpty() ? "empty" : "not empty"));

		return result;
	}

	public Differences CreateFeatureDifferences(String name, Feature_info old_feature, Feature_info new_feature) {
		Logger.getLogger(getClass()).debug("Begin " + name);

		FeatureDifferences feature_differences;
		if (old_feature instanceof Field_info || new_feature instanceof Field_info) {
			feature_differences = new FieldDifferences(name, old_feature, new_feature);

			if (feature_differences.IsRemoved() && new_class.LocateField(name) != null) {
				feature_differences.Inherited(true);
			}
		} else {
			if (((old_feature instanceof Method_info) && ((Method_info) old_feature).IsConstructor()) || ((new_feature instanceof Method_info) && ((Method_info) new_feature).IsConstructor())) {
				feature_differences = new ConstructorDifferences(name, old_feature, new_feature);
			} else {
				feature_differences = new MethodDifferences(name, old_feature, new_feature);
			}

			if (feature_differences.IsRemoved()) {
				Method_info attempt = new_class.LocateMethod(name);
				if ((attempt != null) && (old_feature.Classfile().IsInterface() == attempt.Classfile().IsInterface())) {
					feature_differences.Inherited(true);
				}
			}
		}
		Differences result = feature_differences;
		
		if (old_feature != null && new_feature != null) {
			if (old_feature.IsDeprecated() != new_feature.IsDeprecated()) {
				result = new DeprecatableDifferences(result, old_feature, new_feature);
			}

			if (old_validator.IsAllowed(name) != new_validator.IsAllowed(name)) {
				result = new DocumentableDifferences(result, old_validator, new_validator);
			}
		}

		Logger.getLogger(getClass()).debug("End   " + name + ": " + (result.IsEmpty() ? "empty" : "not empty"));

		return result;
	}
}
