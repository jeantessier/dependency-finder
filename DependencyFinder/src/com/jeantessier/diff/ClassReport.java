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

import com.jeantessier.classreader.*;

public class ClassReport extends Printer implements Comparable {
	private ClassDifferences differences;

	private Collection removed_fields = new TreeSet();
	private Collection removed_constructors = new TreeSet();
	private Collection removed_methods = new TreeSet();

	private Collection deprecated_fields = new TreeSet();
	private Collection deprecated_constructors = new TreeSet();
	private Collection deprecated_methods = new TreeSet();

	private Collection modified_fields = new TreeSet();
	private Collection modified_constructors = new TreeSet();
	private Collection modified_methods = new TreeSet();

	private Collection undeprecated_fields = new TreeSet();
	private Collection undeprecated_constructors = new TreeSet();
	private Collection undeprecated_methods = new TreeSet();

	private Collection new_fields = new TreeSet();
	private Collection new_constructors = new TreeSet();
	private Collection new_methods = new TreeSet();

	public ClassReport() {
		super();
	}

	public ClassReport(String indent) {
		super(indent);
	}

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
	
		if (differences.NewDeprecation()) {
			deprecated_fields.add(differences);
		}
	
		if (differences.IsModified()) {
			modified_fields.add(differences);
		}

		if (differences.RemovedDeprecation()) {
			undeprecated_fields.add(differences);
		}
	
		if (differences.IsNew()) {
			new_fields.add(differences);
		}
	}

	public void VisitConstructorDifferences(ConstructorDifferences differences) {
		if (differences.IsRemoved()) {
			removed_constructors.add(differences);
		}
	
		if (differences.NewDeprecation()) {
			deprecated_constructors.add(differences);
		}
	
		if (differences.IsModified()) {
			modified_constructors.add(differences);
		}

		if (differences.RemovedDeprecation()) {
			undeprecated_constructors.add(differences);
		}
	
		if (differences.IsNew()) {
			new_constructors.add(differences);
		}
	}

	public void VisitMethodDifferences(MethodDifferences differences) {
		if (differences.IsRemoved()) {
			removed_methods.add(differences);
		}
	
		if (differences.NewDeprecation()) {
			deprecated_methods.add(differences);
		}
	
		if (differences.IsModified()) {
			modified_methods.add(differences);
		}

		if (differences.RemovedDeprecation()) {
			undeprecated_methods.add(differences);
		}
	
		if (differences.IsNew()) {
			new_methods.add(differences);
		}
	}

	public String toString() {
		RaiseIndent();
		RaiseIndent();

		Indent().Append("<class visibility=\"");
		if (differences.OldClass().IsPublic() || differences.NewClass().IsPublic()) {
			Append("public");
		} else {
			Append("package");
		}
		Append("\">\n");
		RaiseIndent();

		Indent().Append("<name>").Append(differences.Name()).Append("</name>\n");
	    
		if (!differences.OldDeclaration().equals(differences.NewDeclaration())) {
			Indent().Append("<modified-declaration>\n");
			RaiseIndent();

			Indent().Append("<old-declaration").Append(DeclarationBreakdown(differences.OldClass())).Append(">").Append(differences.OldDeclaration()).Append("</old-declaration>\n");
			Indent().Append("<new-declaration").Append(DeclarationBreakdown(differences.NewClass())).Append(">").Append(differences.NewDeclaration()).Append("</new-declaration>\n");

			LowerIndent();
			Indent().Append("</modified-declaration>\n");
		}

		if (removed_fields.size() != 0) {
			Indent().Append("<removed-fields>\n");
			RaiseIndent();

			Iterator i = removed_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.OldFeature())).Append(fd.Inherited() ? " inherited=\"yes\"" : "").Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</removed-fields>\n");
		}

		if (removed_constructors.size() != 0) {
			Indent().Append("<removed-constructors>\n");
			RaiseIndent();

			Iterator i = removed_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(fd.Inherited() ? " inherited=\"yes\"" : "").Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</removed-constructors>\n");
		}

		if (removed_methods.size() != 0) {
			Indent().Append("<removed-methods>\n");
			RaiseIndent();

			Iterator i = removed_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(fd.Inherited() ? " inherited=\"yes\"" : "").Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</removed-methods>\n");
		}

		if (deprecated_fields.size() != 0) {
			Indent().Append("<deprecated-fields>\n");
			RaiseIndent();

			Iterator i = deprecated_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-fields>\n");
		}

		if (deprecated_constructors.size() != 0) {
			Indent().Append("<deprecated-constructors>\n");
			RaiseIndent();

			Iterator i = deprecated_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-constructors>\n");
		}

		if (deprecated_methods.size() != 0) {
			Indent().Append("<deprecated-methods>\n");
			RaiseIndent();

			Iterator i = deprecated_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-methods>\n");
		}

		if (modified_fields.size() != 0) {
			Indent().Append("<modified-fields>\n");
			RaiseIndent();

			Iterator i = modified_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				Indent().Append("<feature visibility=\"");
				if (fd.OldFeature().IsPublic() || fd.NewFeature().IsPublic()) {
					Append("public");
				} else if (fd.OldFeature().IsProtected() || fd.NewFeature().IsProtected()) {
					Append("protected");
				} else if (fd.OldFeature().IsPackage() || fd.NewFeature().IsPackage()) {
					Append("package");
				} else {
					Append("private");
				}
				Append("\">\n");
				RaiseIndent();
		
				Indent().Append("<name>").Append(fd.Name()).Append("</name>\n");

				Indent().Append("<modified-declaration>\n");
				RaiseIndent();
				Indent().Append("<old-declaration").Append(DeclarationBreakdown((Field_info) fd.OldFeature())).Append(">").Append(fd.OldDeclaration()).Append("</old-declaration>\n");
				Indent().Append("<new-declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</new-declaration>\n");
				LowerIndent();
				Indent().Append("</modified-declaration>\n");
		
				LowerIndent();
				Indent().Append("</feature>\n");
			}

			LowerIndent();
			Indent().Append("</modified-fields>\n");
		}

		if (modified_constructors.size() != 0) {
			Indent().Append("<modified-constructors>\n");
			RaiseIndent();

			Iterator i = modified_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				Indent().Append("<feature visibility=\"");
				if (fd.OldFeature().IsPublic() || fd.NewFeature().IsPublic()) {
					Append("public");
				} else if (fd.OldFeature().IsProtected() || fd.NewFeature().IsProtected()) {
					Append("protected");
				} else if (fd.OldFeature().IsPackage() || fd.NewFeature().IsPackage()) {
					Append("package");
				} else {
					Append("private");
				}
				Append("\">\n");
				RaiseIndent();

				Indent().Append("<name>").Append(fd.Name()).Append("</name>\n");
		
				Indent().Append("<modified-declaration>\n");
				RaiseIndent();
				Indent().Append("<old-declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(">").Append(fd.OldDeclaration()).Append("</old-declaration>\n");
				Indent().Append("<new-declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</new-declaration>\n");
				LowerIndent();
				Indent().Append("</modified-declaration>\n");
		
				LowerIndent();
				Indent().Append("</feature>\n");
			}

			LowerIndent();
			Indent().Append("</modified-constructors>\n");
		}

		if (modified_methods.size() != 0) {
			Indent().Append("<modified-methods>\n");
			RaiseIndent();

			Iterator i = modified_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				Indent().Append("<feature visibility=\"");
				if (fd.OldFeature().IsPublic() || fd.NewFeature().IsPublic()) {
					Append("public");
				} else if (fd.OldFeature().IsProtected() || fd.NewFeature().IsProtected()) {
					Append("protected");
				} else if (fd.OldFeature().IsPackage() || fd.NewFeature().IsPackage()) {
					Append("package");
				} else {
					Append("private");
				}
				Append("\">\n");
				RaiseIndent();
		
				Indent().Append("<name>").Append(fd.Name()).Append("</name>\n");

				Indent().Append("<modified-declaration>\n");
				RaiseIndent();
				Indent().Append("<old-declaration").Append(DeclarationBreakdown((Method_info) fd.OldFeature())).Append(">").Append(fd.OldDeclaration()).Append("</old-declaration>\n");
				Indent().Append("<new-declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</new-declaration>\n");
				LowerIndent();
				Indent().Append("</modified-declaration>\n");
		
				LowerIndent();
				Indent().Append("</feature>\n");
			}

			LowerIndent();
			Indent().Append("</modified-methods>\n");
		}

		if (undeprecated_fields.size() != 0) {
			Indent().Append("<undeprecated-fields>\n");
			RaiseIndent();

			Iterator i = undeprecated_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</undeprecated-fields>\n");
		}

		if (undeprecated_constructors.size() != 0) {
			Indent().Append("<undeprecated-constructors>\n");
			RaiseIndent();

			Iterator i = undeprecated_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</undeprecated-constructors>\n");
		}

		if (undeprecated_methods.size() != 0) {
			Indent().Append("<undeprecated-methods>\n");
			RaiseIndent();

			Iterator i = undeprecated_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</undeprecated-methods>\n");
		}

		if (new_fields.size() != 0) {
			Indent().Append("<new-fields>\n");
			RaiseIndent();

			Iterator i = new_fields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Field_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</new-fields>\n");
		}

		if (new_constructors.size() != 0) {
			Indent().Append("<new-constructors>\n");
			RaiseIndent();

			Iterator i = new_constructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</new-constructors>\n");
		}

		if (new_methods.size() != 0) {
			Indent().Append("<new-methods>\n");
			RaiseIndent();

			Iterator i = new_methods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				Indent().Append("<declaration").Append(DeclarationBreakdown((Method_info) fd.NewFeature())).Append(">").Append(fd.NewDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</new-methods>\n");
		}

		LowerIndent();
		Indent().Append("</class>\n");

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
				result.append(" return-type=\"").append((element.ReturnType() != null) ? element.ReturnType() : "void").append("\"");
			}
	    
			result.append(" signature=\"").append(element.Signature()).append("\"");
	    
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
