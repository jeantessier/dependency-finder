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

public class Report extends Printer {
	private String product;
	private String old_version;
	private String new_version;

	private Collection removed_packages = new TreeSet();
	private Collection removed_interfaces = new TreeSet();
	private Collection removed_classes = new TreeSet();
	private Collection deprecated_interfaces = new TreeSet();
	private Collection deprecated_classes = new TreeSet();

	private Collection modified_interfaces = new TreeSet();
	private Collection modified_classes = new TreeSet();

	private Collection undeprecated_interfaces = new TreeSet();
	private Collection undeprecated_classes = new TreeSet();
	private Collection new_packages = new TreeSet();
	private Collection new_interfaces = new TreeSet();
	private Collection new_classes = new TreeSet();

	public Report() {
		super();
	}

	public Report(String indent) {
		super(indent);
	}

	public void VisitJarDifferences(JarDifferences differences) {
		product     = differences.Product();
		old_version = differences.OldVersion();
		new_version = differences.NewVersion();

		Iterator i = differences.PackageDifferences().iterator();
		while (i.hasNext()) {
			((Differences) i.next()).Accept(this);
		}
	}

	public void VisitPackageDifferences(PackageDifferences differences) {
		if (differences.IsRemoved()) {
			removed_packages.add(differences);
		}
	
		Iterator i = differences.ClassDifferences().iterator();
		while (i.hasNext()) {
			((Differences) i.next()).Accept(this);
		}

		if (differences.IsNew()) {
			new_packages.add(differences);
		}
	}

	public void VisitClassDifferences(ClassDifferences differences) {
		if (differences.IsRemoved()) {
			removed_classes.add(differences);
		}
	
		if (differences.IsModified()) {
			Visitor visitor = new ClassReport();
			differences.Accept(visitor);
			modified_classes.add(visitor);
		}
	
		if (differences.IsNew()) {
			new_classes.add(differences);
		}
	}

	public void VisitInterfaceDifferences(InterfaceDifferences differences) {
		if (differences.IsRemoved()) {
			removed_interfaces.add(differences);
		}
	
		if (differences.IsModified()) {
			Visitor visitor = new ClassReport();
			differences.Accept(visitor);
			modified_interfaces.add(visitor);
		}
	
		if (differences.IsNew()) {
			new_interfaces.add(differences);
		}
	}
	
	public void VisitDeprecatableDifferences(DeprecatableDifferences differences) {
		Differences component = differences.Component();
		
		if (component instanceof InterfaceDifferences) {
			if (differences.NewDeprecation()) {
				deprecated_interfaces.add(component);
			}
			
			if (differences.RemovedDeprecation()) {
				undeprecated_interfaces.add(component);
			}
		} else if (component instanceof ClassDifferences) {
			if (differences.NewDeprecation()) {
				deprecated_classes.add(component);
			}
			
			if (differences.RemovedDeprecation()) {
				undeprecated_classes.add(component);
			}
		} else {
			Logger.getLogger(getClass()).error("Invalid deprecatable, class is " + component.getClass().getName());
		}

		component.Accept(this);
	}

	public String toString() {
		Indent().Append(Preamble());
		Indent().Append("\n");

		Indent().Append("<differences>\n");
		RaiseIndent();

		Indent().Append("<product>").Append(product).Append("</product>\n");
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
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.OldClass())).Append(">").Append(cd).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</removed-interfaces>\n");
		}

		if (removed_classes.size() !=0) {
			Indent().Append("<removed-classes>\n");
			RaiseIndent();

			Iterator i = removed_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.OldClass())).Append(">").Append(cd).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</removed-classes>\n");
		}

		if (deprecated_interfaces.size() !=0) {
			Indent().Append("<deprecated-interfaces>\n");
			RaiseIndent();

			Iterator i = deprecated_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-interfaces>\n");
		}

		if (deprecated_classes.size() !=0) {
			Indent().Append("<deprecated-classes>\n");
			RaiseIndent();

			Iterator i = deprecated_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>\n");
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

		if (undeprecated_interfaces.size() !=0) {
			Indent().Append("<undeprecated-interfaces>\n");
			RaiseIndent();

			Iterator i = undeprecated_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</undeprecated-interfaces>\n");
		}

		if (undeprecated_classes.size() !=0) {
			Indent().Append("<undeprecated-classes>\n");
			RaiseIndent();

			Iterator i = undeprecated_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</undeprecated-classes>\n");
		}

		if (new_packages.size() !=0) {
			Indent().Append("<new-packages>\n");
			RaiseIndent();

			Iterator i = new_packages.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</new-packages>\n");
		}

		if (new_interfaces.size() !=0) {
			Indent().Append("<new-interfaces>\n");
			RaiseIndent();

			Iterator i = new_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</new-interfaces>\n");
		}

		if (new_classes.size() !=0) {
			Indent().Append("<new-classes>\n");
			RaiseIndent();

			Iterator i = new_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>\n");
			}

			LowerIndent();
			Indent().Append("</new-classes>\n");
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
		result.append("<!ELEMENT differences (product,old,new,removed-packages?,removed-interfaces?,removed-classes?,deprecated-interfaces?,deprecated-classes?,modified-interfaces?,modified-classes?,undeprecated-interfaces?,undeprecated-classes?,new-packages?,new-interfaces?,new-classes?) >\n");
		result.append("\n");
		result.append("<!ELEMENT product (#PCDATA)* >\n");
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
		result.append("<!ELEMENT class (name,modified-declaration?,removed-fields?,removed-constructors?,removed-methods?,deprecated-fields?,deprecated-constructors?,deprecated-methods?,modified-fields?,modified-constructors?,modified-methods?,undeprecated-fields?,undeprecated-constructors?,undeprecated-methods?,new-fields?,new-constructors?,new-methods?) >\n");
		result.append("\n");
		result.append("<!ELEMENT name (#PCDATA)* >\n");
		result.append("<!ATTLIST name\n");
		result.append("          visibility (public|protected|package|private) #IMPLIED\n");
		result.append("          static       CDATA #IMPLIED\n");
		result.append("          final        CDATA #IMPLIED\n");
		result.append("          super        CDATA #IMPLIED\n");
		result.append("          synchronized CDATA #IMPLIED\n");
		result.append("          volatile     CDATA #IMPLIED\n");
		result.append("          transient    CDATA #IMPLIED\n");
		result.append("          native       CDATA #IMPLIED\n");
		result.append("          interface    CDATA #IMPLIED\n");
		result.append("          abstract     CDATA #IMPLIED\n");
		result.append("          strict       CDATA #IMPLIED\n");
		result.append("          deprecated   CDATA #IMPLIED\n");
		result.append("          synthetic    CDATA #IMPLIED\n");
		result.append(">\n");
		result.append("\n");
		result.append("<!ELEMENT modified-declaration (old-declaration,new-declaration) >\n");
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
		result.append("<!ELEMENT old-declaration (#PCDATA)* >\n");
		result.append("<!ATTLIST old-declaration\n");
		result.append("          visibility (public|protected|package|private) #IMPLIED\n");
		result.append("          static       CDATA #IMPLIED\n");
		result.append("          final        CDATA #IMPLIED\n");
		result.append("          super        CDATA #IMPLIED\n");
		result.append("          synchronized CDATA #IMPLIED\n");
		result.append("          volatile     CDATA #IMPLIED\n");
		result.append("          transient    CDATA #IMPLIED\n");
		result.append("          native       CDATA #IMPLIED\n");
		result.append("          interface    CDATA #IMPLIED\n");
		result.append("          abstract     CDATA #IMPLIED\n");
		result.append("          strict       CDATA #IMPLIED\n");
		result.append("          deprecated   CDATA #IMPLIED\n");
		result.append("          synthetic    CDATA #IMPLIED\n");
		result.append(">\n");
		result.append("\n");
		result.append("<!ELEMENT new-declaration (#PCDATA)* >\n");
		result.append("<!ATTLIST new-declaration\n");
		result.append("          visibility (public|protected|package|private) #IMPLIED\n");
		result.append("          static       CDATA #IMPLIED\n");
		result.append("          final        CDATA #IMPLIED\n");
		result.append("          super        CDATA #IMPLIED\n");
		result.append("          synchronized CDATA #IMPLIED\n");
		result.append("          volatile     CDATA #IMPLIED\n");
		result.append("          transient    CDATA #IMPLIED\n");
		result.append("          native       CDATA #IMPLIED\n");
		result.append("          interface    CDATA #IMPLIED\n");
		result.append("          abstract     CDATA #IMPLIED\n");
		result.append("          strict       CDATA #IMPLIED\n");
		result.append("          deprecated   CDATA #IMPLIED\n");
		result.append("          synthetic    CDATA #IMPLIED\n");
		result.append(">\n");
		result.append("\n");
		result.append("<!ELEMENT feature (name,modified-declaration?) >\n");
		result.append("\n");
		result.append("<!ELEMENT declaration (#PCDATA)* >\n");
		result.append("<!ATTLIST declaration\n");
		result.append("          visibility (public|protected|package|private) #IMPLIED\n");
		result.append("          static       CDATA #IMPLIED\n");
		result.append("          final        CDATA #IMPLIED\n");
		result.append("          super        CDATA #IMPLIED\n");
		result.append("          synchronized CDATA #IMPLIED\n");
		result.append("          volatile     CDATA #IMPLIED\n");
		result.append("          transient    CDATA #IMPLIED\n");
		result.append("          native       CDATA #IMPLIED\n");
		result.append("          interface    CDATA #IMPLIED\n");
		result.append("          abstract     CDATA #IMPLIED\n");
		result.append("          strict       CDATA #IMPLIED\n");
		result.append("          deprecated   CDATA #IMPLIED\n");
		result.append("          synthetic    CDATA #IMPLIED\n");
		result.append("          inherited    CDATA #IMPLIED\n");
		result.append(">\n");
		result.append("\n");
		result.append("]>\n");
        
		return result.toString();
	}

	private static final String DeclarationBreakdown(Classfile element) {
		StringBuffer result = new StringBuffer();

		if (element != null) {
			if (element.IsPublic())     result.append(" visibility=\"public\"");
			if (element.IsPackage())    result.append(" visibility=\"package\"");
			if (element.IsFinal())      result.append(" final=\"yes\"");
			if (element.IsSuper())      result.append(" super=\"yes\"");
			if (element.IsSynthetic())  result.append(" synthetic=\"yes\"");
			if (element.IsDeprecated()) result.append(" deprecated=\"yes\"");

			result.append(" name=\"").append(element.Class()).append("\"");

			if (element.IsInterface()) {
				result.append(" interface=\"yes\"");
		
				result.append(" extends=\"");
				Iterator i = element.Interfaces().iterator();
				while (i.hasNext()) {
					result.append(i.next());
					if (i.hasNext()) {
						result.append(", ");
					}
				}
				result.append("\"");
			} else {
				if (element.IsAbstract()) result.append(" abstract=\"yes\"");
		
				result.append(" extends=\"").append(element.Superclass()).append("\"");
		
				result.append(" implements=\"");
				Iterator i = element.Interfaces().iterator();
				while (i.hasNext()) {
					result.append(i.next());
					if (i.hasNext()) {
						result.append(", ");
					}
				}
				result.append("\"");
			}
		}

		return result.toString();
	}
}
