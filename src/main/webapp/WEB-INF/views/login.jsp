<%@ include file="/WEB-INF/fragments/header.jspf" %>
<%@ include file="/WEB-INF/fragments/navigation.jspf" %>

<main class="form-page">
    <div class="form-container card">
        <h2>Login</h2>
        <c:if test="${not empty error}">
            <p class="error-message">${error}</p>
        </c:if>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" class="form-input" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" class="form-input" required>
            </div>
            <input type="submit" value="Login" class="btn btn-primary">
        </form>
        <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a>.</p>
    </div>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>