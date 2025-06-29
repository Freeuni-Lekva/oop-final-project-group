<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="../../fragments/header.jspf" %>

<main class="container">
    <div class="card">
        <h1>User Search</h1>
        <form action="user-search" method="GET" class="form-group">
            <input type="text" name="query" placeholder="Enter username" class="form-input" />
            <input type="submit" value="Search" class="btn btn-primary" />
        </form>
    </div>

    <c:if test="${not empty users}">
        <div class="card" style="margin-top: 2rem;">
            <h2>Search Results</h2>
            <div class="user-list">
                <c:forEach var="user" items="${users}">
                    <div class="user-item">
                        <span><a href="user-profile?username=${user.name}">${user.name}</a></span>
                        <form action="send-friend-request" method="post" style="display: inline;">
                            <input type="hidden" name="recipientId" value="${user.id}" />
                            <input type="submit" value="Add Friend" class="btn btn-secondary" />
                        </form>
                    </div>
                </c:forEach>
            </div>
        </div>
    </c:if>
</main>

<%@ include file="../../fragments/footer.jspf" %>