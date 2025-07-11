package com.quizwebsite.friendship;

import User.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/friend-requests")
public class FriendRequestServlet extends HttpServlet {

    private FriendRequestDao friendRequestDao;
    private FriendshipDao friendshipDao;

    @Override
    public void init() {
        friendRequestDao = new FriendRequestDao();
        friendshipDao = new FriendshipDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<FriendRequest> requests = friendRequestDao.getPendingRequestsForUser(currentUser.getId());
        request.setAttribute("requests", requests);
        request.getRequestDispatcher("/WEB-INF/views/friendship/requests.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String action = request.getParameter("action");

            if ("accept".equals(action)) {
                FriendRequest friendRequest = friendRequestDao.getFriendRequestById(requestId);
                if (friendRequest != null && friendRequest.getRecipientId() == currentUser.getId()) {
                    friendshipDao.addFriendship(friendRequest.getRequesterId(), friendRequest.getRecipientId());
                    friendRequestDao.updateFriendRequestStatus(requestId, "accepted");
                }
            } else if ("decline".equals(action)) {
                friendRequestDao.updateFriendRequestStatus(requestId, "declined");
            }
        } catch (NumberFormatException e) {
            // In a real application, you would log this error.
            // For now, we'll just redirect without taking action.
        }

        response.sendRedirect(request.getContextPath() + "/friend-requests");
    }
}