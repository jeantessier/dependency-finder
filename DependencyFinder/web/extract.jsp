<%@ page import="java.io.*, java.util.*, org.apache.oro.text.perl.*, com.jeantessier.dependency.*, com.jeantessier.classreader.*" %>
<%@ page errorPage="errorpage.jsp" %>

<!--
    Copyright (c) 2001-2003, Jean Tessier
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
    private class GroupData {
	private String name;
	private int    size;
	private int    count;

	public GroupData(String name, int size) {
	    this.name = name;
	    this.size = size;

	    this.count = 0;
	}

	public String Name() {
	    return name;
	}

	public int Size() {
	    return size;
	}

	public int Count() {
	    return count;
	}

	public void IncrementCount() {
	    count++;
	}

	public int Ratio() {
	    return Count() * 100 / Size();
	}
    }

    private class MyListener implements LoadListener {
	private JspWriter out;

	private int class_count = 0;

	private LinkedList groups        = new LinkedList();
	private Collection visited_files = new HashSet();
	
	public MyListener(JspWriter out) {
	    this.out = out;
	}
	
	public int ClassCount() {
	    return class_count;
	}
	
	public GroupData CurrentGroup() {
	    return (GroupData) groups.getLast();
	}

	public void BeginSession(LoadEvent event) {
	    // Do nothing
	}
	
	public void BeginGroup(LoadEvent event) {
	    groups.add(new GroupData(event.GroupName(), event.Size()));

	    try {
		out.println();
		out.print("\tSearching ");
		out.print(CurrentGroup().Name());
		if (CurrentGroup().Size() >= 0) {
		    out.print(" (");
		    out.print(CurrentGroup().Size());
		    out.print(" files)");
		}
		out.print(" ...");
		out.println();
	    } catch (IOException ex) {
		// Do nothing
	    }
	}

	public void BeginFile(LoadEvent event) {
	    try {
		if (CurrentGroup().Size() > 0) {
		    int previous_ratio = CurrentGroup().Ratio();
		    CurrentGroup().IncrementCount();
		    int new_ratio = CurrentGroup().Ratio();

		    if (previous_ratio != new_ratio) {
			if (new_ratio < 10) {
			    out.print(" ");
			}
			if (new_ratio < 100) {
			    out.print(" ");
			}
			out.print(new_ratio + "%");
		    }
		}
	    } catch (IOException ex) {
		// Ignore
	    }
	}

	public void BeginClassfile(LoadEvent event) {
	    // Do nothing
	}

	public void EndClassfile(LoadEvent event) {
	    visited_files.add(event.Filename());

	    try {
		out.print("\t\tGetting dependencies from ");
		out.print(event.Classfile());
		out.print(" ...");
		out.println();
	    } catch (IOException ex) {
		// Ignore
	    }

	    class_count++;
	}

	public void EndFile(LoadEvent event) {
	    try {
		if (!visited_files.contains(event.Filename())) {
		    out.print("\t\t<i>Skipping ");
		    out.print(event.Filename());
		    out.print(" ...</i>");
		    out.println();
		}
	    } catch (IOException ex) {
		// Ignore
	    }
	}

	public void EndGroup(LoadEvent event) {
	    visited_files.add(event.GroupName());
	    groups.removeLast();
	}

	public void EndSession(LoadEvent event) {
	    // Do nothing
	}
    }
%>

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Extract <%= application.getInitParameter("name") %></title>
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
	    size and complexity of the codebase to analyze.<br/>
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
		<tr><td colspan="3">Extracting it took <%= application.getAttribute("delta") %> second(s) on <%= application.getAttribute("start") %>.</td></tr>

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

<p>Extracting dependency graph for <b><code><%= application.getInitParameter("name") %></code></b></p>

<pre class="result">

<%
	Date start = new Date();
	MyListener listener = new MyListener(out);

	Perl5Util perl = new Perl5Util();

	Collection sources = new LinkedList();
	perl.split(sources, "/,\\s*/", application.getInitParameter("source"));

	NodeFactory factory = new NodeFactory();
	CodeDependencyCollector collector = new CodeDependencyCollector(factory);

	ClassfileLoader loader = new TransientClassfileLoader();
	loader.addLoadListener(listener);
	loader.addLoadListener(collector);
	loader.Load(sources);

	if ("maximize".equalsIgnoreCase(application.getInitParameter("mode"))) {
	    out.println("Maximizing ...");
	    new LinkMaximizer().TraverseNodes(factory.Packages().values());
	} else if ("minimize".equalsIgnoreCase(application.getInitParameter("mode"))) {
	    out.println("Minimizing ...");
	    new LinkMinimizer().TraverseNodes(factory.Packages().values());
	}

	Date   stop  = new Date();
	double delta = (stop.getTime() - start.getTime()) / (double) 1000;

	application.setAttribute("factory", factory);
	application.setAttribute("start",   start);
	application.setAttribute("delta",   new Double(delta));
%>

</pre>

<%
	switch (listener.ClassCount()) {
	    case 0:
		out.println("<p>Processed nothing in " + delta + " secs.</p>");
		break;
	    case 1:
		out.println("<p>Processed 1 class in " + delta + " secs.</p>");
		break;
	    default:
		out.println("<p>Processed " + listener.ClassCount() + " classes in " + delta + " secs.</p>");
		break;
	}
    }
%>

<jsp:include page="footer.jsp"/>

</body>

</html>
