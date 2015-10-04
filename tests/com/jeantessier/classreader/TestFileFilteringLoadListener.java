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

import java.util.*;

import org.jmock.integration.junit3.*;
import org.jmock.*;

public class TestFileFilteringLoadListener extends MockObjectTestCase {
    private LoadListener mockDelegate;

    private List<String> includes;
    private List<String> excludes;

    public LoadListener sut;

    protected void setUp() throws Exception {
        super.setUp();

        mockDelegate = mock(LoadListener.class);

        includes = new LinkedList<String>();
        excludes = new LinkedList<String>();

        sut = new FileFilteringLoadListener(mockDelegate, includes, excludes);
    }

    public void testBeginFileWithMatchingFileName() {
        includes.add("/Foo/");
        final LoadEvent testEvent = new LoadEvent(this, "", "Foo.class", null);

        checking(new Expectations() {{
            one (mockDelegate).beginFile(testEvent);
        }});

        sut.beginFile(testEvent);
    }

    public void testBeginFileMatchingFileNameOnSecondRE() {
        includes.add("/Foo/");
        includes.add("/Bar/");
        final LoadEvent testEvent = new LoadEvent(this, "", "Bar.class", null);

        checking(new Expectations() {{
            one (mockDelegate).beginFile(testEvent);
        }});

        sut.beginFile(testEvent);
    }

    public void testBeginFileNonMatchingFileName() {
        includes.add("/Foo/");
        final LoadEvent testEvent = new LoadEvent(this, "", "Bar.class", null);

        checking(new Expectations() {{
            never (mockDelegate).beginFile(testEvent);
        }});

        sut.beginFile(testEvent);
    }

    public void testBeginFileExcludingFileName() {
        includes.add("/Foo/");
        excludes.add("/Bar/");
        final LoadEvent testEvent = new LoadEvent(this, "", "FooBar.class", null);

        checking(new Expectations() {{
            never (mockDelegate).beginFile(testEvent);
        }});

        sut.beginFile(testEvent);
    }

    public void testEndFileWithMatchingFileName() {
        includes.add("/Foo/");
        final LoadEvent testEvent = new LoadEvent(this, "", "Foo.class", null);

        checking(new Expectations() {{
            one (mockDelegate).endFile(testEvent);
        }});

        sut.endFile(testEvent);
    }

    public void testEndFileMatchingFileNameOnSecondRE() {
        includes.add("/Foo/");
        includes.add("/Bar/");
        final LoadEvent testEvent = new LoadEvent(this, "", "Bar.class", null);

        checking(new Expectations() {{
            one (mockDelegate).endFile(testEvent);
        }});

        sut.endFile(testEvent);
    }

    public void testEndFileNonMatchingFileName() {
        includes.add("/Foo/");
        final LoadEvent testEvent = new LoadEvent(this, "", "Bar.class", null);

        checking(new Expectations() {{
            never (mockDelegate).endFile(testEvent);
        }});

        sut.endFile(testEvent);
    }

    public void testEndFileExcludingFileName() {
        includes.add("/Foo/");
        excludes.add("/Bar/");
        final LoadEvent testEvent = new LoadEvent(this, "", "FooBar.class", null);

        checking(new Expectations() {{
            never (mockDelegate).endFile(testEvent);
        }});

        sut.endFile(testEvent);
    }
}
