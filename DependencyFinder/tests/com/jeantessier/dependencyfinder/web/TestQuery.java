package com.jeantessier.dependencyfinder.web;

import java.io.*;
import java.util.*;

import com.meterware.httpunit.*;
import com.meterware.servletunit.*;
import com.jeantessier.dependency.*;
import junit.framework.*;
import org.apache.log4j.*;

public class TestQuery extends TestCase {
    private static final String NO_GRAPH_MESSAGE = "There is no dependency graph at this time.";

    private NodeFactory factory;
    FeatureNode foo;
    FeatureNode bar;
    FeatureNode baz;

    private ServletUnitClient client;
    private WebRequest request;
    private InvocationContext context;

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).setLevel(Level.ALL);

        Random random = new Random();

        factory = new NodeFactory();
        foo = factory.createFeature("foo" + random.nextLong() + ".Foo" + random.nextLong() + ".foo" + random.nextLong());
        bar = factory.createFeature("bar" + random.nextLong() + ".Bar" + random.nextLong() + ".bar" + random.nextLong());
        baz = factory.createFeature("baz" + random.nextLong() + ".Baz" + random.nextLong() + ".baz" + random.nextLong());

        ServletRunner runner = new ServletRunner(new File("web/WEB-INF/web.xml"), "/web");
        client = runner.newClient();
        request = new GetMethodWebRequest("http://localhost/web/query.jsp");
        context = client.newInvocation(request);
    }

    public void testNoDependencyGraph() throws Exception {
        context.service();
        WebResponse response = client.getResponse(request);
        assertTrue("Missing text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testEmptyDependencyGraph() throws Exception {
        client.getSession(true).getServletContext().setAttribute("factory", factory);

        context.service();
        WebResponse response = client.getResponse(request);
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testTestDependencyGraph() throws Exception {
        foo.addDependency(bar);
        bar.addDependency(baz);

        client.getSession(true).getServletContext().setAttribute("factory", factory);

        context.service();
        WebResponse response = client.getResponse(request);
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testFormSubmit() throws Exception {
        foo.addDependency(bar);
        bar.addDependency(baz);

        client.getSession(true).getServletContext().setAttribute("factory", factory);

        context.service();
        WebResponse response = client.getResponse(request);

        assertEquals("Nb forms", 1, response.getForms().length);
        WebForm form = response.getForms()[0];
        assertEquals("Nb submit buttons", 1, form.getSubmitButtons().length);
        SubmitButton button = form.getSubmitButtons()[0];

        response = form.submit(button);

        assertNotNull("Missing link to " + foo.getClassNode().getPackageNode().getName(), response.getLinkWith(foo.getClassNode().getPackageNode().getName()));
        assertNull("Unwanted link to " + foo.getClassNode().getName(), response.getLinkWith(foo.getClassNode().getName()));
        assertNull("Unwanted link to " + foo.getName(), response.getLinkWith(foo.getName()));
        assertNotNull("Missing link to " + bar.getClassNode().getPackageNode().getName(), response.getLinkWith(bar.getClassNode().getPackageNode().getName()));
        assertNull("Unwanted link to " + bar.getClassNode().getName(), response.getLinkWith(bar.getClassNode().getName()));
        assertNull("Unwanted link to " + bar.getName(), response.getLinkWith(bar.getName()));
        assertNotNull("Missing link to " + baz.getClassNode().getPackageNode().getName(), response.getLinkWith(baz.getClassNode().getPackageNode().getName()));
        assertNull("Unwanted link to " + baz.getClassNode().getName(), response.getLinkWith(baz.getClassNode().getName()));
        assertNull("Unwanted link to " + baz.getName(), response.getLinkWith(baz.getName()));
    }

    public void testQuery() throws Exception {
        foo.addDependency(bar);
        bar.addDependency(baz);

        client.getSession(true).getServletContext().setAttribute("factory", factory);

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

        assertNotNull("Missing link to " + foo.getClassNode().getPackageNode().getName(), response.getLinkWith(foo.getClassNode().getPackageNode().getName()));
        assertNull("Unwanted link to " + foo.getClassNode().getName(), response.getLinkWith(foo.getClassNode().getName()));
        assertNull("Unwanted link to " + foo.getName(), response.getLinkWith(foo.getName()));
        assertNotNull("Missing link to " + bar.getClassNode().getPackageNode().getName(), response.getLinkWith(bar.getClassNode().getPackageNode().getName()));
        assertNull("Unwanted link to " + bar.getClassNode().getName(), response.getLinkWith(bar.getClassNode().getName()));
        assertNull("Unwanted link to " + bar.getName(), response.getLinkWith(bar.getName()));
        assertNotNull("Missing link to " + baz.getClassNode().getPackageNode().getName(), response.getLinkWith(baz.getClassNode().getPackageNode().getName()));
        assertNull("Unwanted link to " + baz.getClassNode().getName(), response.getLinkWith(baz.getClassNode().getName()));
        assertNull("Unwanted link to " + baz.getName(), response.getLinkWith(baz.getName()));
    }
}
