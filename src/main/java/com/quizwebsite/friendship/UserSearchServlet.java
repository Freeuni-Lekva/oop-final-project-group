package com.quizwebsite.friendship;

import User.User;
import User.UserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/user-search")
public class UserSearchServlet extends HttpServlet {
    private UserDao userDao;

    public UserSearchServlet() {
        this.userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        if (query != null && !query.trim().isEmpty()) {
            List<User> users = userDao.findUsersByUsername(query);
            request.setAttribute("users", users);
        }
        request.getRequestDispatcher("/WEB-INF/views/friendship/search.jsp").forward(request, response);
    }
}