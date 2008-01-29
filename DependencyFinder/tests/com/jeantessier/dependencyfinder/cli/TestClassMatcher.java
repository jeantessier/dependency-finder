/*
 *  Copyright (c) 2001-2008, Jean Tessier
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
import java.util.*;

import junit.framework.*;

import com.jeantessier.classreader.*;

public class TestClassMatcher extends TestCase {
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    public static final String TEST_DIR      = "tests"   + File.separator + "JarJarDiff";
    
    public void testMatchNone() {
        ClassMatcher matcher = new ClassMatcher(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(TEST_FILENAME));

        assertEquals("Number of results", 0, matcher.getResults().size());
    }
    
    public void testMatchClassfile() {
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.EMPTY_LIST);
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(TEST_FILENAME));

        assertEquals("Number of results", 1, matcher.getResults().size());
        assertEquals("key", TEST_CLASS, matcher.getResults().keySet().iterator().next());
        Iterator i = matcher.getResults().values().iterator();
        while (i.hasNext()) {
            List results = (List) i.next();
            assertEquals("number results", 1, results.size());
            assertEquals("value", TEST_FILENAME, results.get(0));
        }
    }

    public void testOneLevelJar() {
        String filename = TEST_DIR + File.separator + "onelevel.jar";
        assertTrue(filename + " missing", new File(filename).exists());
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.EMPTY_LIST);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(filename));

        assertEquals("Number of results", 14, matcher.getResults().size());
        Iterator i = matcher.getResults().values().iterator();
        while (i.hasNext()) {
            List results = (List) i.next();
            assertEquals("number results", 1, results.size());
            assertEquals("value", filename, results.get(0));
        }
    }

    public void testTwoLevelJar() {
        String filename = TEST_DIR + File.separator + "twolevel.jar";
        assertTrue(filename + " missing", new File(filename).exists());
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.EMPTY_LIST);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(filename));

        assertEquals("Number of results", 14, matcher.getResults().size());
        Iterator i = matcher.getResults().values().iterator();
        while (i.hasNext()) {
            List results = (List) i.next();
            assertEquals("number results", 1, results.size());
            assertEquals("value", "onelevel.zip", results.get(0));
        }
    }
    
    public void testMatchDirectory() {
        String dirname = TEST_DIR + File.separator + "new";
        assertTrue(dirname + " missing", new File(dirname).exists());

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.EMPTY_LIST);
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dirname));

        assertEquals("Number of results", 14, matcher.getResults().size());
        Iterator i = matcher.getResults().values().iterator();
        while (i.hasNext()) {
            List results = (List) i.next();
            assertEquals("number results", 1, results.size());
            assertEquals("value", dirname, results.get(0));
        }
    }
    
    public void testIncludes() {
        String dirname = TEST_DIR + File.separator + "new";
        assertTrue(dirname + " missing", new File(dirname).exists());

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("/modified/i"), Collections.EMPTY_LIST);
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dirname));

        assertEquals("Number of results", 13, matcher.getResults().size());
        Iterator i = matcher.getResults().values().iterator();
        while (i.hasNext()) {
            List results = (List) i.next();
            assertEquals("number results", 1, results.size());
            assertEquals("value", dirname, results.get(0));
        }
    }
    
    public void testExcludes() {
        String dirname = TEST_DIR + File.separator + "new";
        assertTrue(dirname + " missing", new File(dirname).exists());

        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.singletonList("/modified/i"));
        
        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(Collections.singleton(dirname));

        assertEquals("Number of results", 1, matcher.getResults().size());
        Iterator i = matcher.getResults().values().iterator();
        while (i.hasNext()) {
            List results = (List) i.next();
            assertEquals("number results", 1, results.size());
            assertEquals("value", dirname, results.get(0));
        }
    }

    public void testMultiples() {
        String filename1 = TEST_DIR + File.separator + "onelevel.zip";
        String filename2 = TEST_DIR + File.separator + "onelevel.jar";
        assertTrue(filename1 + " missing", new File(filename1).exists());
        assertTrue(filename2 + " missing", new File(filename2).exists());

        Collection<String> filenames = new ArrayList<String>();
        filenames.add(filename1);
        filenames.add(filename2);
        
        ClassMatcher matcher = new ClassMatcher(Collections.singletonList("//"), Collections.EMPTY_LIST);

        ClassfileLoader loader = new TransientClassfileLoader();
        loader.addLoadListener(matcher);
        loader.load(filenames);

        assertEquals("Number of results", 14, matcher.getResults().size());
        Iterator i = matcher.getResults().values().iterator();
        while (i.hasNext()) {
            List results = (List) i.next();
            assertEquals("number results", 2, results.size());
            assertEquals("value", filename1, results.get(0));
            assertEquals("value", filename2, results.get(1));
        }
    }
}
