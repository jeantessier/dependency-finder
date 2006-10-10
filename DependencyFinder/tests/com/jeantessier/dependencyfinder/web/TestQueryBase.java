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

package com.jeantessier.dependencyfinder.web;

import java.util.*;
import java.io.*;

import junit.framework.*;
import org.apache.log4j.*;

import com.jeantessier.dependency.*;

import com.meterware.servletunit.*;
import com.meterware.httpunit.*;

public abstract class TestQueryBase extends TestCase {
    private static final String NO_GRAPH_MESSAGE = "There is no dependency graph at this time.";

    private String fooPackageName;
    private String fooClassName;
    private String fooFeatureName;
    private String barPackageName;
    private String barClassName;
    private String barFeatureName;
    private String bazPackageName;
    private String bazClassName;
    private String bazFeatureName;
    private String leftPackageName;
    private String leftClassName;
    private String leftFeatureName;
    private String rightPackageName;
    private String rightClassName;
    private String rightFeatureName;

    private ServletUnitClient client;
    private WebRequest request;
    private InvocationContext context;

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).setLevel(Level.ALL);

        Random random = new Random();

        fooPackageName = "foo" + random.nextLong();
        fooClassName = fooPackageName + ".Foo" + random.nextLong();
        fooFeatureName = fooClassName + ".foo" + random.nextLong();
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

        NodeFactory factory = new NodeFactory();
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

        client.getSession(true).getServletContext().setAttribute("factory", factory);
    }

    public void testNoDependencyGraph() throws Exception {
        client.getSession(true).getServletContext().removeAttribute("factory");

        context.service();
        WebResponse response = client.getResponse(request);
        assertTrue("Missing text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testEmptyDependencyGraph() throws Exception {
        client.getSession(true).getServletContext().setAttribute("factory", new NodeFactory());

        context.service();
        WebResponse response = client.getResponse(request);
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testTestDependencyGraph() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testFormSubmit() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);

        assertEquals("Nb forms", 1, response.getForms().length);
        WebForm form = response.getForms()[0];
        assertEquals("Nb submit buttons", 1, form.getSubmitButtons().length);
        SubmitButton button = form.getSubmitButtons()[0];

        response = form.submit(button);

        assertNotNull("Missing link to " + fooPackageName, response.getLinkWith(fooPackageName));
        assertNull("Unwanted link to " + fooClassName, response.getLinkWith(fooClassName));
        assertNull("Unwanted link to " + fooFeatureName, response.getLinkWith(fooFeatureName));
        assertNotNull("Missing link to " + barPackageName, response.getLinkWith(barPackageName));
        assertNull("Unwanted link to " + barClassName, response.getLinkWith(barClassName));
        assertNull("Unwanted link to " + barFeatureName, response.getLinkWith(barFeatureName));
        assertNotNull("Missing link to " + bazPackageName, response.getLinkWith(bazPackageName));
        assertNull("Unwanted link to " + bazClassName, response.getLinkWith(bazClassName));
        assertNull("Unwanted link to " + bazFeatureName, response.getLinkWith(bazFeatureName));
        assertNotNull("Missing link to " + leftPackageName, response.getLinkWith(leftPackageName));
        assertNull("Unwanted link to " + leftClassName, response.getLinkWith(leftClassName));
        assertNull("Unwanted link to " + leftFeatureName, response.getLinkWith(leftFeatureName));
        assertNotNull("Missing link to " + rightPackageName, response.getLinkWith(rightPackageName));
        assertNull("Unwanted link to " + rightClassName, response.getLinkWith(rightClassName));
        assertNull("Unwanted link to " + rightFeatureName, response.getLinkWith(rightFeatureName));
    }

    public void testDirectQuery() throws Exception {
        request.setParameter("scope-includes", "//");
        request.setParameter("package-scope", "on");
        request.setParameter("filter-includes", "//");
        request.setParameter("package-filter", "on");
        request.setParameter("show-inbounds", "on");
        request.setParameter("show-outbounds", "on");
        request.setParameter("show-empty-nodes", "on");
        request.setParameter("submit", "Run Query");

        context.service();
        WebResponse response = client.getResponse(request);

        assertNotNull("Missing link to " + fooPackageName, response.getLinkWith(fooPackageName));
        assertNull("Unwanted link to " + fooClassName, response.getLinkWith(fooClassName));
        assertNull("Unwanted link to " + fooFeatureName, response.getLinkWith(fooFeatureName));
        assertNotNull("Missing link to " + barPackageName, response.getLinkWith(barPackageName));
        assertNull("Unwanted link to " + barClassName, response.getLinkWith(barClassName));
        assertNull("Unwanted link to " + barFeatureName, response.getLinkWith(barFeatureName));
        assertNotNull("Missing link to " + bazPackageName, response.getLinkWith(bazPackageName));
        assertNull("Unwanted link to " + bazClassName, response.getLinkWith(bazClassName));
        assertNull("Unwanted link to " + bazFeatureName, response.getLinkWith(bazFeatureName));
        assertNotNull("Missing link to " + leftPackageName, response.getLinkWith(leftPackageName));
        assertNull("Unwanted link to " + leftClassName, response.getLinkWith(leftClassName));
        assertNull("Unwanted link to " + leftFeatureName, response.getLinkWith(leftFeatureName));
        assertNotNull("Missing link to " + rightPackageName, response.getLinkWith(rightPackageName));
        assertNull("Unwanted link to " + rightClassName, response.getLinkWith(rightClassName));
        assertNull("Unwanted link to " + rightFeatureName, response.getLinkWith(rightFeatureName));

        assertNotNull("Missing link foo", response.getLinkWithID(fooPackageName));
        assertNotNull("Missing link foo --> bar", response.getLinkWithID(fooPackageName + "_to_" + barPackageName));
        assertNotNull("Missing link bar", response.getLinkWithID(barPackageName));
        assertNotNull("Missing link bar <-- foo", response.getLinkWithID(barPackageName + "_from_" + fooPackageName));
        assertNotNull("Missing link bar --> baz", response.getLinkWithID(barPackageName + "_to_" + bazPackageName));
        assertNotNull("Missing link baz", response.getLinkWithID(bazPackageName));
        assertNotNull("Missing link baz <-- bar", response.getLinkWithID(bazPackageName + "_from_" + barPackageName));
        assertNotNull("Missing link left", response.getLinkWithID(leftPackageName));
        assertNotNull("Missing link left <-> right", response.getLinkWithID(leftPackageName + "_bidirectional_" + rightPackageName));
        assertNotNull("Missing link right", response.getLinkWithID(rightPackageName));
        assertNotNull("Missing link right <-> left", response.getLinkWithID(rightPackageName + "_bidirectional_" + leftPackageName));
    }

    protected abstract String getStartUrl();
}
