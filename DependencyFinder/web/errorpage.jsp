<%@ page isErrorPage="true" %>
<%@ page import="java.io.*, java.util.jar.*, com.jeantessier.dependency.*" %>

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

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Error in <%= application.getInitParameter("name") %></title>
</head>

<%
    String resource = Node.class.getResource("Node.class").toString();
    String jar_name = resource.substring(resource.indexOf("/") + 1);
    jar_name = jar_name.substring(0, jar_name.indexOf(".jar!") + 4);

    JarFile  jar      = new JarFile(jar_name);
    Manifest manifest = jar.getManifest();

    String url     = manifest.getMainAttributes().getValue("Implementation-URL");
    String title   = manifest.getMainAttributes().getValue("Implementation-Title");
    String version = manifest.getMainAttributes().getValue("Implementation-Version");
    String vendor  = manifest.getMainAttributes().getValue("Implementation-Vendor");
    String date    = manifest.getMainAttributes().getValue("Implementation-Date");
%>

<body>

<h1>Error:</h1>

<pre>
<% exception.printStackTrace(new PrintWriter(out)); %>
</pre>

<p class="footer">
Powered by <a href="<%= url %>"><%= title %></a> <%= version %> (&copy; <%= vendor %>)<br />
Compiled <%= date %>.
</p>

</body>

</html>
