/*
 *  Copyright (c) 2001-2004, Jean Tessier
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
	public static final String DEFAULT_ENCODING   = "utf-8";
	public static final String DEFAULT_DTD_PREFIX = "http://depfind.sourceforge.net/dtd";

	private String name;
	private String old_version;
	private String new_version;

	private Collection removed_packages        = new TreeSet();
	private Collection undocumented_packages   = new TreeSet();

	private Collection removed_interfaces      = new TreeSet();
	private Collection removed_classes         = new TreeSet();

	private Collection deprecated_interfaces   = new TreeSet();
	private Collection deprecated_classes      = new TreeSet();
	
	private Collection undocumented_interfaces = new TreeSet();
	private Collection undocumented_classes    = new TreeSet();

	private Collection modified_interfaces     = new TreeSet();
	private Collection modified_classes        = new TreeSet();

	private Collection documented_interfaces   = new TreeSet();
	private Collection documented_classes      = new TreeSet();
	
	private Collection undeprecated_interfaces = new TreeSet();
	private Collection undeprecated_classes    = new TreeSet();
	
	private Collection new_packages            = new TreeSet();
	private Collection documented_packages     = new TreeSet();

	private Collection new_interfaces          = new TreeSet();
	private Collection new_classes             = new TreeSet();

	public Report() {
		this(DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
	}
	
	public Report(String encoding, String dtd_header) {
		AppendHeader(encoding, dtd_header);
	}

	private void AppendHeader(String encoding, String dtd_header) {
		Append("<?xml version=\"1.0\" encoding=\"").Append(encoding).Append("\" ?>").EOL();
		EOL();
		Append("<!DOCTYPE differences SYSTEM \"").Append(dtd_header).Append("/differences.dtd\">").EOL();
		EOL();
	}

	public void VisitJarDifferences(JarDifferences differences) {
		name        = differences.Name();
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

		if (Documented()) {
			documented_packages.add(differences);
		}

		if (Undocumented()) {
			undocumented_packages.add(differences);
		}
	}

	public void VisitClassDifferences(ClassDifferences differences) {
		if (differences.IsRemoved()) {
			removed_classes.add(differences);
		}
	
		if (differences.IsModified()) {
			ClassReport visitor = new ClassReport();
			visitor.IndentText(IndentText());
			differences.Accept(visitor);
			modified_classes.add(visitor);
		}
	
		if (differences.IsNew()) {
			new_classes.add(differences);
		}

		if (Deprecated()) {
			deprecated_classes.add(differences);
		}

		if (Undeprecated()) {
			undeprecated_classes.add(differences);
		}

		if (Documented()) {
			documented_classes.add(differences);
		}

		if (Undocumented()) {
			undocumented_classes.add(differences);
		}
	}

	public void VisitInterfaceDifferences(InterfaceDifferences differences) {
		if (differences.IsRemoved()) {
			removed_interfaces.add(differences);
		}
	
		if (differences.IsModified()) {
			ClassReport visitor = new ClassReport();
			visitor.IndentText(IndentText());
			differences.Accept(visitor);
			modified_interfaces.add(visitor);
		}
	
		if (differences.IsNew()) {
			new_interfaces.add(differences);
		}

		if (Deprecated()) {
			deprecated_interfaces.add(differences);
		}

		if (Undeprecated()) {
			undeprecated_interfaces.add(differences);
		}

		if (Documented()) {
			documented_interfaces.add(differences);
		}

		if (Undocumented()) {
			undocumented_interfaces.add(differences);
		}
	}

	public String toString() {
		Indent().Append("<differences>").EOL();
		RaiseIndent();

		Indent().Append("<name>").Append(name).Append("</name>").EOL();
		Indent().Append("<old>").Append(old_version).Append("</old>").EOL();
		Indent().Append("<new>").Append(new_version).Append("</new>").EOL();
	
		if (removed_packages.size() !=0) {
			Indent().Append("<removed-packages>").EOL();
			RaiseIndent();

			Iterator i = removed_packages.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</removed-packages>").EOL();
		}
	
		if (undocumented_packages.size() !=0) {
			Indent().Append("<undocumented-packages>").EOL();
			RaiseIndent();

			Iterator i = undocumented_packages.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</undocumented-packages>").EOL();
		}

		if (removed_interfaces.size() !=0) {
			Indent().Append("<removed-interfaces>").EOL();
			RaiseIndent();

			Iterator i = removed_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.OldClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</removed-interfaces>").EOL();
		}

		if (removed_classes.size() !=0) {
			Indent().Append("<removed-classes>").EOL();
			RaiseIndent();

			Iterator i = removed_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.OldClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</removed-classes>").EOL();
		}

		if (deprecated_interfaces.size() !=0) {
			Indent().Append("<deprecated-interfaces>").EOL();
			RaiseIndent();

			Iterator i = deprecated_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</deprecated-interfaces>").EOL();
		}

		if (deprecated_classes.size() !=0) {
			Indent().Append("<deprecated-classes>").EOL();
			RaiseIndent();

			Iterator i = deprecated_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</deprecated-classes>").EOL();
		}

		if (undocumented_interfaces.size() !=0) {
			Indent().Append("<undocumented-interfaces>").EOL();
			RaiseIndent();

			Iterator i = undocumented_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</undocumented-interfaces>").EOL();
		}

		if (undocumented_classes.size() !=0) {
			Indent().Append("<undocumented-classes>").EOL();
			RaiseIndent();

			Iterator i = undocumented_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</undocumented-classes>").EOL();
		}

		if (modified_interfaces.size() !=0) {
			Indent().Append("<modified-interfaces>").EOL();
			RaiseIndent();

			Iterator i = modified_interfaces.iterator();
			while (i.hasNext()) {
				Append(i.next());
			}

			LowerIndent();
			Indent().Append("</modified-interfaces>").EOL();
		}

		if (modified_classes.size() !=0) {
			Indent().Append("<modified-classes>").EOL();
			RaiseIndent();

			Iterator i = modified_classes.iterator();
			while (i.hasNext()) {
				Append(i.next());
			}

			LowerIndent();
			Indent().Append("</modified-classes>").EOL();
		}

		if (documented_interfaces.size() !=0) {
			Indent().Append("<documented-interfaces>").EOL();
			RaiseIndent();

			Iterator i = documented_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</documented-interfaces>").EOL();
		}

		if (documented_classes.size() !=0) {
			Indent().Append("<documented-classes>").EOL();
			RaiseIndent();

			Iterator i = documented_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</documented-classes>").EOL();
		}

		if (undeprecated_interfaces.size() !=0) {
			Indent().Append("<undeprecated-interfaces>").EOL();
			RaiseIndent();

			Iterator i = undeprecated_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</undeprecated-interfaces>").EOL();
		}

		if (undeprecated_classes.size() !=0) {
			Indent().Append("<undeprecated-classes>").EOL();
			RaiseIndent();

			Iterator i = undeprecated_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</undeprecated-classes>").EOL();
		}

		if (new_packages.size() !=0) {
			Indent().Append("<new-packages>").EOL();
			RaiseIndent();

			Iterator i = new_packages.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</new-packages>").EOL();
		}
	
		if (documented_packages.size() !=0) {
			Indent().Append("<documented-packages>").EOL();
			RaiseIndent();

			Iterator i = documented_packages.iterator();
			while (i.hasNext()) {
				Indent().Append("<name>").Append(i.next()).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</documented-packages>").EOL();
		}

		if (new_interfaces.size() !=0) {
			Indent().Append("<new-interfaces>").EOL();
			RaiseIndent();

			Iterator i = new_interfaces.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</new-interfaces>").EOL();
		}

		if (new_classes.size() !=0) {
			Indent().Append("<new-classes>").EOL();
			RaiseIndent();

			Iterator i = new_classes.iterator();
			while (i.hasNext()) {
				ClassDifferences cd = (ClassDifferences) i.next();
				Indent().Append("<name").Append(DeclarationBreakdown(cd.NewClass())).Append(">").Append(cd).Append("</name>").EOL();
			}

			LowerIndent();
			Indent().Append("</new-classes>").EOL();
		}

		LowerIndent();
		Indent().Append("</differences>").EOL();

		return super.toString();
	}

	private static final String DeclarationBreakdown(Classfile element) {
		StringBuffer result = new StringBuffer();

		if (element != null) {
			if (element.isPublic())     result.append(" visibility=\"public\"");
			if (element.isPackage())    result.append(" visibility=\"package\"");
			if (element.isFinal())      result.append(" final=\"yes\"");
			if (element.isSuper())      result.append(" super=\"yes\"");
			if (element.isSynthetic())  result.append(" synthetic=\"yes\"");
			if (element.isDeprecated()) result.append(" deprecated=\"yes\"");

			result.append(" name=\"").append(element.getClassName()).append("\"");

			if (element.isInterface()) {
				result.append(" interface=\"yes\"");
		
				result.append(" extends=\"");
				Iterator i = element.getAllInterfaces().iterator();
				while (i.hasNext()) {
					result.append(i.next());
					if (i.hasNext()) {
						result.append(", ");
					}
				}
				result.append("\"");
			} else {
				if (element.isAbstract()) result.append(" abstract=\"yes\"");
		
				result.append(" extends=\"").append(element.getSuperclassName()).append("\"");
		
				result.append(" implements=\"");
				Iterator i = element.getAllInterfaces().iterator();
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
