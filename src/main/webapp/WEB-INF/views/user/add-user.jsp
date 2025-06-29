<%@ include file="/WEB-INF/fragments/header.jspf" %>
<%@ include file="/WEB-INF/fragments/navigation.jspf" %>

<main>
    <h2>Add New User</h2>
    <form action="${pageContext.request.contextPath}/users?action=insert" method="post">
        <label for="username">Username:</label><br>
        <input type="text" id="username" name="username" required><br><br>

        <label for="email">Email:</label><br>
        <input type="email" id="email" name="email" required><br><br>

        <label for="password">Password:</label><br>
        <input type="password" id="password" name="password" required><br><br>

        <input type="submit" value="Submit">
    </form>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>