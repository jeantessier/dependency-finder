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

public class TestClassfileLoaderPermissiveDispatcher extends TestClassfileLoader {
	public static final String TEST_FILENAME = "classes" + File.separator + "test.class";

	private ClassfileLoader loader;

	protected void setUp() throws Exception {
		super.setUp();
		
		loader = new TransientClassfileLoader(new PermissiveDispatcher());
		loader.addLoadListener(this);
	}

	public void testOneFile() {
		String filename = TEST_FILENAME;
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.load(Collections.singleton(filename));

		assertEquals("Begin Session",   1, getBeginSessionEvents().size());
		assertEquals("Begin Group",     1, getBeginGroupEvents().size());
		assertEquals("Begin File",      1, getBeginFileEvents().size());
		assertEquals("Begin Classfile", 1, getBeginClassfileEvents().size());
		assertEquals("End Classfile",   1, getEndClassfileEvents().size());
		assertEquals("End File",        1, getEndFileEvents().size());
		assertEquals("End Group",       1, getEndGroupEvents().size());
		assertEquals("End Session",     1, getEndSessionEvents().size());

		assertEquals("Group size", 1, ((LoadEvent) getBeginGroupEvents().getFirst()).getSize());
	}

	public void testOneLevelZip() {
		String filename = TEST_DIR + File.separator + "onelevel.zip";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.load(Collections.singleton(filename));

		assertEquals("Begin Session",    1, getBeginSessionEvents().size());
		assertEquals("Begin Group",      1, getBeginGroupEvents().size());
		assertEquals("Begin File",      38, getBeginFileEvents().size());
		assertEquals("Begin Classfile", 17, getBeginClassfileEvents().size());
		assertEquals("End Classfile",   17, getEndClassfileEvents().size());
		assertEquals("End File",        38, getEndFileEvents().size());
		assertEquals("End Group",        1, getEndGroupEvents().size());
		assertEquals("End Session",      1, getEndSessionEvents().size());

		assertEquals("Group size", 38, ((LoadEvent) getBeginGroupEvents().getFirst()).getSize());
	}

	public void testOneLevelJar() {
		String filename = TEST_DIR + File.separator + "onelevel.jar";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.load(Collections.singleton(filename));

		assertEquals("Begin Session",    1, getBeginSessionEvents().size());
		assertEquals("Begin Group",      1, getBeginGroupEvents().size());
		assertEquals("Begin File",      40, getBeginFileEvents().size());
		assertEquals("Begin Classfile", 17, getBeginClassfileEvents().size());
		assertEquals("End Classfile",   17, getEndClassfileEvents().size());
		assertEquals("End File",        40, getEndFileEvents().size());
		assertEquals("End Group",        1, getEndGroupEvents().size());
		assertEquals("End Session",      1, getEndSessionEvents().size());

		assertEquals("Group size", 40, ((LoadEvent) getBeginGroupEvents().getFirst()).getSize());
	}
	
	public void testOneLevelMiscellaneous() {
		String filename = TEST_DIR + File.separator + "onelevel.mis";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.load(Collections.singleton(filename));

		assertEquals("Begin Session",    1, getBeginSessionEvents().size());
		assertEquals("Begin Group",      1, getBeginGroupEvents().size());
		assertEquals("Begin File",      38, getBeginFileEvents().size());
		assertEquals("Begin Classfile", 17, getBeginClassfileEvents().size());
		assertEquals("End Classfile",   17, getEndClassfileEvents().size());
		assertEquals("End File",        38, getEndFileEvents().size());
		assertEquals("End Group",        1, getEndGroupEvents().size());
		assertEquals("End Session",      1, getEndSessionEvents().size());
	}

	public void testTwoLevelZip() {
		String filename = TEST_DIR + File.separator + "twolevel.zip";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.load(Collections.singleton(filename));

		assertEquals("Begin Session",    1, getBeginSessionEvents().size());
		assertEquals("Begin Group",      2, getBeginGroupEvents().size());
		assertEquals("Begin File",      39, getBeginFileEvents().size());
		assertEquals("Begin Classfile", 17, getBeginClassfileEvents().size());
		assertEquals("End Classfile",   17, getEndClassfileEvents().size());
		assertEquals("End File",        39, getEndFileEvents().size());
		assertEquals("End Group",        2, getEndGroupEvents().size());
		assertEquals("End Session",      1, getEndSessionEvents().size());
	}

	public void testTwoLevelJar() {
		String filename = TEST_DIR + File.separator + "twolevel.jar";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.load(Collections.singleton(filename));

		assertEquals("Begin Session",    1, getBeginSessionEvents().size());
		assertEquals("Begin Group",      2, getBeginGroupEvents().size());
		assertEquals("Begin File",      41, getBeginFileEvents().size());
		assertEquals("Begin Classfile", 17, getBeginClassfileEvents().size());
		assertEquals("End Classfile",   17, getEndClassfileEvents().size());
		assertEquals("End File",        41, getEndFileEvents().size());
		assertEquals("End Group",        2, getEndGroupEvents().size());
		assertEquals("End Session",      1, getEndSessionEvents().size());
	}
	
	public void testTwoLevelMiscellaneous() {
		String filename = TEST_DIR + File.separator + "twolevel.mis";
		assertTrue(filename + " missing", new File(filename).exists());
		
		loader.load(Collections.singleton(filename));

		assertEquals("Begin Session",    1, getBeginSessionEvents().size());
		assertEquals("Begin Group",      2, getBeginGroupEvents().size());
		assertEquals("Begin File",      39, getBeginFileEvents().size());
		assertEquals("Begin Classfile", 17, getBeginClassfileEvents().size());
		assertEquals("End Classfile",   17, getEndClassfileEvents().size());
		assertEquals("End File",        39, getEndFileEvents().size());
		assertEquals("End Group",        2, getEndGroupEvents().size());
		assertEquals("End Session",      1, getEndSessionEvents().size());
	}
}
