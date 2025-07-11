package dao;

import models.Achievements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    public int getCreatedCount(int userId) {
        try (Connection connection = DatabaseConnection.getConnection();
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
    public int getAttemptCount(int userId) {
        try (Connection connection = DatabaseConnection.getConnection();
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

    public boolean giveAchievementToUser(int userId, Achievements achievement) {
        try (Connection connection = DatabaseConnection.getConnection();
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

    /**
     * Gets an Achievement object from the database based on its unique name.
     *
     * @param name The name of the achievement to fetch.
     * @return The corresponding Achievements object if found, otherwise null.
     * @throws RuntimeException if a database error occurs.
     */
    public Achievements getAchievementByName(String name){
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Achievements WHERE name = ?")) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Achievements(
                            rs.getInt("achievement_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getString("icon_url")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve achievement by name: " + name, e);
        }
        return null;
    }


    /**
     * Gets a list of Achievements that a specific user has earned.
     *
     * @param userId The ID of the user.
     * @return A list of Achievements the user has earned.
     * @throws RuntimeException if a database error occurs.
     */
    public List<Achievements> getUserAchievementList(int userId){
        List<Achievements> result = new ArrayList<>();
        String query = "SELECT a.achievement_id, a.name, a.description, a.icon_url " +
                "FROM UserAchievements ua " +
                "JOIN Achievements a ON ua.achievement_id = a.achievement_id " +
                "WHERE ua.user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pS = connection.prepareStatement(query)){
            pS.setInt(1, userId);
            try (ResultSet rs = pS.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("achievement_id");
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    String iconUrl = rs.getString("icon_url");

                    Achievements achievement = new Achievements(id, name, description, iconUrl);
                    result.add(achievement);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Checks if a user has already earned a specific achievement.
     *
     * @param userId The user's ID.
     * @param achievementId The ID of the achievement.
     * @return true if the user already has the achievement, false otherwise.
     * @throws RuntimeException if a database error occurs.
     */
    public boolean userHasAchievement(int userId, int achievementId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pS = connection.prepareStatement("SELECT COUNT(*) FROM UserAchievements WHERE user_id = ? and achievement_id = ?")){
            pS.setInt(1, userId);
            pS.setInt(2, achievementId);
            try (ResultSet rs = pS.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 1) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}