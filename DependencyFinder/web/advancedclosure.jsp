<%@ page import="java.util.*, com.jeantessier.dependency.*" %>
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
    
    	* Neither the name of the Jean Tessier nor the names of his contributors
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

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Closure in <%= application.getInitParameter("name") %></title>
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

    String package_scope_includes = request.getParameter("package-scope-includes");
    if (package_scope_includes == null) {
	package_scope_includes = "";
    }

    String package_scope_excludes = request.getParameter("package-scope-excludes");
    if (package_scope_excludes == null) {
	package_scope_excludes = "";
    }

    boolean class_scope = "on".equals(request.getParameter("class-scope"));
    if (request.getParameter("submit") == null) {
	class_scope = true;
    }

    String class_scope_includes = request.getParameter("class-scope-includes");
    if (class_scope_includes == null) {
	class_scope_includes = "";
    }

    String class_scope_excludes = request.getParameter("class-scope-excludes");
    if (class_scope_excludes == null) {
	class_scope_excludes = "";
    }

    boolean feature_scope = "on".equals(request.getParameter("feature-scope"));
    if (request.getParameter("submit") == null) {
	feature_scope = true;
    }

    String feature_scope_includes = request.getParameter("feature-scope-includes");
    if (feature_scope_includes == null) {
	feature_scope_includes = "";
    }

    String feature_scope_excludes = request.getParameter("feature-scope-excludes");
    if (feature_scope_excludes == null) {
	feature_scope_excludes = "";
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

    String package_filter_includes = request.getParameter("package-filter-includes");
    if (package_filter_includes == null) {
	package_filter_includes = "";
    }

    String package_filter_excludes = request.getParameter("package-filter-excludes");
    if (package_filter_excludes == null) {
	package_filter_excludes = "";
    }

    boolean class_filter = "on".equals(request.getParameter("class-filter"));
    if (request.getParameter("submit") == null) {
	class_filter = true;
    }

    String class_filter_includes = request.getParameter("class-filter-includes");
    if (class_filter_includes == null) {
	class_filter_includes = "";
    }

    String class_filter_excludes = request.getParameter("class-filter-excludes");
    if (class_filter_excludes == null) {
	class_filter_excludes = "";
    }

    boolean feature_filter = "on".equals(request.getParameter("feature-filter"));
    if (request.getParameter("submit") == null) {
	feature_filter = true;
    }

    String feature_filter_includes = request.getParameter("feature-filter-includes");
    if (feature_filter_includes == null) {
	feature_filter_includes = "";
    }

    String feature_filter_excludes = request.getParameter("feature-filter-excludes");
    if (feature_filter_excludes == null) {
	feature_filter_excludes = "";
    }

    String maximum_inbound_depth = request.getParameter("maximum-inbound-depth");
    if (maximum_inbound_depth == null) {
	maximum_inbound_depth = "0";
    }

    String maximum_outbound_depth = request.getParameter("maximum-outbound-depth");
    if (maximum_outbound_depth == null) {
	maximum_outbound_depth = "";
    }
%>

<body>

<form action="<%= request.getRequestURI() %>" method="post">

<table border="0" cellpadding="5"><tr><td colspan="2">

<p class="title">
<code><%= application.getInitParameter("name") %></code><br />
Transitive closure
</p>

</td></tr><tr><td colspan="2">

<table class="controls"><tr><td class="controls">

<table border="0">
    <tr>
	<td colspan="3">
	    <b>Start with programming elements</b>
	</td>
    </tr>
    <tr>
	<td>
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
	</td>
	<td>
	    <input type="text" name="scope-includes" value="<%= scope_includes %>" onMouseOver="window.status='Package, class, method, or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="scope-excludes" value="<%= scope_excludes %>" onMouseOver="window.status='Package, class, method, or field must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="package-scope" <%= package_scope ? "checked" : "" %> onMouseOver="window.status='Start with packages'" onMouseOut="window.status=''">&nbsp;package
	</td>
	<td>
	    <input type="text" name="package-scope-includes" value="<%= package_scope_includes %>" onMouseOver="window.status='Package must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="package-scope-excludes" value="<%= package_scope_excludes %>" onMouseOver="window.status='Package must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="class-scope" <%= class_scope ? "checked" : "" %> onMouseOver="window.status='Start with classes (with their package)'" onMouseOut="window.status=''">&nbsp;class
	</td>
	<td>
	    <input type="text" name="class-scope-includes" value="<%= class_scope_includes %>" onMouseOver="window.status='Class must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="class-scope-excludes" value="<%= class_scope_excludes %>" onMouseOver="window.status='Class must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="feature-scope" <%= feature_scope ? "checked" : "" %> onMouseOver="window.status='Start with methods and fields (with their class and package)'" onMouseOut="window.status=''">&nbsp;feature
	</td>
	<td>
	    <input type="text" name="feature-scope-includes" value="<%= feature_scope_includes %>" onMouseOver="window.status='Method or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="feature-scope-excludes" value="<%= feature_scope_excludes %>" onMouseOver="window.status='Method or field must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
</table>

</td><td class="controls">

<table border="0">
     <tr>
	<td colspan="3">
	    <b>Follow dependencies</b>
	</td>
    </tr>
   <tr>
	<td>
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
	</td>
	<td>
	    <input type="text" name="filter-includes" value="<%= filter_includes %>" onMouseOver="window.status='Package, class, method, or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="filter-excludes" value="<%= filter_excludes %>" onMouseOver="window.status='Package, class, method, or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="package-filter" <%= package_filter ? "checked" : "" %> onMouseOver="window.status='Follow dependencies to/from packages'" onMouseOut="window.status=''">&nbsp;package
	</td>
	<td>
	    <input type="text" name="package-filter-includes" value="<%= package_filter_includes %>" onMouseOver="window.status='Package at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="package-filter-excludes" value="<%= package_filter_excludes %>" onMouseOver="window.status='Package at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="class-filter" <%= class_filter ? "checked" : "" %> onMouseOver="window.status='Follow dependencies to/from classes'" onMouseOut="window.status=''">&nbsp;class
	</td>
	<td>
	    <input type="text" name="class-filter-includes" value="<%= class_filter_includes %>" onMouseOver="window.status='Class at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="class-filter-excludes" value="<%= class_filter_excludes %>" onMouseOver="window.status='Class at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="feature-filter" <%= feature_filter ? "checked" : "" %> onMouseOver="window.status='Follow dependencies to/from methods and fields'" onMouseOut="window.status=''">&nbsp;feature
	</td>
	<td>
	    <input type="text" name="feature-filter-includes" value="<%= feature_filter_includes %>" onMouseOver="window.status='Method or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" onMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="feature-filter-excludes" value="<%= feature_filter_excludes %>" onMouseOver="window.status='Method or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" onMouseOut="window.status=''">
	</td>
    </tr>
</table>

</td></tr><tr><td colspan="2" align="center" class="controls">

Follow inbounds:
<input type="text" name="maximum-inbound-depth" value="<%= maximum_inbound_depth %>" size="2" onMouseOver="window.status='Maximum hops against the direction dependencies.  Empty field means no limit.'" onMouseOut="window.status=''">
Follow outbounds:
<input type="text" name="maximum-outbound-depth" value="<%= maximum_outbound_depth %>" size="2" onMouseOver="window.status='Maximum hops in the direction of dependencies.  Empty field means no limit.'" onMouseOut="window.status=''">

</td></tr></table>

</td></tr><tr>

<td align="left"><font size="-1">Use Perl regular expressions, <a target="_blank" href="http://depfind.sourceforge.net/Manual.html#Perl+Regular+Expressions">see the manual</a></font></td>
<td align="right"><a href="closure.jsp">&lt;&lt;&lt; simple</a></td>

</td></tr><tr><td align="center" colspan="2">

<input type="submit" name="submit" value="Run Query"/>

</td></tr></table>

</form>

<hr size="3" />

<%
    if (request.getParameter("submit") != null) {
	if (application.getAttribute("factory") != null) {
	    Date start = new Date();

	    SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy();
		
	    strategy.PackageScope(package_scope);
	    strategy.ClassScope(class_scope);
	    strategy.FeatureScope(feature_scope);
	    strategy.ScopeIncludes(scope_includes);
	    strategy.PackageScopeIncludes(package_scope_includes);
	    strategy.ClassScopeIncludes(class_scope_includes);
	    strategy.FeatureScopeIncludes(feature_scope_includes);
	    strategy.ScopeExcludes(scope_excludes);
	    strategy.PackageScopeExcludes(package_scope_excludes);
	    strategy.ClassScopeExcludes(class_scope_excludes);
	    strategy.FeatureScopeExcludes(feature_scope_excludes);
	
	    strategy.PackageFilter(package_filter);
	    strategy.ClassFilter(class_filter);
	    strategy.FeatureFilter(feature_filter);
	    strategy.FilterIncludes(filter_includes);
	    strategy.PackageFilterIncludes(package_filter_includes);
	    strategy.ClassFilterIncludes(class_filter_includes);
	    strategy.FeatureFilterIncludes(feature_filter_includes);
	    strategy.FilterExcludes(filter_excludes);
	    strategy.PackageFilterExcludes(package_filter_excludes);
	    strategy.ClassFilterExcludes(class_filter_excludes);
	    strategy.FeatureFilterExcludes(feature_filter_excludes);

	    TransitiveClosure closure = new TransitiveClosure(strategy);

	    try {
		closure.MaximumInboundDepth(Long.parseLong(maximum_inbound_depth));
	    } catch (NumberFormatException ex) {
		closure.MaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
	    }

	    try {
		closure.MaximumOutboundDepth(Long.parseLong(maximum_outbound_depth));
	    } catch (NumberFormatException ex) {
		closure.MaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
	    }

	    closure.TraverseNodes(((NodeFactory) application.getAttribute("factory")).Packages().values());

	    PrettyPrinter printer = new PrettyPrinter();

	    printer.TraverseNodes(closure.Factory().Packages().values());

	    Date stop = new Date();

	    out.println();
%>

<pre class="result"><%= printer %></pre>

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

</body>

</html>
