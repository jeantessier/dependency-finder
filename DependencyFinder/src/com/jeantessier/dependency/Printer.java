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

package com.jeantessier.dependency;

import java.io.*;
import java.util.*;

public abstract class Printer extends VisitorBase {
	private PrintWriter out;

	private String  indent_text      = "    ";
	private int     indent_level     = 0;
	private boolean show_inbounds    = true;
	private boolean show_outbounds   = true;
	private boolean show_empty_nodes = true;

	public Printer(PrintWriter out) {
		this(new SortedTraversalStrategy(new SelectiveTraversalStrategy()), out);
	}

	public Printer(TraversalStrategy strategy, PrintWriter out) {
		super(strategy);

		this.out = out;
	}

	public String IndentText() {
		return indent_text;
	}

	public void IndentText(String indent_text) {
		this.indent_text = indent_text;
	}

	public boolean ShowInbounds() {
		return show_inbounds;
	}

	public void ShowInbounds(boolean show_inbounds) {
		this.show_inbounds = show_inbounds;
	}
	
	public boolean ShowOutbounds() {
		return show_outbounds;
	}

	public void ShowOutbounds(boolean show_outbounds) {
		this.show_outbounds = show_outbounds;
	}
	
	public boolean ShowEmptyNodes() {
		return show_empty_nodes;
	}

	public void ShowEmptyNodes(boolean show_empty_nodes) {
		this.show_empty_nodes = show_empty_nodes;
	}
	
	protected Printer Append(boolean b) {
		out.print(b);
		return this;
	}

	protected Printer Append(char c) {
		out.print(c);
		return this;
	}

	protected Printer Append(char[] s) {
		out.print(s);
		return this;
	}

	protected Printer Append(double d) {
		out.print(d);
		return this;
	}

	protected Printer Append(float f) {
		out.print(f);
		return this;
	}

	protected Printer Append(int i) {
		out.print(i);
		return this;
	}

	protected Printer Append(long l) {
		out.print(l);
		return this;
	}

	protected Printer Append(Object obj) {
		out.print(obj);
		return this;
	}

	protected Printer Append(String s) {
		out.print(s);
		return this;
	}

	protected Printer Indent() {
		for (int i=0; i<indent_level; i++) {
			Append(IndentText());
		}

		return this;
	}

	protected Printer EOL() {
		out.println();
		return this;
	}

	protected void RaiseIndent() {
		indent_level++;
	}

	protected void LowerIndent() {
		indent_level--;
	}

	protected boolean ShowPackageNode(PackageNode node) {
		boolean result = ShowNode(node);

		Iterator i = node.getClasses().iterator();
		while (!result && i.hasNext()) {
			result = ShowClassNode((ClassNode) i.next());
		}
		
		return result;
	}

	protected boolean ShowClassNode(ClassNode node) {
		boolean result = ShowNode(node);

		Iterator i = node.getFeatures().iterator();
		while (!result && i.hasNext()) {
			result = ShowFeatureNode((FeatureNode) i.next());
		}
		
		return result;
	}
	
	protected boolean ShowFeatureNode(FeatureNode node) {
		return ShowNode(node);
	}
	
	protected boolean ShowNode(Node node) {
		boolean result = ShowEmptyNodes();

		if (!result) {
			result = (ShowOutbounds() && !node.getOutboundDependencies().isEmpty()) || (ShowInbounds() && !node.getInboundDependencies().isEmpty());
		}

		return result;
	}
}
