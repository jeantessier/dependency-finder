<%@ page import="java.io.*, java.text.*, java.util.*, org.apache.oro.text.perl.*, org.xml.sax.*, com.jeantessier.dependency.*, com.jeantessier.dependencyfinder.*, com.jeantessier.classreader.*" %>
<%@ page errorPage="errorpage.jsp" %>

<!--
    Copyright (c) 2001-2004, Jean Tessier
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

	public void BeginSession(DependencyEvent event) {
	    // Do nothing
	}

	public void BeginClass(DependencyEvent event) {
	    try {
		out.print("Loading dependencies for ");
		out.print(event.Classname());
		out.print(" ...");
		out.println();
	    } catch (IOException ex) {
		// Ignore
	    }
	}

	public void Dependency(DependencyEvent event) {
	    // Do nothing
	}

	public void EndClass(DependencyEvent event) {
	    // Do nothing
	}

	public void EndSession(DependencyEvent event) {
	    // Do nothing
	}
    }
%>

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Load <%= application.getInitParameter("name") %></title>
</head>

<body>

<table cellpadding="5">
    <tr>
	<td class="title">
	    <code><%= application.getInitParameter("name") %></code>
	</td>
    </tr>
    <tr>
	<td>
	    <table frame="border" rules="cols" class="controls" width="100%"><tr>

	    <th class="navigation"><a href="query.jsp">Dependency graph</a></th>
	    <th class="navigation"><a href="closure.jsp">Transitive closure</a></th>
	    <th class="navigation"><a href="metrics.jsp">Dependency metrics</a></th>

	    </tr></table>
	</td>
    </tr>

<%
    if (request.getParameter("launch") == null) {
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
	    </form>
	</td>
    </tr>
    <tr>
	<td>
	    <table>

<%
	if (application.getAttribute("factory") != null) {
%>

		<tr><td valign="top" rowspan="3">The current graph contains:</td><td align="right"><%= ((NodeFactory) application.getAttribute("factory")).Packages().size() %></td><td>packages</td></tr>
		<tr><td align="right"><%= ((NodeFactory) application.getAttribute("factory")).Classes().size() %></td><td>classes</td></tr>
		<tr><td align="right"><%= ((NodeFactory) application.getAttribute("factory")).Features().size() %></td><td>features</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td colspan="3">Loading it took <%= application.getAttribute("delta") %> second(s) on <%= application.getAttribute("start") %>.</td></tr>

<%
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

	Perl5Util perl = new Perl5Util();

	Collection files = new LinkedList();
	perl.split(files, "/,\\s*/", application.getInitParameter("file"));

	NodeFactory factory = new NodeFactory();

	NodeLoader loader = new NodeLoader(factory);
	loader.addDependencyListener(listener);

	Iterator i = files.iterator();
	while (i.hasNext()) {
	    String filename = (String) i.next();
	    try {
		loader.Load(filename);
	    } catch (SAXException ex) {
		out.println("<i>Could not load graph from file \"" + filename + "\": " + ex.getMessage() + "</i>");
	    }
	}

	if ("maximize".equalsIgnoreCase(application.getInitParameter("mode"))) {
	    out.println("Maximizing ...");
	    new LinkMaximizer().TraverseNodes(factory.Packages().values());
	} else if ("minimize".equalsIgnoreCase(application.getInitParameter("mode"))) {
	    out.println("Minimizing ...");
	    new LinkMinimizer().TraverseNodes(factory.Packages().values());
	}

	Date   stop  = new Date();
	double delta = (stop.getTime() - start.getTime()) / (double) 1000;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	application.setAttribute("factory", factory);
	application.setAttribute("start",   formatter.format(start));
	application.setAttribute("delta",   new Double(delta));
%>

</pre>

<%
	switch (files.size()) {
	    case 0:
		out.println("<p>Loaded nothing in " + delta + " secs.</p>");
		break;
	    case 1:
		out.println("<p>Loaded 1 file in " + delta + " secs.</p>");
		break;
	    default:
		out.println("<p>Loaded " + files.size() + " files in " + delta + " secs.</p>");
		break;
	}
    }
%>

<jsp:include page="footer.jsp"/>

</body>

</html>
