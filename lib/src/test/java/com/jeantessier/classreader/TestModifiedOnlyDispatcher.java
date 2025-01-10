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

import org.jmock.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import com.jeantessier.MockObjectTestCase;

import static org.junit.jupiter.api.Assertions.*;

public class TestModifiedOnlyDispatcher extends MockObjectTestCase {
    private final ClassfileLoaderDispatcher delegateDispatcher = mock(ClassfileLoaderDispatcher.class);
    private final ClassfileLoaderDispatcher sut = new ModifiedOnlyDispatcher(delegateDispatcher);

    private String testFilename = "no_such_file.txt";

    @Test
    void testDispatchNonExistingFile() {
        // Given
        checking(new Expectations() {{
            oneOf (delegateDispatcher).dispatch(testFilename);
                will(returnValue(ClassfileLoaderAction.CLASS));
        }});

        // When
        var actualAction = sut.dispatch(testFilename);

        // Then
        assertEquals(ClassfileLoaderAction.CLASS, actualAction, "dispatch action");
    }

    @Test
    void testDispatchNewClassFile() throws IOException {
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
        assertEquals(ClassfileLoaderAction.CLASS, actualAction, "dispatch action");
    }
    
    @Test
    void testDispatchIdenticalClassFile() throws IOException {
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
        assertEquals(ClassfileLoaderAction.CLASS, primingAction, "dispatch action");
        assertEquals(ClassfileLoaderAction.IGNORE, actualAction, "repeat dispatch action");
    }

    @Test
    void testDispatchModifiedClassFile() throws IOException {
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

        assertEquals(ClassfileLoaderAction.CLASS, actualAction, "repeat dispatch action");
    }

    @Test
    void testDispatchDirectory() {
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
        assertEquals(ClassfileLoaderAction.DIRECTORY, primingAction, "dispatch action");
        assertEquals(ClassfileLoaderAction.DIRECTORY, actualAction, "repeat dispatch action");
    }

    @Test
    void testDispatchIdenticalZipFile() throws IOException {
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
        assertEquals(ClassfileLoaderAction.ZIP, primingAction, "dispatch action");
        assertEquals(ClassfileLoaderAction.ZIP, actualAction, "repeat dispatch action");
    }

    @Test
    void testDispatchIdenticalJarFile() throws IOException {
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
        assertEquals(ClassfileLoaderAction.JAR, primingAction, "dispatch action");
        assertEquals(ClassfileLoaderAction.JAR, actualAction, "repeat dispatch action");
    }

    private void createFile() throws IOException {
        var tempFile = File.createTempFile(getClass().getSimpleName() + new Random().nextInt(1_000), ".txt");
        tempFile.deleteOnExit();
        testFilename = tempFile.getAbsolutePath();
    }

    private void touchFile() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(testFilename));
        out.println("foobar");
        out.close();
    }
}
