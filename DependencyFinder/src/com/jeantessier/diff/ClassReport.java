/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

	private Collection removedFields            = new TreeSet();
	private Collection removedConstructors      = new TreeSet();
	private Collection removedMethods           = new TreeSet();

	private Collection deprecatedFields         = new TreeSet();
	private Collection deprecatedConstructors   = new TreeSet();
	private Collection deprecatedMethods        = new TreeSet();

	private Collection undocumentedFields       = new TreeSet();
	private Collection undocumentedConstructors = new TreeSet();
	private Collection undocumentedMethods      = new TreeSet();

	private Collection modifiedFields           = new TreeSet();
	private Collection modifiedConstructors     = new TreeSet();
	private Collection modifiedMethods          = new TreeSet();

	private Collection documentedFields         = new TreeSet();
	private Collection documentedConstructors   = new TreeSet();
	private Collection documentedMethods        = new TreeSet();

	private Collection undeprecatedFields       = new TreeSet();
	private Collection undeprecatedConstructors = new TreeSet();
	private Collection undeprecatedMethods      = new TreeSet();

	private Collection newFields                = new TreeSet();
	private Collection newConstructors          = new TreeSet();
	private Collection newMethods               = new TreeSet();

	public void visitClassDifferences(ClassDifferences differences) {
		this.differences = differences;

		Iterator i = differences.getFeatureDifferences().iterator();
		while (i.hasNext()) {
			((Differences) i.next()).accept(this);
		}
	}

	public void visitInterfaceDifferences(InterfaceDifferences differences) {
		this.differences = differences;

		Iterator i = differences.getFeatureDifferences().iterator();
		while (i.hasNext()) {
			((Differences) i.next()).accept(this);
		}
	}

	public void visitFieldDifferences(FieldDifferences differences) {
		if (differences.isRemoved()) {
			removedFields.add(differences);
		}
	
		if (differences.isModified()) {
			modifiedFields.add(differences);
		}
	
		if (differences.isNew()) {
			newFields.add(differences);
		}

		if (isDeprecated()) {
			deprecatedFields.add(differences);
		}

		if (isUndeprecated()) {
			undeprecatedFields.add(differences);
		}

		if (isDocumented()) {
			documentedFields.add(differences);
		}

		if (isUndocumented()) {
			undocumentedFields.add(differences);
		}
	}

	public void visitConstructorDifferences(ConstructorDifferences differences) {
		if (differences.isRemoved()) {
			removedConstructors.add(differences);
		}
	
		if (differences.isModified()) {
			modifiedConstructors.add(differences);
		}
	
		if (differences.isNew()) {
			newConstructors.add(differences);
		}

		if (isDeprecated()) {
			deprecatedConstructors.add(differences);
		}

		if (isUndeprecated()) {
			undeprecatedConstructors.add(differences);
		}

		if (isDocumented()) {
			documentedConstructors.add(differences);
		}

		if (isUndocumented()) {
			undocumentedConstructors.add(differences);
		}
	}

	public void visitMethodDifferences(MethodDifferences differences) {
		if (differences.isRemoved()) {
			removedMethods.add(differences);
		}
	
		if (differences.isModified()) {
			modifiedMethods.add(differences);
		}
	
		if (differences.isNew()) {
			newMethods.add(differences);
		}

		if (isDeprecated()) {
			deprecatedMethods.add(differences);
		}

		if (isUndeprecated()) {
			undeprecatedMethods.add(differences);
		}

		if (isDocumented()) {
			documentedMethods.add(differences);
		}

		if (isUndocumented()) {
			undocumentedMethods.add(differences);
		}
	}

	public String toString() {
		raiseIndent();
		raiseIndent();

		indent().append("<class>").eol();
		raiseIndent();

		indent().append("<name>").append(differences.getName()).append("</name>").eol();

		if (!differences.getOldDeclaration().equals(differences.getNewDeclaration())) {
			indent().append("<modified-declaration>").eol();
			raiseIndent();

			indent().append("<old-declaration").append(breakdownDeclaration(differences.getOldClass())).append(">").append(differences.getOldDeclaration()).append("</old-declaration>").eol();
			indent().append("<new-declaration").append(breakdownDeclaration(differences.getNewClass())).append(">").append(differences.getNewDeclaration()).append("</new-declaration>").eol();

			lowerIndent();
			indent().append("</modified-declaration>").eol();
		}

		if (removedFields.size() != 0) {
			indent().append("<removed-fields>").eol();
			raiseIndent();

			Iterator i = removedFields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getOldFeature())).append(fd.isInherited() ? " inherited=\"yes\"" : "").append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</removed-fields>").eol();
		}

		if (removedConstructors.size() != 0) {
			indent().append("<removed-constructors>").eol();
			raiseIndent();

			Iterator i = removedConstructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(fd.isInherited() ? " inherited=\"yes\"" : "").append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</removed-constructors>").eol();
		}

		if (removedMethods.size() != 0) {
			indent().append("<removed-methods>").eol();
			raiseIndent();

			Iterator i = removedMethods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(fd.isInherited() ? " inherited=\"yes\"" : "").append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</removed-methods>").eol();
		}

		if (deprecatedFields.size() != 0) {
			indent().append("<deprecated-fields>").eol();
			raiseIndent();

			Iterator i = deprecatedFields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</deprecated-fields>").eol();
		}

		if (deprecatedConstructors.size() != 0) {
			indent().append("<deprecated-constructors>").eol();
			raiseIndent();

			Iterator i = deprecatedConstructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</deprecated-constructors>").eol();
		}

		if (deprecatedMethods.size() != 0) {
			indent().append("<deprecated-methods>").eol();
			raiseIndent();

			Iterator i = deprecatedMethods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</deprecated-methods>").eol();
		}

		if (undocumentedFields.size() != 0) {
			indent().append("<undocumented-fields>").eol();
			raiseIndent();

			Iterator i = undocumentedFields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</undocumented-fields>").eol();
		}

		if (undocumentedConstructors.size() != 0) {
			indent().append("<undocumented-constructors>").eol();
			raiseIndent();

			Iterator i = undocumentedConstructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</undocumented-constructors>").eol();
		}

		if (undocumentedMethods.size() != 0) {
			indent().append("<undocumented-methods>").eol();
			raiseIndent();

			Iterator i = undocumentedMethods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</undocumented-methods>").eol();
		}

		if (modifiedFields.size() != 0) {
			indent().append("<modified-fields>").eol();
			raiseIndent();

			Iterator i = modifiedFields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				indent().append("<feature>").eol();
				raiseIndent();
		
				indent().append("<name>").append(fd.getName()).append("</name>").eol();

				indent().append("<modified-declaration>").eol();
				raiseIndent();
				indent().append("<old-declaration").append(breakdownDeclaration((Field_info) fd.getOldFeature())).append(">").append(fd.getOldDeclaration()).append("</old-declaration>").eol();
				indent().append("<new-declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</new-declaration>").eol();
				lowerIndent();
				indent().append("</modified-declaration>").eol();
		
				lowerIndent();
				indent().append("</feature>").eol();
			}

			lowerIndent();
			indent().append("</modified-fields>").eol();
		}

		if (modifiedConstructors.size() != 0) {
			indent().append("<modified-constructors>").eol();
			raiseIndent();

			Iterator i = modifiedConstructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				indent().append("<feature>").eol();
				raiseIndent();

				indent().append("<name>").append(fd.getName()).append("</name>").eol();
		
				indent().append("<modified-declaration>").eol();
				raiseIndent();
				indent().append("<old-declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(">").append(fd.getOldDeclaration()).append("</old-declaration>").eol();
				indent().append("<new-declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</new-declaration>").eol();
				lowerIndent();
				indent().append("</modified-declaration>").eol();
		
				lowerIndent();
				indent().append("</feature>").eol();
			}

			lowerIndent();
			indent().append("</modified-constructors>").eol();
		}

		if (modifiedMethods.size() != 0) {
			indent().append("<modified-methods>").eol();
			raiseIndent();

			Iterator i = modifiedMethods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();

				indent().append("<feature>").eol();
				raiseIndent();
		
				indent().append("<name>").append(fd.getName()).append("</name>").eol();

				indent().append("<modified-declaration>").eol();
				raiseIndent();
				indent().append("<old-declaration").append(breakdownDeclaration((Method_info) fd.getOldFeature())).append(">").append(fd.getOldDeclaration()).append("</old-declaration>").eol();
				indent().append("<new-declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</new-declaration>").eol();
				lowerIndent();
				indent().append("</modified-declaration>").eol();
		
				lowerIndent();
				indent().append("</feature>").eol();
			}

			lowerIndent();
			indent().append("</modified-methods>").eol();
		}

		if (documentedFields.size() != 0) {
			indent().append("<documented-fields>").eol();
			raiseIndent();

			Iterator i = documentedFields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</documented-fields>").eol();
		}

		if (documentedConstructors.size() != 0) {
			indent().append("<documented-constructors>").eol();
			raiseIndent();

			Iterator i = documentedConstructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</documented-constructors>").eol();
		}

		if (documentedMethods.size() != 0) {
			indent().append("<documented-methods>").eol();
			raiseIndent();

			Iterator i = documentedMethods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</documented-methods>").eol();
		}

		if (undeprecatedFields.size() != 0) {
			indent().append("<undeprecated-fields>").eol();
			raiseIndent();

			Iterator i = undeprecatedFields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</undeprecated-fields>").eol();
		}

		if (undeprecatedConstructors.size() != 0) {
			indent().append("<undeprecated-constructors>").eol();
			raiseIndent();

			Iterator i = undeprecatedConstructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</undeprecated-constructors>").eol();
		}

		if (undeprecatedMethods.size() != 0) {
			indent().append("<undeprecated-methods>").eol();
			raiseIndent();

			Iterator i = undeprecatedMethods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getOldDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</undeprecated-methods>").eol();
		}

		if (newFields.size() != 0) {
			indent().append("<new-fields>").eol();
			raiseIndent();

			Iterator i = newFields.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Field_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</new-fields>").eol();
		}

		if (newConstructors.size() != 0) {
			indent().append("<new-constructors>").eol();
			raiseIndent();

			Iterator i = newConstructors.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</new-constructors>").eol();
		}

		if (newMethods.size() != 0) {
			indent().append("<new-methods>").eol();
			raiseIndent();

			Iterator i = newMethods.iterator();
			while (i.hasNext()) {
				FeatureDifferences fd = (FeatureDifferences) i.next();
				indent().append("<declaration").append(breakdownDeclaration((Method_info) fd.getNewFeature())).append(">").append(fd.getNewDeclaration()).append("</declaration>").eol();
			}

			lowerIndent();
			indent().append("</new-methods>").eol();
		}

		lowerIndent();
		indent().append("</class>").eol();

		return super.toString();
	}

	private static final String breakdownDeclaration(Classfile element) {
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

	private static final String breakdownDeclaration(Field_info element) {
		StringBuffer result = new StringBuffer();

		if (element != null) {
			if (element.isPublic())     result.append(" visibility=\"public\"");
			if (element.isProtected())  result.append(" visibility=\"protected\"");
			if (element.isPackage())    result.append(" visibility=\"package\"");
			if (element.isPrivate())    result.append(" visibility=\"private\"");
			if (element.isStatic())     result.append(" static=\"yes\"");
			if (element.isFinal())      result.append(" final=\"yes\"");
			if (element.isVolatile())   result.append(" volatile=\"yes\"");
			if (element.isTransient())  result.append(" transient=\"yes\"");
			if (element.isSynthetic())  result.append(" synthetic=\"yes\"");
			if (element.isDeprecated()) result.append(" deprecated=\"yes\"");

			result.append(" type=\"").append(element.getType()).append("\"");
			result.append(" name=\"").append(element.getName()).append("\"");
			result.append(" signature=\"").append(element.getSignature()).append("\"");
			result.append(" full-signature=\"").append(element.getFullSignature()).append("\"");
		}

		return result.toString();
	}

	private static String breakdownDeclaration(Method_info element) {
		StringBuffer result = new StringBuffer();

		if (element != null) {
			if (element.isPublic())       result.append(" visibility=\"public\"");
			if (element.isProtected())    result.append(" visibility=\"protected\"");
			if (element.isPackage())      result.append(" visibility=\"package\"");
			if (element.isPrivate())      result.append(" visibility=\"private\"");
			if (element.isStatic())       result.append(" static=\"yes\"");
			if (element.isFinal())        result.append(" final=\"yes\"");
			if (element.isSynchronized()) result.append(" synchronized=\"yes\"");
			if (element.isNative())       result.append(" native=\"yes\"");
			if (element.isAbstract())     result.append(" abstract=\"yes\"");
			if (element.isStrict())       result.append(" strict=\"yes\"");
			if (element.isSynthetic())    result.append(" synthetic=\"yes\"");
			if (element.isDeprecated())   result.append(" deprecated=\"yes\"");

			if (!element.getName().equals("<init>") && !element.getName().equals("<clinit>")) {
				result.append(" return-type=\"").append(element.getReturnType()).append("\"");
			}

			result.append(" signature=\"").append(element.getSignature()).append("\"");
			result.append(" full-signature=\"").append(element.getFullSignature()).append("\"");

			result.append(" throws=\"");
			Iterator i = element.getExceptions().iterator();
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
