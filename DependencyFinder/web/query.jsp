<%@ page import="java.io.*, java.util.*, com.jeantessier.dependency.*" %>
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

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Query <%= application.getInitParameter("name") %></title>
</head>

<!-- Reading the parameters and setting up the forms -->

<%
    String scope_includes = request.getParameter("scope-includes");
    if (scope_includes == null) {
	scope_includes = "//";
    }

    String scope_excludes = request.getParameter("scope-excludes");
    if (scope_excludes == null) {
	scope_excludes = "";
    }

    boolean package_scope = "on".equals(request.getParameter("package-scope"));
    if (request.getParameter("submit") == null) {
	package_scope = true;
    }

    boolean class_scope = "on".equals(request.getParameter("class-scope"));
    if (request.getParameter("submit") == null) {
	class_scope = false;
    }

    boolean feature_scope = "on".equals(request.getParameter("feature-scope"));
    if (request.getParameter("submit") == null) {
	feature_scope = false;
    }

    String filter_includes = request.getParameter("filter-includes");
    if (filter_includes == null) {
	filter_includes = "//";
    }

    String filter_excludes = request.getParameter("filter-excludes");
    if (filter_excludes == null) {
	filter_excludes = "";
    }

    boolean package_filter = "on".equals(request.getParameter("package-filter"));
    if (request.getParameter("submit") == null) {
	package_filter = true;
    }

    boolean class_filter = "on".equals(request.getParameter("class-filter"));
    if (request.getParameter("submit") == null) {
	class_filter = false;
    }

    boolean feature_filter = "on".equals(request.getParameter("feature-filter"));
    if (request.getParameter("submit") == null) {
	feature_filter = false;
    }

    boolean show_inbounds = "on".equals(request.getParameter("show-inbounds"));
    if (request.getParameter("submit") == null) {
	show_inbounds = true;
    }

    boolean show_outbounds = "on".equals(request.getParameter("show-outbounds"));
    if (request.getParameter("submit") == null) {
	show_outbounds = true;
    }

    boolean show_empty_nodes = "on".equals(request.getParameter("show-empty-nodes"));
    if (request.getParameter("submit") == null) {
	show_empty_nodes = true;
    }
%>

<body>

<form action="<%= request.getRequestURI() %>" method="post">

<table border="0" cellpadding="5"><tr><td colspan="2">

<p class="title"><code><%= application.getInitParameter("name") %></code></p>

</td></tr><tr><td colspan="2">

<table frame="border" rules="cols" class="controls" width="100%"><tr>

<th class="currentnavigation">Dependency graph</th>
<th class="navigation"><a href="closure.jsp">Transitive closure</a></th>
<th class="navigation"><a href="metrics.jsp">Dependency metrics</a></th>

</tr></table>

</td></tr><tr><td colspan="2">

<table frame="border" rules="groups" class="controls">

    <colgroup span="2" />
    <colgroup span="2" />

    <tbody>
    <tr>
	<td colspan="2">
	    <b>Select programming elements</b>
	</td>
	<td colspan="2">
	    <b>Show dependencies</b>
	</td>
    </tr>
    <tr>
	<td align="center" colspan="2">
	    <input type="checkbox" name="package-scope" <%= package_scope ? "checked" : "" %> onMouseOver="window.status='Select packages'" onMouseOut="window.status=''">&nbsp;package
	    <input type="checkbox" name="class-scope" <%= class_scope ? "checked" : "" %> onMouseOver="window.status='Select classes (with their package)'" onMouseOut="window.status=''">&nbsp;class
	    <input type="checkbox" name="feature-scope" <%= feature_scope ? "checked" : "" %> onMouseOver="window.status='Select methods and fields (with their class and package)'" onMouseOut="window.status=''">&nbsp;feature
	</td>
	<td align="center" colspan="2">
	    <input type="checkbox" name="package-filter" <%= package_filter ? "checked" : "" %> onMouseOver="window.status='Show dependencies to/from packages'" onMouseOut="window.status=''">&nbsp;package
	    <input type="checkbox" name="class-filter" <%= class_filter ? "checked" : "" %> onMouseOver="window.status='Show dependencies to/from classes'" onMouseOut="window.status=''">&nbsp;class
	    <input type="checkbox" name="feature-filter" <%= feature_filter ? "checked" : "" %> onMouseOver="window.status='Show dependencies to/from methods and fields'" onMouseOut="window.status=''">&nbsp;feature
	</td>
    </tr>
    <tr>
	<td>
	    including:
	</td>
	<td>
	    excluding:
	</td>
	<td>
	    including:
	</td>
	<td>
	    excluding:
	</td>
    </tr>
    <tr>
	<td>
	    <input type="text" name="scope-includes" value="<%= scope_includes %>" onMouseOver="window.status='Package, class, method, or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="scope-excludes" value="<%= scope_excludes %>" onMouseOver="window.status='Package, class, method, or field must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="filter-includes" value="<%= filter_includes %>" onMouseOver="window.status='Package, class, method, or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="filter-excludes" value="<%= filter_excludes %>" onMouseOver="window.status='Package, class, method, or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
    </tbody>

    <tbody>
    <tr>
        <td colspan="4" align="center">

Show dependencies
<input type="checkbox" name="show-inbounds" <%= show_inbounds ? "checked" : "" %> onMouseOver="window.status='Show dependencies that point to the selected packages, classes, methods, or fields'" onMouseOut="window.status=''">&nbsp;to element
<input type="checkbox" name="show-outbounds" <%= show_outbounds ? "checked" : "" %> onMouseOver="window.status='Show dependencies that originate from the selected packages, classes, methods, or fields'" onMouseOut="window.status=''">&nbsp;from element
<input type="checkbox" name="show-empty-nodes" <%= show_empty_nodes ? "checked" : "" %> onMouseOver="window.status='Show selected packages, classes, methods, and fields even if they do not have dependencies'" onMouseOut="window.status=''">&nbsp;(empty elements)

        </td>
    </tr>
    </tbody>
</table>

</td></tr><tr>

<td align="left"><font size="-1">Use Perl regular expressions, <a target="_blank" href="<jsp:getProperty name="version" property="ImplementationURL"/>Manual.html#Perl+Regular+Expressions">see the manual</a></font></td>
<td align="right"><a href="advancedquery.jsp">advanced &gt;&gt;&gt;</a></td>

</tr><tr><td align="center" colspan="2">

<input type="submit" name="submit" value="Run Query"/>

</td></tr></table>

</form>

<hr size="3" />

<%
    if (request.getParameter("submit") != null) {
	if (application.getAttribute("factory") != null) {
%>

<pre class="result">

<%
	    Date start = new Date();

	    RegularExpressionSelectionCriteria scope_criteria  = new RegularExpressionSelectionCriteria();
	    RegularExpressionSelectionCriteria filter_criteria = new RegularExpressionSelectionCriteria();
	    
	    scope_criteria.MatchPackage(package_scope);
	    scope_criteria.MatchClass(class_scope);
	    scope_criteria.MatchFeature(feature_scope);
	    scope_criteria.GlobalIncludes(scope_includes);
	    scope_criteria.GlobalExcludes(scope_excludes);
	
	    filter_criteria.MatchPackage(package_filter);
	    filter_criteria.MatchClass(class_filter);
	    filter_criteria.MatchFeature(feature_filter);
	    filter_criteria.GlobalIncludes(filter_includes);
	    filter_criteria.GlobalExcludes(filter_excludes);

	    GraphCopier dependencies_query = new GraphSummarizer(scope_criteria, filter_criteria);
	    if ("maximize".equalsIgnoreCase(application.getInitParameter("mode"))) {
		SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy(scope_criteria, filter_criteria);
		dependencies_query = new GraphCopier(strategy);
	    }
	
	    dependencies_query.TraverseNodes(((NodeFactory) application.getAttribute("factory")).Packages().values());

	    TextPrinter printer = new TextPrinter(new PrintWriter(out));

	    printer.ShowInbounds(show_inbounds);
	    printer.ShowOutbounds(show_outbounds);
	    printer.ShowEmptyNodes(show_empty_nodes);
		
	    printer.TraverseNodes(dependencies_query.ScopeFactory().Packages().values());

	    Date stop = new Date();
%>

</pre>

<p><%= (stop.getTime() - start.getTime()) / (double) 1000 %> secs.</p>

<%
	} else {
%>

<h3>No dependency graph available</h3>

<p>Please ask the webmaster to extract a dependency graph before you start placing queries.</p>

<%
	}
    }
%>

<jsp:include page="footer.jsp"/>

</body>

</html>
