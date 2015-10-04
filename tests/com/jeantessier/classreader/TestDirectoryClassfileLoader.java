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

public class TestDirectoryClassfileLoader extends TestClassfileLoaderBase {
    public static final String TEST_CLASS          = "test";
    public static final String TEST_FILENAME       = "classes" + File.separator + "test.class";
    public static final String BOGUS_TEST_FILENAME = "classes" + File.separator + "bogus" + File.separator + "test.class";
    public static final String TEST_DIRNAME        = "classes" + File.separator + "testpackage";
    public static final String OTHER_DIRNAME       = "classes" + File.separator + "otherpackage";
    
    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(this);
        loader = new DirectoryClassfileLoader(eventSource);
    }

    public void testLoadClassFile() {
        loader.load(TEST_FILENAME);

        assertEquals("Begin Session",   0, getBeginSessionEvents().size());
        assertEquals("Begin Group",     1, getBeginGroupEvents().size());
        assertEquals("Begin File",      1, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 1, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   1, getEndClassfileEvents().size());
        assertEquals("End File",        1, getEndFileEvents().size());
        assertEquals("End Group",       1, getEndGroupEvents().size());
        assertEquals("End Session",     0, getEndSessionEvents().size());

        assertEquals(TEST_FILENAME, getEndClassfileEvents().getLast().getGroupName());
        assertNotNull("Classfile", getEndClassfileEvents().getLast().getClassfile());
    }

    public void testLoadClassInputStream() throws IOException {
        loader.load(TEST_FILENAME, new FileInputStream(TEST_FILENAME));

        assertEquals("Begin Session",   0, getBeginSessionEvents().size());
        assertEquals("Begin Group",     0, getBeginGroupEvents().size());
        assertEquals("Begin File",      0, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 0, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   0, getEndClassfileEvents().size());
        assertEquals("End File",        0, getEndFileEvents().size());
        assertEquals("End Group",       0, getEndGroupEvents().size());
        assertEquals("End Session",     0, getEndSessionEvents().size());
    }

    public void testLoadBogusFile() {
        loader.load(BOGUS_TEST_FILENAME);

        assertEquals("Begin Session",   0, getBeginSessionEvents().size());
        assertEquals("Begin Group",     1, getBeginGroupEvents().size());
        assertEquals("Begin File",      0, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 0, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   0, getEndClassfileEvents().size());
        assertEquals("End File",        0, getEndFileEvents().size());
        assertEquals("End Group",       1, getEndGroupEvents().size());
        assertEquals("End Session",     0, getEndSessionEvents().size());
    }

    public void testLoadBogusInputStream() throws IOException {
        loader.load(BOGUS_TEST_FILENAME, new FileInputStream(TEST_FILENAME));

        assertEquals("Begin Session",   0, getBeginSessionEvents().size());
        assertEquals("Begin Group",     0, getBeginGroupEvents().size());
        assertEquals("Begin File",      0, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 0, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   0, getEndClassfileEvents().size());
        assertEquals("End File",        0, getEndFileEvents().size());
        assertEquals("End Group",       0, getEndGroupEvents().size());
        assertEquals("End Session",     0, getEndSessionEvents().size());
    }

    public void testLoadDirectory() {
        loader.load(TEST_DIRNAME);

        assertEquals("Begin Session",   0, getBeginSessionEvents().size());
        assertEquals("Begin Group",     1, getBeginGroupEvents().size());
        assertEquals("Begin File",      7, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 6, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   6, getEndClassfileEvents().size());
        assertEquals("End File",        7, getEndFileEvents().size());
        assertEquals("End Group",       1, getEndGroupEvents().size());
        assertEquals("End Session",     0, getEndSessionEvents().size());
    }
    
    public void testMultipleDirectories() {
        loader.load(TEST_DIRNAME);
        loader.load(OTHER_DIRNAME);

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      2, getBeginGroupEvents().size());
        assertEquals("Begin File",      11, getBeginFileEvents().size());
        assertEquals("Begin Classfile",  9, getBeginClassfileEvents().size());
        assertEquals("End Classfile",    9, getEndClassfileEvents().size());
        assertEquals("End File",        11, getEndFileEvents().size());
        assertEquals("End Group",        2, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());
    }
}
