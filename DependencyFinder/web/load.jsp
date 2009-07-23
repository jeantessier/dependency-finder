<%@ page import="java.io.*, java.text.*, java.util.*, javax.servlet.jsp.*, org.apache.oro.text.perl.*, org.xml.sax.*, com.jeantessier.dependency.*" %>
<%@ page errorPage="errorpage.jsp" %>

<!--
    Copyright (c) 2001-2009, Jean Tessier
    All rights reserved.
    
    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:
    
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
    
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
    
        * Neither the name of Jean Tessier nor the names of his contributors
          may be used to endorse or promote products derived from this software
          without specific prior written permission.
    
    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
    CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
    EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
    PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
    PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
    LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
    NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->

<jsp:useBean id="version" class="com.jeantessier.dependencyfinder.Version" scope="application"/>

<%!
    private class VerboseListener implements DependencyListener {
        private JspWriter out;

        public VerboseListener(JspWriter out) {
            this.out = out;
        }

        public void beginSession(DependencyEvent event) {
            // Do nothing
        }

        public void beginClass(DependencyEvent event) {
            try {
                out.print("Loading dependencies for ");
                out.print(event.getClassName());
                out.print(" ...");
                out.println();
            } catch (IOException ex) {
                // Ignore
            }
        }

        public void dependency(DependencyEvent event) {
            // Do nothing
        }

        public void endClass(DependencyEvent event) {
            // Do nothing
        }

        public void endSession(DependencyEvent event) {
            // Do nothing
        }
    }
%>

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<link rel="shortcut icon" href="images/logoicon.gif" type="image/gif" />
<title>Load <%= application.getInitParameter("name") %></title>
</head>

<!-- Reading the parameters and setting up the forms -->

<%
    String label = request.getParameter("label");
    if (label == null) {
        label = (String) application.getAttribute("label");
    }
%>

<body>

<table cellpadding="5">
    <tr>
        <td>

<div class="title">
<span id="name"><%= application.getInitParameter("name") %></span>
<% if (label != null ) { %>
<span id="label"><%= label %></span>
<% } %>
</div>

        </td>
    </tr>
    <tr>
        <td>
            <table border="0" class="controls" width="100%"><tr>

            <th><fieldset class="navigation"><a href="query.jsp">Dependency graph</a></fieldset></th>
            <th><fieldset class="navigation"><a href="closure.jsp">Transitive closure</a></fieldset></th>
            <th><fieldset class="navigation"><a href="cycles.jsp">Dependency cycles</a></fieldset></th>
            <th><fieldset class="navigation"><a href="metrics.jsp">Dependency metrics</a></fieldset></th>

            </tr></table>
        </td>
    </tr>

<%
    Perl5Util perl = new Perl5Util();
    Collection<String> files = new LinkedList<String>();
    perl.split(files, "/,\\s*/", application.getInitParameter("file"));

    if (request.getParameter("launch") == null) {
        if (Boolean.valueOf(application.getInitParameter("showFile"))) {
%>

    <tr>
        <td>
            <br />
            This operation will extract dependencies from the following
            locations:
            <ul>

<%
            for (String filename : files) {
                if (new File(filename).exists()) {
%>
                    <li><tt><%= filename %></tt></li>
<%
                } else {
%>
                    <li><tt><span class="error"><%= filename %></span></tt></li>
<%
                }
            }
%>

            </ul>
        </td>
    </tr>

<%
        }
%>

    <tr>
        <td>
            <br />
            This operation may take a few minutes, depending on the
            size and complexity of the graph to load.<br/>
            If you really want to do this at this time, please click
            on the <i>Launch</i> button.
        </td>
    </tr>
    <tr>
        <td align="center">
            <br />
            <form method="post" action="<%= request.getRequestURI() %>">
                <input type="submit" name="launch" value="Launch"/>
                <font size="smaller">optional label:</font> <input type="text" name="label" value="<%= (label != null) ? label : "" %>" />
            </form>
        </td>
    </tr>
    <tr>
        <td>
            <table>

<%
        if (application.getAttribute("factory") != null) {
%>
                <tr><td valign="top" rowspan="3">The current graph contains:</td><td align="right"><%= ((NodeFactory) application.getAttribute("factory")).getPackages().size() %></td><td>packages</td></tr>
                <tr><td align="right"><%= ((NodeFactory) application.getAttribute("factory")).getClasses().size() %></td><td>classes</td></tr>
                <tr><td align="right"><%= ((NodeFactory) application.getAttribute("factory")).getFeatures().size() %></td><td>features</td></tr>
<%
            if (application.getAttribute("loadStart") != null) {
%>
                <tr><td>&nbsp;</td></tr>
                <tr><td colspan="3">Loading it took <%= application.getAttribute("loadDuration") %> second(s) on <%= application.getAttribute("loadStart") %>.</td></tr>

<%
            }
        } else {
%>
                There is no dependency graph at this time.
<%
        }
%>

            </table>
        </td>
    </tr>
</table>

<%
    } else {
%>
</table>

<p>Loading dependency graph for <b><code><%= application.getInitParameter("name") %></code></b></p>

<pre class="result">

<%
        Date start = new Date();
        VerboseListener listener = new VerboseListener(out);

        NodeFactory factory = new NodeFactory();
        NodeLoader loader = new NodeLoader(factory);
        loader.addDependencyListener(listener);

        for (String filename : files) {
            try {
                loader.load(filename);
            } catch (SAXException ex) {
                out.println("<i class=\"error\">Could not load graph from file \"" + filename + "\": " + ex.getMessage() + "</i>");
            } catch (FileNotFoundException ex) {
                out.println("<i class=\"error\">Could not load graph from file \"" + filename + "\": " + ex.getMessage() + "</i>");
            }
        }

        if ("maximize".equalsIgnoreCase(application.getInitParameter("mode"))) {
            out.println("Maximizing ...");
            new LinkMaximizer().traverseNodes(factory.getPackages().values());
        } else if ("minimize".equalsIgnoreCase(application.getInitParameter("mode"))) {
            out.println("Minimizing ...");
            new LinkMinimizer().traverseNodes(factory.getPackages().values());
        }

        Date   stop     = new Date();
        double duration = (stop.getTime() - start.getTime()) / (double) 1000;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        application.setAttribute("factory",      factory);
        application.setAttribute("loadStart",    formatter.format(start));
        application.setAttribute("loadDuration", duration);

        if ("".equals(request.getParameter("label"))) {
            application.setAttribute("label", null);
        } else {
            application.setAttribute("label", request.getParameter("label"));
        }

        application.removeAttribute("dispatcher");
        application.removeAttribute("monitor");
        application.removeAttribute("extractStart");
        application.removeAttribute("extractDuration");
        application.removeAttribute("updateStart");
        application.removeAttribute("updateDuration");
%>

</pre>

<%
        switch (files.size()) {
            case 0:
                out.println("<p>Loaded nothing in " + duration + " secs.</p>");
                break;
            case 1:
                out.println("<p>Loaded 1 file in " + duration + " secs.</p>");
                break;
            default:
                out.println("<p>Loaded " + files.size() + " files in " + duration + " secs.</p>");
                break;
        }
    }
%>

<jsp:include page="footer.jsp"/>

</body>

</html>
