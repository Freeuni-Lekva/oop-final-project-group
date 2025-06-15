<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Welcome <%= request.getAttribute("username") %></title>
</head>
<body>

<h1>Welcome <%= request.getAttribute("username") %> </h1>

<a href="HomePage.html">log out</a>
</body>
</html>