/*
 *  Dependency Finder - Comparing API differences between JAR files
 *  Copyright (C) 2001  Jean Tessier
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
