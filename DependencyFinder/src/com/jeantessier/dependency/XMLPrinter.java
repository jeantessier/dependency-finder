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
