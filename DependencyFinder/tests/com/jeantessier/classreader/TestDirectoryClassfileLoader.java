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

package com.jeantessier.classreader;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.apache.log4j.*;

public class TestDirectoryClassfileLoader extends TestCase implements LoadListener {
	public static final String TEST_CLASS          = "test";
	public static final String TEST_FILENAME       = "classes" + File.separator + "test.class";
	public static final String BOGUS_TEST_FILENAME = "classes" + File.separator + "bogus" + File.separator + "test.class";
	public static final String TEST_DIRNAME        = "classes" + File.separator + "testpackage";
	public static final String OTHER_DIRNAME       = "classes" + File.separator + "otherpackage";

	private LinkedList begin_session;
	private LinkedList begin_group;
	private LinkedList begin_classfile;
	private LinkedList end_classfile;
	private LinkedList end_group;
	private LinkedList end_session;

	protected void setUp() throws Exception {
		Logger.getLogger(getClass()).info("Starting test: " + getName());

		begin_session   = new LinkedList();
		begin_group     = new LinkedList();
		begin_classfile = new LinkedList();
		end_classfile   = new LinkedList();
		end_group       = new LinkedList();
		end_session     = new LinkedList();
	}

	protected void tearDown() throws Exception {
		Logger.getLogger(getClass()).info("End of " + getName());
	}
	
	public void testCreateWithAggregatingClassfileLoader() {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());

		assertEquals("Different number of class names",
					 0,
					 loader.Classnames().size());
		assertNull(TEST_CLASS + " should have been null",
				   loader.Classfile(TEST_CLASS));
	}
	
	public void testLoadWithAggregatingClassfileLoader() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());

		assertEquals("Different number of class names",
					 0,
					 loader.Classnames().size());
		assertNull(TEST_CLASS + " should have been null",
				   loader.Classfile(TEST_CLASS));

		loader.Load(Collections.singleton(TEST_FILENAME));
		
		assertEquals("Different number of class names",
					 1,
					 loader.Classnames().size());
		assertTrue("Missing class name \"" + TEST_CLASS + "\"",
				   loader.Classnames().contains(TEST_CLASS));
		assertNotNull(TEST_CLASS + " should not have been null",
					  loader.Classfile(TEST_CLASS));
	}
	
	public void testEventsWithAggregatingClassfileLoader() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());
		loader.addLoadListener(this);

		loader.Load(Collections.singleton(TEST_FILENAME));

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     1, begin_group.size());
		assertEquals("Begin Classfile", 1, begin_classfile.size());
		assertEquals("End Classfile",   1, end_classfile.size());
		assertEquals("End Group",       1, end_group.size());
		assertEquals("End Session",     1, end_session.size());

		assertEquals(TEST_FILENAME, ((LoadEvent) end_classfile.getLast()).Filename());
		assertNotNull("Classfile", ((LoadEvent) end_classfile.getLast()).Classfile());
	}	
	
	public void testMultipleEventsWithAggregatingClassfileLoader() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());
		loader.addLoadListener(this);

		Collection dirs = new ArrayList(2);
		dirs.add(TEST_DIRNAME);
		dirs.add(OTHER_DIRNAME);
		loader.Load(dirs);

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     2, begin_group.size());
		assertEquals("Begin Classfile", 6, begin_classfile.size());
		assertEquals("End Classfile",   6, end_classfile.size());
		assertEquals("End Group",       2, end_group.size());
		assertEquals("End Session",     1, end_session.size());
	}	
	
	public void testEventsWithAggregatingClassfileLoaderWithFailures() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());
		loader.addLoadListener(this);

		loader.Load(Collections.singleton(BOGUS_TEST_FILENAME));

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     1, begin_group.size());
		assertEquals("Begin Classfile", 1, begin_classfile.size());
		assertEquals("End Classfile",   1, end_classfile.size());
		assertEquals("End Group",       1, end_group.size());
		assertEquals("End Session",     1, end_session.size());

		assertEquals(BOGUS_TEST_FILENAME, ((LoadEvent) end_classfile.getLast()).Filename());
		assertNull("Classfile", ((LoadEvent) end_classfile.getLast()).Classfile());
	}	
	
	public void testEventsWithAggregatingClassfileLoaderWithNothing() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new AggregatingClassfileLoader());
		loader.addLoadListener(this);

		loader.Load(Collections.EMPTY_SET);

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     0, begin_group.size());
		assertEquals("Begin Classfile", 0, begin_classfile.size());
		assertEquals("End Classfile",   0, end_classfile.size());
		assertEquals("End Group",       0, end_group.size());
		assertEquals("End Session",     1, end_session.size());
	}	

	public void testCreateWithTransientClassfileLoader() {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new TransientClassfileLoader());

		assertEquals("Different number of class names",
					 0,
					 loader.Classnames().size());
		assertNull(TEST_CLASS + " should have been null",
				   loader.Classfile(TEST_CLASS));
	}
	
	public void testLoadWithTransientClassfileLoader() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new TransientClassfileLoader());

		assertEquals("Different number of class names",
					 0,
					 loader.Classnames().size());
		assertNull(TEST_CLASS + " should have been null",
				   loader.Classfile(TEST_CLASS));

		loader.Load(Collections.singleton(TEST_FILENAME));
		
		assertEquals("Different number of class names",
					 0,
					 loader.Classnames().size());
		assertNull(TEST_CLASS + " should not have remained null",
				   loader.Classfile(TEST_CLASS));
	}
	
	public void testEventsWithTransientClassfileLoader() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new TransientClassfileLoader());
		loader.addLoadListener(this);

		loader.Load(Collections.singleton(TEST_FILENAME));

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     1, begin_group.size());
		assertEquals("Begin Classfile", 1, begin_classfile.size());
		assertEquals("End Classfile",   1, end_classfile.size());
		assertEquals("End Group",       1, end_group.size());
		assertEquals("End Session",     1, end_session.size());

		assertEquals(TEST_FILENAME, ((LoadEvent) end_classfile.getLast()).Filename());
		assertNotNull("Classfile", ((LoadEvent) end_classfile.getLast()).Classfile());
	}	
	
	public void testMultipleEventsWithTransientClassfileLoader() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new TransientClassfileLoader());
		loader.addLoadListener(this);

		Collection dirs = new ArrayList(2);
		dirs.add(TEST_DIRNAME);
		dirs.add(OTHER_DIRNAME);
		loader.Load(dirs);

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     2, begin_group.size());
		assertEquals("Begin Classfile", 6, begin_classfile.size());
		assertEquals("End Classfile",   6, end_classfile.size());
		assertEquals("End Group",       2, end_group.size());
		assertEquals("End Session",     1, end_session.size());
	}	
	
	public void testEventsWithTransientClassfileLoaderWithFailures() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new TransientClassfileLoader());
		loader.addLoadListener(this);

		loader.Load(Collections.singleton(BOGUS_TEST_FILENAME));

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     1, begin_group.size());
		assertEquals("Begin Classfile", 1, begin_classfile.size());
		assertEquals("End Classfile",   1, end_classfile.size());
		assertEquals("End Group",       1, end_group.size());
		assertEquals("End Session",     1, end_session.size());

		assertEquals(BOGUS_TEST_FILENAME, ((LoadEvent) end_classfile.getLast()).Filename());
		assertNull("Classfile", ((LoadEvent) end_classfile.getLast()).Classfile());
	}	
	
	public void testEventsWithTransientClassfileLoaderWithNothing() throws IOException {
		DirectoryClassfileLoader loader = new DirectoryClassfileLoader(new TransientClassfileLoader());
		loader.addLoadListener(this);

		loader.Load(Collections.EMPTY_SET);

		assertEquals("Begin Session",   1, begin_session.size());
		assertEquals("Begin Group",     0, begin_group.size());
		assertEquals("Begin Classfile", 0, begin_classfile.size());
		assertEquals("End Classfile",   0, end_classfile.size());
		assertEquals("End Group",       0, end_group.size());
		assertEquals("End Session",     1, end_session.size());
	}	

	public void BeginSession(LoadEvent event) {
		begin_session.add(event);
	}
	
	public void BeginGroup(LoadEvent event) {
		begin_group.add(event);
	}
	
	public void BeginClassfile(LoadEvent event) {
		begin_classfile.add(event);
	}
	
	public void EndClassfile(LoadEvent event) {
		end_classfile.add(event);
	}
	
	public void EndGroup(LoadEvent event) {
		end_group.add(event);
	}
	
	public void EndSession(LoadEvent event) {
		end_session.add(event);
	}
}
