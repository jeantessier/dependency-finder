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

public class ExternalReport extends Printer {
	private String old_version;
	private String new_version;

	private Collection removed_packages = new TreeSet();
	private Collection removed_interfaces = new TreeSet();
	private Collection removed_classes = new TreeSet();
	private Collection deprecated_interfaces = new TreeSet();
	private Collection deprecated_classes = new TreeSet();

	private Collection modified_interfaces = new TreeSet();
	private Collection modified_classes = new TreeSet();

	public ExternalReport() {
		super();
	}

	public ExternalReport(String indent) {
		super(indent);
	}

	public void VisitJarDifferences(JarDifferences differences) {
		old_version = differences.OldVersion();
		new_version = differences.NewVersion();

		Iterator i = differences.PackageDifferences().iterator();
		while (i.hasNext()) {
			((PackageDifferences) i.next()).Accept(this);
		}
	}

	public void VisitPackageDifferences(PackageDifferences differences) {
		if (differences.IsRemoved()) {
			removed_packages.add(differences);
		}
	
		Iterator i = differences.ClassDifferences().iterator();
		while (i.hasNext()) {
			((ClassDifferences) i.next()).Accept(this);
		}
	}

	public void VisitClassDifferences(ClassDifferences differences) {
		if (differences.IsRemoved() && differences.OldClass().IsPublic()) {
			removed_classes.add(differences);
		}
	
		if (differences.NewDeprecation() && differences.NewClass().IsPublic()) {
			deprecated_classes.add(differences);
		}
	
		if (differences.IsModified() && differences.OldClass().IsPublic()) {
			Visitor visitor = new ExternalClassReport();
			differences.Accept(visitor);
			modified_classes.add(visitor);
		}
	}

	public void VisitInterfaceDifferences(InterfaceDifferences differences) {
		if (differences.IsRemoved() && differences.OldClass().IsPublic()) {
			removed_interfaces.add(differences);
		}
	
		if (differences.NewDeprecation() && differences.NewClass().IsPublic()) {
			deprecated_interfaces.add(differences);
		}
	
		if (differences.IsModified() && differences.OldClass().IsPublic()) {
			Visitor visitor = new ExternalClassReport();
			differences.Accept(visitor);
			modified_interfaces.add(visitor);
		}
	}

	public String toString() {
		Indent().Append(Preamble());
		Indent().Append("\n");

		Indent().Append("<differences>\n");
		RaiseIndent();

		Indent().Append("<old>").Append(old_version).Append("</old>\n");
		Indent().Append("<new>").Append(new_version).Append("</new>\n");
	
		if (removed_packages.size() !=0) {
			Indent().Append("<removed-packages>\n");
			RaiseIndent();

			Iterator i = removed_packages.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</removed-packages>\n");
		}

		if (removed_interfaces.size() !=0) {
			Indent().Append("<removed-interfaces>\n");
			RaiseIndent();

			Iterator i = removed_interfaces.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</removed-interfaces>\n");
		}

		if (removed_classes.size() !=0) {
			Indent().Append("<removed-classes>\n");
			RaiseIndent();

			Iterator i = removed_classes.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</removed-classes>\n");
		}

		if (deprecated_interfaces.size() !=0) {
			Indent().Append("<deprecated-interfaces>\n");
			RaiseIndent();

			Iterator i = deprecated_interfaces.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-interfaces>\n");
		}

		if (deprecated_classes.size() !=0) {
			Indent().Append("<deprecated-classes>\n");
			RaiseIndent();

			Iterator i = deprecated_classes.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-classes>\n");
		}

		if (modified_interfaces.size() !=0) {
			Indent().Append("<modified-interfaces>\n");
			RaiseIndent();

			Iterator i = modified_interfaces.iterator();
			while (i.hasNext()) {
				Append(i.next());
			}

			LowerIndent();
			Indent().Append("</modified-interfaces>\n");
		}

		if (modified_classes.size() !=0) {
			Indent().Append("<modified-classes>\n");
			RaiseIndent();

			Iterator i = modified_classes.iterator();
			while (i.hasNext()) {
				Append(i.next());
			}

			LowerIndent();
			Indent().Append("</modified-classes>\n");
		}

		LowerIndent();
		Indent().Append("</differences>\n");

		return super.toString();
	}

	private String Preamble() {
		StringBuffer result = new StringBuffer();
        
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		result.append("\n");
		result.append("<!DOCTYPE differences [\n");
		result.append("\n");
		result.append("<!ELEMENT differences (old,new,removed-packages?,removed-interfaces?,removed-classes?,deprecated-interfaces?,deprecated-classes?,modified-interfaces?,modified-classes?,undeprecated-interfaces?,undeprecated-classes?,new-packages?,new-interfaces?,new-classes?) >\n");
		result.append("\n");
		result.append("<!ELEMENT old (#PCDATA)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new (#PCDATA)* >\n");
		result.append("\n");
		result.append("<!ELEMENT removed-packages (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT removed-interfaces (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT removed-classes (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT deprecated-interfaces (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT deprecated-classes (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT modified-interfaces (class)* >\n");
		result.append("\n");
		result.append("<!ELEMENT modified-classes (class)* >\n");
		result.append("\n");
		result.append("<!ELEMENT undeprecated-interfaces (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT undeprecated-classes (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new-packages (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new-interfaces (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new-classes (name)* >\n");
		result.append("\n");
		result.append("<!ELEMENT class (name,(old-declaration,new-declaration)?,removed-fields?,removed-constructors?,removed-methods?,deprecated-fields?,deprecated-constructors?,deprecated-methods?,modified-fields?,modified-interfaces?,modified-methods?,undeprecated-fields?,undeprecated-constructors?,undeprecated-methods?,new-fields?,new-interfaces?,new-methods?) >\n");
		result.append("\n");
		result.append("<!ELEMENT name (#PCDATA)* >\n");
		result.append("\n");
		result.append("<!ELEMENT old-declaration (#PCDATA)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new-declaration (#PCDATA)* >\n");
		result.append("\n");
		result.append("<!ELEMENT removed-fields (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT removed-constructors (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT removed-methods (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT deprecated-fields (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT deprecated-constructors (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT deprecated-methods (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT modified-fields (feature)* >\n");
		result.append("\n");
		result.append("<!ELEMENT modified-constructors (feature)* >\n");
		result.append("\n");
		result.append("<!ELEMENT modified-methods (feature)* >\n");
		result.append("\n");
		result.append("<!ELEMENT undeprecated-fields (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT undeprecated-constructors (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT undeprecated-methods (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new-fields (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new-constructors (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT new-methods (declaration)* >\n");
		result.append("\n");
		result.append("<!ELEMENT feature (name, (old-declaration,new-declaration)?) >\n");
		result.append("\n");
		result.append("<!ELEMENT declaration (#PCDATA)* >\n");
		result.append("\n");
		result.append("]>\n");
        
		return result.toString();
	}
}
