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
					Logger.getLogger(getClass()).debug(Name() + " declaration has been modified.");
				} else {
					Logger.getLogger(getClass()).debug(Name() + " declaration has not been modified.");
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

				Logger.getLogger(getClass()).debug(Name() + " has " + ClassDifferences().size() + " class(es) that changed.");
			}
		} else if (new_package != null) {
			NewDeclaration(new_package.Name());
		}

		Logger.getLogger(getClass()).debug(Name() + " " + !IsEmpty());

		return !IsEmpty();
	}

	public void Accept(Visitor visitor) {
		visitor.VisitPackageDifferences(this);
	}
}
