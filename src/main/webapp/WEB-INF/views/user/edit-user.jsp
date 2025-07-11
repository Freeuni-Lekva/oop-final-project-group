<%@ include file="/WEB-INF/fragments/header.jspf" %>
<%@ include file="/WEB-INF/fragments/navigation.jspf" %>

<main>
    <h2>Edit User</h2>
    <form action="${pageContext.request.contextPath}/users?action=update" method="post">
        <input type="hidden" name="id" value="${user.id}" />

        <label for="username">Username:</label><br>
        <input type="text" id="username" name="username" value="${user.username}" required><br><br>

        <label for="email">Email:</label><br>
        <input type="email" id="email" name="email" value="${user.email}" required><br><br>

        <input type="submit" value="Submit">
    </form>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>