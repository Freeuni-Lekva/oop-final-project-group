package service;

import dao.AbstractQuestionDao;
import dao.DatabaseConnection;
import factory.QuestionDaoFactory;
import models.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for loading questions from the database across different question types.
 * Provides a unified interface for retrieving all questions belonging to a quiz or individual questions,
 * regardless of their specific question type implementations, which makes polymorphism easy.
 * Uses the Factory pattern to delegate to appropriate DAOs for each question type.
 */
public class QuestionService {
    /**
     * Retrieves all questions for a given quiz, ordered by their position in the quiz.
     * Handles mixed question types by using the QuestionDaoFactory to get the appropriate
     * DAO for each question and loading the complete question data including answers.
     * @param quizId the ID of the quiz to load questions for
     * @return ordered list of all questions in the quiz, or empty list if an error occurs
     */
    public List<Question> getAllQuestionsFromQuiz(int quizId) {
        List<Question> questions = new ArrayList<>();
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Questions WHERE quiz_id=? ORDER BY order_in_quiz")) {
            preparedStatement.setInt(1, quizId);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String questionType = resultSet.getString("question_type");
                    AbstractQuestionDao dao = QuestionDaoFactory.getDao(questionType);
                    Question question = dao.getQuestionById(resultSet.getInt("question_id"));
                    questions.add(question);
                }
            }
            return questions;
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a single question by its ID.
     * Uses the factory pattern to get the appropriate DAO for the question type.
     * @param questionId the ID of the question to retrieve
     * @return the Question object if found, null otherwise
     */
    public Question getQuestionById(int questionId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement("SELECT question_type FROM Questions WHERE question_id = ?")) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String questionType = rs.getString("question_type");
                AbstractQuestionDao dao = QuestionDaoFactory.getDao(questionType);
                return dao.getQuestionById(questionId);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
