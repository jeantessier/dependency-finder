<%@ page import="java.io.*, java.util.*, org.apache.oro.text.perl.*, com.jeantessier.dependency.*, com.jeantessier.classreader.*" %>
<%@ page errorPage="Error.jsp" %>

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Extract <%= application.getInitParameter("name") %></title>
</head>

<body>

<%!
    private class MyListener implements LoadListener {
	private JspWriter out;

	private int count = 0;
	
	public MyListener(JspWriter out) {
	    this.out = out;
	}
	
	public int Count() {
	    return count;
	}
	
	public void LoadStart(LoadEvent event) {
	    // Do nothing
	}
	public void LoadElement(LoadEvent event) {
	    // Do nothing
	}
	public void LoadedClassfile(LoadEvent event) {
	    try {
		out.println("    Getting dependencies from " + event.Classfile() + " ...");
	    } catch (IOException ex) {
		// Do nothing
	    }
	    count++;
	}
	public void LoadStop(LoadEvent event) {
	    // Do nothing
	}
    }
%>

<%
    if (request.getParameter("launch") == null) {
%>

<table>
    <tr>
	<td class="title">
	    <code><%= application.getInitParameter("name") %></code><br />
	    Extract Dependency Graph
	</td>
    </tr>
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
		
	Iterator i = sources.iterator();
	while (i.hasNext()) {
	    String filename = (String) i.next();

	    out.println("Extracting from " + filename + " ...");

	    try {
		if (filename.endsWith(".jar")) {
		    JarClassfileLoader jar_loader = new JarClassfileLoader(loader);
		    jar_loader.Load(filename);
		} else if (filename.endsWith(".zip")) {
		    ZipClassfileLoader zip_loader = new ZipClassfileLoader(loader);
		    zip_loader.Load(filename);
		} else {
		    DirectoryClassfileLoader directory_loader = new DirectoryClassfileLoader(loader);
		    directory_loader.Load(new DirectoryExplorer(filename));
		}
	    } catch (IOException ex) {
		out.println("Cannot extract from " + filename + ": " + ex.getClass().getName() + ": " + ex.getMessage());
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

	application.setAttribute("factory", factory);
	application.setAttribute("start",   start);
	application.setAttribute("delta",   new Double(delta));
%>

</pre>

<%
	switch (listener.Count()) {
	    case 0:
		out.println("<p>Processed nothing in " + delta + " secs.</p>");
		break;
	    case 1:
		out.println("<p>Processed 1 class in " + delta + " secs.</p>");
		break;
	    default:
		out.println("<p>Processed " + listener.Count() + " classes in " + delta + " secs.</p>");
		break;
	}
    }
%>

</body>

</html>
