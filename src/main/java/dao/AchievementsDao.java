package dao;

import model.Achievements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


/**
 * DAO class for interacting with achievement-related data in the database.
 * Handles operations related to quiz creation count, attempt count, and assigning achievements to users.
 */
public class AchievementsDao {


    /**
     * Retrieves the number of quizzes created by a specific user.
     *
     * @param userId The ID of the user whose quiz creation count is to be retrieved.
     * @return The number of quizzes created by the user.
     */
    public int getCreatedCount(Integer userId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pS = connection.prepareStatement("SELECT COUNT(*) FROM Quizzes WHERE creator_user_id = ?")){
            pS.setInt(1, userId);
            try (ResultSet rs = pS.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }


    /**
     * Retrieves the number of quiz attempts made by a specific user.
     *
     * @param userId The ID of the user whose quiz attempt count is to be retrieved.
     * @return The number of attempts made by the user.
     */
    public int getAttemptCount(Integer userId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pS = connection.prepareStatement("SELECT COUNT(*) FROM UserQuizAttempts WHERE user_id = ?")){
            pS.setInt(1, userId);
            try (ResultSet rs = pS.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Assigns a specific achievement to a user by inserting a record into the UserAchievements table.
     *
     * @param userId      The ID of the user to whom the achievement will be given.
     * @param achievement The achievement object to assign to the user.
     * @return true if the achievement was successfully assigned; false otherwise.
     */
    
    public boolean giveAchievementToUser(Integer userId, Achievements achievement) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pS = connection.prepareStatement("INSERT INTO UserAchievements (user_id, achievement_id) VALUES (?, ?)")){
            pS.setInt(1, userId);
            pS.setInt(2, achievement.getId());

            int affectedRows = pS.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
