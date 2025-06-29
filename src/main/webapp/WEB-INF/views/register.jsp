<%@ include file="/WEB-INF/fragments/header.jspf" %>
<%@ include file="/WEB-INF/fragments/navigation.jspf" %>

<main class="form-page">
    <div class="form-container card">
        <h2>Register</h2>
        <c:if test="${not empty error}">
            <p class="error-message">${error}</p>
        </c:if>
        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <label for="username">Username:</label>
                <input type="text" id="username" name="username" class="form-input" required>
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" class="form-input" required>
            </div>
            <div class="form-group">
                <label for="password">Password:</label>
                <input type="password" id="password" name="password" class="form-input" required>
            </div>
            <input type="submit" value="Register" class="btn btn-primary">
        </form>
    </div>
</main>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>