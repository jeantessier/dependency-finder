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

import org.apache.log4j.*;

public class TestAggregatingClassfileLoader extends TestCase {
    public static final String TEST_CLASS    = "test";
    public static final String TEST_FILENAME = "classes" + File.separator + "test.class";
    
    private AggregatingClassfileLoader loader;

    protected void setUp() throws Exception {
        Logger.getLogger(getClass()).info("Starting test: " + getName());

        loader = new AggregatingClassfileLoader();
    }

    protected void tearDown() throws Exception {
        Logger.getLogger(getClass()).info("End of " + getName());
    }
    
    public void testCreate() {
        assertEquals("Different number of class names",
                     0,
                     loader.getAllClassNames().size());
        assertNull(TEST_CLASS + " should have been null",
                   loader.getClassfile(TEST_CLASS));
    }
    
    public void testStart() throws IOException {
        assertEquals("Different number of class names",
                     0,
                     loader.getAllClassNames().size());
        assertNull(TEST_CLASS + " should have been null",
                   loader.getClassfile(TEST_CLASS));

        loader.load(new DataInputStream(new FileInputStream(TEST_FILENAME)));
        
        assertEquals("Different number of class names",
                     1,
                     loader.getAllClassNames().size());
        assertTrue("Missing class name \"" + TEST_CLASS + "\"",
                   loader.getAllClassNames().contains(TEST_CLASS));
        assertNotNull(TEST_CLASS + " should not have been null",
                      loader.getClassfile(TEST_CLASS));
    }

    public void testClassfile() {
        loader.load(Collections.singleton(TEST_FILENAME));

        assertNotNull("No Classfile from " + TEST_FILENAME, loader.getClassfile(TEST_CLASS));
    }
}
