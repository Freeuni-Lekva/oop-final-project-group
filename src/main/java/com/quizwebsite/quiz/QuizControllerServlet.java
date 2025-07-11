package com.quizwebsite.quiz;

import dao.QuizDao;
import models.Question;
import models.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizControllerServlet extends HttpServlet {

    private QuizDao quizDao;

    @Override
    public void init() throws ServletException {
        super.init();
        // In a real application, you would inject this dependency
        quizDao = new QuizDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.endsWith("/start")) {
            startQuiz(request, response);
        } else if (path.endsWith("/question")) {
            displayQuestion(request, response);
        } else if (path.endsWith("/finish")) {
            finishQuiz(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/quizzes");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.endsWith("/answer")) {
            submitAnswer(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void startQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int quizId = Integer.parseInt(request.getParameter("id"));
            Quiz quiz = quizDao.getQuizById(quizId); // Assumes getQuizById fetches questions

            if (quiz == null) {
                response.sendRedirect(request.getContextPath() + "/quizzes");
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("currentQuiz", quiz);
            session.setAttribute("currentQuestionIndex", 0);
            session.setAttribute("userAnswers", new HashMap<Integer, List<String>>());

            response.sendRedirect(request.getContextPath() + "/quiz/question");
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
        }
    }

    private void displayQuestion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Quiz currentQuiz = (Quiz) session.getAttribute("currentQuiz");
        Integer currentQuestionIndex = (Integer) session.getAttribute("currentQuestionIndex");

        if (currentQuiz == null || currentQuestionIndex == null) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        List<Question> questions = currentQuiz.getQuestions();
        if (currentQuestionIndex >= questions.size()) {
            // This case should be handled by the finish logic, but as a safeguard:
            response.sendRedirect(request.getContextPath() + "/quiz/finish");
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        request.setAttribute("currentQuestion", currentQuestion);
        request.setAttribute("questionNumber", currentQuestionIndex + 1);
        request.setAttribute("totalQuestions", questions.size());

        request.getRequestDispatcher("/WEB-INF/views/quiz/takeQuiz.jsp").forward(request, response);
    }

    private void submitAnswer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Map<Integer, List<String>> userAnswers = (Map<Integer, List<String>>) session.getAttribute("userAnswers");
        Integer currentQuestionIndex = (Integer) session.getAttribute("currentQuestionIndex");
        Quiz currentQuiz = (Quiz) session.getAttribute("currentQuiz");

        if (userAnswers == null || currentQuestionIndex == null || currentQuiz == null) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        try {
            int questionId = Integer.parseInt(request.getParameter("questionId"));
            String[] answers = request.getParameterValues("userAnswers");
            String optionId = request.getParameter("optionId");

            List<String> submittedAnswers = new ArrayList<>();
            if (answers != null) {
                Collections.addAll(submittedAnswers, answers);
            } else if (optionId != null) {
                submittedAnswers.add(optionId);
            }

            userAnswers.put(questionId, submittedAnswers);
        } catch (NumberFormatException e) {
            // Handle cases where parameters are missing or invalid, maybe redirect with an error
            response.sendRedirect(request.getContextPath() + "/quiz/question");
            return;
        }

        session.setAttribute("currentQuestionIndex", currentQuestionIndex + 1);

        if ((currentQuestionIndex + 1) < currentQuiz.getQuestions().size()) {
            response.sendRedirect(request.getContextPath() + "/quiz/question");
        } else {
            response.sendRedirect(request.getContextPath() + "/quiz/finish");
        }
    }

    private void finishQuiz(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Quiz currentQuiz = (Quiz) session.getAttribute("currentQuiz");
        Map<Integer, List<String>> userAnswers = (Map<Integer, List<String>>) session.getAttribute("userAnswers");

        if (currentQuiz == null) {
            response.sendRedirect(request.getContextPath() + "/quizzes");
            return;
        }

        int score = 0;
        List<Question> questions = currentQuiz.getQuestions();
        for (Question question : questions) {
            List<String> submittedAnswers = userAnswers.get(question.getQuestionId());
            if (submittedAnswers != null && !submittedAnswers.isEmpty()) {
                if (question.calculateScore(submittedAnswers) > 0) {
                    score++;
                }
            }
        }

        request.setAttribute("finalScore", score);
        request.setAttribute("totalQuestions", questions.size());

        // Clear the session
        session.removeAttribute("currentQuiz");
        session.removeAttribute("currentQuestionIndex");
        session.removeAttribute("userAnswers");

        request.getRequestDispatcher("/WEB-INF/views/quiz/quizResult.jsp").forward(request, response);
    }
}