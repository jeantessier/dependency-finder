<%@ page import="java.io.*, java.util.*, org.apache.oro.text.perl.*, com.jeantessier.dependency.*, com.jeantessier.classreader.*" %>
<html>

<head>
<title>Extract</title>
</head>

<body bgcolor="#ffffff">

<p>Extracting Dependency Graph for <b><code><%= application.getInitParameter("name") %></code></b></p>

<pre>

<%
    Date start = new Date();

    Perl5Util perl = new Perl5Util();

    Collection sources = new LinkedList();
    perl.split(sources, "/,\\s*/", application.getInitParameter("source"));

    NodeFactory factory = new NodeFactory();
    CodeDependencyCollector collector = new CodeDependencyCollector(factory);

    Iterator i = sources.iterator();
    while (i.hasNext()) {
	String filename = (String) i.next();

	out.println("Extracting from " + filename + " ...");

	try {
	    ClassfileLoader loader;
	    if (filename.endsWith(".jar")) {
		loader = new JarClassfileLoader(new String[] {filename});
	    } else if (filename.endsWith(".zip")) {
		loader = new ZipClassfileLoader(new String[] {filename});
	    } else {
		loader = new DirectoryClassfileLoader(new String[] {filename});
	    }

	    loader.Start();

	    Iterator j = loader.Classfiles().iterator();
	    while (j.hasNext()) {
		Classfile classfile = (Classfile) j.next();
		out.println("    Getting dependencies from " + classfile + " ...");
		classfile.Accept(collector);
	    }
	} catch (IOException ex) {
	    out.println("Cannot extract from " + filename + ": " + ex.getClass().getName() + ": " + ex.getMessage());
	}
    }

    if ("maximize".equalsIgnoreCase(application.getInitParameter("mode"))) {
	out.println("Maximizing ...");
	new LinkMaximizer().TraverseNodes(factory.Packages().Values());
    } else if ("minimize".equalsIgnoreCase(application.getInitParameter("mode"))) {
	out.println("Minimizing ...");
	new LinkMinimizer().TraverseNodes(factory.Packages().Values());
    }

    application.setAttribute("factory", factory);

    Date stop = new Date();

    out.println();
    out.println(((stop.getTime() - start.getTime()) / (double) 1000) + " secs.");
%>

</pre>

</body>

</html>
