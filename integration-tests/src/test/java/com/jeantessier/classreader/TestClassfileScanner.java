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

import java.io.*;
import java.nio.file.*;
import java.util.*;

import junit.framework.*;

public class TestClassfileScanner extends TestCase {
    private static final Path TEST_DIR = Paths.get("../tests/JarJarDiff");
    public static final String TEST_FILENAME = Paths.get("build/classes/java/main/test.class").toString();

    private ClassfileScanner scanner;

    protected void setUp() throws Exception {
        super.setUp();
        
        scanner = new ClassfileScanner();
    }

    public void testOneFile() {
        String filename = TEST_FILENAME;
        assertTrue(filename + " missing", new File(filename).exists());
        
        scanner.load(Collections.singleton(filename));

        assertEquals("Number of files", 1, scanner.getNbFiles());
        assertEquals("Number of classes", 1, scanner.getNbClasses());
    }

    public void testOneLevelZip() {
        String filename = TEST_DIR.resolve("onelevel.zip").toString();
        assertTrue(filename + " missing", new File(filename).exists());
        
        scanner.load(Collections.singleton(filename));

        assertEquals("Number of files",   31, scanner.getNbFiles());
        assertEquals("Number of classes", 14, scanner.getNbClasses());
    }

    public void testOneLevelJar() {
        String filename = TEST_DIR.resolve("onelevel.jar").toString();
        assertTrue(filename + " missing", new File(filename).exists());
        
        scanner.load(Collections.singleton(filename));

        assertEquals("Number of files",   33, scanner.getNbFiles());
        assertEquals("Number of classes", 14, scanner.getNbClasses());
    }
    
    public void testOneLevelMiscellaneous() {
        String filename = TEST_DIR.resolve("onelevel.mis").toString();
        assertTrue(filename + " missing", new File(filename).exists());
        
        scanner.load(Collections.singleton(filename));

        assertEquals("Number of files",   31, scanner.getNbFiles());
        assertEquals("Number of classes", 14, scanner.getNbClasses());
    }

    public void testTwoLevelZip() {
        String filename = TEST_DIR.resolve("twolevel.zip").toString();
        assertTrue(filename + " missing", new File(filename).exists());
        
        scanner.load(Collections.singleton(filename));

        assertEquals("Number of files",   32, scanner.getNbFiles());
        assertEquals("Number of classes", 14, scanner.getNbClasses());
    }

    public void testTwoLevelJar() {
        String filename = TEST_DIR.resolve("twolevel.jar").toString();
        assertTrue(filename + " missing", new File(filename).exists());
        
        scanner.load(Collections.singleton(filename));

        assertEquals("Number of files",   34, scanner.getNbFiles());
        assertEquals("Number of classes", 14, scanner.getNbClasses());
    }
    
    public void testTwoLevelMiscellaneous() {
        String filename = TEST_DIR.resolve("twolevel.mis").toString();
        assertTrue(filename + " missing", new File(filename).exists());
        
        scanner.load(Collections.singleton(filename));

        assertEquals("Number of files",   32, scanner.getNbFiles());
        assertEquals("Number of classes", 14, scanner.getNbClasses());
    }
}
