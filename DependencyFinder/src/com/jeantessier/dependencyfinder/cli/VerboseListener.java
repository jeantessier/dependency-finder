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

import java.io.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.metrics.*;

public class VerboseListener extends VerboseListenerBase implements DependencyListener, MetricsListener {
	private PrintWriter writer = new NullPrintWriter();
	
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

	public void Close() {
		Writer().close();
	}
	
	public void Print(String x) {
		Writer().println(x);
	}
	
	public void BeginSession(LoadEvent event) {
		super.BeginSession(event);
		
		Writer().print("Searching for classes ...");
		Writer().println();
		Writer().flush();
	}
	
	public void BeginGroup(LoadEvent event) {
		super.BeginGroup(event);
		
		Writer().print("Searching ");
		Writer().print(event.GroupName());

		switch (CurrentGroup().Size()) {
			case -1:
				break;

			case 0:
			case 1:
				Writer().print(" (");
				Writer().print(CurrentGroup().Size());
				Writer().print(" file)");
				break;

			default:
				Writer().print(" (");
				Writer().print(CurrentGroup().Size());
				Writer().print(" files)");
				break;
		}
		
		Writer().print(" ...");
		Writer().println();
		Writer().flush();
	}

	public void EndClassfile(LoadEvent event) {
	    super.EndClassfile(event);

		Writer().print("Loading ");
		Writer().print(event.Classfile());
		Writer().print(" ...");
		Writer().println();
		Writer().flush();
	}
	
	public void EndFile(LoadEvent event) {
		super.EndFile(event);
		
		if (!VisitedFiles().contains(event.Filename())) {
			Writer().print("Skipping ");
			Writer().print(event.Filename());
			Writer().print(" ...");
			Writer().println();
			Writer().flush();
		}
	}

	public void StartClass(DependencyEvent event) {
		Writer().print("Getting dependencies from ");
		Writer().print(event.Classname());
		Writer().print(" ...");
		Writer().println();
		Writer().flush();
	}
	
	public void StopClass(DependencyEvent event) {
		// Do nothing
	}
	
	public void Dependency(DependencyEvent event) {
		// Do nothing
	}

	public void StartClass(MetricsEvent event) {
		Writer().print("Computing metrics for ");
		Writer().print(event.Classfile());
		Writer().print(" ...");
		Writer().println();
		Writer().flush();
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
