/*
 *  Copyright (c) 2001-2007, Jean Tessier
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

import java.util.*;
import java.io.*;

import junit.framework.*;

import com.jeantessier.dependency.*;
import com.jeantessier.classreader.*;

import com.opensymphony.xwork.*;

public class TestExtractAction extends TestCase {
    private Map application;

    private ExtractAction action;

    protected void setUp() throws Exception {
        super.setUp();

        application = new HashMap();
        application.put("source", "classes" + File.separator + "test.class");

        action = new ExtractAction();
    }

    public void testFirstExtract() {
        action.setApplication(application);
        String result = action.doExtract();

        assertEquals("Result", Action.SUCCESS, result);
        assertNotNull("Missing dispatcher", application.get("dispatcher"));
        assertNotNull("Missing factory", application.get("factory"));
        assertNotNull("Missing monitor", application.get("monitor"));
    }

    public void testUpdate() {
        ClassfileLoaderDispatcher dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER);
        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory);
        DeletingVisitor deletingVisitor = new DeletingVisitor(factory);
        Monitor monitor = new Monitor(collector, deletingVisitor);

        application.put("dispatcher", dispatcher);
        application.put("factory", factory);
        application.put("monitor", monitor);

        action.setApplication(application);
        String result = action.doUpdate();

        assertEquals("Result", Action.SUCCESS, result);
        assertSame("Dispatcher", dispatcher, application.get("dispatcher"));
        assertSame("Factory", factory, application.get("factory"));
        assertSame("Monitor", monitor, application.get("monitor"));
    }

    public void testExtract() {
        ClassfileLoaderDispatcher dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER);
        NodeFactory factory = new NodeFactory();
        CodeDependencyCollector collector = new CodeDependencyCollector(factory);
        DeletingVisitor deletingVisitor = new DeletingVisitor(factory);
        Monitor monitor = new Monitor(collector, deletingVisitor);

        application.put("dispatcher", dispatcher);
        application.put("factory", factory);
        application.put("monitor", monitor);

        action.setApplication(application);
        String result = action.doExtract();

        assertEquals("Result", Action.SUCCESS, result);
        assertNotSame("Dispatcher", dispatcher, application.get("dispatcher"));
        assertNotSame("Factory", factory, application.get("factory"));
        assertNotSame("Monitor", monitor, application.get("monitor"));
    }
}
