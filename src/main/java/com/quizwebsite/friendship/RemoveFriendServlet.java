package com.quizwebsite.friendship;

import User.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/remove-friend")
public class RemoveFriendServlet extends HttpServlet {
    private final FriendshipService friendshipService;

    public RemoveFriendServlet() {
        this.friendshipService = new FriendshipService(new FriendshipDao(), new FriendRequestDao());
    }

    public RemoveFriendServlet(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp"); // Redirect to login if not authenticated
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        int friendId = Integer.parseInt(request.getParameter("profileUserId"));

        friendshipService.removeFriend(currentUser.getId(), friendId);

        response.sendRedirect(request.getContextPath() + "/friends-list");
    }
}