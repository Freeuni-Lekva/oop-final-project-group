<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
   <title>Friend Requests</title>
   <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
   <%@ include file="/WEB-INF/fragments/header.jspf" %>
   <main class="container">
       <h2>Incoming Friend Requests</h2>

       <c:if test="${empty requests}">
           <div class="card">
               <p>You have no incoming friend requests.</p>
           </div>
       </c:if>

       <c:if test="${not empty requests}">
           <div class="card">
               <div class="user-list">
                   <c:forEach var="request" items="${requests}">
                       <div class="user-item">
                           <span>${request.requesterUsername}</span>
                           <div class="user-item-actions">
                               <form action="manage-friend-request" method="post" style="display: inline;">
                                   <input type="hidden" name="requestId" value="${request.requestId}">
                                   <input type="hidden" name="action" value="accept">
                                   <button type="submit" class="btn btn-primary">Accept</button>
                               </form>
                               <form action="manage-friend-request" method="post" style="display: inline;">
                                   <input type="hidden" name="requestId" value="${request.requestId}">
                                   <input type="hidden" name="action" value="reject">
                                   <button type="submit" class="btn btn-secondary">Decline</button>
                               </form>
                           </div>
                       </div>
                   </c:forEach>
               </div>
           </div>
       </c:if>
   </main>
   <%@ include file="/WEB-INF/fragments/footer.jspf" %>
</body>