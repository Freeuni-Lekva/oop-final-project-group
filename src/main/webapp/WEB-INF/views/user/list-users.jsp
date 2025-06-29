<head>
   <title>User List</title>
   <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
   <%@ include file="/WEB-INF/fragments/header.jspf" %>
   <main class="container">
       <h2>User List</h2>
       <p><a href="${pageContext.request.contextPath}/users?action=add" class="btn btn-primary">Add New User</a></p>
       <div class="card">
           <table class="styled-table">
               <thead>
                   <tr>
                       <th>ID</th>
                       <th>Username</th>
                   </tr>
               </thead>
               <tbody>
                   <c:forEach var="user" items="${userList}">
                       <tr>
                           <td>${user.id}</td>
                           <td>${user.username}</td>
                       </tr>
                   </c:forEach>
               </tbody>
           </table>
       </div>
   </main>
   <%@ include file="/WEB-INF/fragments/footer.jspf" %>
</body>