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

import junit.framework.*;

public class TestZipClassfileLoader extends TestClassfileLoader {
	private ClassfileLoader loader;
	
	protected void setUp() throws Exception {
		super.setUp();

		ClassfileLoader event_source = new TransientClassfileLoader();
		event_source.addLoadListener(this);
		loader = new ZipClassfileLoader(event_source);
	}

	public void testLoadFile() throws IOException {
		String filename = TEST_DIR + File.separator + "onelevel.zip";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.Load(filename);

		assertEquals("Begin Session",    0, BeginSession().size());
		assertEquals("Begin Group",      1, BeginGroup().size());
		assertEquals("Begin File",      38, BeginFile().size());
		assertEquals("Begin Classfile", 17, BeginClassfile().size());
		assertEquals("End Classfile",   17, EndClassfile().size());
		assertEquals("End File",        38, EndFile().size());
		assertEquals("End Group",        1, EndGroup().size());
		assertEquals("End Session",      0, EndSession().size());

		assertEquals("Group size", 38, ((LoadEvent) BeginGroup().getFirst()).Size());
	}

	public void testLoadFileWrongName() throws IOException {
		String filename = TEST_DIR + File.separator + "onelevel.mis";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.Load(filename);

		assertEquals("Begin Session",    0, BeginSession().size());
		assertEquals("Begin Group",      1, BeginGroup().size());
		assertEquals("Begin File",      38, BeginFile().size());
		assertEquals("Begin Classfile", 17, BeginClassfile().size());
		assertEquals("End Classfile",   17, EndClassfile().size());
		assertEquals("End File",        38, EndFile().size());
		assertEquals("End Group",        1, EndGroup().size());
		assertEquals("End Session",      0, EndSession().size());

		assertEquals("Group size", 38, ((LoadEvent) BeginGroup().getFirst()).Size());
	}

	public void testLoadWrongFile() throws IOException {
		String filename = TEST_DIR + File.separator + "old" + File.separator + "ModifiedPackage" + File.separator + "ModifiedClass.class";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.Load(filename);

		assertEquals("Begin Session",   0, BeginSession().size());
		assertEquals("Begin Group",     0, BeginGroup().size());
		assertEquals("Begin File",      0, BeginFile().size());
		assertEquals("Begin Classfile", 0, BeginClassfile().size());
		assertEquals("End Classfile",   0, EndClassfile().size());
		assertEquals("End File",        0, EndFile().size());
		assertEquals("End Group",       0, EndGroup().size());
		assertEquals("End Session",     0, EndSession().size());
	}

	public void testLoadInputStream() throws IOException {
		String filename = TEST_DIR + File.separator + "onelevel.zip";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.Load(filename, new FileInputStream(filename));

		assertEquals("Begin Session",    0, BeginSession().size());
		assertEquals("Begin Group",      1, BeginGroup().size());
		assertEquals("Begin File",      38, BeginFile().size());
		assertEquals("Begin Classfile", 17, BeginClassfile().size());
		assertEquals("End Classfile",   17, EndClassfile().size());
		assertEquals("End File",        38, EndFile().size());
		assertEquals("End Group",        1, EndGroup().size());
		assertEquals("End Session",      0, EndSession().size());

		assertEquals("Group size", -1, ((LoadEvent) BeginGroup().getFirst()).Size());
	}

	public void testLoadInputStreamWrongName() throws IOException {
		String filename = TEST_DIR + File.separator + "onelevel.mis";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.Load(filename, new FileInputStream(filename));

		assertEquals("Begin Session",    0, BeginSession().size());
		assertEquals("Begin Group",      1, BeginGroup().size());
		assertEquals("Begin File",      38, BeginFile().size());
		assertEquals("Begin Classfile", 17, BeginClassfile().size());
		assertEquals("End Classfile",   17, EndClassfile().size());
		assertEquals("End File",        38, EndFile().size());
		assertEquals("End Group",        1, EndGroup().size());
		assertEquals("End Session",      0, EndSession().size());

		assertEquals("Group size", -1, ((LoadEvent) BeginGroup().getFirst()).Size());
	}

	public void testLoadWrongInputStream() throws IOException {
		String filename = TEST_DIR + File.separator + "old" + File.separator + "ModifiedPackage" + File.separator + "ModifiedClass.class";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.Load(filename, new FileInputStream(filename));

		assertEquals("Begin Session",   0, BeginSession().size());
		assertEquals("Begin Group",     1, BeginGroup().size());
		assertEquals("Begin File",      0, BeginFile().size());
		assertEquals("Begin Classfile", 0, BeginClassfile().size());
		assertEquals("End Classfile",   0, EndClassfile().size());
		assertEquals("End File",        0, EndFile().size());
		assertEquals("End Group",       1, EndGroup().size());
		assertEquals("End Session",     0, EndSession().size());
	}
}
