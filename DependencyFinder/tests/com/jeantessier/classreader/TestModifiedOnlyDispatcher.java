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

import junit.framework.*;

public class TestModifiedOnlyDispatcher extends TestCase {
    private MockDispatcher            mockDispatcher;
    private ClassfileLoaderDispatcher dispatcher;

    private String testDirname;
    private String testFilename;

    protected void setUp() throws Exception {
        super.setUp();
        
        mockDispatcher = new MockDispatcher();
        dispatcher = new ModifiedOnlyDispatcher(mockDispatcher);

        testDirname  = "classes";
        testFilename = testDirname + File.separator + getClass().getName() + "." + getName() + ".txt";
    }

    protected void tearDown() throws Exception {
        File file = new File(testFilename);
        file.delete();

        super.tearDown();
    }
    
    public void testDispatchNonExistingFile() {
        assertEquals("dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("delegated calls", 1, mockDispatcher.getDispatchCount(testFilename));
    }

    public void testDispatchNewClassFile() throws IOException {
        createFile();
        mockDispatcher.setReturnedAction(ClassfileLoaderAction.CLASS);

        assertEquals("dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("delegated calls", 1, mockDispatcher.getDispatchCount(testFilename));
    }
    
    public void testDispatchIdenticalClassFile() throws IOException {
        createFile();
        mockDispatcher.setReturnedAction(ClassfileLoaderAction.CLASS);

        assertEquals("first dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("first delegated calls", 1, mockDispatcher.getDispatchCount(testFilename));

        assertEquals("repeat dispatch action", ClassfileLoaderAction.IGNORE, dispatcher.dispatch(testFilename));
        assertEquals("repeat delegated calls", 2, mockDispatcher.getDispatchCount(testFilename));
    }

    public void testDispatchModifiedClassFile() throws IOException {
        createFile();
        mockDispatcher.setReturnedAction(ClassfileLoaderAction.CLASS);

        assertEquals("first dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("first delegated calls", 1, mockDispatcher.getDispatchCount(testFilename));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            // Ignore
        }
        createFile();
        
        assertEquals("repeat dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("repeat delegated calls", 2, mockDispatcher.getDispatchCount(testFilename));
    }

    public void testDispatchDirectory() {
        mockDispatcher.setReturnedAction(ClassfileLoaderAction.DIRECTORY);

        assertEquals("first dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testDirname));
        assertEquals("first delegated calls", 1, mockDispatcher.getDispatchCount(testDirname));

        assertEquals("repeat dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testDirname));
        assertEquals("repeat delegated calls", 2, mockDispatcher.getDispatchCount(testDirname));
    }

    public void testDispatchIdenticalZipFile() throws IOException {
        createFile();
        mockDispatcher.setReturnedAction(ClassfileLoaderAction.ZIP);

        assertEquals("first dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("first delegated calls", 1, mockDispatcher.getDispatchCount(testFilename));

        assertEquals("repeat dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("repeat delegated calls", 2, mockDispatcher.getDispatchCount(testFilename));
    }

    public void testDispatchIdenticalJarFile() throws IOException {
        createFile();
        mockDispatcher.setReturnedAction(ClassfileLoaderAction.JAR);

        assertEquals("first dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("first delegated calls", 1, mockDispatcher.getDispatchCount(testFilename));

        assertEquals("repeat dispatch action", mockDispatcher.getReturnedAction(), dispatcher.dispatch(testFilename));
        assertEquals("repeat delegated calls", 2, mockDispatcher.getDispatchCount(testFilename));
    }

    private void createFile() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(testFilename));
        out.println("foobar");
        out.close();
    }
}
