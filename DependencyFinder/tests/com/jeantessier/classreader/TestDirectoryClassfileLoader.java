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

package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

public class TestDirectoryClassfileLoader extends TestClassfileLoader {
	public static final String TEST_CLASS          = "test";
	public static final String TEST_FILENAME       = "classes" + File.separator + "test.class";
	public static final String BOGUS_TEST_FILENAME = "classes" + File.separator + "bogus" + File.separator + "test.class";
	public static final String TEST_DIRNAME        = "classes" + File.separator + "testpackage";
	public static final String OTHER_DIRNAME       = "classes" + File.separator + "otherpackage";
	
	private ClassfileLoader loader;

	protected void setUp() throws Exception {
		super.setUp();

		ClassfileLoader event_source = new TransientClassfileLoader();
		event_source.addLoadListener(this);
		loader = new DirectoryClassfileLoader(event_source);
	}

	public void testLoadClassFile() throws IOException {
		loader.Load(TEST_FILENAME);

		assertEquals("Begin Session",   0, BeginSession().size());
		assertEquals("Begin Group",     1, BeginGroup().size());
		assertEquals("Begin File",      1, BeginFile().size());
		assertEquals("Begin Classfile", 1, BeginClassfile().size());
		assertEquals("End Classfile",   1, EndClassfile().size());
		assertEquals("End File",        1, EndFile().size());
		assertEquals("End Group",       1, EndGroup().size());
		assertEquals("End Session",     0, EndSession().size());

		assertEquals(TEST_FILENAME, ((LoadEvent) EndClassfile().getLast()).GroupName());
		assertNotNull("Classfile", ((LoadEvent) EndClassfile().getLast()).Classfile());
	}	

	public void testLoadClassInputStream() throws IOException {
		loader.Load(TEST_FILENAME, new FileInputStream(TEST_FILENAME));

		assertEquals("Begin Session",   0, BeginSession().size());
		assertEquals("Begin Group",     0, BeginGroup().size());
		assertEquals("Begin File",      0, BeginFile().size());
		assertEquals("Begin Classfile", 0, BeginClassfile().size());
		assertEquals("End Classfile",   0, EndClassfile().size());
		assertEquals("End File",        0, EndFile().size());
		assertEquals("End Group",       0, EndGroup().size());
		assertEquals("End Session",     0, EndSession().size());
	}	

	public void testLoadBogusFile() throws IOException {
		loader.Load(BOGUS_TEST_FILENAME);

		assertEquals("Begin Session",   0, BeginSession().size());
		assertEquals("Begin Group",     1, BeginGroup().size());
		assertEquals("Begin File",      0, BeginFile().size());
		assertEquals("Begin Classfile", 0, BeginClassfile().size());
		assertEquals("End Classfile",   0, EndClassfile().size());
		assertEquals("End File",        0, EndFile().size());
		assertEquals("End Group",       1, EndGroup().size());
		assertEquals("End Session",     0, EndSession().size());
	}	

	public void testLoadBogusInputStream() throws IOException {
		loader.Load(BOGUS_TEST_FILENAME, new FileInputStream(TEST_FILENAME));

		assertEquals("Begin Session",   0, BeginSession().size());
		assertEquals("Begin Group",     0, BeginGroup().size());
		assertEquals("Begin File",      0, BeginFile().size());
		assertEquals("Begin Classfile", 0, BeginClassfile().size());
		assertEquals("End Classfile",   0, EndClassfile().size());
		assertEquals("End File",        0, EndFile().size());
		assertEquals("End Group",       0, EndGroup().size());
		assertEquals("End Session",     0, EndSession().size());
	}	

	public void testLoadDirectory() throws IOException {
		loader.Load(TEST_DIRNAME);

		assertEquals("Begin Session",   0, BeginSession().size());
		assertEquals("Begin Group",     1, BeginGroup().size());
		assertEquals("Begin File",      7, BeginFile().size());
		assertEquals("Begin Classfile", 6, BeginClassfile().size());
		assertEquals("End Classfile",   6, EndClassfile().size());
		assertEquals("End File",        7, EndFile().size());
		assertEquals("End Group",       1, EndGroup().size());
		assertEquals("End Session",     0, EndSession().size());
	}	
	
	public void testMultipleDirectories() throws IOException {
		loader.Load(TEST_DIRNAME);
		loader.Load(OTHER_DIRNAME);

		assertEquals("Begin Session",    0, BeginSession().size());
		assertEquals("Begin Group",      2, BeginGroup().size());
		assertEquals("Begin File",      11, BeginFile().size());
		assertEquals("Begin Classfile",  9, BeginClassfile().size());
		assertEquals("End Classfile",    9, EndClassfile().size());
		assertEquals("End File",        11, EndFile().size());
		assertEquals("End Group",        2, EndGroup().size());
		assertEquals("End Session",      0, EndSession().size());
	}	
}
