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
