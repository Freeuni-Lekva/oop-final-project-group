package com.quizwebsite.friendship;

import User.User;
import User.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user-profile")
public class UserProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usernameToView = request.getParameter("username");
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserDao userDao = new UserDao();
        User profileUser = userDao.findUserByUsername(usernameToView);

        if (profileUser == null) {
            // Handle user not found
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        FriendshipService friendshipService = new FriendshipService();
        String friendshipStatus = friendshipService.getFriendshipStatus(currentUser.getId(), profileUser.getId());

        request.setAttribute("profileUser", profileUser);
        request.setAttribute("friendshipStatus", friendshipStatus);
        request.getRequestDispatcher("/WEB-INF/views/friendship/profile.jsp").forward(request, response);
    }
}