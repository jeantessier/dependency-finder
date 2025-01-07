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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClassfileLoaderPermissiveDispatcher {
    private static final String TEST_FILENAME = Paths.get("build/classes/java/main/test.class").toString();
    private static final Path TEST_DIR = Paths.get("jarjardiff/old/build/archives");
    private static final String ONELEVEL_JAR = TEST_DIR.resolve("onelevel.jar").toString();
    private static final String ONELEVEL_MISC = TEST_DIR.resolve("onelevel.mis").toString();
    private static final String ONELEVEL_ZIP = TEST_DIR.resolve("onelevel.zip").toString();
    private static final String TWOLEVEL_JAR = TEST_DIR.resolve("twolevel.jar").toString();
    private static final String TWOLEVEL_MISC = TEST_DIR.resolve("twolevel.mis").toString();
    private static final String TWOLEVEL_ZIP = TEST_DIR.resolve("twolevel.zip").toString();

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final LoadListener mockListener = context.mock(LoadListener.class);

    private final ClassfileLoader loader = new TransientClassfileLoader(new PermissiveDispatcher());

    @BeforeEach
    void setUp() {
        loader.addLoadListener(mockListener);
    }

    @Test
    void testOneFile() {
        String filename = TEST_FILENAME;
        assertTrue(new File(filename).exists(), filename + " missing");

        var expectedGroupSizes = List.of(1).iterator();

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginGroup(with(any(LoadEvent.class)));
                will(new CustomAction("check the group's size") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(expectedGroupSizes.next(), ((LoadEvent) invocation.getParameter(0)).getSize());
                        return null;
                    }
                });
            exactly(1).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(Collections.singleton(filename));
    }

    @Test
    void testOneLevelZip() {
        String filename = ONELEVEL_ZIP;
        assertTrue(new File(filename).exists(), filename + " missing");

        var expectedGroupSizes = List.of(32, -1).iterator();

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).beginGroup(with(any(LoadEvent.class)));
                will(new CustomAction("check the group's size") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(expectedGroupSizes.next(), ((LoadEvent) invocation.getParameter(0)).getSize());
                        return null;
                    }
                });
            exactly(32).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(32).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(Collections.singleton(filename));
    }

    @Test
    void testOneLevelJar() {
        String filename = ONELEVEL_JAR;
        assertTrue(new File(filename).exists(), filename + " missing");

        var expectedGroupSizes = List.of(34, -1).iterator();

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).beginGroup(with(any(LoadEvent.class)));
                will(new CustomAction("check the group's size") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(expectedGroupSizes.next(), ((LoadEvent) invocation.getParameter(0)).getSize());
                        return null;
                    }
                });
            exactly(34).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(34).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(Collections.singleton(filename));
    }

    @Test
    void testOneLevelMiscellaneous() {
        String filename = ONELEVEL_MISC;
        assertTrue(new File(filename).exists(), filename + " missing");

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(32).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(32).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(2).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(Collections.singleton(filename));
    }

    @Test
    void testTwoLevelZip() {
        String filename = TWOLEVEL_ZIP;
        assertTrue(new File(filename).exists(), filename + " missing");

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(3).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(33).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(33).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(3).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(Collections.singleton(filename));
    }

    @Test
    void testTwoLevelJar() {
        String filename = TWOLEVEL_JAR;
        assertTrue(new File(filename).exists(), filename + " missing");

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(3).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(35).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(35).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(3).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(Collections.singleton(filename));
    }

    @Test
    void testTwoLevelMiscellaneous() {
        String filename = TWOLEVEL_MISC;
        assertTrue(new File(filename).exists(), filename + " missing");

        context.checking(new Expectations() {{
            exactly(1).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(3).of (mockListener).beginGroup(with(any(LoadEvent.class)));
            exactly(33).of (mockListener).beginFile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
            exactly(14).of (mockListener).endClassfile(with(any(LoadEvent.class)));
            exactly(33).of (mockListener).endFile(with(any(LoadEvent.class)));
            exactly(3).of (mockListener).endGroup(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});

        loader.load(Collections.singleton(filename));
    }
}
