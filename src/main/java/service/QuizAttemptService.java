package service;

import dao.*;
import models.*;
import quiz_engine.QuizSession;

import java.sql.*;
import java.util.*;

/**
 * Service class that orchestrates the complete quiz-taking workflow.
 * Acts as the bridge between the web layer and domain objects, handling the complex
 * translation between domain representations (List<String>) and database persistence
 * (individual UserAnswer records). Manages quiz attempt lifecycle from creation
 * through completion with proper scoring and answer tracking.
 * This class implements the application service pattern, coordinating between
 * multiple DAOs while maintaining clean separation of concerns.
 */
public class QuizAttemptService {
    private final QuizAttemptDao quizAttemptDao;
    private final UserAnswerDao userAnswerDao;
    private final QuizDao quizDao;
    private final QuestionService questionService;

    /**
     * Constructs a new QuizAttemptService with all required dependencies.
     * Initializes DAO instances for quiz attempts, user answers, quizzes, and question retrieval.
     * Uses the default constructors for all dependencies, following the composition pattern
     * for clean separation between service and data access layers.
     */
    public QuizAttemptService() {
        quizAttemptDao = new QuizAttemptDao();
        userAnswerDao = new UserAnswerDao();
        quizDao = new QuizDao();
        questionService = new QuestionService();
    }

    /**
     * Creates a new quiz attempt and returns a QuizSession for taking the quiz.
     * Validates that the quiz exists before creating the attempt record.
     *
     * @param userId the ID of the user taking the quiz
     * @param quizId the ID of the quiz to be taken
     * @return QuizSession configured for this attempt
     * @throws IllegalArgumentException if quiz doesn't exist
     * @throws RuntimeException         if attempt creation fails
     */
    public QuizSession startQuizAttempt(int userId, int quizId) {
        if (!quizDao.quizExists(quizId)) {
            throw new IllegalArgumentException("This Quiz Id does not exist!");
        }
        if (quizDao.getQuestionCount(quizId) == 0) {
            throw new RuntimeException("Could not create Quiz Attempt!");
        }
        QuizAttempt quizAttempt = new QuizAttempt(userId, quizId);
        boolean wasCreated = quizAttemptDao.createAttempt(quizAttempt);
        if (!wasCreated) {
            throw new RuntimeException("Could not create Quiz Attempt!");
        }
        return new QuizSession(quizDao.getQuizById(quizId), quizAttempt.getAttemptId());
    }

    /**
     * Submits and saves a user's answer to a specific question within a quiz attempt.
     * Handles the complex translation from domain objects (List<String>) to database
     * records (individual UserAnswer entries). Replaces any existing answers for
     * the same question to handle answer changes.
     *
     * @param attemptId     the ID of the quiz attempt
     * @param questionOrder the position of the question in the quiz (0-based)
     * @param userAnswers   list of user's answers for this question
     * @return true if answers were saved successfully, false otherwise
     */
    public boolean submitAnswer(int attemptId, int questionOrder, List<String> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return false;
        }
        try {
            Question question = getQuestionByOrder(attemptId, questionOrder);
            List<Boolean> correctness = question.checkAnswers(userAnswers);
            userAnswerDao.deleteAnswersForQuestion(attemptId, question.getQuestionId());
            saveAnswersForQuestion(question, userAnswers, attemptId, correctness);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Completes a quiz attempt by calculating the final score and updating the database.
     * Reconstructs user answers from database records, calculates partial credit using
     * each question's scoring logic, and stores the final score as a percentage.
     *
     * @param attemptId the ID of the quiz attempt to complete
     * @return the completed QuizAttempt with final score and timing, null if error occurs
     */
    public QuizAttempt completeQuiz(int attemptId) {
        try {
            List<UserAnswer> allAnswers = userAnswerDao.getAnswersForAttempt(attemptId);
            Map<Integer, List<UserAnswer>> answersByQuestion = groupAnswersByQuestionId(allAnswers);
            double totalScore = calculateTotalScore(answersByQuestion);
            quizAttemptDao.completeAttempt(attemptId, totalScore);
            return quizAttemptDao.getAttemptById(attemptId);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Retrieves the Question object for a specific position in a quiz attempt.
     * Maps from question order (used by QuizSession) to actual Question objects
     * by using the same ordering as the original quiz.
     */
    private Question getQuestionByOrder(int attemptId, int questionOrder) {
        QuizAttempt quizAttempt = quizAttemptDao.getAttemptById(attemptId);
        Quiz quiz = quizDao.getQuizById(quizAttempt.getQuizId());
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz attempt not found: " + attemptId);
        }
        List<Question> questions = questionService.getAllQuestionsFromQuiz(quiz.getQuizId());
        if (questionOrder >= 0 && questionOrder < questions.size()) {
            return questions.get(questionOrder);
        }
        throw new IllegalArgumentException("Invalid question order: " + questionOrder);
    }

    /*
     * Saves individual answer pieces to the database with correctness tracking.
     * Handles different question types appropriately - stores option IDs for
     * multiple choice questions and text for all other question types.
     */
    private void saveAnswersForQuestion(Question question, List<String> userAnswers, int attemptId, List<Boolean> correctness) {
        for (int i = 0; i < userAnswers.size(); i++) {
            String answer = userAnswers.get(i);
            boolean isCorrect = correctness.get(i);
            UserAnswer userAnswer;
            if (question.getQuestionType().equals("MULTIPLE_CHOICE")) {
                Integer optionId = findOptionId(question, answer);
                userAnswer = new UserAnswer(attemptId, question.getQuestionId(), answer, optionId, isCorrect);
            } else {
                userAnswer = new UserAnswer(attemptId, question.getQuestionId(), answer, null, isCorrect);
            }
            userAnswerDao.saveAnswer(userAnswer);
        }
    }

    /*
     * Finds the database option ID for a multiple choice answer text.
     * Required for maintaining foreign key relationships in the UserAnswers table.
     */
    private Integer findOptionId(Question question, String answerText) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT option_id FROM AnswerOptionsMC WHERE question_id = ? AND option_text = ?")) {
            stmt.setInt(1, question.getQuestionId());
            stmt.setString(2, answerText);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("option_id");
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    /*
     * Groups UserAnswer records by question ID for scoring calculations.
     * Converts flat list of individual answer pieces back into question-grouped structure.
     */
    private Map<Integer, List<UserAnswer>> groupAnswersByQuestionId(List<UserAnswer> allAnswers) {
        Map<Integer, List<UserAnswer>> answersByQuestion = new HashMap<>();
        for (UserAnswer answer : allAnswers) {
            int questionId = answer.getQuestionId();
            if (!answersByQuestion.containsKey(questionId)) {
                answersByQuestion.put(questionId, new ArrayList<>());
            }
            answersByQuestion.get(questionId).add(answer);
        }
        return answersByQuestion;
    }

    /*
     * Calculates total quiz score by reconstructing domain objects and using
     * each question's built-in scoring logic. Supports partial credit with
     * decimal scoring (each question worth 1.0 points maximum).
     */
    private double calculateTotalScore(Map<Integer, List<UserAnswer>> answersByQuestion) {
        double totalScore = 0.0;
        for (Integer questionId : answersByQuestion.keySet()) {
            Question question = questionService.getQuestionById(questionId);
            List<String> userAnswers = new ArrayList<>();
            for (UserAnswer answer : answersByQuestion.get(questionId)) {
                userAnswers.add(answer.getAnswerGivenText());
            }
            double questionScore = question.calculateScore(userAnswers);
            totalScore += questionScore;
        }
        return totalScore;
    }
}
