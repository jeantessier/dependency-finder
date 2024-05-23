/*
 *  Copyright (c) 2001-2023, Jean Tessier
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class TestJarClassfileWithMultiReleaseJarFileLoader extends TestClassfileLoaderBase {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();

    public static final List<String> TARGET_JDK_VERSIONS = List.of("22", "21", "17", "9");

    private String multiReleaseJarFileName;
    private final List<String> expectedFilenames = new ArrayList<>();

    protected void setUp() throws Exception {
        super.setUp();

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

    public void testNoTargetJDK_loadsHighestVersionAvailable() {
        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(this);
        var loader = new JarClassfileLoader(eventSource);

        loader.load(multiReleaseJarFileName);

        assertEvents("META-INF/versions/22/test.class");
    }

    public void testWithTargetJDK_loadsUpToThatVersion() {
        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(this);
        var loader = new JarClassfileLoader(eventSource, 19);

        loader.load(multiReleaseJarFileName);

        assertEvents("META-INF/versions/17/test.class");
    }

    public void testWithAbsentTargetJDK_loadsDefaultVersion() {
        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(this);
        var loader = new JarClassfileLoader(eventSource, 8);

        loader.load(multiReleaseJarFileName);

        assertEvents("test.class");
    }

    private void assertEvents(String expectedClassfileFilename) {
        assertEquals("Begin Session", 0, getBeginSessionEvents().size());
        assertEquals("End Session", 0, getEndSessionEvents().size());

        assertEquals("Group names", List.of(multiReleaseJarFileName), getBeginGroupEvents().stream().map(LoadEvent::getGroupName).toList());
        assertEquals("Group sizes", List.of(expectedFilenames.size()), getBeginGroupEvents().stream().map(LoadEvent::getSize).toList());
        assertEquals("Group names", List.of(multiReleaseJarFileName), getEndGroupEvents().stream().map(LoadEvent::getGroupName).toList());

        assertEquals("File names", expectedFilenames, getBeginFileEvents().stream().map(LoadEvent::getFilename).toList());
        assertEquals("File names", expectedFilenames, getEndFileEvents().stream().map(LoadEvent::getFilename).toList());

        assertEquals("Classfile filename", List.of(expectedClassfileFilename), getBeginClassfileEvents().stream().map(LoadEvent::getFilename).toList());
        assertEquals("Classfile filename", List.of(expectedClassfileFilename), getEndClassfileEvents().stream().map(LoadEvent::getFilename).toList());
        assertEquals("Classfine name", List.of(TEST_CLASS), getEndClassfileEvents().stream().map(event -> event.getClassfile().getClassName()).toList());
    }
}
