/*
 *  Copyright (c) 2001-2006, Jean Tessier
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *  
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *  
 *      * Neither the name of Jean Tessier nor the names of his contributors
 *        may be used to endorse or promote products derived from this software
 *        without specific prior written permission.
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

public class TestMonitor extends TestCase {
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    
    private MockVisitor       addVisitor;
    private MockRemoveVisitor removeVisitor;

    private Monitor monitor;

    private Classfile testClassfile;
    
    protected void setUp() throws Exception {
        super.setUp();

        addVisitor    = new MockVisitor();
        removeVisitor = new MockRemoveVisitor();

        monitor = new Monitor(addVisitor, removeVisitor);

        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(TEST_FILENAME);
        testClassfile = loader.getClassfile(TEST_CLASS);
    }

    public void testFileTracking() {
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));

        assertEquals("previous", 0, monitor.previousFiles.size());
        assertEquals("current", 0, monitor.currentFiles.size());
        
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        
        assertEquals("previous", 0, monitor.previousFiles.size());
        assertEquals("current", 1, monitor.currentFiles.size());
        assertTrue("TEST_FILENAME not in current", monitor.currentFiles.contains(TEST_FILENAME));
        
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        
        assertEquals("previous", 0, monitor.previousFiles.size());
        assertEquals("current", 1, monitor.currentFiles.size());
        assertTrue("TEST_FILENAME not in current", monitor.currentFiles.contains(TEST_FILENAME));
        
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 0, monitor.currentFiles.size());
    }

    public void testFileTrackingAcrossSessions() {
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 0, monitor.currentFiles.size());
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 1, monitor.currentFiles.size());
        assertTrue("TEST_FILENAME not in current", monitor.currentFiles.contains(TEST_FILENAME));

        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));

        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 1, monitor.currentFiles.size());
        assertTrue("TEST_FILENAME not in current", monitor.currentFiles.contains(TEST_FILENAME));

        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals("previous", 0, monitor.previousFiles.size());
        assertEquals("current", 1, monitor.currentFiles.size());
        assertTrue("TEST_FILENAME not in current", monitor.currentFiles.contains(TEST_FILENAME));

        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 0, monitor.currentFiles.size());
    }

    public void testFileTrackingWithSkippedFile() {
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 0, monitor.currentFiles.size());
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 1, monitor.currentFiles.size());
        assertTrue("TEST_FILENAME not in current", monitor.currentFiles.contains(TEST_FILENAME));

        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals("previous", 0, monitor.previousFiles.size());
        assertEquals("current", 1, monitor.currentFiles.size());
        assertTrue("TEST_FILENAME not in current", monitor.currentFiles.contains(TEST_FILENAME));

        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 0, monitor.currentFiles.size());
    }

    public void testFileTrackingWithMissingFile() {
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 0, monitor.currentFiles.size());
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 1, monitor.previousFiles.size());
        assertTrue("TEST_FILENAME not in previous", monitor.previousFiles.contains(TEST_FILENAME));
        assertEquals("current", 0, monitor.currentFiles.size());

        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals("previous", 0, monitor.previousFiles.size());
        assertEquals("current", 0, monitor.currentFiles.size());
    }
    
    public void testNewClassfile() {
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));

        assertEquals("added", 1, addVisitor.getVisitedClassfiles().size());
        assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClassfiles().contains(testClassfile));
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

        assertEquals("added", 2, addVisitor.getVisitedClassfiles().size());
        Iterator i = addVisitor.getVisitedClassfiles().iterator();
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

        assertEquals("added", 2, addVisitor.getVisitedClassfiles().size());
        Iterator i = addVisitor.getVisitedClassfiles().iterator();
        assertEquals("added 1", testClassfile, i.next());
        assertEquals("added 2", testClassfile, i.next());
        assertEquals("removed", 1, removeVisitor.getVisitedClasses().size());
        assertTrue("removed missed " + TEST_CLASS, removeVisitor.getVisitedClasses().contains(TEST_CLASS));
    }

    public void testRemoval() {
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

        assertEquals("added", 1, addVisitor.getVisitedClassfiles().size());
        assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClassfiles().contains(testClassfile));
        assertEquals("removed", 1, removeVisitor.getVisitedClasses().size());
        assertTrue("removed missed " + TEST_CLASS, removeVisitor.getVisitedClasses().contains(TEST_CLASS));
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

        assertEquals("added", 1, addVisitor.getVisitedClassfiles().size());
        assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClassfiles().contains(testClassfile));
        assertEquals("removed", 0, removeVisitor.getVisitedClasses().size());
    }

    public void testOpenSession() {
        monitor.setClosedSession(false);
        
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

        assertEquals("added", 1, addVisitor.getVisitedClassfiles().size());
        assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClassfiles().contains(testClassfile));
        assertEquals("removed", 0, removeVisitor.getVisitedClasses().size());
    }

    public void testExtractAgainInOpenSession() {
        monitor.setClosedSession(false);
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        addVisitor.reset();
        removeVisitor.reset();
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        assertEquals("added", 1, addVisitor.getVisitedClassfiles().size());
        assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClassfiles().contains(testClassfile));
        assertEquals("removed", 0, removeVisitor.getVisitedClasses().size());
    }

    public void testReloadAfterClosingSessionSession() {
        monitor.setClosedSession(false);
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        monitor.setClosedSession(true);

        addVisitor.reset();
        removeVisitor.reset();
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        assertEquals("added", 1, addVisitor.getVisitedClassfiles().size());
        assertTrue("added missed " + TEST_CLASS, addVisitor.getVisitedClassfiles().contains(testClassfile));
        assertEquals("removed", 1, removeVisitor.getVisitedClasses().size());
        assertTrue("removed missed " + TEST_CLASS, removeVisitor.getVisitedClasses().contains(TEST_CLASS));
    }
}
