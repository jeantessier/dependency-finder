/*
 *  Dependency Finder - Computes quality factors from compiled Java code
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

package com.jeantessier.dependency;

import java.util.*;

public class XMLPrinter extends Printer {
	private boolean at_top_level = false;

	public XMLPrinter() {
		super();
	}

	public XMLPrinter(TraversalStrategy strategy) {
		super(strategy);
	}

	public XMLPrinter(String indent_text) {
		super(indent_text);
	}

	public XMLPrinter(TraversalStrategy strategy, String indent_text) {
		super(strategy, indent_text);
	}

	public void TraverseNodes(Collection nodes) {
		if (at_top_level) {
			super.TraverseNodes(nodes);
		} else {
			at_top_level = true;
			Append(Preamble());
			Indent().Append("<dependencies>").Append("\n");
			RaiseIndent();
			super.TraverseNodes(nodes);
			LowerIndent();
			Indent().Append("</dependencies>").Append("\n");
			at_top_level = false;
		}
	}

	private String Preamble() {
		StringBuffer result = new StringBuffer();

		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n");
		result.append("\n");
		result.append("<!DOCTYPE dependencies [\n");
		result.append("\n");
		result.append("<!ELEMENT dependencies (package)* >\n");
		result.append("\n");
		result.append("<!ELEMENT package (name,outbound*,inbound*,class*) >\n");
		result.append("\n");
		result.append("<!ELEMENT class (name,outbound*,inbound*,feature*) >\n");
		result.append("\n");
		result.append("<!ELEMENT feature (name,outbound*,inbound*) >\n");
		result.append("\n");
		result.append("<!ELEMENT name (#PCDATA)* >\n");
		result.append("\n");
		result.append("<!ELEMENT outbound (#PCDATA)* >\n");
		result.append("<!ATTLIST outbound\n");
		result.append("          type (package|class|feature) #REQUIRED\n");
		result.append(">\n");
		result.append("\n");
		result.append("<!ELEMENT inbound (#PCDATA)* >\n");
		result.append("<!ATTLIST inbound\n");
		result.append("          type (package|class|feature) #REQUIRED\n");
		result.append(">\n");
		result.append("\n");
		result.append("]>\n");
		result.append("\n");

		return result.toString();
	}

	protected void PreprocessPackageNode(PackageNode node) {
		super.PreprocessPackageNode(node);

		Indent().Append("<package>").Append("\n");
		RaiseIndent();
		Indent().Append("<name>").Append(node.Name()).Append("</name>\n");
	}

	protected void PostprocessPackageNode(PackageNode node) {
		LowerIndent();
		Indent().Append("</package>").Append("\n");
	}

	public void VisitInboundPackageNode(PackageNode node) {
		Indent().Append("<inbound type=\"package\">").Append(node.Name()).Append("</inbound>\n");
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		Indent().Append("<outbound type=\"package\">").Append(node.Name()).Append("</outbound>\n");
	}

	protected void PreprocessClassNode(ClassNode node) {
		super.PreprocessClassNode(node);

		Indent().Append("<class>").Append("\n");
		RaiseIndent();
		Indent().Append("<name>").Append(node.Name()).Append("</name>\n");
	}

	protected void PostprocessClassNode(ClassNode node) {
		LowerIndent();
		Indent().Append("</class>").Append("\n");
	}

	public void VisitInboundClassNode(ClassNode node) {
		Indent().Append("<inbound type=\"class\">").Append(node.Name()).Append("</inbound>\n");
	}

	public void VisitOutboundClassNode(ClassNode node) {
		Indent().Append("<outbound type=\"class\">").Append(node.Name()).Append("</outbound>\n");
	}

	protected void PreprocessFeatureNode(FeatureNode node) {
		super.PreprocessFeatureNode(node);

		Indent().Append("<feature>").Append("\n");
		RaiseIndent();
		Indent().Append("<name>").Append(node.Name()).Append("</name>\n");
	}

	protected void PostprocessFeatureNode(FeatureNode node) {
		LowerIndent();
		Indent().Append("</feature>").Append("\n");
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		Indent().Append("<inbound type=\"feature\">").Append(node.Name()).Append("</inbound>\n");
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		Indent().Append("<outbound type=\"feature\">").Append(node.Name()).Append("</outbound>\n");
	}
}
