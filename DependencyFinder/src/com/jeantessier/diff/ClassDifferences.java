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

/**
 *  Documents the difference, if any, for a given object
 *  type (class or interface).  Its subclasses only
 *  differ in which Visitor callback they invoke.
 *
 *  @see Visitor
 */
public class ClassDifferences extends RemovableDifferences {
	private Classfile old_class;
	private Classfile new_class;

	private Collection feature_differences = new LinkedList();

	public ClassDifferences(String name, Validator old_validator, Classfile old_class, Validator new_validator, Classfile new_class) {
		super(name);

		Logger.getLogger(getClass()).debug("Begin " + Name());

		OldClass(old_class);
		NewClass(new_class);
		
		if (old_class != null) {
			OldDeclaration(old_class.Declaration());

			if (new_class != null) {
				NewDeclaration(new_class.Declaration());
		
				if (IsModified()) {
					Logger.getLogger(getClass()).debug(Name() + " declaration has been modified.");
				} else {
					Logger.getLogger(getClass()).debug(Name() + " declaration has not been modified.");
				}

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

					FeatureDifferences feature_differences = new FieldDifferences(field_fullname, old_field, new_field);
					Differences differences = new DeprecatableDifferences(feature_differences, old_field, new_field);
					differences = new DocumentableDifferences(differences, old_validator, new_validator);
					if (!differences.IsEmpty()) {
						FeatureDifferences().add(differences);
						if (feature_differences.IsRemoved() && new_class.LocateField(field_name) != null) {
							feature_differences.Inherited(true);
						}
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
		    
					FeatureDifferences feature_differences;
					if (((old_method != null) && old_method.IsConstructor()) || ((new_method != null) && new_method.IsConstructor())) {
						feature_differences = new ConstructorDifferences(method_fullname, old_method, new_method);
					} else {
						feature_differences = new MethodDifferences(method_fullname, old_method, new_method);
					}
					Differences differences = new DeprecatableDifferences(feature_differences, old_method, new_method);
					differences = new DocumentableDifferences(differences, old_validator, new_validator);
					if (!differences.IsEmpty()) {
						FeatureDifferences().add(differences);
						if (feature_differences.IsRemoved()) {
							Method_info attempt = new_class.LocateMethod(method_name);
							if ((attempt != null) && (old_method.Classfile().IsInterface() == attempt.Classfile().IsInterface())) {
								feature_differences.Inherited(true);
							}
						}
					}
				}

				Logger.getLogger(getClass()).debug(Name() + " has " + FeatureDifferences().size() + " feature(s) that changed.");
			}
		} else if (new_class != null) {
			NewDeclaration(new_class.Declaration());
		}

		Logger.getLogger(getClass()).debug("End   " + Name() + ": " + (IsEmpty() ? "empty" : "not empty"));
	}

	public Classfile OldClass() {
		return old_class;
	}

	protected void OldClass(Classfile old_class) {
		this.old_class = old_class;
	}

	public Classfile NewClass() {
		return new_class;
	}

	protected void NewClass(Classfile new_class) {
		this.new_class = new_class;
	}

	public Collection FeatureDifferences() {
		return feature_differences;
	}

	public boolean IsModified() {
		return super.IsModified() || (FeatureDifferences().size() != 0);
	}

	public void Accept(Visitor visitor) {
		visitor.VisitClassDifferences(this);
	}
}
