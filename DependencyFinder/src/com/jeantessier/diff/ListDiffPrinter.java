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

package com.jeantessier.diff;

import java.util.*;

import org.apache.oro.text.perl.*;

public class ListDiffPrinter {
	public static final boolean DEFAULT_COMPRESS    = false;
	public static final String  DEFAULT_ENCODING    = "utf-8";
	public static final String  DEFAULT_DTD_PREFIX  = "http://depfind.sourceforge.net/dtd";

	private static final Perl5Util perl = new Perl5Util();
	
	private StringBuffer buffer = new StringBuffer();

	private String  indentText  = "    ";
	private int     indentLevel = 0;
	private boolean compress;

	private String     name       = "";
	private String     oldVersion = "";
	private String     newVersion = "";
	private Collection removed    = new TreeSet();
	private Collection added      = new TreeSet();
	
	public ListDiffPrinter() {
		this(DEFAULT_COMPRESS, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
	}
	
	public ListDiffPrinter(boolean compress) {
		this(compress, DEFAULT_ENCODING, DEFAULT_DTD_PREFIX);
	}
	
	public ListDiffPrinter(String encoding, String dtdPrefix) {
		this(DEFAULT_COMPRESS, encoding, dtdPrefix);
	}
	
	public ListDiffPrinter(boolean compress, String encoding, String dtdPrefix) {
		this.compress = compress;

		appendHeader(encoding, dtdPrefix);
	}

	public String getIndentText() {
		return indentText;
	}

	public void setIndentText(String indentText) {
		this.indentText = indentText;
	}

	private void appendHeader(String encoding, String dtdPrefix) {
		append("<?xml version=\"1.0\" encoding=\"").append(encoding).append("\" ?>").eol();
		eol();
		append("<!DOCTYPE list-diff SYSTEM \"").append(dtdPrefix).append("/list-diff.dtd\">").eol();
		eol();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}
	
	public Collection getRemoved() {
		return Collections.unmodifiableCollection(removed);
	}
	
	public void removeAll(Collection removed) {
		this.removed.addAll(removed);
	}
	
	public void remove(String line) {
		this.removed.add(line);
	}
	
	public Collection getAdded() {
		return Collections.unmodifiableCollection(added);
	}

	public void addAll(Collection added) {
		this.added.addAll(added);
	}
	
	public void add(String line) {
		this.added.add(line);
	}

	protected ListDiffPrinter append(boolean b) {
		buffer.append(b);
		return this;
	}

	protected ListDiffPrinter append(char c) {
		buffer.append(c);
		return this;
	}

	protected ListDiffPrinter append(char[] str) {
		buffer.append(str);
		return this;
	}

	protected ListDiffPrinter append(char[] str, int offset, int len) {
		buffer.append(str, offset, len);
		return this;
	}

	protected ListDiffPrinter append(double d) {
		buffer.append(d);
		return this;
	}

	protected ListDiffPrinter append(float f) {
		buffer.append(f);
		return this;
	}

	protected ListDiffPrinter append(int i) {
		buffer.append(i);
		return this;
	}

	protected ListDiffPrinter append(long l) {
		buffer.append(l);
		return this;
	}

	protected ListDiffPrinter append(Object obj) {
		buffer.append(obj);
		return this;
	}

	protected ListDiffPrinter append(String str) {
		buffer.append(str);
		return this;
	}

	protected ListDiffPrinter indent() {
		for (int i=0; i<indentLevel; i++) {
			append(getIndentText());
		}

		return this;
	}

	protected ListDiffPrinter eol() {
		return append(System.getProperty("line.separator", "\n"));
	}

	protected void raiseIndent() {
		indentLevel++;
	}

	protected void lowerIndent() {
		indentLevel--;
	}

	public String toString() {
		indent().append("<list-diff>").eol();
		raiseIndent();
		
		indent().append("<name>").append(getName()).append("</name>").eol();
		indent().append("<old>").append(getOldVersion()).append("</old>").eol();
		indent().append("<new>").append(getNewVersion()).append("</new>").eol();
		
		indent().append("<removed>").eol();
		raiseIndent();
		printLines(buffer, compress ? compress(getRemoved()) : getRemoved());
		lowerIndent();
		indent().append("</removed>").eol();
		
		indent().append("<added>").eol();
		raiseIndent();
		printLines(buffer, compress ? compress(getAdded()) : getAdded());
		lowerIndent();
		indent().append("</added>").eol();
		
		lowerIndent();
		indent().append("</list-diff>").eol();
		
		return buffer.toString();
	}

	private void printLines(StringBuffer buffer, Collection lines) {
		Iterator i = lines.iterator();
		while (i.hasNext()) {
			String line = (String) i.next();

			int pos = line.lastIndexOf(" [");
			if (pos != -1) {
				indent().append("<line>").append(line.substring(0, pos)).append("</line>").eol();
			} else {
				indent().append("<line>").append(line).append("</line>").eol();
			}
		}
	}

	private Collection compress(Collection lines) {
		Collection result = new TreeSet();

		Iterator i = lines.iterator();
		while (i.hasNext()) {
			String line = (String) i.next();

			boolean add = true;
			if (line.endsWith(" [C]")) {
				String packageName = extractPackageName(line);
				
				add = !lines.contains(packageName + " [P]");
			} else if (line.endsWith(" [F]")) {
				String className   = extractClassName(line);
				String packageName = extractPackageName(className);

				add = !lines.contains(packageName + " [P]") && !lines.contains(className + " [C]");
			}
			
			if (add) {
				result.add(line);
			}
		}
		
		return result;
	}

	private String extractPackageName(String className) {
		String result = "";

		int pos = className.lastIndexOf('.');
		if (pos != -1) {
			result = className.substring(0, pos);
		}
		
		return result;
	}

	private String extractClassName(String featureName) {
		String result = "";

		synchronized (perl) {
			if (perl.match("/^(.*)\\.[^\\.]*\\(.*\\)/", featureName)) {
				result = perl.group(1);
			} else if (perl.match("/^(.*)\\.[\\^.]*/", featureName)) {
				result = perl.group(1);
			}
		}
		
		return result;
	}
}
