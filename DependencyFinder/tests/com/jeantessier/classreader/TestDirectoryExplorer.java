/*
 *  Copyright (c) 2001-2009, Jean Tessier
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
    private File testFile;
    private File otherFile;
    private File missingFile;
    private File testDir;
    private File otherDir;

    protected void setUp() throws Exception {
        super.setUp();

        testFile = File.createTempFile(getName(), null);

        otherFile = File.createTempFile(getName(), null);

        missingFile = File.createTempFile(getName(), null);
        missingFile.delete();

        testDir = File.createTempFile(getName(), null);
        testDir.delete();
        testDir.mkdir();
        File.createTempFile(getName(), null, testDir);
        File.createTempFile(getName(), null, testDir);

        otherDir = File.createTempFile(getName(), null);
        otherDir.delete();
        otherDir.mkdir();
        File.createTempFile(getName(), null, otherDir);
    }

    protected void tearDown() throws Exception {
        testFile.delete();

        otherFile.delete();

        for (File file : testDir.listFiles()) {
            file.delete();
        }
        testDir.delete();

        for (File file : otherDir.listFiles()) {
            file.delete();
        }
        otherDir.delete();

        super.tearDown();
    }

    public void testExploreFilename() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(testFile.getPath());

        List<File> list = new ArrayList<File>(explorer.getFiles());
        
        assertEquals("size", 1, list.size());
        assertEquals(testFile, list.get(0));
    }

    public void testExploreOtherFilename() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(otherFile.getPath());

        List<File> list = new ArrayList<File>(explorer.getFiles());
        
        assertEquals("size", 1, list.size());
        assertEquals(otherFile, list.get(0));
    }

    public void testExploreMissingFilename() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(missingFile.getPath());
        
        assertEquals("size", 0, explorer.getFiles().size());
    }

    public void testExploreDirectory() throws IOException {
        DirectoryExplorer explorer = new DirectoryExplorer(testDir.getPath());

        List<File> list = new ArrayList<File>(explorer.getFiles());
        
        assertEquals("size", 3, list.size());
        assertEquals(testDir, list.get(0));
    }
    
    public void testExploreMultipleDirectories() throws IOException {
        Collection<String> directories = new ArrayList<String>();
        directories.add(testDir.getPath());
        directories.add(otherDir.getPath());
        
        DirectoryExplorer explorer = new DirectoryExplorer(directories);

        List<File> list = new ArrayList<File>(explorer.getFiles());
        
        assertEquals("size", 5, list.size());
        assertEquals(testDir, list.get(0));
    }
    
    public void testExploreSingletonFile() throws IOException {
        Collection<String> files = new ArrayList<String>();
        files.add(testFile.getPath());
        
        DirectoryExplorer explorer = new DirectoryExplorer(files);

        List<File> list = new ArrayList<File>(explorer.getFiles());
        
        assertEquals("size", 1, list.size());
        assertEquals(testFile, list.get(0));
    }
}
