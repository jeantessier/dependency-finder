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

import org.jmock.*;
import org.jmock.integration.junit3.*;

public class TestModifiedOnlyDispatcher extends MockObjectTestCase {
    private ClassfileLoaderDispatcher delegateDispatcher;
    private ClassfileLoaderDispatcher sut;

    private String testFilename;

    protected void setUp() throws Exception {
        super.setUp();
        
        delegateDispatcher = mock(ClassfileLoaderDispatcher.class);
        sut = new ModifiedOnlyDispatcher(delegateDispatcher);

        testFilename = "no_such_file.txt";
    }

    public void testDispatchNonExistingFile() {
        // Given
        checking(new Expectations() {{
            oneOf (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.CLASS));
        }});

        // When
        var actualAction = sut.dispatch(testFilename);

        // Then
        assertEquals("dispatch action", ClassfileLoaderAction.CLASS, actualAction);
    }

    public void testDispatchNewClassFile() throws IOException {
        // Given
        createFile();

        // And
        checking(new Expectations() {{
            oneOf (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.CLASS));
        }});

        // When
        var actualAction = sut.dispatch(testFilename);

        // Then
        assertEquals("dispatch action", ClassfileLoaderAction.CLASS, actualAction);
    }
    
    public void testDispatchIdenticalClassFile() throws IOException {
        // Given
        createFile();

        // And
        checking(new Expectations() {{
            oneOf (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.CLASS));
            oneOf (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.IGNORE));
        }});

        // And prime the cache
        var primingAction = sut.dispatch(testFilename);

        // When
        var actualAction = sut.dispatch(testFilename);

        // Then
        assertEquals("dispatch action", ClassfileLoaderAction.CLASS, primingAction);
        assertEquals("repeat dispatch action", ClassfileLoaderAction.IGNORE, actualAction);
    }

    public void testDispatchModifiedClassFile() throws IOException {
        // Given
        createFile();

        // And
        checking(new Expectations() {{
            exactly(2).of (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.CLASS));
        }});

        // And prime the cache
        sut.dispatch(testFilename);

        // And modify the file since last dispatched
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            // Ignore
        }
        touchFile();

        // When
        var actualAction = sut.dispatch(testFilename);

        assertEquals("repeat dispatch action", ClassfileLoaderAction.CLASS, actualAction);
    }

    public void testDispatchDirectory() {
        // Given
        checking(new Expectations() {{
            exactly(2).of (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.DIRECTORY));
        }});

        // And prime the cache
        var primingAction = sut.dispatch(testFilename);

        // When
        var actualAction = sut.dispatch(testFilename);

        // Then
        assertEquals("dispatch action", ClassfileLoaderAction.DIRECTORY, primingAction);
        assertEquals("repeat dispatch action", ClassfileLoaderAction.DIRECTORY, actualAction);
    }

    public void testDispatchIdenticalZipFile() throws IOException {
        // Given
        createFile();

        // And
        checking(new Expectations() {{
            exactly(2).of (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.ZIP));
        }});

        // And prime the cache
        var primingAction = sut.dispatch(testFilename);

        // When
        var actualAction = sut.dispatch(testFilename);

        // Then
        assertEquals("dispatch action", ClassfileLoaderAction.ZIP, primingAction);
        assertEquals("repeat dispatch action", ClassfileLoaderAction.ZIP, actualAction);
    }

    public void testDispatchIdenticalJarFile() throws IOException {
        // Given
        createFile();

        // And
        checking(new Expectations() {{
            exactly(2).of (delegateDispatcher).dispatch(testFilename);
            will(returnValue(ClassfileLoaderAction.JAR));
        }});

        // And prime the cache
        var primingAction = sut.dispatch(testFilename);

        // When
        var actualAction = sut.dispatch(testFilename);

        // Then
        assertEquals("dispatch action", ClassfileLoaderAction.JAR, primingAction);
        assertEquals("repeat dispatch action", ClassfileLoaderAction.JAR, actualAction);
    }

    private void createFile() throws IOException {
        var tempFile = File.createTempFile(getClass() + "." + getName(), ".txt");
        tempFile.deleteOnExit();
        testFilename = tempFile.getAbsolutePath();
    }

    private void touchFile() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(testFilename));
        out.println("foobar");
        out.close();
    }
}
