package dao;

import models.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for question data access objects.
 * Implements the Template Method pattern to provide common CRUD operations
 * while delegating question-type-specific logic to subclasses.
 * Subclasses must implement methods for handling their specific answer formats
 * and database table structures.
 */
public abstract class AbstractQuestionDao {

    /**
     * Returns the question with the given id from the database
     * @param questionId the id of a question to return
     * @return the question to return
     */
    public final Question getQuestionById(int questionId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Questions WHERE question_id = ?")){
            preparedStatement.setInt(1, questionId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    String questionText = resultSet.getString("question_text");
                    int quizId = resultSet.getInt("quiz_id");
                    String imageUrl = resultSet.getString("image_url");
                    Integer orderInQuiz = (Integer) resultSet.getObject("order_in_quiz");
                    double maxScore = resultSet.getDouble("max_score");
                    Object answers = getAnswersFromDB(connection, questionId);
                    Question question = createQuestionObject(questionId, questionText, answers, quizId, orderInQuiz, maxScore, imageUrl);
                    question.setImageUrl(imageUrl);
                    return question;
                }
            } return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a question to the database
     * @param question the question to add
     * @return returns boolean indicating if insertion was successful or not
     */
    public final boolean addQuestion(Question question) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (!executeInsertQuestion(question, connection)) {
                return false;
            }
            insertAnswersIntoDB(question.getQuestionId(), question, connection);
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     * Inserts the question to the database, accepts question and an active database connection as parameters.
     * Returns the questionId of an inserted question (it gets assigned by the database)
     */
    private boolean executeInsertQuestion(Question question, Connection connection) throws SQLException {
        String query = "INSERT INTO Questions (quiz_id, question_text, question_type, image_url, order_in_quiz, max_score) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, question.getQuizId());
            preparedStatement.setString(2, question.getQuestionText());
            preparedStatement.setString(3, question.getQuestionType());
            preparedStatement.setString(4, question.getImageUrl());
            preparedStatement.setInt(5, question.getOrderInQuiz());
            preparedStatement.setDouble(6, question.getMaxScore());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                return false;
            }
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    question.setQuestionId(resultSet.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * Changes the question to a new Question. The id and a quiz_id remain the same.
     * @param question A new question containing quiz_id and a question_id of an old question
     * @return returns boolean indicating if the operation was successful or not
     */
    public final boolean updateQuestion(Question question) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE Questions SET question_text = ?, question_type = ?, order_in_quiz = ?, max_score = ?, image_url = ? WHERE question_id = ?");
             PreparedStatement preparedStatement2 = connection.prepareStatement("DELETE FROM " + getAnswerTableName() +
                     " WHERE question_id = ?")) {
            preparedStatement.setString(1, question.getQuestionText());
            preparedStatement.setString(2, question.getQuestionType());
            preparedStatement.setInt(3, question.getOrderInQuiz());
            preparedStatement.setDouble(4, question.getMaxScore());
            preparedStatement.setString(5, question.getImageUrl());
            preparedStatement.setInt(6, question.getQuestionId());
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                return false;
            }
            preparedStatement2.setInt(1, question.getQuestionId());
            preparedStatement2.executeUpdate();
            insertAnswersIntoDB(question.getQuestionId(), question, connection);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Deletes the question from the database
     * @param questionId the id of a question to delete
     * @return returns boolean based on if the operation was successful or not
     */
    public final boolean deleteQuestion(int questionId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Questions WHERE question_id = ?")){
            preparedStatement.setInt(1, questionId);
            int rows = preparedStatement.executeUpdate();
            return rows != 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns all the questions in the quiz as a list
     * @param quizId the id of a quiz to return answers from
     * @return All The questions in a quiz as a list.
     */
    public final List<Question> getAllQuestions(int quizId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Questions WHERE quiz_id = ?")){
            preparedStatement.setInt(1, quizId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                ArrayList<Question> questions = new ArrayList<>();
                while (resultSet.next()) {
                    int questionId = resultSet.getInt("question_id");
                    String questionText = resultSet.getString("question_text");
                    Object answers = getAnswersFromDB(connection, questionId);
                    String imageUrl = resultSet.getString("image_url");
                    int orderInQuiz = resultSet.getInt("order_in_quiz");
                    double maxScore = resultSet.getDouble("max_score");
                    Question q = createQuestionObject(questionId, questionText, answers, quizId, orderInQuiz, maxScore, imageUrl);
                    q.setImageUrl(imageUrl);
                    questions.add(q);
                }
                return questions;
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves answers from the database in the format expected by this question type.
     * @param connection active database connection
     * @param questionId the question ID to retrieve answers for
     * @return answers in question-type-specific format
     * @throws SQLException if database error occurs
     */
    protected abstract Object getAnswersFromDB(Connection connection, int questionId) throws SQLException;

    /**
     * Inserts answers into the appropriate database table for this question type.
     * @param questionId the question ID to associate answers with
     * @param question the question containing answers to insert
     * @param connection active database connection
     * @throws SQLException if database error occurs
     */
    protected abstract void insertAnswersIntoDB(int questionId, Question question, Connection connection) throws SQLException;

    /**
     * Creates a concrete question object of the appropriate subtype.
     *
     * @param questionId the question ID
     * @param questionText the question text
     * @param answers the answers in the format returned by getAnswersFromDB
     * @param quizId the quiz this question belongs to
     * @param orderInQuiz The N of a question in a quiz
     * @return concrete Question instance
     */
    protected abstract Question createQuestionObject(int questionId, String questionText, Object answers, int quizId, int orderInQuiz, double maxScore, String imageUrl);

    /**
     * Returns the database table name where this question type stores its answers.
     * @return the answer table name
     */
    protected abstract String getAnswerTableName();
}
