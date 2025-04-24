/*
 *  Copyright (c) 2001-2025, Jean Tessier
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

import java.nio.file.*;

import org.jmock.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestMonitor {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    
    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();
    
    private final Visitor addVisitor = context.mock(Visitor.class);
    private final RemoveVisitor removeVisitor = context.mock(RemoveVisitor.class);

    private final Monitor monitor = new Monitor(addVisitor, removeVisitor);

    private Classfile testClassfile;
    
    @BeforeEach
    void setUp() throws Exception {
        ClassfileLoader loader = new AggregatingClassfileLoader();
        loader.load(TEST_FILENAME);
        testClassfile = loader.getClassfile(TEST_CLASS);
    }

    @Test
    void testFileTracking() {
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));

        assertEquals(0, monitor.previousFiles.size(), "previous");
        assertEquals(0, monitor.currentFiles.size(), "current");
        
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        
        assertEquals(0, monitor.previousFiles.size(), "previous");
        assertEquals(1, monitor.currentFiles.size(), "current");
        assertTrue(monitor.currentFiles.contains(TEST_FILENAME), "TEST_FILENAME not in current");
        
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        
        assertEquals(0, monitor.previousFiles.size(), "previous");
        assertEquals(1, monitor.currentFiles.size(), "current");
        assertTrue(monitor.currentFiles.contains(TEST_FILENAME), "TEST_FILENAME not in current");
        
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(0, monitor.currentFiles.size(), "current");
    }

    @Test
    void testFileTrackingAcrossSessions() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(0, monitor.currentFiles.size(), "current");

        context.checking(new Expectations() {{
            oneOf (removeVisitor).removeClass(TEST_CLASS);
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(1, monitor.currentFiles.size(), "current");
        assertTrue(monitor.currentFiles.contains(TEST_FILENAME), "TEST_FILENAME not in current");

        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));

        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(1, monitor.currentFiles.size(), "current");
        assertTrue(monitor.currentFiles.contains(TEST_FILENAME), "TEST_FILENAME not in current");

        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals(0, monitor.previousFiles.size(), "previous");
        assertEquals(1, monitor.currentFiles.size(), "current");
        assertTrue(monitor.currentFiles.contains(TEST_FILENAME), "TEST_FILENAME not in current");

        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(0, monitor.currentFiles.size(), "current");
    }

    @Test
    void testFileTrackingWithSkippedFile() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(0, monitor.currentFiles.size(), "current");
        
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(1, monitor.currentFiles.size(), "current");
        assertTrue(monitor.currentFiles.contains(TEST_FILENAME), "TEST_FILENAME not in current");

        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));

        assertEquals(0, monitor.previousFiles.size(), "previous");
        assertEquals(1, monitor.currentFiles.size(), "current");
        assertTrue(monitor.currentFiles.contains(TEST_FILENAME), "TEST_FILENAME not in current");

        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(0, monitor.currentFiles.size(), "current");
    }

    @Test
    void testFileTrackingWithMissingFile() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(0, monitor.currentFiles.size(), "current");

        context.checking(new Expectations() {{
            oneOf (removeVisitor).removeClass(TEST_CLASS);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        
        assertEquals(1, monitor.previousFiles.size(), "previous");
        assertTrue(monitor.previousFiles.contains(TEST_FILENAME), "TEST_FILENAME not in previous");
        assertEquals(0, monitor.currentFiles.size(), "current");

        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
        
        assertEquals(0, monitor.previousFiles.size(), "previous");
        assertEquals(0, monitor.currentFiles.size(), "current");
    }
    
    @Test
    void testNewClassfile() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
    }

    @Test
    void testRepeatInSession() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        // Begin session
        monitor.beginSession(new LoadEvent(this, null, null, null));

        // See file for the first time
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));

        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        // See file for the second time
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));

        // Finish session
        monitor.endSession(new LoadEvent(this, null, null, null));
    }

    @Test
    void testRepeatInGroup() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        // Begin session
        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));

        // See file for the first time
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));

        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        // See file for the second time
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));

        // Finish session
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
    }

    @Test
    void testRepeatAcrossSessions() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        context.checking(new Expectations() {{
            oneOf (removeVisitor).removeClass(TEST_CLASS);
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
    }

    @Test
    void testRemoval() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        context.checking(new Expectations() {{
            oneOf (removeVisitor).removeClass(TEST_CLASS);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
    }

    @Test
    void testSkip() {
        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

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
    }

    @Test
    void testOpenSession() {
        monitor.setClosedSession(false);

        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

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
    }

    @Test
    void testExtractAgainInOpenSession() {
        monitor.setClosedSession(false);

        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
    }

    @Test
    void testReloadAfterClosingSessionSession() {
        monitor.setClosedSession(false);

        context.checking(new Expectations() {{
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));

        monitor.setClosedSession(true);

        context.checking(new Expectations() {{
            oneOf (removeVisitor).removeClass(TEST_CLASS);
            oneOf (addVisitor).visitClassfile(testClassfile);
        }});

        monitor.beginSession(new LoadEvent(this, null, null, null));
        monitor.beginGroup(new LoadEvent(this, null, null, null));
        monitor.beginFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.beginClassfile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endClassfile(new LoadEvent(this, null, TEST_FILENAME, testClassfile));
        monitor.endFile(new LoadEvent(this, null, TEST_FILENAME, null));
        monitor.endGroup(new LoadEvent(this, null, null, null));
        monitor.endSession(new LoadEvent(this, null, null, null));
    }
}
