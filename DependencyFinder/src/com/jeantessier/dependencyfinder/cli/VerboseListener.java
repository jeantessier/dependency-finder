/*
 *  Copyright (c) 2001-2003, Jean Tessier
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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.metrics.*;

public class VerboseListener extends PrintWriter implements LoadListener, DependencyListener, MetricsListener {
	private PrintWriter writer = new NullPrintWriter();

	public VerboseListener() {
		super(new NullPrintWriter());
	}
	
	public PrintWriter Writer() {
		return writer;
	}

	public void Writer(OutputStream stream) {
		Writer(new PrintWriter(stream));
	}

	public void Writer(Writer writer) {
		Writer(new PrintWriter(writer));
	}

	public void Writer(PrintWriter writer) {
		this.writer = writer;
	}

	public void flush() {
		Writer().flush();
	}

	public void close() {
		Writer().close();
	}

	public boolean checkError() {
		return Writer().checkError();
	}

	protected void setError() {
		// Do nothing
	}

	public void write(int c) {
		Writer().write(c);
	}

	public void write(char[] buf, int off, int len) {
		Writer().write(buf, off, len);
	}

	public void write(char[] buf) {
		Writer().write(buf);
	}

	public void write(String s, int off, int len) {
		Writer().write(s, off, len);
	}

	public void write(String s) {
		Writer().write(s);
	}

	public void print(boolean b) {
		Writer().print(b);
	}

	public void print(char c) {
		Writer().print(c);
	}

	public void print(int i) {
		Writer().print(i);
	}

	public void print(long l) {
		Writer().print(l);
	}

	public void print(float f) {
		Writer().print(f);
	}

	public void print(double d) {
		Writer().print(d);
	}

	public void print(char[] s) {
		Writer().print(s);
	}

	public void print(String s) {
		Writer().print(s);
	}

	public void print(Object obj) {
		Writer().print(obj);
	}

	public void println() {
		Writer().println();
	}

	public void println(boolean x) {
		Writer().println(x);
	}

	public void println(char x) {
		Writer().println(x);
	}

	public void println(int x) {
		Writer().println(x);
	}

	public void println(long x) {
		Writer().println(x);
	}

	public void println(float x) {
		Writer().println(x);
	}

	public void println(double x) {
		Writer().println(x);
	}

	public void println(char[] x) {
		Writer().println(x);
	}

	public void println(String x) {
		Writer().println(x);
	}

	public void println(Object x) {
		Writer().println(x);
	}
	
	public void BeginSession(LoadEvent event) {
		print("Searching for classes ...");
		println();
		flush();
	}
	
	public void BeginGroup(LoadEvent event) {
		print("Searching ");
		print(event.Filename());
		print(" ...");
		println();
		flush();
	}
	
	public void BeginClassfile(LoadEvent event) {
		print("Loading ");
		print(event.Filename());

		if (event.Element() != null) {
			print(" >> ");
			print(event.Element());
		}

		print(" ...");
		println();
		flush();
	}
	
	public void EndClassfile(LoadEvent event) {
		// Do nothing
	}
	
	public void EndGroup(LoadEvent event) {
		// Do nothing
	}
	
	public void EndSession(LoadEvent event) {
		// Do nothing
	}

	public void StartClass(DependencyEvent event) {
		print("Getting dependencies from ");
		print(event.Classname());
		print(" ...");
		println();
		flush();
	}
	
	public void StopClass(DependencyEvent event) {
		// Do nothing
	}
	
	public void Dependency(DependencyEvent event) {
		// Do nothing
	}

	public void StartClass(MetricsEvent event) {
		print("Computing metrics for ");
		print(event.Classfile());
		print(" ...");
		println();
		flush();
	}
	
	public void StartMethod(MetricsEvent event) {
		// Do nothing
	}
	
	public void StopMethod(MetricsEvent event) {
		// Do nothing
	}
	
	public void StopClass(MetricsEvent event) {
		// Do nothing
	}
}
