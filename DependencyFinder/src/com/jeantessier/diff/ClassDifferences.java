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
 *  	* Neither the name of the Jean Tessier nor the names of his contributors
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

public class ClassDifferences extends DeprecatableDifferences{
	private Classfile old_class = null;
	private Classfile new_class = null;

	private Collection feature_differences = new LinkedList();

	public ClassDifferences(String name) {
		super(name);
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

	public boolean Compare(Classfile old_class, Classfile new_class) {
		if (old_class != null) {
			OldClass(old_class);
			OldDeclaration(old_class.Declaration());

			if (new_class != null) {
				NewClass(new_class);
				NewDeclaration(new_class.Declaration());
		
				if (IsModified()) {
					Category.getInstance(getClass().getName()).debug(Name() + " declaration has been modified.");
				} else {
					Category.getInstance(getClass().getName()).debug(Name() + " declaration has not been modified.");
				}

				RemovedDeprecation(old_class.IsDeprecated() && !new_class.IsDeprecated());
				NewDeprecation(!old_class.IsDeprecated() && new_class.IsDeprecated());
		
				Collection field_level = new TreeSet();
				Iterator i;

				i = old_class.Fields().iterator();
				while (i.hasNext()) {
					field_level.add(((Field_info) i.next()).Name());
				}
		
				i = new_class.Fields().iterator();
				while (i.hasNext()) {
					field_level.add(((Field_info) i.next()).Name());
				}
		
				i = field_level.iterator();
				while (i.hasNext()) {
					String field_name = (String) i.next();

					Field_info new_field = new_class.Field(field_name);
					Field_info old_field = old_class.Field(field_name);

					FeatureDifferences differences = new FieldDifferences(field_name);
					if (differences.Compare(old_field, new_field)) {
						FeatureDifferences().add(differences);
						if (differences.IsRemoved() && new_class.LocateField(field_name) != null) {
							differences.Inherited(true);
						}
					}
				}

				Collection method_level = new TreeSet();
		
				i = old_class.Methods().iterator();
				while (i.hasNext()) {
					method_level.add(((Method_info) i.next()).Signature());
				}
		
				i = new_class.Methods().iterator();
				while (i.hasNext()) {
					method_level.add(((Method_info) i.next()).Signature());
				}
		
				i = method_level.iterator();
				while (i.hasNext()) {
					String method_name = (String) i.next();
		    
					Method_info new_method = new_class.Method(method_name);
					Method_info old_method = old_class.Method(method_name);
		    
					FeatureDifferences differences;
					if (((old_method != null) && old_method.IsConstructor()) || ((new_method != null) && new_method.IsConstructor())) {
						differences = new ConstructorDifferences(method_name);
					} else {
						differences = new MethodDifferences(method_name);
					}
					if (differences.Compare(old_method, new_method)) {
						FeatureDifferences().add(differences);
						if (differences.IsRemoved()) {
							Method_info attempt = new_class.LocateMethod(method_name);
							if ((attempt != null) && (old_method.Classfile().IsInterface() == attempt.Classfile().IsInterface())) {
								differences.Inherited(true);
							}
						}
					}
				}

				Category.getInstance(getClass().getName()).debug(Name() + " has " + FeatureDifferences().size() + " feature(s) that changed.");
			}
		} else if (new_class != null) {
			NewClass(new_class);
			NewDeclaration(new_class.Declaration());
		}

		Category.getInstance(getClass().getName()).debug(Name() + " " + !IsEmpty());

		return NewDeprecation() || RemovedDeprecation() || !IsEmpty();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitClassDifferences(this);
	}
}
