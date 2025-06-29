<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../../fragments/header.jspf" %>

<main class="container">
    <h1>My Friends</h1>
    <div class="card">
        <c:if test="${empty friends}">
            <p>You have no friends yet. Use the search to find and add friends.</p>
        </c:if>
        <c:if test="${not empty friends}">
            <div class="user-list">
                <c:forEach var="friend" items="${friends}">
                    <div class="user-item">
                        <a href="user-profile?username=${friend.username}">${friend.username}</a>
                        <form action="remove-friend" method="post" style="display: inline;">
                            <input type="hidden" name="friendId" value="${friend.id}" />
                            <button type="submit" class="btn btn-danger">Remove</button>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </div>
</main>

<%@ include file="../../fragments/footer.jspf" %>