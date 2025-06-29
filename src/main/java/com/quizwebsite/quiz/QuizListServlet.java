package com.quizwebsite.quiz;

import dao.QuizDao;
import models.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class QuizListServlet extends HttpServlet {
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
        List<Quiz> quizList = quizDao.getAllQuizzes();
        request.setAttribute("quizList", quizList);
        request.getRequestDispatcher("/WEB-INF/views/quiz/quizList.jsp").forward(request, response);
    }
}