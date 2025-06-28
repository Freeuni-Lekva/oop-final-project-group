package com.quizwebsite.friendship;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ManageFriendRequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FriendshipService friendshipService;

    @Override
    public void init() throws ServletException {
        this.friendshipService = (FriendshipService) getServletContext().getAttribute("friendshipService");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String action = request.getParameter("action");

            if ("accept".equals(action)) {
                friendshipService.acceptFriendRequest(requestId);
            } else if ("reject".equals(action)) {
                friendshipService.rejectFriendRequest(requestId);
            }

            response.sendRedirect(request.getContextPath() + "/home");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request ID.");
        } catch (Exception e) {
            throw new ServletException("Error managing friend request", e);
        }
    }
}