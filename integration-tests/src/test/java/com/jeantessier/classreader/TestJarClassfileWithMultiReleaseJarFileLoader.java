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
import java.util.jar.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestJarClassfileWithMultiReleaseJarFileLoader {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    private static final String TEST_CLASS = "test";
    private static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();

    private static final List<String> TARGET_JDK_VERSIONS = List.of("22", "21", "17", "9");

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    private final LoadListener mockListener = context.mock(LoadListener.class);

    private String multiReleaseJarFileName;
    private final List<String> expectedFilenames = new ArrayList<>();

    @BeforeEach
    void setUp() throws IOException {
        var multiReleaseJarFile = File.createTempFile("multi-release-jar-file", ".jar");
        multiReleaseJarFile.deleteOnExit();

        multiReleaseJarFileName = multiReleaseJarFile.getAbsolutePath();

        byte[] bytes;
        try (var in = new FileInputStream(TEST_FILENAME)) {
            bytes = in.readAllBytes();
        }

        expectedFilenames.add("META-INF/MANIFEST.MF");
        expectedFilenames.add("META-INF/versions/");
        TARGET_JDK_VERSIONS.forEach(jdkVersion -> {
                    expectedFilenames.add("META-INF/versions/" + jdkVersion + "/");
                    expectedFilenames.add("META-INF/versions/" + jdkVersion + "/" + TEST_CLASS + ".class");
                });
        expectedFilenames.add(TEST_CLASS + ".class");

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MULTI_RELEASE, "true");
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(multiReleaseJarFile), manifest)) {
            expectedFilenames.stream()
                    .filter(name -> !name.equals("META-INF/MANIFEST.MF"))
                    .sorted()
                    .forEach(name -> {
                        try {
                            jarOutputStream.putNextEntry(new JarEntry(name));
                            if (name.endsWith(TEST_CLASS + ".class")) {
                                jarOutputStream.write(bytes);
                            }
                            jarOutputStream.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    @Test
    void testNoTargetJDK_loadsHighestVersionAvailable() {
        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(mockListener);
        var loader = new JarClassfileLoader(eventSource);

        setExpectations("META-INF/versions/22/test.class");

        loader.load(multiReleaseJarFileName);
    }

    @Test
    void testWithTargetJDK_loadsUpToThatVersion() {
        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(mockListener);
        var loader = new JarClassfileLoader(eventSource, 19);

        setExpectations("META-INF/versions/17/test.class");

        loader.load(multiReleaseJarFileName);
    }

    @Test
    void testWithAbsentTargetJDK_loadsDefaultVersion() {
        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(mockListener);
        var loader = new JarClassfileLoader(eventSource, 8);

        setExpectations("test.class");

        loader.load(multiReleaseJarFileName);
    }

    private void setExpectations(String expectedClassfileFilename) {
        var expectedBeginFileNames = expectedFilenames.iterator();
        var expectedEndFileNames = expectedFilenames.iterator();

        context.checking(new Expectations() {{
            exactly(0).of (mockListener).beginSession(with(any(LoadEvent.class)));
            exactly(1).of (mockListener).beginGroup(with(any(LoadEvent.class)));
                will(new CustomAction("check the group's metadata") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(multiReleaseJarFileName, ((LoadEvent) invocation.getParameter(0)).getGroupName());
                        assertEquals(expectedFilenames.size(), ((LoadEvent) invocation.getParameter(0)).getSize());
                        return null;
                    }
                });
            exactly(11).of (mockListener).beginFile(with(any(LoadEvent.class)));
                will(new CustomAction("check the file's metadata") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(expectedBeginFileNames.next(), ((LoadEvent) invocation.getParameter(0)).getFilename());
                        return null;
                    }
                });
            exactly(1).of (mockListener).beginClassfile(with(any(LoadEvent.class)));
                will(new CustomAction("check the group's metadata") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(expectedClassfileFilename, ((LoadEvent) invocation.getParameter(0)).getFilename());
                        return null;
                    }
                });
            exactly(1).of (mockListener).endClassfile(with(any(LoadEvent.class)));
                will(new CustomAction("check the group's metadata") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(expectedClassfileFilename, ((LoadEvent) invocation.getParameter(0)).getFilename());
                        assertEquals(TEST_CLASS, ((LoadEvent) invocation.getParameter(0)).getClassfile().getClassName());
                        return null;
                    }
                });
            exactly(11).of (mockListener).endFile(with(any(LoadEvent.class)));
                will(new CustomAction("check the file's metadata") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(expectedEndFileNames.next(), ((LoadEvent) invocation.getParameter(0)).getFilename());
                        return null;
                    }
                });
            exactly(1).of (mockListener).endGroup(with(any(LoadEvent.class)));
                will(new CustomAction("check the group's metadata") {
                    public Object invoke(Invocation invocation) {
                        assertEquals(multiReleaseJarFileName, ((LoadEvent) invocation.getParameter(0)).getGroupName());
                        return null;
                    }
                });
            exactly(0).of (mockListener).endSession(with(any(LoadEvent.class)));
        }});
    }
}
