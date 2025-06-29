package com.quizwebsite.friendship;

import com.quizwebsite.friendship.FriendshipService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/manage-friend-request")
public class ManageFriendRequestServlet extends HttpServlet {

    private final FriendshipService friendshipService;

    public ManageFriendRequestServlet() {
        this.friendshipService = new FriendshipService(new FriendshipDao(), new FriendRequestDao());
    }

    public ManageFriendRequestServlet(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String action = request.getParameter("action");

        if ("accept".equals(action)) {
            friendshipService.acceptFriendRequest(requestId);
        } else if ("reject".equals(action)) {
            friendshipService.rejectFriendRequest(requestId);
        }

        response.sendRedirect(request.getContextPath() + "/friend-requests");
    }
}