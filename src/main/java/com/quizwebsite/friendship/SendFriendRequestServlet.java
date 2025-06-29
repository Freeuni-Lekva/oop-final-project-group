package com.quizwebsite.friendship;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import User.User;
import User.UserDao;

@WebServlet("/send-friend-request")
public class SendFriendRequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final FriendshipService friendshipService;
    private final UserDao userDao;

    public SendFriendRequestServlet() {
        this.friendshipService = new FriendshipService(new FriendshipDao(), new FriendRequestDao());
        this.userDao = new UserDao();
    }

    public SendFriendRequestServlet(FriendshipService friendshipService, UserDao userDao) {
        this.friendshipService = friendshipService;
        this.userDao = userDao;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int recipientId = Integer.parseInt(request.getParameter("recipientId"));
            friendshipService.sendFriendRequest(currentUser.getId(), recipientId);

            User recipient = userDao.getUserById(recipientId);
            String recipientUsername = "";
            if (recipient != null) {
                recipientUsername = recipient.getUsername();
            } else {
                // This case should ideally not be reached if the UI is built correctly.
                // Redirect to a safe page if recipient is not found.
                response.sendRedirect(request.getContextPath() + "/user-search");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/user-profile?username=" + recipientUsername);
        } catch (NumberFormatException e) {
            // Handle error if recipientId is not a valid integer
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid recipient ID.");
        } catch (Exception e) {
            // Log and handle other potential exceptions from the service layer
            // For now, we'll just print stack trace and send a server error response.
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while sending the friend request.");
        }
    }
}