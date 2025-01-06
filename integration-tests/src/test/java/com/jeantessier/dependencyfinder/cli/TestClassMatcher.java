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

package com.jeantessier.dependencyfinder.cli;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.jmock.*;
import org.jmock.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import com.jeantessier.classreader.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClassMatcher {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    private static final Path TEST_DIR = Paths.get("jarjardiff/old/build/archives");
    public static final String ONELEVEL_JAR = TEST_DIR.resolve("onelevel.jar").toString();
    public static final String ONELEVEL_ZIP = TEST_DIR.resolve("onelevel.zip").toString();
    public static final String TWOLEVEL_JAR = TEST_DIR.resolve("twolevel.jar").toString();
    private static final String TEST_ARCHIVE = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    @RegisterExtension
    JUnit5Mockery context = new JUnit5Mockery();

    @Test
    void testReadsGroupNameFromEndClassfileEvent() {
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        String groupName1 = "groupName1";
        String filename1 = "filename1";
        String groupName2 = "groupName2";
        String filename2 = "filename2";

        final Classfile mockClassfile = context.mock(Classfile.class);

        context.checking(new Expectations() {{
            oneOf (mockClassfile).getClassName();
        }});

        matcher.beginSession(new LoadEvent(this, null, null, null));
        matcher.beginGroup(new LoadEvent(this, groupName1, 1));
        matcher.beginFile(new LoadEvent(this, groupName1, filename1, null));
        matcher.beginClassfile(new LoadEvent(this, groupName1, filename1, null));
        matcher.endClassfile(new LoadEvent(this, groupName2, filename2, mockClassfile));
        matcher.endFile(new LoadEvent(this, groupName1, filename1, null));
        matcher.endGroup(new LoadEvent(this, groupName1, null, null));
        matcher.endSession(new LoadEvent(this, null, null, null));

        assertEquals(1, matcher.getResults().size(), "Number of results");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(1, results.size(), "number results");
            assertEquals(groupName2, results.get(0), "value");
        }
    }

    @Test
    void testMatchNone() {
        ClassMatcher matcher = new ClassMatcher(Collections.emptyList(), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(TEST_FILENAME));

        assertEquals(0, matcher.getResults().size(), "Number of results");
    }
    
    @Test
    void testMatchClassfile() {
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(TEST_FILENAME));

        assertEquals(1, matcher.getResults().size(), "Number of results");
        assertEquals(TEST_CLASS, matcher.getResults().keySet().iterator().next(), "key");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(1, results.size(), "number results");
            assertEquals(TEST_FILENAME, results.get(0), "value");
        }
    }

    @Test
    void testOneLevelJar() {
        File file = new File(ONELEVEL_JAR);
        assertTrue(file.exists(), file + " missing");
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(file.getPath()));

        assertEquals(14, matcher.getResults().size(), "Number of results");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(1, results.size(), "number results");
            assertEquals(file.getPath(), results.get(0), "value");
        }
    }

    @Test
    void testTwoLevelJar() {
        File file = new File(TWOLEVEL_JAR);
        assertTrue(file.exists(), file + " missing");
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(file.getPath()));

        assertEquals(14, matcher.getResults().size(), "Number of results");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(1, results.size(), "number results");
            assertEquals("onelevel.zip", results.get(0), "value");
        }
    }
    
    @Test
    void testMatchDirectory() {
        File dir = new File(TEST_ARCHIVE);
        assertTrue(dir.exists(), dir + " missing");

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dir.getPath()));

        assertEquals(14, matcher.getResults().size(), "Number of results");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(1, results.size(), "number results");
            assertEquals(dir.getPath(), results.get(0), "value");
        }
    }
    
    @Test
    void testIncludes() {
        File dir = new File(TEST_ARCHIVE);
        assertTrue(dir.exists(), dir + " missing");

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("/modified/i"), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dir.getPath()));

        assertEquals(13, matcher.getResults().size(), "Number of results");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(1, results.size(), "number results");
            assertEquals(dir.getPath(), results.get(0), "value");
        }
    }
    
    @Test
    void testExcludes() {
        File dir = new File(TEST_ARCHIVE);
        assertTrue(dir.exists(), dir + " missing");

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.singletonList("/modified/i"));
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dir.getPath()));

        assertEquals(1, matcher.getResults().size(), "Number of results");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(1, results.size(), "number results");
            assertEquals(dir.getPath(), results.get(0), "value");
        }
    }

    @Test
    void testMultiples() {
        File file1 = new File(ONELEVEL_ZIP);
        File file2 = new File(ONELEVEL_JAR);
        assertTrue(file1.exists(), file1 + " missing");
        assertTrue(file2.exists(), file2 + " missing");

        Collection<String> filenames = new ArrayList<>();
        filenames.add(file1.getPath());
        filenames.add(file2.getPath());
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(filenames);

        assertEquals(14, matcher.getResults().size(), "Number of results");
        for (List<String> results : matcher.getResults().values()) {
            assertEquals(2, results.size(), "number results");
            assertEquals(file1.getPath(), results.get(0), "value");
            assertEquals(file2.getPath(), results.get(1), "value");
        }
    }
}
