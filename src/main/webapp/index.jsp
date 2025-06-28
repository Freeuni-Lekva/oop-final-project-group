<%@ page import="User.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user != null) {
        // User is logged in, forward to the home page servlet
        request.getRequestDispatcher("/home").forward(request, response);
    } else {
        // User is not logged in, forward to the login page
        request.getRequestDispatcher("/LoginPage.jsp").forward(request, response);
    }
%>