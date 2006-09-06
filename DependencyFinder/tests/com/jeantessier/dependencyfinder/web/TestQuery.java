package com.jeantessier.dependencyfinder.web;

import java.io.*;

import com.meterware.httpunit.*;
import com.meterware.servletunit.*;
import com.jeantessier.dependency.*;
import junit.framework.*;
import org.apache.log4j.*;

public class TestQuery extends TestCase {
    private static final String NO_GRAPH_MESSAGE = "There is no dependency graph at this time.";

    private NodeFactory factory;
    FeatureNode a_A_a;
    FeatureNode b_B_b;
    FeatureNode c_C_c;

    private ServletUnitClient client;

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(getClass()).setLevel(Level.ALL);

        factory = new NodeFactory();
        a_A_a = factory.createFeature("a.A.a");
        b_B_b = factory.createFeature("b.B.b");
        c_C_c = factory.createFeature("c.C.c");

        ServletRunner runner = new ServletRunner(new File("web/WEB-INF/web.xml"), "/web");
        client = runner.newClient();
    }

    public void testNoDependencyGraph() throws Exception {
        WebRequest request = new PostMethodWebRequest("http://localhost/web/query.jsp");
        InvocationContext context = client.newInvocation(request);
        context.service();
        WebResponse response = context.getServletResponse();
        assertTrue("Missing text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testEmptyDependencyGraph() throws Exception {
        client.getSession(true).getServletContext().setAttribute("factory", factory);
        WebRequest request = new PostMethodWebRequest("http://localhost/web/query.jsp");
        InvocationContext context = client.newInvocation(request);
        context.service();
        WebResponse response = context.getServletResponse();
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testTestDependencyGraph() throws Exception {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        client.getSession(true).getServletContext().setAttribute("factory", factory);
        WebRequest request = new PostMethodWebRequest("http://localhost/web/query.jsp");
        InvocationContext context = client.newInvocation(request);
        context.service();
        WebResponse response = context.getServletResponse();
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));
    }

    public void testQuery() throws Exception {
        a_A_a.addDependency(b_B_b);
        b_B_b.addDependency(c_C_c);

        client.getSession(true).getServletContext().setAttribute("factory", factory);
        WebRequest request = new PostMethodWebRequest("http://localhost/web/query.jsp");
        InvocationContext context = client.newInvocation(request);
        context.service();
        WebResponse response = context.getServletResponse();
        assertFalse("Unexpected text \"" + NO_GRAPH_MESSAGE + "\"", response.getText().contains(NO_GRAPH_MESSAGE));

        response.getForms()[0].getSubmitButton("submit").click();
        Logger.getLogger(getClass()).debug(response.getText());
        String a2b = "a --&gt; b";
        assertFalse("Missing text \"" + a2b + "\"", response.getText().contains(a2b));
    }
}
