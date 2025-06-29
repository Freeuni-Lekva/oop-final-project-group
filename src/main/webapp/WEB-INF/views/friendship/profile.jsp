<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/fragments/header.jspf" %>

<div class="container">
    <h2>User Profile</h2>
    <h3>${profileUser.username}</h3>

    <c:choose>
        <c:when test="${friendshipStatus == 'NONE'}">
            <form action="send-friend-request" method="post">
                <input type="hidden" name="profileUserId" value="${profileUser.id}">
                <button type="submit" class="btn btn-primary">Send Friend Request</button>
            </form>
        </c:when>
        <c:when test="${friendshipStatus == 'PENDING_SENT'}">
            <form action="cancel-request" method="post">
                <input type="hidden" name="profileUserId" value="${profileUser.id}">
                <button type="submit" class="btn btn-warning">Cancel Friend Request</button>
            </form>
        </c:when>
        <c:when test="${friendshipStatus == 'PENDING_RECEIVED'}">
            <a href="manage-requests" class="btn btn-info">Respond to Request</a>
        </c:when>
        <c:when test="${friendshipStatus == 'FRIENDS'}">
            <form action="remove-friend" method="post">
                <input type="hidden" name="profileUserId" value="${profileUser.id}">
                <button type="submit" class="btn btn-danger">Remove Friend</button>
            </form>
        </c:when>
    </c:choose>
</div>

<%@ include file="/WEB-INF/fragments/footer.jspf" %>