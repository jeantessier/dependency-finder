/*
 *  Copyright (c) 2001-2006, Jean Tessier
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

public class TestStrictDispatcher extends TestCase {
    private ClassfileLoaderDispatcher dispatcher;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        dispatcher = new StrictDispatcher();
    }
    
    public void testDispatch() {
        assertEquals("foo.class",      ClassfileLoaderDispatcher.ACTION_CLASS,     dispatcher.dispatch("foo.class"));
        
        assertEquals("src",            ClassfileLoaderDispatcher.ACTION_DIRECTORY, dispatcher.dispatch("src"));

        assertEquals("MANIFEST.MF",    ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("MANIFEST.MF"));
        assertEquals("foo.bat",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.bat"));
        assertEquals("foo.css",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.css"));
        assertEquals("foo.dtd",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.dtd"));
        assertEquals("foo.gif",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.gif"));
        assertEquals("foo.htm",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.htm"));
        assertEquals("foo.html",       ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.html"));
        assertEquals("foo.java",       ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.java"));
        assertEquals("foo.jpeg",       ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.jpeg"));
        assertEquals("foo.jpg",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.jpg"));
        assertEquals("foo.js",         ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.js"));
        assertEquals("foo.jsp",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.jsp"));
        assertEquals("foo.properties", ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.properties"));
        assertEquals("foo.ps",         ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.ps"));
        assertEquals("foo.txt",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.txt"));
        assertEquals("foo.xml",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.xml"));
        assertEquals("foo.xsl",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.xsl"));
        assertEquals("foo/",           ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo/"));
        
        assertEquals("foo.jar",        ClassfileLoaderDispatcher.ACTION_JAR,       dispatcher.dispatch("foo.jar"));

        assertEquals("foo.zip",        ClassfileLoaderDispatcher.ACTION_ZIP,       dispatcher.dispatch("foo.zip"));

        assertEquals("foo.foo",        ClassfileLoaderDispatcher.ACTION_IGNORE,    dispatcher.dispatch("foo.foo"));
    }
}
