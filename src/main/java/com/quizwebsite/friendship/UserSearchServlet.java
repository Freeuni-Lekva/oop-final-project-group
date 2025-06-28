package com.quizwebsite.friendship;

import User.User;
import User.UserDao;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDao userDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.userDao = (UserDao) getServletContext().getAttribute("userDao");
        this.gson = new Gson();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        if (query == null || query.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query parameter is missing.");
            return;
        }

        List<User> users = userDao.findUsersByUsername(query);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(users));
    }
}