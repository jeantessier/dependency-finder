/*
 *  Copyright (c) 2001-2005, Jean Tessier
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

public class TestDirectoryExplorer extends TestCase {
    public static final String TEST_FILENAME    = "classes" + File.separator + "test.class";
    public static final String OTHER_FILENAME   = "tests" + File.separator + "JarJarDiff" + File.separator + "build.xml";
    public static final String MISSING_FILENAME = "tests" + File.separator + "JarJarDiff" + File.separator + "missing";
    public static final String TEST_DIRNAME     = "tests" + File.separator + "JarJarDiff" + File.separator + "old";
    public static final String OTHER_DIRNAME    = "tests" + File.separator + "JarJarDiff" + File.separator + "new";
    
    public void testExploreFilename() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(TEST_FILENAME);

        List list = new ArrayList(explorer.getCollection());
        
        assertEquals("size", 1, list.size());
        assertEquals(TEST_FILENAME, ((File) list.get(0)).getPath());
    }

    public void testExploreOtherFilename() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(OTHER_FILENAME);

        List list = new ArrayList(explorer.getCollection());
        
        assertEquals("size", 1, list.size());
        assertEquals(OTHER_FILENAME, ((File) list.get(0)).getPath());
    }

    public void testExploreMissingFilename() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(MISSING_FILENAME);
        
        assertEquals("size", 0, explorer.getCollection().size());
    }

    public void testExploreDirectory() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(TEST_DIRNAME);

        List list = new ArrayList(explorer.getCollection());
        
        assertEquals("size", 59, list.size());
        assertEquals(TEST_DIRNAME, ((File) list.get(0)).getPath());
    }
    
    public void testExploreMultipleDirectories() throws IOException {
        Collection directories = new ArrayList();
        directories.add(TEST_DIRNAME);
        directories.add(OTHER_DIRNAME);
        
        DirectoryExplorer explorer = new DirectoryExplorer(directories);

        List list = new ArrayList(explorer.getCollection());
        
        assertEquals("size", 118, list.size());
        assertEquals(TEST_DIRNAME, ((File) list.get(0)).getPath());
    }
    
    public void testExploreSingletonFile() throws IOException {
        Collection files = new ArrayList();
        files.add(TEST_FILENAME);
        
        DirectoryExplorer explorer = new DirectoryExplorer(files);

        List list = new ArrayList(explorer.getCollection());
        
        assertEquals("size", 1, list.size());
        assertEquals(TEST_FILENAME, ((File) list.get(0)).getPath());
    }
}
