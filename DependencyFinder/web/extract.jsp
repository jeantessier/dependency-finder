<%@ page import="java.io.*, java.util.*, org.apache.oro.text.perl.*, com.jeantessier.dependency.*, com.jeantessier.classreader.*" %>
<html>

<head>
<title>Extract</title>
</head>

<body bgcolor="#ffffff">

<p>Extracting Dependency Graph for <b><code><%= application.getInitParameter("name") %></code></b></p>

<pre>

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

    application.setAttribute("factory", factory);

    Date   stop  = new Date();
    double delta = (stop.getTime() - start.getTime()) / (double) 1000;

    out.println();
    switch (listener.Count()) {
	case 0:
	    out.println("Processed nothing in " + delta + " secs.");
	    break;
	case 1:
	    out.println("Processed 1 class in " + delta + " secs.");
	    break;
	default:
	    out.println("Processed " + listener.Count() + " classes in " + delta + " secs.");
	    break;
    }
%>

</pre>

</body>

</html>
