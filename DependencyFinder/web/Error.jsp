<%@ page isErrorPage="true" %>
<%@ page import="java.io.*" %>

<html>

<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title>Blue Screen of Death</title>
</head>

<body>

<h1>Error:</h1>

<pre>
<% exception.printStackTrace(new PrintWriter(out)); %>
</pre>

</body>

</html>
