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

public class TestAggregatingClassfileLoader {
    private static final Path CLASSES_DIR = Paths.get("build/classes/java/main");
    private static final String TEST_CLASS = "test";
    private static final String TEST_FILENAME = CLASSES_DIR.resolve(TEST_CLASS + ".class").toString();
    
    private final AggregatingClassfileLoader loader = new AggregatingClassfileLoader();

    @Test
    void testCreate() {
        assertEquals(0, loader.getAllClassNames().size(), "Different number of class names");
        assertNull(loader.getClassfile(TEST_CLASS), TEST_CLASS + " should have been null");
    }
    
    @Test
    void testStart() throws IOException {
        assertEquals(0, loader.getAllClassNames().size(), "Different number of class names");
        assertNull(loader.getClassfile(TEST_CLASS), TEST_CLASS + " should have been null");

        loader.load(new DataInputStream(new FileInputStream(TEST_FILENAME)));
        
        assertEquals(1, loader.getAllClassNames().size(), "Different number of class names");
        assertTrue(loader.getAllClassNames().contains(TEST_CLASS), "Missing class name \"" + TEST_CLASS + "\"");
        assertNotNull(loader.getClassfile(TEST_CLASS), TEST_CLASS + " should not have been null");
    }

    @Test
    void testClassfile() {
        loader.load(Collections.singleton(TEST_FILENAME));

        assertNotNull(loader.getClassfile(TEST_CLASS), "No Classfile from " + TEST_FILENAME);
    }
}
