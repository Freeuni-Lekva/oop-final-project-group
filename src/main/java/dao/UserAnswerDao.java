package dao;
import models.UserAnswer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for user answers.
 * Handles database operations for individual user answer records stored in the UserAnswers table.
 * Each UserAnswer represents one answer piece (e.g., one blank in fill-in-blank, one selected
 * option in multiple choice). Multiple UserAnswer records may exist per question for questions
 * with multiple parts.
 */
public class UserAnswerDao {

    /**
     * Saves a new user answer to the database.
     * Inserts the answer record and assigns the generated user_answer_id back to the UserAnswer object.
     * @param answer the UserAnswer object to save (should have userAnswerId = -1)
     * @return true if the answer was saved successfully, false otherwise
     */
    public boolean saveAnswer(UserAnswer answer){
        try(Connection connection = DatabaseConnection.getConnection()){
            String query = "INSERT INTO UserAnswers (attempt_id, question_id, answer_given_text, selected_option_id, is_correct)" +
                    " VALUES (?, ?, ?, ?, ?)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                setUserAnswerParameters(preparedStatement, answer);
                preparedStatement.executeUpdate();
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    int userAnswerId = 0;
                    if (resultSet.next()) {
                        userAnswerId = resultSet.getInt(1);
                        answer.setUserAnswerId(userAnswerId);
                    }else{
                        throw new SQLException("Failed to get generated user answer ID");
                    }
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Retrieves a specific user answer by its ID.
     * @param userAnswerId the ID of the answer to retrieve
     * @return the UserAnswer object if found, null otherwise
     */
    public UserAnswer getAnswerById(int userAnswerId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM UserAnswers WHERE user_answer_id = ?")){
            preparedStatement.setInt(1,userAnswerId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return getUserAnswerFromResultSet(resultSet, userAnswerId);
                }
            } return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates an existing user answer with new values.
     * All fields except the userAnswerId can be modified.
     * @param answer the UserAnswer object with updated values (must have valid userAnswerId)
     * @return true if the answer was updated successfully, false otherwise
     */
    public boolean updateAnswer(UserAnswer answer){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE UserAnswers SET attempt_id = ?, question_id = ?, answer_given_text = ?, " +
                            "selected_option_id = ?, is_correct = ? WHERE user_answer_id = ?")){
            setUserAnswerParameters(preparedStatement, answer);
            preparedStatement.setInt(6, answer.getUserAnswerId());
            int rows = preparedStatement.executeUpdate();
            return rows != 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    //Helper method, sets the preparedStatement with answer's getter methods
    private void setUserAnswerParameters(PreparedStatement preparedStatement, UserAnswer answer) throws SQLException {
        preparedStatement.setInt(1, answer.getAttemptId());
        preparedStatement.setInt(2, answer.getQuestionId());
        preparedStatement.setString(3, answer.getAnswerGivenText());
        if (answer.getSelectedOptionId() != null) {
            preparedStatement.setInt(4, answer.getSelectedOptionId());
        } else {
            preparedStatement.setNull(4, Types.INTEGER);
        }
        preparedStatement.setBoolean(5, answer.isCorrect());
    }

    /**
     * Retrieves all user answers for a specific quiz attempt.
     * Returns all individual answer pieces that belong to the given attempt,
     * which can then be grouped by question_id to reconstruct complete answers.
     * @param attemptId the ID of the quiz attempt
     * @return list of UserAnswer objects for the attempt, empty list if none found
     */
    public List<UserAnswer> getAnswersForAttempt(int attemptId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM UserAnswers WHERE attempt_id = ?")){
            preparedStatement.setInt(1, attemptId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return getUserAnswers(resultSet);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Deletes all user answers for a specific question within a quiz attempt.
     * Used when a user changes their answer to a question - removes the old answers
     * before saving new ones to prevent duplicate/conflicting answer records.
     * @param attemptId the ID of the quiz attempt
     * @param questionId the ID of the question of whose answers should be deleted
     * @return true if deletion was successful (including if no answers existed), false if database error occurred
     */
    public boolean deleteAnswersForQuestion(int attemptId, int questionId) {
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "DELETE FROM UserAnswers WHERE attempt_id = ? AND question_id = ?")) {
            preparedStatement.setInt(1, attemptId);
            preparedStatement.setInt(2, questionId);
            int rows = preparedStatement.executeUpdate();
            return rows != 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //helper method, gets all the parameters from resultset, constructs a quiz with them and returns it
    private List<UserAnswer> getUserAnswers(ResultSet resultSet) throws SQLException {
        List<UserAnswer> userAnswers = new ArrayList<>();
        while (resultSet.next()) {
            int userAnswerId = resultSet.getInt("user_answer_id");
            UserAnswer userAnswer = getUserAnswerFromResultSet(resultSet, userAnswerId);
            userAnswers.add(userAnswer);
        }
        return userAnswers;
    }


    //helper method, gets all the parameters from resultset, constructs a userAnswerId with them and returns it
    private UserAnswer getUserAnswerFromResultSet(ResultSet resultSet, int userAnswerId) throws SQLException {
        int attemptId = resultSet.getInt("attempt_id");
        int questionId = resultSet.getInt("question_id");
        String answerGivenText = resultSet.getString("answer_given_text");
        Integer selectedOptionId = resultSet.getObject("selected_option_id", Integer.class);
        boolean isCorrect = resultSet.getBoolean("is_correct");
        return new UserAnswer(userAnswerId, attemptId, questionId, answerGivenText, selectedOptionId, isCorrect);
    }
}
