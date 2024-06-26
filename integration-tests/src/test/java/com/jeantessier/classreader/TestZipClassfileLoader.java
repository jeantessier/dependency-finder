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

import java.io.*;
import java.nio.file.*;

public class TestZipClassfileLoader extends TestClassfileLoaderBase {
    public static final String CLASSFILE_NAME = Paths.get("jarjardiff/old/build/classes/java/main/ModifiedPackage/ModifiedClass.class").toString();
    private ClassfileLoader loader;

    protected void setUp() throws Exception {
        super.setUp();

        ClassfileLoader eventSource = new TransientClassfileLoader();
        eventSource.addLoadListener(this);
        loader = new ZipClassfileLoader(eventSource);
    }

    public void testLoadFile() {
        String filename = ONELEVEL_ZIP;
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename);

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      2, getBeginGroupEvents().size());
        assertEquals("Begin File",      32, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        32, getEndFileEvents().size());
        assertEquals("End Group",        2, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", 32, getBeginGroupEvents().getFirst().getSize());
    }

    public void testLoadFileWrongName() {
        String filename = ONELEVEL_MISC;
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename);

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      2, getBeginGroupEvents().size());
        assertEquals("Begin File",      32, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        32, getEndFileEvents().size());
        assertEquals("End Group",        2, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", 32, getBeginGroupEvents().getFirst().getSize());
    }

    public void testLoadWrongFile() {
        String filename = CLASSFILE_NAME;
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename);

        assertEquals("Begin Session",   0, getBeginSessionEvents().size());
        assertEquals("Begin Group",     0, getBeginGroupEvents().size());
        assertEquals("Begin File",      0, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 0, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   0, getEndClassfileEvents().size());
        assertEquals("End File",        0, getEndFileEvents().size());
        assertEquals("End Group",       0, getEndGroupEvents().size());
        assertEquals("End Session",     0, getEndSessionEvents().size());
    }

    public void testLoadInputStream() throws IOException {
        String filename = ONELEVEL_ZIP;
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename, new FileInputStream(filename));

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      2, getBeginGroupEvents().size());
        assertEquals("Begin File",      32, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        32, getEndFileEvents().size());
        assertEquals("End Group",        2, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", -1, getBeginGroupEvents().getFirst().getSize());
    }

    public void testLoadInputStreamWrongName() throws IOException {
        String filename = ONELEVEL_MISC;
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename, new FileInputStream(filename));

        assertEquals("Begin Session",    0, getBeginSessionEvents().size());
        assertEquals("Begin Group",      2, getBeginGroupEvents().size());
        assertEquals("Begin File",      32, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 14, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   14, getEndClassfileEvents().size());
        assertEquals("End File",        32, getEndFileEvents().size());
        assertEquals("End Group",        2, getEndGroupEvents().size());
        assertEquals("End Session",      0, getEndSessionEvents().size());

        assertEquals("Group size", -1, getBeginGroupEvents().getFirst().getSize());
    }

    public void testLoadWrongInputStream() throws IOException {
        String filename = CLASSFILE_NAME;
        assertTrue(filename + " missing", new File(filename).exists());
        
        loader.load(filename, new FileInputStream(filename));

        assertEquals("Begin Session",   0, getBeginSessionEvents().size());
        assertEquals("Begin Group",     1, getBeginGroupEvents().size());
        assertEquals("Begin File",      0, getBeginFileEvents().size());
        assertEquals("Begin Classfile", 0, getBeginClassfileEvents().size());
        assertEquals("End Classfile",   0, getEndClassfileEvents().size());
        assertEquals("End File",        0, getEndFileEvents().size());
        assertEquals("End Group",       1, getEndGroupEvents().size());
        assertEquals("End Session",     0, getEndSessionEvents().size());
    }
}
