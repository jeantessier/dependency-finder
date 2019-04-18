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

package com.jeantessier.dependencyfinder.web;

import java.io.*;
import java.util.*;

import javax.servlet.*;

import junit.framework.*;

import org.apache.log4j.*;

import com.meterware.httpunit.*;
import com.meterware.servletunit.*;

import com.jeantessier.dependency.*;

/**
 * Sets up a dependency graph like the one in <a href="graph.xml">graph.xml</a>.
 */
public abstract class TestBase extends TestCase {
    private static final String NO_GRAPH_MESSAGE = "There is no dependency graph at this time.";

    protected String fooPackageName;
    protected String fooClassName;
    protected String fooFeatureName;
    protected String foo2ClassName;
    protected String foo2FeatureName;
    protected String barPackageName;
    protected String barClassName;
    protected String barFeatureName;
    protected String bazPackageName;
    protected String bazClassName;
    protected String bazFeatureName;
    protected String leftPackageName;
    protected String leftClassName;
    protected String leftFeatureName;
    protected String rightPackageName;
    protected String rightClassName;
    protected String rightFeatureName;

    protected NodeFactory factory;
    protected ServletUnitClient client;
    protected WebRequest request;
    protected InvocationContext context;

    protected String label;

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).setLevel(Level.ALL);

        Random random = new Random();

        fooPackageName = "foo" + random.nextLong();
        fooClassName = fooPackageName + ".Foo" + random.nextLong();
        fooFeatureName = fooClassName + ".foo" + random.nextLong();
        foo2ClassName = fooPackageName + ".Foo2" + random.nextLong();
        foo2FeatureName = foo2ClassName + ".foo2" + random.nextLong();
        barPackageName = "bar" + random.nextLong();
        barClassName = barPackageName + ".Bar" + random.nextLong();
        barFeatureName = barClassName + ".bar" + random.nextLong();
        bazPackageName = "baz" + random.nextLong();
        bazClassName = bazPackageName + ".Baz" + random.nextLong();
        bazFeatureName = bazClassName + ".baz" + random.nextLong();
        leftPackageName = "left" + random.nextLong();
        leftClassName = leftPackageName + ".Left" + random.nextLong();
        leftFeatureName = leftClassName + ".left" + random.nextLong();
        rightPackageName = "right" + random.nextLong();
        rightClassName = rightPackageName + ".Right" + random.nextLong();
        rightFeatureName = rightClassName + ".right" + random.nextLong();

        factory = new NodeFactory();
        FeatureNode foo = factory.createFeature(fooFeatureName);
        FeatureNode bar = factory.createFeature(barFeatureName);
        FeatureNode baz = factory.createFeature(bazFeatureName);
        FeatureNode left = factory.createFeature(leftFeatureName);
        FeatureNode right = factory.createFeature(rightFeatureName);

        foo.addDependency(bar);
        bar.addDependency(baz);
        left.addDependency(right);
        right.addDependency(left);

        ServletRunner runner = new ServletRunner(new File("web/WEB-INF/web.xml"), "/web");
        client = runner.newClient();
        request = new GetMethodWebRequest(getStartUrl());
        context = client.newInvocation(request);

        label = "label " + random.nextLong();

        getApplication().setAttribute("label", label);
        getApplication().setAttribute("factory", factory);
    }

    public void testNoLabel() throws Exception {
        getApplication().removeAttribute("label");

        context.service();
        WebResponse response = client.getResponse(request);
        assertEquals("name", "Dependency Finder", response.getElementWithID("name").getText());
        assertNull("label", response.getElementWithID("label"));
    }

    public void testLabel() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);
        assertEquals("name", "Dependency Finder", response.getElementWithID("name").getText());
        assertEquals("label", label, response.getElementWithID("label").getText());
    }

    public void testNoDependencyGraph() throws Exception {
        getApplication().removeAttribute("factory");

        context.service();
        WebResponse response = client.getResponse(request);
        assertTrue("Missing text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testEmptyDependencyGraph() throws Exception {
        getApplication().setAttribute("factory", new NodeFactory());

        context.service();
        WebResponse response = client.getResponse(request);
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testTestDependencyGraph() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    protected ServletContext getApplication() {
      return client.getSession(true).getServletContext();
    }

    protected abstract String getStartUrl();
}
