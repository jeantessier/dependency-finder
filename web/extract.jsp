<%@ page import="java.io.*, java.text.*, java.util.*, org.apache.oro.text.perl.*, com.jeantessier.classreader.*, com.jeantessier.dependency.*, com.jeantessier.dependencyfinder.*" %>
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
    private class VerboseListener extends VerboseListenerBase {
        private JspWriter out;

        private int classCount = 0;

        public VerboseListener(JspWriter out) {
            this.out = out;
        }

        public int getClassCount() {
            return classCount;
        }
        
        public void beginGroup(LoadEvent event) {
            super.beginGroup(event);

            try {
                out.println();
                out.print("\tSearching ");
                out.print(getCurrentGroup().getName());
                if (getCurrentGroup().getSize() >= 0) {
                    out.print(" (");
                    out.print(getCurrentGroup().getSize());
                    out.print(" files)");
                }
                out.print(" ...");
                out.println();
            } catch (IOException ex) {
                // Do nothing
            }
        }

        public void beginFile(LoadEvent event) {
            super.beginFile(event);

            try {
                out.print(getRatioIndicator());
            } catch (IOException ex) {
                // Ignore
            }
        }

        public void endClassfile(LoadEvent event) {
            super.endClassfile(event);

            classCount++;

            try {
                out.print("\t\tGetting dependencies from ");
                out.print(event.getClassfile());
                out.print(" ...");
                out.println();
            } catch (IOException ex) {
                // Ignore
            }
        }

        public void endFile(LoadEvent event) {
            super.endFile(event);

            try {
                if (!getVisitedFiles().contains(event.getFilename())) {
                    out.print("\t\t<i>Skipping ");
                    out.print(event.getFilename());
                    out.print(" ...</i>");
                    out.println();
                }
            } catch (IOException ex) {
                // Ignore
            }
        }
    }
%>

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<link rel="shortcut icon" href="images/logoicon.gif" type="image/gif" />
<title>Extract <%= application.getInitParameter("name") %></title>
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
    Collection<String> sources = new LinkedList<String>();
    perl.split(sources, "/,\\s*/", application.getInitParameter("source"));

    if (request.getParameter("launch") == null) {
        if (Boolean.valueOf(application.getInitParameter("showSource"))) {
%>

    <tr>
        <td>
            <br />
            This operation will extract dependencies from the following
            locations:
            <ul>

<%
            for (String filename : sources) {
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

            It will include dependencies on [<code><%= application.getInitParameter("filterIncludes") %></code>].
            <br />
            But it will exclude dependencies on [<code><%= application.getInitParameter("filterExcludes") %></code>].
        </td>
    </tr>

<%
        }
%>

    <tr>
        <td>
            <br />
            This operation may take a few minutes, depending on the
            size and complexity of the codebase to analyze.<br />
            If you really want to do this at this time, please click
            on the <i>Launch</i> button.
        </td>
    </tr>
    <tr>
        <td align="center">
            <br />
            <form method="post" action="<%= request.getRequestURI() %>">
                <input type="submit" name="launch" value="Launch">
                <font size="smaller">optional label:</font> <input type="text" name="label" value="<%= (label != null) ? label : "" %>" />
<%
        if (application.getAttribute("factory") != null) {
%>
                <br />
                <label title="Uncheck to discard the current graph and extract a new one from scratch" for="update">
                    <input type="checkbox" name="update" checked id="update">
                    Update the current graph
                </label>
<%
        }
%>
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
            if (application.getAttribute("extractStart") != null) {
%>
                <tr><td>&nbsp;</td></tr>
                <tr><td colspan="3">
                    <table>
                        <tr>
                            <td align="left">Extracting it took</td>
                            <td align="right"><%= application.getAttribute("extractDuration") %> second(s) on</td>
                            <td align="right"><%= application.getAttribute("extractStart") %>.</td>
                        </tr>

<%
                if (application.getAttribute("updateStart") != null) {
%>
                        <tr>
                            <td align="left">Last update took</td>
                            <td align="right"><%= application.getAttribute("updateDuration") %> second(s) on</td>
                            <td align="rignt"><%= application.getAttribute("updateStart") %>.</td>
                        </tr>
<%
                }
%>
                    </table>
                </td></tr>
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

<p>Extracting dependency graph for <b><code><%= application.getInitParameter("name") %></code></b></p>

<pre class="result">

<%
        Date start = new Date();
        VerboseListener listener = new VerboseListener(out);

        ClassfileLoaderDispatcher dispatcher = (ClassfileLoaderDispatcher) application.getAttribute("dispatcher");
        if (dispatcher == null || request.getParameter("update") == null) {
            dispatcher = new ModifiedOnlyDispatcher(ClassfileLoaderEventSource.DEFAULT_DISPATCHER);
        }

        NodeFactory factory = (NodeFactory) application.getAttribute("factory");
        if (factory == null || request.getParameter("update") == null) {
            factory = new NodeFactory();
        }

        Monitor monitor = (Monitor) application.getAttribute("monitor");
        if (monitor == null || request.getParameter("update") == null) {
            RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
            filterCriteria.setGlobalIncludes(application.getInitParameter("filterIncludes"));
            filterCriteria.setGlobalExcludes(application.getInitParameter("filterExcludes"));

            CodeDependencyCollector collector = new CodeDependencyCollector(factory, filterCriteria);
            DeletingVisitor deletingVisitor = new DeletingVisitor(factory);

            monitor = new Monitor(collector, deletingVisitor);
        }

        ClassfileLoader loader = new TransientClassfileLoader(dispatcher);
        loader.addLoadListener(listener);
        loader.addLoadListener(monitor);
        loader.load(sources);

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

        application.setAttribute("dispatcher", dispatcher);
        application.setAttribute("factory",    factory);
        application.setAttribute("monitor",    monitor);

        if (request.getParameter("update") == null) {
            application.setAttribute("extractStart",    formatter.format(start));
            application.setAttribute("extractDuration", duration);
            application.removeAttribute("updateStart");
            application.removeAttribute("updateDuration");
        } else {
            application.setAttribute("updateStart",    formatter.format(start));
            application.setAttribute("updateDuration", duration);
        }

        if ("".equals(request.getParameter("label"))) {
            application.setAttribute("label", null);
        } else {
            application.setAttribute("label", request.getParameter("label"));
        }

        application.removeAttribute("loadStart");
        application.removeAttribute("loadDuration");
%>

</pre>

<%
        switch (listener.getClassCount()) {
            case 0:
                out.println("<p>Processed nothing in " + duration + " secs.</p>");
                break;
            case 1:
                out.println("<p>Processed 1 class in " + duration + " secs.</p>");
                break;
            default:
                out.println("<p>Processed " + listener.getClassCount() + " classes in " + duration + " secs.</p>");
                break;
        }
    }
%>

<jsp:include page="footer.jsp"/>

</body>

</html>
