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

public class ExternalClassReport extends Printer implements Comparable {
	private RemovableDifferences differences;

	private Collection removed_fields = new TreeSet();
	private Collection removed_constructors = new TreeSet();
	private Collection removed_methods = new TreeSet();

	private Collection deprecated_fields = new TreeSet();
	private Collection deprecated_constructors = new TreeSet();
	private Collection deprecated_methods = new TreeSet();

	private Collection modified_fields = new TreeSet();
	private Collection modified_constructors = new TreeSet();
	private Collection modified_methods = new TreeSet();

	public ExternalClassReport() {
		super();
	}

	public ExternalClassReport(String indent) {
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
		if (differences.IsRemoved() && (differences.OldFeature().IsPublic() || differences.OldFeature().IsProtected())) {
			removed_fields.add(differences);
		}
	
		if (differences.NewDeprecation() && (differences.NewFeature().IsPublic() || differences.NewFeature().IsProtected())) {
			deprecated_fields.add(differences);
		}
	
		if (differences.IsModified() && (differences.OldFeature().IsPublic() || differences.OldFeature().IsProtected())) {
			modified_fields.add(differences);
		}
	}

	public void VisitConstructorDifferences(ConstructorDifferences differences) {
		if (differences.IsRemoved() && (differences.OldFeature().IsPublic() || differences.OldFeature().IsProtected())) {
			removed_constructors.add(differences);
		}
	
		if (differences.NewDeprecation() && (differences.NewFeature().IsPublic() || differences.NewFeature().IsProtected())) {
			deprecated_constructors.add(differences);
		}
	
		if (differences.IsModified() && (differences.OldFeature().IsPublic() || differences.OldFeature().IsProtected())) {
			modified_constructors.add(differences);
		}
	}

	public void VisitMethodDifferences(MethodDifferences differences) {
		if (differences.IsRemoved() && (differences.OldFeature().IsPublic() || differences.OldFeature().IsProtected())) {
			removed_methods.add(differences);
		}
	
		if (differences.NewDeprecation() && (differences.NewFeature().IsPublic() || differences.NewFeature().IsProtected())) {
			deprecated_methods.add(differences);
		}
	
		if (differences.IsModified() && (differences.OldFeature().IsPublic() || differences.OldFeature().IsProtected())) {
			modified_methods.add(differences);
		}
	}

	public String toString() {
		RaiseIndent();
		RaiseIndent();

		Indent().Append("<class>\n");
		RaiseIndent();

		Indent().Append("<name>").Append(differences.Name()).Append("</name>\n");
	    
		if (differences.IsModified()) {
			Indent().Append("<old-declaration>").Append(differences.OldDeclaration()).Append("</old-declaration>\n");
			Indent().Append("<new-declaration>").Append(differences.NewDeclaration()).Append("</new-declaration>\n");
		}

		if (removed_fields.size() != 0) {
			Indent().Append("<removed-fields>\n");
			RaiseIndent();

			Iterator i = removed_fields.iterator();
			while (i.hasNext()) {
				Indent().Append("<declaration>").Append(((FeatureDifferences) i.next()).OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</removed-fields>\n");
		}

		if (removed_constructors.size() != 0) {
			Indent().Append("<removed-constructors>\n");
			RaiseIndent();

			Iterator i = removed_constructors.iterator();
			while (i.hasNext()) {
				Indent().Append("<declaration>").Append(((FeatureDifferences) i.next()).OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</removed-constructors>\n");
		}

		if (removed_methods.size() != 0) {
			Indent().Append("<removed-methods>\n");
			RaiseIndent();

			Iterator i = removed_methods.iterator();
			while (i.hasNext()) {
				Indent().Append("<declaration>").Append(((FeatureDifferences) i.next()).OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</removed-methods>\n");
		}

		if (deprecated_fields.size() != 0) {
			Indent().Append("<deprecated-fields>\n");
			RaiseIndent();

			Iterator i = deprecated_fields.iterator();
			while (i.hasNext()) {
				Indent().Append("<declaration>").Append(((FeatureDifferences) i.next()).OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-fields>\n");
		}

		if (deprecated_constructors.size() != 0) {
			Indent().Append("<deprecated-constructors>\n");
			RaiseIndent();

			Iterator i = deprecated_constructors.iterator();
			while (i.hasNext()) {
				Indent().Append("<declaration>").Append(((FeatureDifferences) i.next()).OldDeclaration()).Append("</declaration>\n");
			}

			LowerIndent();
			Indent().Append("</deprecated-constructors>\n");
		}

		if (deprecated_methods.size() != 0) {
			Indent().Append("<deprecated-methods>\n");
			RaiseIndent();

			Iterator i = deprecated_methods.iterator();
			while (i.hasNext()) {
				Indent().Append("<declaration>").Append(((FeatureDifferences) i.next()).OldDeclaration()).Append("</declaration>\n");
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

				Indent().Append("<feature>\n");
				RaiseIndent();
		
				Indent().Append("<name>").Append(fd.Name()).Append("</name>\n");
				Indent().Append("<old-declaration>").Append(fd.OldDeclaration()).Append("</old-declaration>\n");
				Indent().Append("<new-declaration>").Append(fd.NewDeclaration()).Append("</new-declaration>\n");
		
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

				Indent().Append("<feature>\n");
				RaiseIndent();
		
				Indent().Append("<name>").Append(fd.Name()).Append("</name>\n");
				Indent().Append("<old-declaration>").Append(fd.OldDeclaration()).Append("</old-declaration>\n");
				Indent().Append("<new-declaration>").Append(fd.NewDeclaration()).Append("</new-declaration>\n");
		
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

				Indent().Append("<feature>\n");
				RaiseIndent();
		
				Indent().Append("<name>").Append(fd.Name()).Append("</name>\n");
				Indent().Append("<old-declaration>").Append(fd.OldDeclaration()).Append("</old-declaration>\n");
				Indent().Append("<new-declaration>").Append(fd.NewDeclaration()).Append("</new-declaration>\n");
		
				LowerIndent();
				Indent().Append("</feature>\n");
			}

			LowerIndent();
			Indent().Append("</modified-methods>\n");
		}

		LowerIndent();
		Indent().Append("</class>\n");

		return super.toString();
	}

	public int compareTo(Object other) {
		int result = 0;

		if (other instanceof ExternalClassReport) {
			result = differences.compareTo(((ExternalClassReport) other).differences);
		} else {
			throw new ClassCastException("Unable to compare ExternalClassReport to " + other.getClass().getName());
		}

		return result;
	}
}
