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

package com.jeantessier.dependencyfinder;

import java.util.*;

import com.jeantessier.classreader.*;

public class VerboseListenerBase implements LoadListener {
	private int        class_count     = 0;
	private LinkedList groups          = new LinkedList();
	private Collection visited_files   = new HashSet();
	private String     ratio_indicator = "";
	
	public int ClassCount() {
		return class_count;
	}
	
	protected GroupData CurrentGroup() {
		return (GroupData) groups.getLast();
	}

	protected Collection VisitedFiles() {
		return visited_files;
	}
	
	protected String RatioIndicator() {
		return ratio_indicator;
	}
	
	public void BeginSession(LoadEvent event) {
		// Do nothing
	}
	
	public void BeginGroup(LoadEvent event) {
		groups.add(new GroupData(event.GroupName(), event.Size()));
	}

	public void BeginFile(LoadEvent event) {
		if (CurrentGroup().Size() > 0) {
			int previous_ratio = CurrentGroup().Ratio();
			CurrentGroup().IncrementCount();
			int new_ratio = CurrentGroup().Ratio();
			
			if (previous_ratio != new_ratio) {
				StringBuffer buffer = new StringBuffer(4);

				if (new_ratio < 10) {
					buffer.append(" ");
				}
				if (new_ratio < 100) {
					buffer.append(" ");
				}
				buffer.append(new_ratio).append("%");

				ratio_indicator = buffer.toString();
			}
		}
	}

	public void BeginClassfile(LoadEvent event) {
		// Do nothing
	}

	public void EndClassfile(LoadEvent event) {
		visited_files.add(event.Filename());
		class_count++;
	}

	public void EndFile(LoadEvent event) {
		// Do nothing
	}

	public void EndGroup(LoadEvent event) {
		visited_files.add(event.GroupName());
		groups.removeLast();
	}

	public void EndSession(LoadEvent event) {
		// Do nothing
	}
}
