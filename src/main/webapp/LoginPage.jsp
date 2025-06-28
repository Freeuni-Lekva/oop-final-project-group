<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<div class="container">
    <div class="form-container">
        <h1>Login</h1>
        <% if (request.getAttribute("error") != null) { %><p class="error"><%= request.getAttribute("error") %></p><% } %>
        <form action="<%= request.getContextPath() %>/login" method="post">
            <input type="text" name="user" placeholder="Username" required>
            <input type="password" name="pass" placeholder="Password" required>
            <button type="submit">Log In</button>
        </form>
        <p>Don't have an account? <a href="<%= request.getContextPath() %>/CreateAccount.jsp">Create one</a></p>
    </div>
</div>
</body>
</html>