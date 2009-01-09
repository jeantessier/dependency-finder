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

public class TestJarClassfileLoader extends TestClassfileLoaderBase {
    private ClassfileLoader loader;
    
    protected void setUp() throws Exception {
        super.setUp();

        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(this);
        loader = new JarClassfileLoader(eventSource);
    }

    public void testLoadFile() {
        String filename = TEST_DIR + File.separator + "onelevel.jar";
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename);

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      1, getBeginGroupEvents().size());
        assertEquals("Begin File",      33, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        33, getEndFileEvents().size());
        assertEquals("End Group",        1, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", 33, getBeginGroupEvents().getFirst().getSize());
    }

    public void testLoadWrongFile() {
        String filename = TEST_DIR + File.separator + "onelevel.mis";
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename);

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      1, getBeginGroupEvents().size());
        assertEquals("Begin File",      31, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        31, getEndFileEvents().size());
        assertEquals("End Group",        1, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", 31, getBeginGroupEvents().getFirst().getSize());
    }

    public void testLoadInputStream() throws IOException {
        String filename = TEST_DIR + File.separator + "onelevel.jar";
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename, new FileInputStream(filename));

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      1, getBeginGroupEvents().size());
        assertEquals("Begin File",      31, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        31, getEndFileEvents().size());
        assertEquals("End Group",        1, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", -1, getBeginGroupEvents().getFirst().getSize());
    }

    public void testLoadWrongInputStream() throws IOException {
        String filename = TEST_DIR + File.separator + "onelevel.mis";
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename, new FileInputStream(filename));

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      1, getBeginGroupEvents().size());
        assertEquals("Begin File",      31, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        31, getEndFileEvents().size());
        assertEquals("End Group",        1, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", -1, getBeginGroupEvents().getFirst().getSize());
    }
}
