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

package com.jeantessier.dependencyfinder.cli;

import java.util.*;

public class ListDiffPrinter {
	private StringBuffer buffer = new StringBuffer();

	private String  indent_text;
	private int     indent_level = 0;

	private String     name        = "";
	private String     old_version = "";
	private String     new_version = "";
	private Collection removed     = new TreeSet();
	private Collection added       = new TreeSet();
	
	public ListDiffPrinter() {
		this("    ");
	}

	public ListDiffPrinter(String indent_text) {
		this.indent_text = indent_text;
	}

	public String Name() {
		return name;
	}

	public void Name(String name) {
		this.name = name;
	}

	public String OldVersion() {
		return old_version;
	}

	public void OldVersion(String old_version) {
		this.old_version = old_version;
	}

	public String NewVersion() {
		return new_version;
	}

	public void NewVersion(String new_version) {
		this.new_version = new_version;
	}
	
	public Collection Removed() {
		return Collections.unmodifiableCollection(removed);
	}
	
	public void RemoveAll(Collection removed) {
		this.removed.addAll(removed);
	}
	
	public void Remove(String line) {
		this.removed.add(line);
	}
	
	public Collection Added() {
		return Collections.unmodifiableCollection(added);
	}

	public void AddAll(Collection added) {
		this.added.addAll(added);
	}
	
	public void Add(String line) {
		this.added.add(line);
	}

	protected ListDiffPrinter Append(boolean b) {
		buffer.append(b);
		return this;
	}

	protected ListDiffPrinter Append(char c) {
		buffer.append(c);
		return this;
	}

	protected ListDiffPrinter Append(char[] str) {
		buffer.append(str);
		return this;
	}

	protected ListDiffPrinter Append(char[] str, int offset, int len) {
		buffer.append(str, offset, len);
		return this;
	}

	protected ListDiffPrinter Append(double d) {
		buffer.append(d);
		return this;
	}

	protected ListDiffPrinter Append(float f) {
		buffer.append(f);
		return this;
	}

	protected ListDiffPrinter Append(int i) {
		buffer.append(i);
		return this;
	}

	protected ListDiffPrinter Append(long l) {
		buffer.append(l);
		return this;
	}

	protected ListDiffPrinter Append(Object obj) {
		buffer.append(obj);
		return this;
	}

	protected ListDiffPrinter Append(String str) {
		buffer.append(str);
		return this;
	}

	protected ListDiffPrinter Indent() {
		for (int i=0; i<indent_level; i++) {
			Append(indent_text);
		}

		return this;
	}

	protected void RaiseIndent() {
		indent_level++;
	}

	protected void LowerIndent() {
		indent_level--;
	}

	public String toString() {
		Indent().Append(Preamble());
		Indent().Append("\n");

		Indent().Append("<list-diff>\n");
		RaiseIndent();

		Indent().Append("<name>").Append(Name()).Append("</name>\n");
		Indent().Append("<old>").Append(OldVersion()).Append("</old>\n");
		Indent().Append("<new>").Append(NewVersion()).Append("</new>\n");
	
		Indent().Append("<removed>\n");
		RaiseIndent();

		Iterator i;

		i = Removed().iterator();
		while (i.hasNext()) {
			Indent().Append("<line>").Append(i.next()).Append("</line>\n");
		}

		LowerIndent();
		Indent().Append("</removed>\n");
	
		Indent().Append("<added>\n");
		RaiseIndent();
		
		i = Added().iterator();
		while (i.hasNext()) {
			Indent().Append("<line>").Append(i.next()).Append("</line>\n");
		}
		
		LowerIndent();
		Indent().Append("</added>\n");

		LowerIndent();
		Indent().Append("</list-diff>\n");
		
		return buffer.toString();
	}

	private String Preamble() {
		StringBuffer result = new StringBuffer();
        
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
		result.append("\n");
		result.append("<!DOCTYPE list-diff STSTEM \"http://depfind.sourceforge.net/dtd/list-diff.dtd\">\n");
		result.append("\n");

		return result.toString();
	}
}
