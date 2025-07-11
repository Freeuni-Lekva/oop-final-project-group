package com.quizwebsite.quiz;

import User.User;
import dao.QuizDao;
import models.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/create-quiz")
public class QuizCreatorServlet extends HttpServlet {
    private QuizDao quizDao;

    @Override
    public void init() throws ServletException {
        super.init();
        // For simplicity, we instantiate it directly. In a real application,
        // you might use a dependency injection framework or a service locator.
        this.quizDao = new QuizDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/quiz/createQuiz.jsp").forward(request, response);
    }

}
