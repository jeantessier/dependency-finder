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

package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.log4j.*;

public class TestClassfileLoader extends TestCase implements LoadListener {
	public static final String TEST_DIR = "tests" + File.separator + "JarJarDiff";

	private LinkedList begin_session;
	private LinkedList begin_group;
	private LinkedList begin_file;
	private LinkedList begin_classfile;
	private LinkedList end_classfile;
	private LinkedList end_file;
	private LinkedList end_group;
	private LinkedList end_session;
	
	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).info("Starting test: " + getName());

		begin_session   = new LinkedList();
		begin_group     = new LinkedList();
		begin_file      = new LinkedList();
		begin_classfile = new LinkedList();
		end_classfile   = new LinkedList();
		end_file        = new LinkedList();
		end_group       = new LinkedList();
		end_session     = new LinkedList();
	}

	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).info("End of " + getName());
	}

	protected LinkedList BeginSession() {
		return begin_session;
	}

	protected LinkedList BeginGroup() {
		return begin_group;
	}

	protected LinkedList BeginFile() {
		return begin_file;
	}

	protected LinkedList BeginClassfile() {
		return begin_classfile;
	}

	protected LinkedList EndClassfile() {
		return end_classfile;
	}

	protected LinkedList EndFile() {
		return end_file;
	}

	protected LinkedList EndGroup() {
		return end_group;
	}

	protected LinkedList EndSession() {
		return end_session;
	}
	
	public void beginSession(LoadEvent event) {
		BeginSession().add(event);
	}
	
	public void beginGroup(LoadEvent event) {
		BeginGroup().add(event);
	}
	
	public void beginFile(LoadEvent event) {
		BeginFile().add(event);
	}
	
	public void beginClassfile(LoadEvent event) {
		BeginClassfile().add(event);
	}
	
	public void endClassfile(LoadEvent event) {
		EndClassfile().add(event);
	}
	
	public void endFile(LoadEvent event) {
		EndFile().add(event);
	}
	
	public void endGroup(LoadEvent event) {
		EndGroup().add(event);
	}
	
	public void endSession(LoadEvent event) {
		EndSession().add(event);
	}
}
