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

import org.apache.oro.text.perl.*;

public class TextPrinter extends Printer {
	private static final Perl5Util perl = new Perl5Util();
    
	protected static Perl5Util Perl() {
		return perl;
	}

	public TextPrinter() {
		super();
	}

	public TextPrinter(TraversalStrategy strategy) {
		super(strategy);
	}

	public TextPrinter(String indent_text) {
		super(indent_text);
	}

	public TextPrinter(TraversalStrategy strategy, String indent_text) {
		super(strategy, indent_text);
	}

	protected void PreprocessPackageNode(PackageNode node) {
		super.PreprocessPackageNode(node);

		Indent().Append(node.Name()).Append("\n");
		RaiseIndent();
	}

	protected void PostprocessPackageNode(PackageNode node) {
		LowerIndent();
	}

	public void VisitInboundPackageNode(PackageNode node) {
		Indent().Append("<- ").Append(node.Name()).Append("\n");
	}

	public void VisitOutboundPackageNode(PackageNode node) {
		Indent().Append("-> ").Append(node.Name()).Append("\n");
	}

	protected void PreprocessClassNode(ClassNode node) {
		super.PreprocessClassNode(node);

		Indent().Append(node.Name().substring(node.Name().lastIndexOf('.') + 1)).Append("\n");
		RaiseIndent();
	}

	protected void PostprocessClassNode(ClassNode node) {
		LowerIndent();
	}

	public void VisitInboundClassNode(ClassNode node) {
		Indent().Append("<- ").Append(node.Name()).Append("\n");
	}

	public void VisitOutboundClassNode(ClassNode node) {
		Indent().Append("-> ").Append(node.Name()).Append("\n");
	}

	protected void PreprocessFeatureNode(FeatureNode node) {
		super.PreprocessFeatureNode(node);

		if (Perl().match("/([^\\.]*\\(.*\\))$/", node.Name())) {
			Indent().Append(Perl().group(1)).Append("\n");
		} else if (Perl().match("/([^\\.]*)$/", node.Name())) {
			Indent().Append(Perl().group(1)).Append("\n");
		} else {
			Indent().Append(node.Name().substring(node.Name().lastIndexOf('.') + 1)).Append("\n");
		}

		RaiseIndent();
	}

	protected void PostprocessFeatureNode(FeatureNode node) {
		LowerIndent();
	}

	public void VisitInboundFeatureNode(FeatureNode node) {
		Indent().Append("<- ").Append(node.Name()).Append("\n");
	}

	public void VisitOutboundFeatureNode(FeatureNode node) {
		Indent().Append("-> ").Append(node.Name()).Append("\n");
	}
}
