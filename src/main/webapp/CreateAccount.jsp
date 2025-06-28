<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create Account</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
    <div class="container">
        <div class="form-container">
            <h1>Create Account</h1>
            <% if (request.getAttribute("error") != null) { %><p class="error"><%= request.getAttribute("error") %></p><% } %>
            <form action="<%= request.getContextPath() %>/createAccount" method="post">
                <input type="text" id="user" name="user" placeholder="Username" required>
                <input type="password" id="pass" name="pass" placeholder="Password" required>
                <button type="submit">Create Account</button>
            </form>
            <p>Already have an account? <a href="<%= request.getContextPath() %>/login">Log in</a></p>
        </div>
    </div>
</body>
</html>