<%@ page isErrorPage="true" %>
<%@ page import="java.io.*" %>

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Error in <%= application.getInitParameter("name") %></title>
</head>

<body>

<h1>Error:</h1>

<pre>
<% exception.printStackTrace(new PrintWriter(out)); %>
</pre>

</body>

</html>
