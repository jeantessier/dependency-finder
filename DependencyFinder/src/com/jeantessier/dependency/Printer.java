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

public abstract class Printer extends VisitorBase {
	private LinkedList buffers       = new LinkedList();
	private String     indent_text;
	private int        indent_level = 0;

	public Printer() {
		this(new SortedTraversalStrategy(new SelectiveTraversalStrategy()), "    ");
	}

	public Printer(TraversalStrategy strategy) {
		this(strategy, "    ");
	}

	public Printer(String indent_text) {
		this(new SortedTraversalStrategy(new SelectiveTraversalStrategy()), indent_text);
	}

	public Printer(TraversalStrategy strategy, String indent_text) {
		super(strategy);
		PushBuffer();
		this.indent_text = indent_text;
	}

	private StringBuffer CurrentBuffer() {
		return (StringBuffer) buffers.getLast();
	}

	protected void PushBuffer() {
		buffers.add(new StringBuffer());
	}

	protected void PopBuffer(String message) {
		StringBuffer buffer = (StringBuffer) buffers.removeLast();

		Indent().Append(message).Append("\n");
		CurrentBuffer().append(buffer);
	}

	protected void KillBuffer() {
		buffers.removeLast();
	}

	protected int CurrentBufferLength() {
		return CurrentBuffer().length();
	}
	
	protected Printer Append(boolean b) {
		CurrentBuffer().append(b);
		return this;
	}

	protected Printer Append(char c) {
		CurrentBuffer().append(c);
		return this;
	}

	protected Printer Append(char[] str) {
		CurrentBuffer().append(str);
		return this;
	}

	protected Printer Append(char[] str, int offset, int len) {
		CurrentBuffer().append(str, offset, len);
		return this;
	}

	protected Printer Append(double d) {
		CurrentBuffer().append(d);
		return this;
	}

	protected Printer Append(float f) {
		CurrentBuffer().append(f);
		return this;
	}

	protected Printer Append(int i) {
		CurrentBuffer().append(i);
		return this;
	}

	protected Printer Append(long l) {
		CurrentBuffer().append(l);
		return this;
	}

	protected Printer Append(Object obj) {
		CurrentBuffer().append(obj);
		return this;
	}

	protected Printer Append(String str) {
		CurrentBuffer().append(str);
		return this;
	}

	protected Printer Indent() {
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
		return CurrentBuffer().toString();
	}
}
