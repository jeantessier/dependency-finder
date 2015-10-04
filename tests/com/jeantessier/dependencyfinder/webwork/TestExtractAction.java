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

package com.jeantessier.dependencyfinder.webwork;

import java.io.*;
import java.util.*;

import com.opensymphony.xwork.*;
import static org.hamcrest.Matchers.*;
import static org.jmock.Expectations.*;
import org.jmock.*;
import org.jmock.integration.junit4.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;

import com.jeantessier.classreader.*;
import com.jeantessier.dependency.*;

@RunWith(JMock.class)
public class TestExtractAction {
    private Mockery context;

    private Map<String, Object> application;

    private ExtractAction sut;

    @Before
    public void setUp() throws Exception {
        context = new Mockery();

        application = new HashMap<String, Object>();
        application.put("source", "classes" + File.separator + "test.class");

        sut = new ExtractAction();
    }

    @Test
    public void testExecute() throws Exception {
        sut.setApplication(application);
        String result = sut.execute();

        assertThat("Result", result, is(equal(Action.INPUT)));
        assertThat("Unexpected dispatcher", (ClassfileLoaderDispatcher) application.get("dispatcher"), is(aNull(ClassfileLoaderDispatcher.class)));
        assertThat("Unexpected factory", (NodeFactory) application.get("factory"), is(aNull(NodeFactory.class)));
        assertThat("Unexpected monitor", (Monitor) application.get("monitor"), is(aNull(Monitor.class)));
        assertThat("Unexpected start", (Date) sut.getStart(), is(aNull(Date.class)));
        assertThat("Unexpected stop", (Date) sut.getStop(), is(aNull(Date.class)));
    }

    @Test
    public void testFirstExtract() {
        sut.setApplication(application);
        String result = sut.doExtract();

        assertThat("Result", result, is(equal(Action.SUCCESS)));
        assertThat("Missing dispatcher", (ClassfileLoaderDispatcher) application.get("dispatcher"), is(aNonNull(ClassfileLoaderDispatcher.class)));
        assertThat("Missing factory", (NodeFactory) application.get("factory"), is(aNonNull(NodeFactory.class)));
        assertThat("Missing monitor", (Monitor) application.get("monitor"), is(aNonNull(Monitor.class)));
    }

    @Test
    public void testSecondExtract() {
        ClassfileLoaderDispatcher dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER);
        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory);
        DeletingVisitor deletingVisitor = new DeletingVisitor(factory);
        Monitor monitor = new Monitor(collector, deletingVisitor);

        application.put("dispatcher", dispatcher);
        application.put("factory", factory);
        application.put("monitor", monitor);

        sut.setApplication(application);
        String result = sut.doExtract();

        assertThat("Result", result, is(equal(Action.SUCCESS)));
        assertThat("Dispatcher", (ClassfileLoaderDispatcher) application.get("dispatcher"), is(not(same(dispatcher))));
        assertThat("Factory", (NodeFactory) application.get("factory"), is(not(same(factory))));
        assertThat("Monitor", (Monitor) application.get("monitor"), is(not(same(monitor))));
    }

    @Test
    public void testUpdate() {
        ClassfileLoaderDispatcher dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER);
        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory);
        DeletingVisitor deletingVisitor = new DeletingVisitor(factory);
        Monitor monitor = new Monitor(collector, deletingVisitor);

        application.put("dispatcher", dispatcher);
        application.put("factory", factory);
        application.put("monitor", monitor);

        sut.setApplication(application);
        String result = sut.doUpdate();

        assertThat("Result", result, is(equal(Action.SUCCESS)));
        assertThat("Dispatcher", (ClassfileLoaderDispatcher) application.get("dispatcher"), is(same(dispatcher)));
        assertThat("Factory", (NodeFactory) application.get("factory"), is(same(factory)));
        assertThat("Monitor", (Monitor) application.get("monitor"), is(same(monitor)));
    }
}
