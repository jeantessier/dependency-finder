/*
 *  Copyright (c) 2001-2024, Jean Tessier
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

import org.jmock.*;
import org.jmock.api.*;
import org.jmock.junit5.*;
import org.jmock.lib.action.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestDirectoryClassfileLoader {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    private static final String TEST_CLASS = "test";
    private static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    private static final String BOGUS_TEST_FILENAME = CLASSES_DIR.resolve("bogus").resolve(TEST_CLASS + ".class").toString();
    private static final String TEST_DIRNAME = CLASSES_DIR.resolve("testpackage").toString();
    private static final String OTHER_DIRNAME = CLASSES_DIR.resolve("otherpackage").toString();

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final LoadListener mockListener = context.mock(LoadListener.class);
    
    private ClassfileLoader loader;

    @BeforeEach
    void setUp() {
        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(mockListener);
        loader = new DirectoryClassfileLoader(eventSource);
    }

    @Test
    void testLoadClassFile() {
        context.checking(new Expectations() {{
            exactly(0).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endClassfile(with(any(LoadEvent.class)));
                will(new CustomAction("confirm details of LoadEvent") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(TEST_FILENAME, ((LoadEvent) invocation.getParameter(0)).getGroupName());
                        assertNotNull(((LoadEvent) invocation.getParameter(0)).getClassfile(), "Classfile");
                        return null;
                    }
                });
            exactly(1).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(TEST_FILENAME);
    }

    @Test
    void testLoadClassInputStream() throws IOException {
        context.checking(new Expectations() {{
            exactly(0).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(TEST_FILENAME, new FileInputStream(TEST_FILENAME));
    }

    @Test
    void testLoadBogusFile() {
        context.checking(new Expectations() {{
            exactly(0).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(BOGUS_TEST_FILENAME);
    }

    @Test
    void testLoadBogusInputStream() throws IOException {
        context.checking(new Expectations() {{
            exactly(0).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(BOGUS_TEST_FILENAME, new FileInputStream(TEST_FILENAME));
    }

    @Test
    void testLoadDirectory() {
        context.checking(new Expectations() {{
            exactly(0).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(7).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(6).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(6).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(7).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(TEST_DIRNAME);
    }
    
    @Test
    void testMultipleDirectories() {
        context.checking(new Expectations() {{
            exactly(0).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(11).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(9).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(9).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(11).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(0).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(TEST_DIRNAME);
        loader.load(OTHER_DIRNAME);
    }
}
