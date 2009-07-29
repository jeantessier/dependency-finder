<%@ page import="java.io.*, java.text.*, java.util.*, com.jeantessier.dependency.*" %>
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

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<link rel="shortcut icon" href="images/logoicon.gif" type="image/gif" />
<title>Query <%= application.getInitParameter("name") %></title>
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
        classScope = false;
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
        featureScope = false;
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
        classFilter = false;
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
        featureFilter = false;
    }

    String featureFilterIncludes = request.getParameter("feature-filter-includes");
    if (featureFilterIncludes == null) {
        featureFilterIncludes = "";
    }

    String featureFilterExcludes = request.getParameter("feature-filter-excludes");
    if (featureFilterExcludes == null) {
        featureFilterExcludes = "";
    }

    boolean showInbounds = "on".equals(request.getParameter("show-inbounds"));
    if (request.getParameter("submit") == null) {
        showInbounds = true;
    }

    boolean showOutbounds = "on".equals(request.getParameter("show-outbounds"));
    if (request.getParameter("submit") == null) {
        showOutbounds = true;
    }

    boolean showEmptyNodes = "on".equals(request.getParameter("show-empty-nodes"));
    if (request.getParameter("submit") == null) {
        showEmptyNodes = true;
    }

    boolean copyOnly = "on".equals(request.getParameter("copy-only"));
    if (request.getParameter("submit") == null) {
        copyOnly = false;
    }
%>

<body>

<form action="<%= request.getRequestURI() %>" method="get">

<table border="0" cellpadding="5"><tr><td colspan="2">

<div class="title">
<span id="name"><%= application.getInitParameter("name") %></span>
<% if (application.getAttribute("label") != null ) { %>
<span id="label"><%= application.getAttribute("label") %></span>
<% } %>
</div>

</td></tr><tr><td colspan="2">

<table border="0" class="controls" width="100%"><tr>

<th><fieldset class="currentnavigation">Dependency graph</fieldset></th>
<th><fieldset class="navigation"><a href="advancedclosure.jsp">Transitive closure</a></fieldset></th>
<th><fieldset class="navigation"><a href="advancedcycles.jsp">Dependency cycles</a></fieldset></th>
<th><fieldset class="navigation"><a href="advancedmetrics.jsp">Dependency metrics</a></fieldset></th>

</tr></table>

</td></tr><tr><td colspan="2">

<table border="0" class="controls">
    <tr>
        <td width="50%">

<fieldset>
    <legend>Select programming elements</legend>
    <table>
        <tr>
            <td>
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
            </td>
            <td>
                <input type="text" name="scope-includes" value="<%= scopeIncludes %>" title="Package, class, method, or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/">
            </td>
            <td>
                <input type="text" name="scope-excludes" value="<%= scopeExcludes %>" title="Package, class, method, or field must NOT match any of these expressions. E.g., /Test/">
            </td>
        </tr>
        <tr>
            <td>
                <input type="checkbox" name="package-scope" <%= packageScope ? "checked" : "" %> title="Select packages" id="packageScope">
            </td>
            <td>
                <label for="packageScope">package</label>
            </td>
            <td>
                <input type="text" name="package-scope-includes" value="<%= packageScopeIncludes %>" onMouseOver="window.status='Package must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
            </td>
            <td>
                <input type="text" name="package-scope-excludes" value="<%= packageScopeExcludes %>" onMouseOver="window.status='Package must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
            </td>
        </tr>
        <tr>
            <td>
                <input type="checkbox" name="class-scope" <%= classScope ? "checked" : "" %> title="Select classes (with their package)" id="classScope">
            </td>
            <td>
                <label for="classScope">class</label>
            </td>
            <td>
                <input type="text" name="class-scope-includes" value="<%= classScopeIncludes %>" onMouseOver="window.status='Class must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
            </td>
            <td>
                <input type="text" name="class-scope-excludes" value="<%= classScopeExcludes %>" onMouseOver="window.status='Class must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
            </td>
        </tr>
        <tr>
            <td>
                <input type="checkbox" name="feature-scope" <%= featureScope ? "checked" : "" %> title="Select methods and fields (with their class and package)" id="featureScope">
            </td>
            <td>
                <label for="featureScope">feature</label>
            </td>
            <td>
                <input type="text" name="feature-scope-includes" value="<%= featureScopeIncludes %>" onMouseOver="window.status='Method or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
            </td>
            <td>
                <input type="text" name="feature-scope-excludes" value="<%= featureScopeExcludes %>" onMouseOver="window.status='Method or field must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
            </td>
        </tr>
    </table>
</fieldset>

        </td>
        <td>

<fieldset>
    <legend>Show dependencies</legend>
    <table>
        <tr>
            <td>
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
            </td>
            <td>
                <input type="text" name="filter-includes" value="<%= filterIncludes %>" title="Package, class, method, or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/">
            </td>
            <td>
                <input type="text" name="filter-excludes" value="<%= filterExcludes %>" title="Package, class, method, or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/">
            </td>
        </tr>
        <tr>
            <td>
                <input type="checkbox" name="package-filter" <%= packageFilter ? "checked" : "" %> title="Show dependencies to/from packages" id="packageFilter">
            </td>
            <td>
                <label for="packageFilter">package</label>
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
                <input type="checkbox" name="class-filter" <%= classFilter ? "checked" : "" %> title="Show dependencies to/from classes" id="classFilter">
            </td>
            <td>
                <label for="classFilter">class</label>
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
                <input type="checkbox" name="feature-filter" <%= featureFilter ? "checked" : "" %> title="Show dependencies to/from methods and fields" id="featureFilter">
            </td>
            <td>
                <label for="featureFilter">feature</label>
            </td>
            <td>
                <input type="text" name="feature-filter-includes" value="<%= featureFilterIncludes %>" onMouseOver="window.status='Method or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/'" OnMouseOut="window.status=''">
            </td>
            <td>
                <input type="text" name="feature-filter-excludes" value="<%= featureFilterExcludes %>" onMouseOver="window.status='Method or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/'" OnMouseOut="window.status=''">
            </td>
        </tr>
    </table>
</fieldset>

        </td>
    </tr>
    <tr>
        <td colspan="2" align="center">

<fieldset>
Show&nbsp;&nbsp;
<label title="Show dependencies that point to the selected packages, classes, methods, or fields" for="showInbounds"><input type="checkbox" name="show-inbounds" <%= showInbounds ? "checked" : "" %> id="showInbounds"> <tt>&lt;--</tt>&nbsp;&nbsp;</label>
<label title="Show dependencies that originate from the selected packages, classes, methods, or fields" for="showOutbounds"><input type="checkbox" name="show-outbounds" <%= showOutbounds ? "checked" : "" %> id="showOutbounds"> <tt>--&gt;</tt>&nbsp;&nbsp;</label>
<label title="Show selected packages, classes, methods, and fields even if they do not have dependencies" for="showEmptyNodes"><input type="checkbox" name="show-empty-nodes" <%= showEmptyNodes ? "checked" : "" %> id="showEmptyNodes"> empty elements</label>
<br />
<label title="Only copy explicit dependencies to the result graph, do not introduce implicit dependencies where explicit dependencies match the regular expressions but are otherwise out of scope" for="copyOnly">Only copy <input type="checkbox" name="copy-only" <%= copyOnly ? "checked" : "" %> id="copyOnly"> explicit dependencies</label>
</fieldset>

        </td>
    </tr>
</table>

</td></tr><tr>

<td align="left"><font size="-1">Use Perl regular expressions, <a target="_blank" href="<jsp:getProperty name="version" property="ImplementationURL"/>Manual.html#PerlRegularExpressions">see the manual</a>.</font></td>
<td align="right"><a href="query.jsp">&lt;&lt;&lt; simple</a></td>

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

            GraphCopier dependenciesQuery = new GraphSummarizer(scopeCriteria, filterCriteria);
            if (copyOnly || "maximize".equalsIgnoreCase(application.getInitParameter("mode"))) {
                SelectiveTraversalStrategy strategy = new SelectiveTraversalStrategy(scopeCriteria, filterCriteria);
                dependenciesQuery = new GraphCopier(strategy);
            }

            dependenciesQuery.traverseNodes(((NodeFactory) application.getAttribute("factory")).getPackages().values());

            StringBuffer urlPattern = new StringBuffer();
            urlPattern.append(request.getRequestURI());
            urlPattern.append("?");
            Iterator entries = request.getParameterMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                if ("scope-includes".equals(entry.getKey())) {
                    urlPattern.append(entry.getKey()).append("=/^{0}/");
                } else {
                    String[] values = (String[]) entry.getValue();
                    for (int i = 0; i < values.length; i++) {
                        urlPattern.append(entry.getKey()).append("=").append(values[i]);
                        if (i < values.length - 1) {
                            urlPattern.append("&");
                        }
                    }
                }
                if (entries.hasNext()) {
                    urlPattern.append("&");
                }
            }

            MessageFormat urlFormat = new MessageFormat(urlPattern.toString());

            Printer printer = new HTMLPrinter(new PrintWriter(out), urlFormat);

            printer.setShowInbounds(showInbounds);
            printer.setShowOutbounds(showOutbounds);
            printer.setShowEmptyNodes(showEmptyNodes);

            printer.traverseNodes(dependenciesQuery.getScopeFactory().getPackages().values());

            Date stop = new Date();

            out.println();
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
