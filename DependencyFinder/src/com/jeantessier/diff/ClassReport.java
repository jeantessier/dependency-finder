/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

public class ClassReport extends Printer implements Comparable {
	private ClassDifferences differences;

	private Collection removed_fields            = new TreeSet();
	private Collection removed_constructors      = new TreeSet();
	private Collection removed_methods           = new TreeSet();

	private Collection deprecated_fields         = new TreeSet();
	private Collection deprecated_constructors   = new TreeSet();
	private Collection deprecated_methods        = new TreeSet();

	private Collection undocumented_fields       = new TreeSet();
	private Collection undocumented_constructors = new TreeSet();
	private Collection undocumented_methods      = new TreeSet();

	private Collection modified_fields           = new TreeSet();
	private Collection modified_constructors     = new TreeSet();
	private Collection modified_methods          = new TreeSet();

	private Collection documented_fields         = new TreeSet();
	private Collection documented_constructors   = new TreeSet();
	private Collection documented_methods        = new TreeSet();

	private Collection undeprecated_fields       = new TreeSet();
	private Collection undeprecated_constructors = new TreeSet();
	private Collection undeprecated_methods      = new TreeSet();

	private Collection new_fields                = new TreeSet();
	private Collection new_constructors          = new TreeSet();
	private Collection new_methods               = new TreeSet();

	public void VisitClassDifferences(ClassDifferences differences) {
		this.differences = differences;

		Iterator i = differences.FeatureDifferences().iterator();
		while (i.hasNext()) {
			((Differences) i.next()).Accept(this);
		}
	}

	public void VisitInterfaceDifferences(InterfaceDifferences differences) {
		this.differences = differences;

		Iterator i = differences.FeatureDifferences().iterator();
		while (i.hasNext()) {
			((Differences) i.next()).Accept(this);
		}
	}

	public void VisitFieldDifferences(FieldDifferences differences) {
		if (differences.IsRemoved()) {
			removed_fields.add(differences);
		}
	
		if (differences.IsModified()) {
			modified_fields.add(differences);
		}
	
		if (differences.IsNew()) {
			new_fields.add(differences);
		}

		if (Deprecated()) {
			deprecated_fields.add(differences);
		}

		if (Undeprecated()) {
			undeprecated_fields.add(differences);
		}

		if (Documented()) {
			documented_fields.add(differences);
		}

		if (Undocumented()) {
			undocumented_fields.add(differences);
		}
	}

	public void VisitConstructorDifferences(ConstructorDifferences differences) {
		if (differences.IsRemoved()) {
			removed_constructors.add(differences);
		}
	
		if (differences.IsModified()) {
			modified_constructors.add(differences);
		}
	
		if (differences.IsNew()) {
			new_constructors.add(differences);
		}

		if (Deprecated()) {
			deprecated_constructors.add(differences);
		}

		if (Undeprecated()) {
			undeprecated_constructors.add(differences);
		}

		if (Documented()) {
			documented_constructors.add(differences);
		}

		if (Undocumented()) {
			undocumented_constructors.add(differences);
		}
	}

	public void VisitMethodDifferences(MethodDifferences differences) {
		if (differences.IsRemoved()) {
			removed_methods.add(differences);
		}
	
		if (differences.IsModified()) {
			modified_methods.add(differences);
		}
	
		if (differences.IsNew()) {
			new_methods.add(differences);
		}

		if (Deprecated()) {
			deprecated_methods.add(differences);
		}

		if (Undeprecated()) {
			undeprecated_methods.add(differences);
		}

		if (Documented()) {
			documented_methods.add(differences);
		}

		if (Undocumented()) {
			undocumented_methods.add(differences);
		}
	}

	public String toString() {
		RaiseIndent();
		RaiseIndent();

		Indent().Append("<class>").EOL();
		RaiseIndent();

		Indent().Append("<name>").Append(differences.Name()).Append("</name>").EOL();

		if (!differences.OldDeclaration().equals(differences.NewDeclaration())) {
			Indent().Append("<modified-declaration>").EOL();
			RaiseIndent();

			Indent().Append("<old-declaration").Append(DeclarationBreakdown(differences.OldClass())).Append(">").Append(differences.OldDeclaration()).Append("</old-declaration>").EOL();
			Indent().Append("<new-declaration").Append(DeclarationBreakdown(differences.NewClass())).Append(">").Append(differences.NewDeclaration()).Append("</new-declaration>").EOL();

			LowerIndent();
			Indent().Append("</modified-declaration>").EOL();
		}

		if (removed_fields.size() != 0) {
			Indent().Append("<removed-fields>").EOL();
			RaiseIndent();

			Iterator i = removed_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.OldFeature())).Append(fd.Inherited() ? " inherited=\"yes\"" : "").Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</removed-fields>").EOL();
		}

		if (removed_constructors.size() != 0) {
			Indent().Append("<removed-constructors>").EOL();
			RaiseIndent();

			Iterator i = removed_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(fd.Inherited() ? " inherited=\"yes\"" : "").Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</removed-constructors>").EOL();
		}

		if (removed_methods.size() != 0) {
			Indent().Append("<removed-methods>").EOL();
			RaiseIndent();

			Iterator i = removed_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(fd.Inherited() ? " inherited=\"yes\"" : "").Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</removed-methods>").EOL();
		}

		if (deprecated_fields.size() != 0) {
			Indent().Append("<deprecated-fields>").EOL();
			RaiseIndent();

			Iterator i = deprecated_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</deprecated-fields>").EOL();
		}

		if (deprecated_constructors.size() != 0) {
			Indent().Append("<deprecated-constructors>").EOL();
			RaiseIndent();

			Iterator i = deprecated_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</deprecated-constructors>").EOL();
		}

		if (deprecated_methods.size() != 0) {
			Indent().Append("<deprecated-methods>").EOL();
			RaiseIndent();

			Iterator i = deprecated_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</deprecated-methods>").EOL();
		}

		if (undocumented_fields.size() != 0) {
			Indent().Append("<undocumented-fields>").EOL();
			RaiseIndent();

			Iterator i = undocumented_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</undocumented-fields>").EOL();
		}

		if (undocumented_constructors.size() != 0) {
			Indent().Append("<undocumented-constructors>").EOL();
			RaiseIndent();

			Iterator i = undocumented_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</undocumented-constructors>").EOL();
		}

		if (undocumented_methods.size() != 0) {
			Indent().Append("<undocumented-methods>").EOL();
			RaiseIndent();

			Iterator i = undocumented_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</undocumented-methods>").EOL();
		}

		if (modified_fields.size() != 0) {
			Indent().Append("<modified-fields>").EOL();
			RaiseIndent();

			Iterator i = modified_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				Indent().Append("<feature>").EOL();
				RaiseIndent();
		
				Indent().Append("<name>").Append(fd.Name()).Append("</name>").EOL();

				Indent().Append("<modified-declaration>").EOL();
				RaiseIndent();
				Indent().Append("<old-declaration").Append(DeclarationBreakdown((Field_info) fd.OldFeature())).Append(">").Append(fd.OldDeclaration()).Append("</old-declaration>").EOL();
				Indent().Append("<new-declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</new-declaration>").EOL();
				LowerIndent();
				Indent().Append("</modified-declaration>").EOL();
		
				LowerIndent();
				Indent().Append("</feature>").EOL();
			}

			LowerIndent();
			Indent().Append("</modified-fields>").EOL();
		}

		if (modified_constructors.size() != 0) {
			Indent().Append("<modified-constructors>").EOL();
			RaiseIndent();

			Iterator i = modified_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				Indent().Append("<feature>").EOL();
				RaiseIndent();

				Indent().Append("<name>").Append(fd.Name()).Append("</name>").EOL();
		
				Indent().Append("<modified-declaration>").EOL();
				RaiseIndent();
				Indent().Append("<old-declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(">").Append(fd.OldDeclaration()).Append("</old-declaration>").EOL();
				Indent().Append("<new-declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</new-declaration>").EOL();
				LowerIndent();
				Indent().Append("</modified-declaration>").EOL();
		
				LowerIndent();
				Indent().Append("</feature>").EOL();
			}

			LowerIndent();
			Indent().Append("</modified-constructors>").EOL();
		}

		if (modified_methods.size() != 0) {
			Indent().Append("<modified-methods>").EOL();
			RaiseIndent();

			Iterator i = modified_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				Indent().Append("<feature>").EOL();
				RaiseIndent();
		
				Indent().Append("<name>").Append(fd.Name()).Append("</name>").EOL();

				Indent().Append("<modified-declaration>").EOL();
				RaiseIndent();
				Indent().Append("<old-declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(">").Append(fd.OldDeclaration()).Append("</old-declaration>").EOL();
				Indent().Append("<new-declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</new-declaration>").EOL();
				LowerIndent();
				Indent().Append("</modified-declaration>").EOL();
		
				LowerIndent();
				Indent().Append("</feature>").EOL();
			}

			LowerIndent();
			Indent().Append("</modified-methods>").EOL();
		}

		if (documented_fields.size() != 0) {
			Indent().Append("<documented-fields>").EOL();
			RaiseIndent();

			Iterator i = documented_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</documented-fields>").EOL();
		}

		if (documented_constructors.size() != 0) {
			Indent().Append("<documented-constructors>").EOL();
			RaiseIndent();

			Iterator i = documented_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</documented-constructors>").EOL();
		}

		if (documented_methods.size() != 0) {
			Indent().Append("<documented-methods>").EOL();
			RaiseIndent();

			Iterator i = documented_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</documented-methods>").EOL();
		}

		if (undeprecated_fields.size() != 0) {
			Indent().Append("<undeprecated-fields>").EOL();
			RaiseIndent();

			Iterator i = undeprecated_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</undeprecated-fields>").EOL();
		}

		if (undeprecated_constructors.size() != 0) {
			Indent().Append("<undeprecated-constructors>").EOL();
			RaiseIndent();

			Iterator i = undeprecated_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</undeprecated-constructors>").EOL();
		}

		if (undeprecated_methods.size() != 0) {
			Indent().Append("<undeprecated-methods>").EOL();
			RaiseIndent();

			Iterator i = undeprecated_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</undeprecated-methods>").EOL();
		}

		if (new_fields.size() != 0) {
			Indent().Append("<new-fields>").EOL();
			RaiseIndent();

			Iterator i = new_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</new-fields>").EOL();
		}

		if (new_constructors.size() != 0) {
			Indent().Append("<new-constructors>").EOL();
			RaiseIndent();

			Iterator i = new_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</new-constructors>").EOL();
		}

		if (new_methods.size() != 0) {
			Indent().Append("<new-methods>").EOL();
			RaiseIndent();

			Iterator i = new_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</declaration>").EOL();
			}

			LowerIndent();
			Indent().Append("</new-methods>").EOL();
		}

		LowerIndent();
		Indent().Append("</class>").EOL();

		return super.toString();
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

	private static final String DeclarationBreakdown(Field_info element) {
		StringBuffer result = new StringBuffer();

		if (element != null) {
			if (element.IsPublic())     result.append(" visibility=\"public\"");
			if (element.IsProtected())  result.append(" visibility=\"protected\"");
			if (element.IsPackage())    result.append(" visibility=\"package\"");
			if (element.IsPrivate())    result.append(" visibility=\"private\"");
			if (element.IsStatic())     result.append(" static=\"yes\"");
			if (element.IsFinal())      result.append(" final=\"yes\"");
			if (element.IsVolatile())   result.append(" volatile=\"yes\"");
			if (element.IsTransient())  result.append(" transient=\"yes\"");
			if (element.IsSynthetic())  result.append(" synthetic=\"yes\"");
			if (element.IsDeprecated()) result.append(" deprecated=\"yes\"");

			result.append(" type=\"").append(element.Type()).append("\"");
			result.append(" name=\"").append(element.Name()).append("\"");
			result.append(" signature=\"").append(element.Signature()).append("\"");
			result.append(" full-signature=\"").append(element.FullSignature()).append("\"");
		}

		return result.toString();
	}

	private static String DeclarationBreakdown(Method_info element) {
		StringBuffer result = new StringBuffer();

		if (element != null) {
			if (element.IsPublic())       result.append(" visibility=\"public\"");
			if (element.IsProtected())    result.append(" visibility=\"protected\"");
			if (element.IsPackage())      result.append(" visibility=\"package\"");
			if (element.IsPrivate())      result.append(" visibility=\"private\"");
			if (element.IsStatic())       result.append(" static=\"yes\"");
			if (element.IsFinal())        result.append(" final=\"yes\"");
			if (element.IsSynchronized()) result.append(" synchronized=\"yes\"");
			if (element.IsNative())       result.append(" native=\"yes\"");
			if (element.IsAbstract())     result.append(" abstract=\"yes\"");
			if (element.IsStrict())       result.append(" strict=\"yes\"");
			if (element.IsSynthetic())    result.append(" synthetic=\"yes\"");
			if (element.IsDeprecated())   result.append(" deprecated=\"yes\"");

			if (!element.Name().equals("<init>") && !element.Name().equals("<clinit>")) {
				result.append(" return-type=\"").append(element.ReturnType()).append("\"");
			}

			result.append(" signature=\"").append(element.Signature()).append("\"");
			result.append(" full-signature=\"").append(element.FullSignature()).append("\"");

			result.append(" throws=\"");
			Iterator i = element.Exceptions().iterator();
			while (i.hasNext()) {
				result.append(i.next());
				if (i.hasNext()) {
					result.append(", ");
				}
			}
			result.append("\"");
		}

		return result.toString();
	}

	public int compareTo(Object other) {
		int result = 0;

		if (other instanceof ClassReport) {
			result = differences.compareTo(((ClassReport) other).differences);
		} else {
			throw new ClassCastException("Unable to compare ClassReport to " + other.getClass().getName());
		}

		return result;
	}
}
