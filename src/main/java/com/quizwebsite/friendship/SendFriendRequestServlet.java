package com.quizwebsite.friendship;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import User.User;

public class SendFriendRequestServlet extends HttpServlet {
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
            response.sendRedirect("Login.html"); // Or some error page
            return;
        }

        try {
            int recipientId = Integer.parseInt(request.getParameter("recipientId"));
            int requesterId = currentUser.getId();

            friendshipService.sendFriendRequest(requesterId, recipientId);

            // Redirect back to the user's profile or a confirmation page
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (NumberFormatException e) {
            // Handle invalid recipientId
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid recipient ID.");
        } catch (Exception e) {
            // Handle other errors (e.g., database errors)
            throw new ServletException("Error sending friend request", e);
        }
    }
}