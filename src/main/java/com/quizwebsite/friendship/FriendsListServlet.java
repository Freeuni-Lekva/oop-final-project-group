package com.quizwebsite.friendship;

import User.User;
import com.quizwebsite.friendship.FriendshipDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/friends-list")
public class FriendsListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser != null) {
            FriendshipDao friendshipDao = new FriendshipDao();
            List<User> friends = friendshipDao.getFriendsForUser(currentUser.getId());
            request.setAttribute("friends", friends);
            request.getRequestDispatcher("/WEB-INF/views/friendship/friends.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}