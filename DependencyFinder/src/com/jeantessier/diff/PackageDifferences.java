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
import com.jeantessier.dependency.*;

public class PackageDifferences extends RemovableDifferences {
	private Collection class_differences = new LinkedList();

	public PackageDifferences(String name) {
		super(name);
	}

	public Collection ClassDifferences() {
		return class_differences;
	}

	public boolean IsModified() {
		return super.IsModified() || (ClassDifferences().size() != 0);
	}

	public boolean Compare(ClassfileLoader old_jar, PackageNode old_package, ClassfileLoader new_jar, PackageNode new_package) {
		Collection class_level = new TreeSet();
		Iterator   i;
	
		if (old_package != null) {
			OldDeclaration(old_package.Name());

			i = old_package.Classes().iterator();
			while (i.hasNext()) {
				class_level.add(i.next().toString());
			}
	    
			if (new_package != null) {
				NewDeclaration(new_package.Name());

				if (IsModified()) {
					Category.getInstance(getClass().getName()).debug(Name() + " declaration has been modified.");
				} else {
					Category.getInstance(getClass().getName()).debug(Name() + " declaration has not been modified.");
				}

				i = new_package.Classes().iterator();
				while (i.hasNext()) {
					class_level.add(i.next().toString());
				}
		
				i = class_level.iterator();
				while (i.hasNext()) {
					String class_name = (String) i.next();
		    
					Classfile old_class = old_jar.Classfile(class_name);
					Classfile new_class = new_jar.Classfile(class_name);
		    
					ClassDifferences differences;
					if (((old_class != null) && old_class.IsInterface()) || ((new_class != null) && new_class.IsInterface())) {
						differences = new InterfaceDifferences(class_name);
					} else {
						differences = new ClassDifferences(class_name);
					}
					if (differences.Compare(old_class, new_class)) {
						ClassDifferences().add(differences);
					}
				}

				Category.getInstance(getClass().getName()).debug(Name() + " has " + ClassDifferences().size() + " class(es) that changed.");
			}
		} else if (new_package != null) {
			NewDeclaration(new_package.Name());
		}

		Category.getInstance(getClass().getName()).debug(Name() + " " + !IsEmpty());

		return !IsEmpty();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitPackageDifferences(this);
	}
}
