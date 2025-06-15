<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create Account</title>
</head>
<body>

<h1>The name <%= request.getAttribute("username") %> is already in use</h1>

<p>Please enter another name and password</p>

<form action="Account" method="post">

    <label for="user">User Name: </label>
    <input type="text" id="user" name="user"><br>

    <label for="pass">Password: </label>
    <input type="text" id="pass" name="pass">
    <input type="submit" value="Login"><br>

</form>

</body>
</html>
