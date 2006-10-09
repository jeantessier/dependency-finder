package com.jeantessier.dependencyfinder.web;

import java.io.*;
import java.util.*;

import com.jeantessier.dependency.*;
import com.meterware.httpunit.*;
import com.meterware.servletunit.*;
import junit.framework.*;
import org.apache.log4j.*;

public class TestQuery extends TestCase {
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

    FeatureNode foo;
    FeatureNode bar;
    FeatureNode baz;
    FeatureNode left;
    FeatureNode right;

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
        foo = factory.createFeature(fooFeatureName);
        bar = factory.createFeature(barFeatureName);
        baz = factory.createFeature(bazFeatureName);
        left = factory.createFeature(leftFeatureName);
        right = factory.createFeature(rightFeatureName);

        foo.addDependency(bar);
        bar.addDependency(baz);
        left.addDependency(right);
        right.addDependency(left);

        ServletRunner runner = new ServletRunner(new File("web/WEB-INF/web.xml"), "/web");
        client = runner.newClient();
        request = new GetMethodWebRequest("http://localhost/web/query.jsp");
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
}
