<%@ page import="java.io.*, java.util.*, java.util.stream.*, com.jeantessier.dependency.*" %>
<%@ page errorPage="errorpage.jsp" %>

<!--
    Copyright (c) 2001-2025, Jean Tessier
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
<title>Closure in <%= application.getInitParameter("name") %></title>
</head>

<!-- Reading the parameters and setting up the forms -->

<%
    String startIncludes = request.getParameter("start-includes");
    if (startIncludes == null) {
        startIncludes = "//";
    }

    String startExcludes = request.getParameter("start-excludes");
    if (startExcludes == null) {
        startExcludes = "";
    }

    String stopIncludes = request.getParameter("stop-includes");
    if (stopIncludes == null) {
        stopIncludes = "";
    }

    String stopExcludes = request.getParameter("stop-excludes");
    if (stopExcludes == null) {
        stopExcludes = "";
    }

    String maximumInboundDepth = request.getParameter("maximum-inbound-depth");
    if (maximumInboundDepth == null) {
        maximumInboundDepth = "0";
    }

    String maximumOutboundDepth = request.getParameter("maximum-outbound-depth");
    if (maximumOutboundDepth == null) {
        maximumOutboundDepth = "";
    }

    String scope = request.getParameter("scope");
    if (scope == null) {
        scope = "feature";
    }

    String filter = request.getParameter("filter");
    if (filter == null) {
        filter = "feature";
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

</td></tr><tr><td colspan="2" align="center">

<table border="0" class="controls" width="100%"><tr>

<th><fieldset class="navigation"><a href="query.jsp">Dependency graph</a></fieldset></th>
<th><fieldset class="currentnavigation">Transitive closure</fieldset></th>
<th><fieldset class="navigation"><a href="cycles.jsp">Dependency cycles</a></fieldset></th>
<th><fieldset class="navigation"><a href="metrics.jsp">Dependency metrics</a></fieldset></th>

</tr></table>

</td></tr><tr><td colspan="2" align="center">

<table border="0" class="controls">
    <tr>
        <td width="50%">

<fieldset>
    <legend>Start with programming elements</legend>
    <table>
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
                <input type="text" name="start-includes" value="<%= startIncludes %>" title="Package, class, method, or field must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/">
            </td>
            <td>
                <input type="text" name="start-excludes" value="<%= startExcludes %>" title="Package, class, method, or field must NOT match any of these expressions. E.g., /Test/">
            </td>
        </tr>
    </table>
</fieldset>

        </td>
        <td>

<fieldset>
    <legend>Stop with programming elements</legend>
    <table>
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
                <input type="text" name="stop-includes" value="<%= stopIncludes %>" title="Package, class, method, or field at the other end of the dependency must match any these expressions. E.g., /^com.mycompany/, /\\.get\\w+\\(/">
            </td>
            <td>
                <input type="text" name="stop-excludes" value="<%= stopExcludes %>" title="Package, class, method, or field at the other end of the dependency must NOT match any of these expressions. E.g., /Test/">
            </td>
        </tr>
    </table>
</fieldset>

        </td>
    </tr>
    <tr>
        <td colspan="2" align="center">

<fieldset>
<label title="Maximum hops against the direction dependencies.  Empty field means no limit." for="maximumInboundDepth"><input type="text" name="maximum-inbound-depth" value="<%= maximumInboundDepth %>" size="2" id="maximumInboundDepth"> <tt>&lt;--</tt></label>
follow
<label title="Maximum hops in the direction of dependencies.  Empty field means no limit." for="maximumOutboundDepth"><tt>--&gt;</tt> <input type="text" name="maximum-outbound-depth" value="<%= maximumOutboundDepth %>" size="2" id="maximumOutboundDepth"></label>
</fieldset>

        </td>
    </tr>
</table>

</td></tr><tr><td colspan="2" align="center">

<table border="0" class="controls" width="100%">
    <tr>
        <td align="center" width="50%">

<fieldset>
    <legend>Summarize programming elements</legend>
    <table>
        <tr>
            <td align="center">
                <label title="Start with packages" for="packageScope"><input type="radio" name="scope" value="package" <%= "package".equals(scope) ? "checked" : "" %> id="packageScope">&nbsp;package</label>
                <label title="Start with classes (with their package)" for="classScope"><input type="radio" name="scope" value="class" <%= "class".equals(scope) ? "checked" : "" %> id="classScope">&nbsp;class</label>
                <label title="Start with methods and fields (with their class and package)" for="featureScope"><input type="radio" name="scope" value="feature" <%= "feature".equals(scope) ? "checked" : "" %> id="featureScope">&nbsp;feature</label>
            </td>
        </tr>
    </table>
</fieldset>

        </td>
        <td align="center">

<fieldset>
    <legend>Summarize dependencies</legend>
    <table>
        <tr>
            <td align="center">
                <label title="Stop with packages" for="packageFilter"><input type="radio" name="filter" value="package" <%= "package".equals(filter) ? "checked" : "" %> id="packageFilter">&nbsp;package</label>
                <label title="Stop with classes (with their package)" for="classFilter"><input type="radio" name="filter" value="class" <%= "class".equals(filter) ? "checked" : "" %> id="classFilter">&nbsp;class</label>
                <label title="Stop with methods and fields (with their class and package)" for="featureFilter"><input type="radio" name="filter" value="feature" <%= "feature".equals(filter) ? "checked" : "" %> id="featureFilter">&nbsp;feature</label>
            </td>
        </tr>
    </table>
</fieldset>

        </td>
    </tr>
</table>

</td></tr><tr>

<td align="left"><font size="-1">Use Perl regular expressions, <a target="_blank" href="<jsp:getProperty name="version" property="ImplementationURL"/>Manual.html#PerlRegularExpressions">see the manual</a>.</font></td>
<td align="right"><a href="advancedclosure.jsp">advanced &gt;&gt;&gt;</a></td>

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

            RegularExpressionSelectionCriteria startCriteria  = new RegularExpressionSelectionCriteria();
            startCriteria.setGlobalIncludes(startIncludes);
            startCriteria.setGlobalExcludes(startExcludes);
        
            RegularExpressionSelectionCriteria stopCriteria = new RegularExpressionSelectionCriteria();
            stopCriteria.setGlobalIncludes(stopIncludes);
            stopCriteria.setGlobalExcludes(stopExcludes);

            TransitiveClosure closure = new TransitiveClosure(startCriteria, stopCriteria);

            try {
                closure.setMaximumInboundDepth(Long.parseLong(maximumInboundDepth));
            } catch (NumberFormatException ex) {
                closure.setMaximumInboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
            }

            try {
                closure.setMaximumOutboundDepth(Long.parseLong(maximumOutboundDepth));
            } catch (NumberFormatException ex) {
                closure.setMaximumOutboundDepth(TransitiveClosure.UNBOUNDED_DEPTH);
            }

            closure.traverseNodes(((NodeFactory) application.getAttribute("factory")).getPackages().values());

            RegularExpressionSelectionCriteria scopeCriteria  = new RegularExpressionSelectionCriteria();
            scopeCriteria.setMatchingPackages("package".equals(scope));
            scopeCriteria.setMatchingClasses("class".equals(scope));
            scopeCriteria.setMatchingFeatures("feature".equals(scope));
            scopeCriteria.setGlobalIncludes("//");

            RegularExpressionSelectionCriteria filterCriteria = new RegularExpressionSelectionCriteria();
            filterCriteria.setMatchingPackages("package".equals(filter));
            filterCriteria.setMatchingClasses("class".equals(filter));
            filterCriteria.setMatchingFeatures("feature".equals(filter));
            filterCriteria.setGlobalIncludes("//");

            GraphSummarizer summarizer = new GraphSummarizer(scopeCriteria, filterCriteria);
            summarizer.traverseNodes(closure.getFactory().getPackages().values());

            StringBuilder urlFormat = new StringBuilder();
            urlFormat.append(request.getRequestURI());
            urlFormat.append("?");
            urlFormat.append(
                    request.getParameterMap().entrySet().stream()
                            .flatMap(entry -> switch (entry.getKey()) {
                                case "start-includes" -> Stream.of(entry.getKey() + "=%%2F%%5E%s%%2F");
                                default -> Arrays.stream(entry.getValue()).map(value -> entry.getKey() + "=" + value);
                            })
                            .collect(Collectors.joining("&"))
            );

            Printer printer = new HTMLPrinter(new PrintWriter(out), urlFormat.toString());

            printer.traverseNodes(summarizer.getScopeFactory().getPackages().values());

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
