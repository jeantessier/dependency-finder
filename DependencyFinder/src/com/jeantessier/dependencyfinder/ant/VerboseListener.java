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

package com.jeantessier.dependencyfinder.ant;

import org.apache.tools.ant.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;
import com.jeantessier.dependencyfinder.*;
import com.jeantessier.metrics.*;

public class VerboseListener extends VerboseListenerBase implements DependencyListener, MetricsListener {
	private Task task;

	public VerboseListener(Task task) {
		this.task = task;
	}
	
	public void BeginSession(LoadEvent event) {
		super.BeginSession(event);
		
		task.log("Searching for classes ...", Project.MSG_VERBOSE);
	}
	
	public void BeginGroup(LoadEvent event) {
		super.BeginGroup(event);

		switch (CurrentGroup().Size()) {
			case -1:
				task.log("Searching " + CurrentGroup().Name() + " ...", Project.MSG_VERBOSE);
				break;

			case 0:
			case 1:
				task.log("Searching " + CurrentGroup().Name() + " (" + CurrentGroup().Size() + " file) ...", Project.MSG_VERBOSE);
				break;

			default:
				task.log("Searching " + CurrentGroup().Name() + " (" + CurrentGroup().Size() + " files) ...", Project.MSG_VERBOSE);
				break;
		}
	}

	public void EndClassfile(LoadEvent event) {
	    super.EndClassfile(event);

		task.log("Loading " + event.Classfile() + " ...", Project.MSG_VERBOSE);
	}
	
	public void EndFile(LoadEvent event) {
		super.EndFile(event);

		if (!VisitedFiles().contains(event.Filename())) {
			task.log("Skipping " + event.Filename() + " ...", Project.MSG_VERBOSE);
		}
	}
	
	public void BeginSession(DependencyEvent event) {
		// Do nothing
	}

	public void BeginClass(DependencyEvent event) {
		task.log("Getting dependencies from " + event.Classname() + " ...", Project.MSG_VERBOSE);
	}
	
	public void Dependency(DependencyEvent event) {
		// Do nothing
	}
	
	public void EndClass(DependencyEvent event) {
		// Do nothing
	}
	
	public void EndSession(DependencyEvent event) {
		// Do nothing
	}

	public void BeginSession(MetricsEvent event) {
		// Do nothing
	}

	public void BeginClass(MetricsEvent event) {
		task.log("Computing metrics for " + event.Classfile() + " ...", Project.MSG_VERBOSE);
	}
	
	public void BeginMethod(MetricsEvent event) {
		// Do nothing
	}
	
	public void EndMethod(MetricsEvent event) {
		// Do nothing
	}
	
	public void EndClass(MetricsEvent event) {
		// Do nothing
	}
	
	public void EndSession(MetricsEvent event) {
		// Do nothing
	}
}
