package dao;

import models.QuizAttempt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dao object for quiz attempts.
 * Handles database operations for managing user quiz attempts including
 * creation, completion, and retrieval. Manages the lifecycle of quiz attempts
 * from when a user starts taking a quiz until they complete it.
 * Uses the "UserQuizAttempts" table to store attempt records with timing
 * and scoring information for quiz history and performance tracking.
 */
public class QuizAttemptDao {

    /**
     * Creates a new quiz attempt record in the database.
     * Sets the start_time to current timestamp and assigns the generated
     * attempt_id back to the QuizAttempt object using setAttemptId().
     * The attempt is created in an incomplete state (end_time and score are null).
     * @param quizAttempt the QuizAttempt object to insert (should have attemptId = -1)
     * @return true if the attempt was created successfully, false otherwise
     */
    public boolean createAttempt(QuizAttempt quizAttempt){
        if(!(quizAttempt.getStartTime() == null && quizAttempt.getEndTime() == null && quizAttempt.getScore() == 0 &&
                quizAttempt.getAttemptId() == -1)){
            throw new IllegalArgumentException("attempt id, quiz start time, end time and getScore must be assigned by this class");
        }
        try(Connection connection = DatabaseConnection.getConnection()){
            String query = "INSERT INTO UserQuizAttempts (user_id, quiz_id, start_time, end_time," +
                    "score, time_taken_seconds) VALUES (?, ?, CURRENT_TIMESTAMP, NULL, 0, 0)";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setInt(1, quizAttempt.getUserId());
                preparedStatement.setInt(2, quizAttempt.getQuizId());
                preparedStatement.executeUpdate();
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    int attemptId = 0;
                    if (resultSet.next()) {
                        attemptId = resultSet.getInt(1);
                        quizAttempt.setAttemptId(attemptId);
                    }else{
                        throw new SQLException("Failed to get generated attempt ID");
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
     * Retrieves a specific quiz attempt by its ID.
     * @param attemptId the ID of the attempt to retrieve
     * @return the QuizAttempt object if found, null otherwise
     */
    public QuizAttempt getAttemptById(int attemptId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM UserQuizAttempts WHERE attempt_id = ?")){
            preparedStatement.setInt(1, attemptId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()) {
                    return getAttemptFromResultSet(resultSet, attemptId);
                }
            } return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Completes an existing quiz attempt by updating it with final results.
     * Sets end_time to current timestamp, stores the final score, and calculates
     * time_taken_seconds
     * @param attemptId the ID of the attempt to complete
     * @param score the final score achieved by the user
     * @return true if the attempt was completed successfully, false otherwise
     */
    public boolean completeAttempt(int attemptId, double score){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE UserQuizAttempts SET score = ?, end_time = ?, time_taken_seconds = ? " +
                            "WHERE attempt_id = ?")){
            QuizAttempt quizAttempt = getAttemptById(attemptId);
            if(quizAttempt == null) {
                return false;
            }
            preparedStatement.setDouble(1, score);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(2, timestamp);
            preparedStatement.setInt(3, (int)((timestamp.getTime() - quizAttempt.getStartTime().getTime()) / 1000));
            preparedStatement.setInt(4, attemptId);
            int rows = preparedStatement.executeUpdate();
            return rows != 0;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all quiz attempts made by a specific user.
     * Results are ordered by start_time in descending order (most recent first).
     * @param userId the ID of the user whose attempts to retrieve
     * @return list of QuizAttempt objects, empty list if no attempts found
     */
    public List<QuizAttempt> getUserAttempts(int userId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM UserQuizAttempts WHERE user_id = ? ORDER BY start_time DESC")){
            preparedStatement.setInt(1, userId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return getUserAttempts(resultSet);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //helper method, gets all the parameters from resultset, constructs a quiz with them and returns it
    private List<QuizAttempt> getUserAttempts(ResultSet resultSet) throws SQLException {
        List<QuizAttempt> quizAttempts = new ArrayList<>();
        while (resultSet.next()) {
            int attemptId = resultSet.getInt("attempt_id");
            QuizAttempt quizAttempt = getAttemptFromResultSet(resultSet, attemptId);
            quizAttempts.add(quizAttempt);
        }
        return quizAttempts;
    }

    /**
     * Retrieves all attempts for a specific quiz across all users.
     * Used for generating leaderboards and quiz statistics.
     * Results are ordered by score descending, then by time_taken_seconds ascending.
     * @param quizId the ID of the quiz whose attempts to retrieve
     * @return list of QuizAttempt objects, empty list if no attempts found
     */
    public List<QuizAttempt> getQuizAttempts(int quizId){
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM UserQuizAttempts WHERE quiz_id = ? ORDER BY score DESC, time_taken_seconds")){
            preparedStatement.setInt(1, quizId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                return getUserAttempts(resultSet);
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //helper method, gets all the parameters from resultset, constructs a QuizAttempt with them and returns it
    private QuizAttempt getAttemptFromResultSet(ResultSet resultSet, int attemptId) throws SQLException {
        int userId = resultSet.getInt("user_id");
        int quizId = resultSet.getInt("quiz_id");
        Timestamp startTime = resultSet.getTimestamp("start_time");
        Timestamp endTime = resultSet.getTimestamp("end_time");
        double score = resultSet.getDouble("score");
        return new QuizAttempt(attemptId, userId, quizId, startTime, endTime, score);
    }
}
