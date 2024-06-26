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
import org.jmock.integration.junit3.*;

import com.jeantessier.classreader.*;

public class TestClassMatcher extends MockObjectTestCase {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    private static final Path TEST_DIR = Paths.get("jarjardiff/old/build/archives");
    public static final String ONELEVEL_JAR = TEST_DIR.resolve("onelevel.jar").toString();
    public static final String ONELEVEL_ZIP = TEST_DIR.resolve("onelevel.zip").toString();
    public static final String TWOLEVEL_JAR = TEST_DIR.resolve("twolevel.jar").toString();
    private static final String TEST_ARCHIVE = Paths.get("jarjardiff/new/build/libs/new.jar").toString();

    public void testReadsGroupNameFromEndClassfileEvent() {
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        String groupName1 = "groupName1";
        String filename1 = "filename1";
        String groupName2 = "groupName2";
        String filename2 = "filename2";

        final Classfile mockClassfile = mock(Classfile.class);

        checking(new Expectations() {{
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

        assertEquals("Number of results", 1, matcher.getResults().size());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 1, results.size());
            assertEquals("value", groupName2, results.get(0));
        }
    }

    public void testMatchNone() {
        ClassMatcher matcher = new ClassMatcher(Collections.emptyList(), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(TEST_FILENAME));

        assertEquals("Number of results", 0, matcher.getResults().size());
    }
    
    public void testMatchClassfile() {
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(TEST_FILENAME));

        assertEquals("Number of results", 1, matcher.getResults().size());
        assertEquals("key", TEST_CLASS, matcher.getResults().keySet().iterator().next());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 1, results.size());
            assertEquals("value", TEST_FILENAME, results.get(0));
        }
    }

    public void testOneLevelJar() {
        File file = new File(ONELEVEL_JAR);
        assertTrue(file + " missing", file.exists());
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(file.getPath()));

        assertEquals("Number of results", 14, matcher.getResults().size());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 1, results.size());
            assertEquals("value", file.getPath(), results.get(0));
        }
    }

    public void testTwoLevelJar() {
        File file = new File(TWOLEVEL_JAR);
        assertTrue(file + " missing", file.exists());
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(file.getPath()));

        assertEquals("Number of results", 14, matcher.getResults().size());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 1, results.size());
            assertEquals("value", "onelevel.zip", results.get(0));
        }
    }
    
    public void testMatchDirectory() {
        File dir = new File(TEST_ARCHIVE);
        assertTrue(dir + " missing", dir.exists());

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dir.getPath()));

        assertEquals("Number of results", 14, matcher.getResults().size());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 1, results.size());
            assertEquals("value", dir.getPath(), results.get(0));
        }
    }
    
    public void testIncludes() {
        File dir = new File(TEST_ARCHIVE);
        assertTrue(dir + " missing", dir.exists());

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("/modified/i"), Collections.emptyList());
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dir.getPath()));

        assertEquals("Number of results", 13, matcher.getResults().size());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 1, results.size());
            assertEquals("value", dir.getPath(), results.get(0));
        }
    }
    
    public void testExcludes() {
        File dir = new File(TEST_ARCHIVE);
        assertTrue(dir + " missing", dir.exists());

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.singletonList("/modified/i"));
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dir.getPath()));

        assertEquals("Number of results", 1, matcher.getResults().size());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 1, results.size());
            assertEquals("value", dir.getPath(), results.get(0));
        }
    }

    public void testMultiples() {
        File file1 = new File(ONELEVEL_ZIP);
        File file2 = new File(ONELEVEL_JAR);
        assertTrue(file1 + " missing", file1.exists());
        assertTrue(file2 + " missing", file2.exists());

        Collection<String> filenames = new ArrayList<>();
        filenames.add(file1.getPath());
        filenames.add(file2.getPath());
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.emptyList());

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(filenames);

        assertEquals("Number of results", 14, matcher.getResults().size());
        for (List<String> results : matcher.getResults().values()) {
            assertEquals("number results", 2, results.size());
            assertEquals("value", file1.getPath(), results.get(0));
            assertEquals("value", file2.getPath(), results.get(1));
        }
    }
}
