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
<title>Dependency Metrics <%= application.getInitParameter("name") %></title>
</head>

<!-- Reading the parameters and setting up the forms -->

<%
    String scopeIncludes = request.getParameter("scope-includes");
    if (scopeIncludes == null) {
	scopeIncludes = "//";
    }

    String scopeExcludes = request.getParameter("scope-excludes");
    if (scopeExcludes == null) {
	scopeExcludes = "";
    }

    boolean packageScope = "on".equals(request.getParameter("package-scope"));
    if (request.getParameter("submit") == null) {
	packageScope = true;
    }

    String packageScopeIncludes = request.getParameter("package-scope-includes");
    if (packageScopeIncludes == null) {
	packageScopeIncludes = "";
    }

    String packageScopeExcludes = request.getParameter("package-scope-excludes");
    if (packageScopeExcludes == null) {
	packageScopeExcludes = "";
    }

    boolean classScope = "on".equals(request.getParameter("class-scope"));
    if (request.getParameter("submit") == null) {
	classScope = true;
    }

    String classScopeIncludes = request.getParameter("class-scope-includes");
    if (classScopeIncludes == null) {
	classScopeIncludes = "";
    }

    String classScopeExcludes = request.getParameter("class-scope-excludes");
    if (classScopeExcludes == null) {
	classScopeExcludes = "";
    }

    boolean featureScope = "on".equals(request.getParameter("feature-scope"));
    if (request.getParameter("submit") == null) {
	featureScope = true;
    }

    String featureScopeIncludes = request.getParameter("feature-scope-includes");
    if (featureScopeIncludes == null) {
	featureScopeIncludes = "";
    }

    String featureScopeExcludes = request.getParameter("feature-scope-excludes");
    if (featureScopeExcludes == null) {
	featureScopeExcludes = "";
    }

    String filterIncludes = request.getParameter("filter-includes");
    if (filterIncludes == null) {
	filterIncludes = "//";
    }

    String filterExcludes = request.getParameter("filter-excludes");
    if (filterExcludes == null) {
	filterExcludes = "";
    }

    boolean packageFilter = "on".equals(request.getParameter("package-filter"));
    if (request.getParameter("submit") == null) {
	packageFilter = true;
    }

    String packageFilterIncludes = request.getParameter("package-filter-includes");
    if (packageFilterIncludes == null) {
	packageFilterIncludes = "";
    }

    String packageFilterExcludes = request.getParameter("package-filter-excludes");
    if (packageFilterExcludes == null) {
	packageFilterExcludes = "";
    }

    boolean classFilter = "on".equals(request.getParameter("class-filter"));
    if (request.getParameter("submit") == null) {
	classFilter = true;
    }

    String classFilterIncludes = request.getParameter("class-filter-includes");
    if (classFilterIncludes == null) {
	classFilterIncludes = "";
    }

    String classFilterExcludes = request.getParameter("class-filter-excludes");
    if (classFilterExcludes == null) {
	classFilterExcludes = "";
    }

    boolean featureFilter = "on".equals(request.getParameter("feature-filter"));
    if (request.getParameter("submit") == null) {
	featureFilter = true;
    }

    String featureFilterIncludes = request.getParameter("feature-filter-includes");
    if (featureFilterIncludes == null) {
	featureFilterIncludes = "";
    }

    String featureFilterExcludes = request.getParameter("feature-filter-excludes");
    if (featureFilterExcludes == null) {
	featureFilterExcludes = "";
    }

    boolean listElements = "on".equals(request.getParameter("list-elements"));
    if (request.getParameter("submit") == null) {
	listElements = false;
    }
%>

<body>

<form action="<%= request.getRequestURI() %>" method="post">

<table border="0" cellpadding="5"><tr><td colspan="2">

<p class="title"><code><%= application.getInitParameter("name") %></code></p>

</td></tr><tr><td colspan="2">

<table frame="border" rules="cols" class="controls" width="100%"><tr>

<th class="navigation"><a href="advancedquery.jsp">Dependency graph</a></th>
<th class="navigation"><a href="advancedclosure.jsp">Transitive closure</a></th>
<th class="currentnavigation">Dependency metrics</th>

</tr></table>

</td></tr><tr><td colspan="2">

<table frame="border" rules="groups" class="controls">

    <colgroup span="3" />
    <colgroup span="3" />

    <tbody>
    <tr>
	<td colspan="3">
	    <b>Count programming elements</b>
	</td>
	<td colspan="3">
	    <b>Count dependencies</b>
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
	    <input type="text" name="scope-includes" value="<%= scopeIncludes %>" onMouseOver="window.status='Package, class, method, or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="scope-excludes" value="<%= scopeExcludes %>" onMouseOver="window.status='Package, class, method, or field must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
	<td>
	</td>
	<td>
	    <input type="text" name="filter-includes" value="<%= filterIncludes %>" onMouseOver="window.status='Package, class, method, or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="filter-excludes" value="<%= filterExcludes %>" onMouseOver="window.status='Package, class, method, or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="package-scope" <%= packageScope ? "checked" : "" %> onMouseOver="window.status='Count packages'" OnMouseOut="window.status=''">&nbsp;package
	</td>
	<td>
	    <input type="text" name="package-scope-includes" value="<%= packageScopeIncludes %>" onMouseOver="window.status='Package must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="package-scope-excludes" value="<%= packageScopeExcludes %>" onMouseOver="window.status='Package must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="checkbox" name="package-filter" <%= packageFilter ? "checked" : "" %> onMouseOver="window.status='Count dependencies to/from packages'" OnMouseOut="window.status=''">&nbsp;package
	</td>
	<td>
	    <input type="text" name="package-filter-includes" value="<%= packageFilterIncludes %>" onMouseOver="window.status='Package at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="package-filter-excludes" value="<%= packageFilterExcludes %>" onMouseOver="window.status='Package at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="class-scope" <%= classScope ? "checked" : "" %> onMouseOver="window.status='Count classes (with their package)'" OnMouseOut="window.status=''">&nbsp;class
	</td>
	<td>
	    <input type="text" name="class-scope-includes" value="<%= classScopeIncludes %>" onMouseOver="window.status='Class must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="class-scope-excludes" value="<%= classScopeExcludes %>" onMouseOver="window.status='Class must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="checkbox" name="class-filter" <%= classFilter ? "checked" : "" %> onMouseOver="window.status='Count dependencies to/from classes'" OnMouseOut="window.status=''">&nbsp;class
	</td>
	<td>
	    <input type="text" name="class-filter-includes" value="<%= classFilterIncludes %>" onMouseOver="window.status='Class at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="class-filter-excludes" value="<%= classFilterExcludes %>" onMouseOver="window.status='Class at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
    </tr>
    <tr>
	<td>
	    <input type="checkbox" name="feature-scope" <%= featureScope ? "checked" : "" %> onMouseOver="window.status='Count methods and fields (with their class and package)'" OnMouseOut="window.status=''">&nbsp;feature
	</td>
	<td>
	    <input type="text" name="feature-scope-includes" value="<%= featureScopeIncludes %>" onMouseOver="window.status='Method or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="feature-scope-excludes" value="<%= featureScopeExcludes %>" onMouseOver="window.status='Method or field must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="checkbox" name="feature-filter" <%= featureFilter ? "checked" : "" %> onMouseOver="window.status='Count dependencies to/from methods and fields'" OnMouseOut="window.status=''">&nbsp;feature
	</td>
	<td>
	    <input type="text" name="feature-filter-includes" value="<%= featureFilterIncludes %>" onMouseOver="window.status='Method or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
	</td>
	<td>
	    <input type="text" name="feature-filter-excludes" value="<%= featureFilterExcludes %>" onMouseOver="window.status='Method or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
	</td>
    </tr>
    </tbody>

    <tbody>
    <tr>
        <td colspan="6" align="center">

<input type="checkbox" name="list-elements" <%= listElements ? "checked" : "" %> onMouseOver="window.status='List packages, classes, methods, and fields'" onMouseOut="window.status=''">&nbsp;List programming elements

        </td>
    </tr>
    </tbody>
</table>

</td></tr><tr>

<td align="left"><font size="-1">Use Perl regular expressions, <a target="_blank" href="<jsp:getProperty name="version" property="ImplementationURL"/>Manual.html#PerlRegularExpressions">see the manual</a></font></td>
<td align="right"><a href="metrics.jsp">&lt;&lt;&lt; simple</a></td>

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

	    RegularExpressionSelectionCriteria scopeCriteria  = new RegularExpressionSelectionCriteria();
	    RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
	    
	    scopeCriteria.setMatchingPackages(packageScope);
	    scopeCriteria.setMatchingClasses(classScope);
	    scopeCriteria.setMatchingFeatures(featureScope);
	    scopeCriteria.setGlobalIncludes(scopeIncludes);
	    scopeCriteria.setPackageIncludes(packageScopeIncludes);
	    scopeCriteria.setClassIncludes(classScopeIncludes);
	    scopeCriteria.setFeatureIncludes(featureScopeIncludes);
	    scopeCriteria.setGlobalExcludes(scopeExcludes);
	    scopeCriteria.setPackageExcludes(packageScopeExcludes);
	    scopeCriteria.setClassExcludes(classScopeExcludes);
	    scopeCriteria.setFeatureExcludes(featureScopeExcludes);
	
	    filterCriteria.setMatchingPackages(packageFilter);
	    filterCriteria.setMatchingClasses(classFilter);
	    filterCriteria.setMatchingFeatures(featureFilter);
	    filterCriteria.setGlobalIncludes(filterIncludes);
	    filterCriteria.setPackageIncludes(packageFilterIncludes);
	    filterCriteria.setClassIncludes(classFilterIncludes);
	    filterCriteria.setFeatureIncludes(featureFilterIncludes);
	    filterCriteria.setGlobalExcludes(filterExcludes);
	    filterCriteria.setPackageExcludes(packageFilterExcludes);
	    filterCriteria.setClassExcludes(classFilterExcludes);
	    filterCriteria.setFeatureExcludes(featureFilterExcludes);

	    SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);
	    MetricsGatherer metrics = new MetricsGatherer(strategy);
	    metrics.traverseNodes(((NodeFactory) application.getAttribute("factory")).getPackages().values());
	
	    MetricsReport reporter = new MetricsReport(new PrintWriter(out));
	    reporter.setListingElements(listElements);
	    reporter.process(metrics);

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
