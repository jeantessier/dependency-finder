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

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestClassfileScanner {
    private static final Path TEST_DIR = Paths.get("jarjardiff/old/build/archives");
    private static final String TEST_FILENAME = Paths.get("build/classes/java/main/test.class").toString();

    private final ClassfileScanner scanner = new ClassfileScanner();

    @Test
    void testOneFile() {
        String filename = TEST_FILENAME;
        assertTrue(new File(filename).exists(), filename + " missing");
        
        scanner.load(Collections.singleton(filename));

        assertEquals(1, scanner.getNbFiles(), "Number of files");
        assertEquals(1, scanner.getNbClasses(), "Number of classes");
    }

    @Test
    void testOneLevelZip() {
        String filename = TEST_DIR.resolve("onelevel.zip").toString();
        assertTrue(new File(filename).exists(), filename + " missing");
        
        scanner.load(Collections.singleton(filename));

        assertEquals(40, scanner.getNbFiles(), "Number of files");
        assertEquals(18, scanner.getNbClasses(), "Number of classes");
    }

    @Test
    void testOneLevelJar() {
        String filename = TEST_DIR.resolve("onelevel.jar").toString();
        assertTrue(new File(filename).exists(), filename + " missing");
        
        scanner.load(Collections.singleton(filename));

        assertEquals(42, scanner.getNbFiles(), "Number of files");
        assertEquals(18, scanner.getNbClasses(), "Number of classes");
    }
    
    @Test
    void testOneLevelMiscellaneous() {
        String filename = TEST_DIR.resolve("onelevel.mis").toString();
        assertTrue(new File(filename).exists(), filename + " missing");
        
        scanner.load(Collections.singleton(filename));

        assertEquals(40, scanner.getNbFiles(), "Number of files");
        assertEquals(18, scanner.getNbClasses(), "Number of classes");
    }

    @Test
    void testTwoLevelZip() {
        String filename = TEST_DIR.resolve("twolevel.zip").toString();
        assertTrue(new File(filename).exists(), filename + " missing");
        
        scanner.load(Collections.singleton(filename));

        assertEquals(41, scanner.getNbFiles(), "Number of files");
        assertEquals(18, scanner.getNbClasses(), "Number of classes");
    }

    @Test
    void testTwoLevelJar() {
        String filename = TEST_DIR.resolve("twolevel.jar").toString();
        assertTrue(new File(filename).exists(), filename + " missing");
        
        scanner.load(Collections.singleton(filename));

        assertEquals(43, scanner.getNbFiles(), "Number of files");
        assertEquals(18, scanner.getNbClasses(), "Number of classes");
    }
    
    @Test
    void testTwoLevelMiscellaneous() {
        String filename = TEST_DIR.resolve("twolevel.mis").toString();
        assertTrue(new File(filename).exists(), filename + " missing");
        
        scanner.load(Collections.singleton(filename));

        assertEquals(41, scanner.getNbFiles(), "Number of files");
        assertEquals(18, scanner.getNbClasses(), "Number of classes");
    }
}
