/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

public class TestMonitor extends TestCase {
	public static final String TEST_CLASS = "test";
	public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
	
	private MockVisitor addVisitor;
	private MockVisitor removeVisitor;

	private LoadListener monitor;

	private Classfile testClassfile;
	
	protected void setUp() throws Exception {
		super.setUp();

		addVisitor = new MockVisitor();
		removeVisitor = new MockVisitor();

		monitor = new Monitor(addVisitor, removeVisitor);

		ClassfileLoader loader = new AggregatingClassfileLoader();
		loader.load(TEST_FILENAME);
		testClassfile = loader.getClassfile(TEST_CLASS);
	}

	public void testNewClassfile() {
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));

		assertEquals("added", 1, addVisitor.getVisitedClasses().size());
		assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClasses().contains(testClassfile));
		assertEquals("removed", 0, removeVisitor.getVisitedClasses().size());
	}

	public void testRepeatInSession() {
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
		monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));

		assertEquals("added", 2, addVisitor.getVisitedClasses().size());
		Iterator i = addVisitor.getVisitedClasses().iterator();
		assertEquals("added 1", testClassfile, i.next());
		assertEquals("added 2", testClassfile, i.next());
		assertEquals("removed", 0, removeVisitor.getVisitedClasses().size());
	}

	public void testRepeatAcrossSessions() {
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
		monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endGroup(new LoadEvent(this, null, null, null));
		monitor.endSession(new LoadEvent(this, null, null, null));
		
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));

		assertEquals("added", 2, addVisitor.getVisitedClasses().size());
		Iterator i = addVisitor.getVisitedClasses().iterator();
		assertEquals("added 1", testClassfile, i.next());
		assertEquals("added 2", testClassfile, i.next());
		assertEquals("removed", 1, removeVisitor.getVisitedClasses().size());
		assertTrue("removed missed " + TEST_CLASS, removeVisitor.getVisitedClasses().contains(testClassfile));
	}

	public void testRemoved() {
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
		monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endGroup(new LoadEvent(this, null, null, null));
		monitor.endSession(new LoadEvent(this, null, null, null));
		
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.endGroup(new LoadEvent(this, null, null, null));
		monitor.endSession(new LoadEvent(this, null, null, null));

		assertEquals("added", 1, addVisitor.getVisitedClasses().size());
		assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClasses().contains(testClassfile));
		assertEquals("removed", 1, removeVisitor.getVisitedClasses().size());
		assertTrue("removed missed " + TEST_CLASS, removeVisitor.getVisitedClasses().contains(testClassfile));
	}

	public void testSkip() {
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
		monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endGroup(new LoadEvent(this, null, null, null));
		monitor.endSession(new LoadEvent(this, null, null, null));
		
		monitor.beginSession(new LoadEvent(this, null, null, null));
		monitor.beginGroup(new LoadEvent(this, null, null, null));
		monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
		monitor.endGroup(new LoadEvent(this, null, null, null));
		monitor.endSession(new LoadEvent(this, null, null, null));

		assertEquals("added", 1, addVisitor.getVisitedClasses().size());
		assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClasses().contains(testClassfile));
		assertEquals("removed", 0, removeVisitor.getVisitedClasses().size());
	}
}
