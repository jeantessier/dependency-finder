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

    boolean classScope = "on".equals(request.getParameter("class-scope"));
    if (request.getParameter("submit") == null) {
        classScope = false;
    }

    boolean featureScope = "on".equals(request.getParameter("feature-scope"));
    if (request.getParameter("submit") == null) {
        featureScope = false;
    }

    String maximumCycleLength = request.getParameter("maximum-cycle-length");
    if (maximumCycleLength == null) {
        maximumCycleLength = "";
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

<th><fieldset class="navigation"><a href="query.jsp">Dependency graph</a></fieldset></th>
<th><fieldset class="navigation"><a href="closure.jsp">Transitive closure</a></fieldset></th>
<th><fieldset class="currentnavigation">Dependency cycles</fieldset></th>
<th><fieldset class="navigation"><a href="metrics.jsp">Dependency metrics</a></fieldset></th>

</tr></table>

</td></tr><tr><td colspan="2">

<table border="0" class="controls" width="100%">
    <tr>
        <td align="center">

<fieldset>
    <legend>Start with programming elements</legend>
    <table width="100%">
        <tr>
            <td align="center" colspan="2">
                <label title="Select packages" for="packageScope"><input type="checkbox" name="package-scope" <%= packageScope ? "checked" : "" %> id="packageScope"> package</label>
                <label title="Select classes (with their package)" for="classScope"><input type="checkbox" name="class-scope" <%= classScope ? "checked" : "" %> id="classScope"> class</label>
                <label title="Select methods and fields (with their class and package)" for="featureScope"><input type="checkbox" name="feature-scope" <%= featureScope ? "checked" : "" %> id="featureScope"> feature</label>
            </td>
        </tr>
        <tr>
            <td>
                including:
            </td>
            <td>
                excluding:
            </td>
        </tr>
        <tr>
            <td>
                <input type="text" name="scope-includes" value="<%= scopeIncludes %>" title="Package, class, method, or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/">
            </td>
            <td>
                <input type="text" name="scope-excludes" value="<%= scopeExcludes %>" title="Package, class, method, or field must NOT match any of these expressions. E.g., /Test/">
            </td>
        </tr>
    </table>
</fieldset>

        </td>
    </tr>
    <tr>
        <td align="center">

<fieldset>
    <label title="Maximum length of dependencies cycles.  Empty field means no limit." for="maximumCycleLength">max length <input type="text" name="maximum-cycle-length" value="<%= maximumCycleLength %>" size="2" id="maximumCycleLength"></label>
</fieldset>

        </td>
    </tr>
</table>

</td></tr><tr>

<td align="left"><font size="-1">Use Perl regular expressions, <a target="_blank" href="<jsp:getProperty name="version" property="ImplementationURL"/>Manual.html#PerlRegularExpressions">see the manual</a>.</font></td>
<td align="right"><a href="advancedcycles.jsp">advanced &gt;&gt;&gt;</a></td>

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

            scopeCriteria.setMatchingPackages(packageScope);
            scopeCriteria.setMatchingClasses(classScope);
            scopeCriteria.setMatchingFeatures(featureScope);
            scopeCriteria.setGlobalIncludes(scopeIncludes);
            scopeCriteria.setGlobalExcludes(scopeExcludes);
        
            CycleDetector detector = new CycleDetector(scopeCriteria);
            try {
                detector.setMaximumCycleLength(Integer.parseInt(maximumCycleLength));
            } catch (NumberFormatException ex) {
                // Ignore
            }

            detector.traverseNodes(((NodeFactory) application.getAttribute("factory")).getPackages().values());

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
                    for (int i=0; i<values.length; i++) {
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

            CyclePrinter printer = new HTMLCyclePrinter(new PrintWriter(out), urlFormat);
            printer.visitCycles(detector.getCycles());

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
