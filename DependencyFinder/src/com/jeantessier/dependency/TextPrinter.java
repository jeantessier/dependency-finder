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
