package com.quizwebsite.friendship;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import User.User;

public class RemoveFriendServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FriendshipService friendshipService;

    @Override
    public void init() throws ServletException {
        this.friendshipService = (FriendshipService) getServletContext().getAttribute("friendshipService");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect("Login.html");
            return;
        }

        try {
            int friendId = Integer.parseInt(request.getParameter("friendId"));
            int userId = currentUser.getId();

            friendshipService.removeFriend(userId, friendId);

            response.sendRedirect(request.getContextPath() + "/home");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid friend ID.");
        } catch (Exception e) {
            throw new ServletException("Error removing friend", e);
        }
    }
}