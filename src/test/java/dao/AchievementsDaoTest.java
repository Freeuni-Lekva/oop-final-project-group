package dao;

import models.Achievements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AchievementsDaoTest {
    AchievementsDao achievementsDao;
    private DatabaseConnection dbConnection;
    private List<Integer> userIds;
    private List<Integer> quizIds;
    private List<Integer> attemptIds;

    @BeforeEach
    void setUp() throws SQLException {

        dbConnection = new DatabaseConnection();
        userIds = addUsers(dbConnection);

        quizIds = new ArrayList<>();
        quizIds.addAll(insertQuizzes(dbConnection, 1, userIds.get(1)));
        quizIds.addAll(insertQuizzes(dbConnection, 5, userIds.get(2)));
        quizIds.addAll(insertQuizzes(dbConnection, 10, userIds.get(3)));

        attemptIds = new ArrayList<>();
        attemptIds.addAll(insertUserQuizAttempts(dbConnection, userIds.get(0), quizIds.get(0), 2));
        attemptIds.addAll(insertUserQuizAttempts(dbConnection, userIds.get(0), quizIds.get(1), 1));

        for (int quizId : quizIds) {
            attemptIds.addAll(insertUserQuizAttempts(dbConnection, userIds.get(2), quizId, 1));
        }
        achievementsDao = new AchievementsDao();


    }

    private List<Integer> addUsers(DatabaseConnection dbConnection) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String insertUsers = "INSERT INTO Users (username, email, password_hash, salt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(insertUsers, Statement.RETURN_GENERATED_KEYS)) {

            String[][] userData = {
                    {"user1", "u1@mail.com", "hash1", "salt1"},
                    {"user2", "u2@mail.com", "hash2", "salt2"},
                    {"user3", "u3@mail.com", "hash3", "salt3"},
                    {"user4", "u4@mail.com", "hash4", "salt4"}
            };

            for (String[] user : userData) {
                ps.setString(1, user[0]);
                ps.setString(2, user[1]);
                ps.setString(3, user[2]);
                ps.setString(4, user[3]);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    while (rs.next()) {
                        userIds.add(rs.getInt(1));
                    }
                }
            }



            return userIds;
        }
    }

    private List<Integer> insertQuizzes(DatabaseConnection dbConnection, int amount, Integer userId) throws SQLException {
        String insertQuizzes = "INSERT INTO Quizzes (creator_user_id, title, description, is_random_order, display_type, is_immediate_correction, is_practice_mode_enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
        List<Integer> quizIds = new ArrayList<>();
        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(insertQuizzes, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < amount; i++) {
                ps.setInt(1, userId);
                ps.setString(2, "Quiz " + i);
                ps.setString(3, "Description " + i);
                ps.setBoolean(4, false);
                ps.setString(5, "SINGLE_PAGE");
                ps.setBoolean(6, false);
                ps.setBoolean(7, false);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    while (rs.next()) {
                        quizIds.add(rs.getInt(1));
                    }
                }
            }

        }
        return quizIds;
    }


    private List<Integer> insertUserQuizAttempts(DatabaseConnection dbConnection, Integer userId, Integer quizId, int amount) throws SQLException {
        List<Integer> attemptIds = new ArrayList<>();

        String insertAttempts = "INSERT INTO UserQuizAttempts (user_id, quiz_id, start_time, end_time, score, time_taken_seconds) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(insertAttempts, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < amount; i++) {
                ps.setInt(1, userId);
                ps.setInt(2, quizId);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis() - 60000));
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                ps.setInt(5, 100);
                ps.setInt(6, 60);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    while (rs.next()) {
                        attemptIds.add(rs.getInt(1));
                    }
                }
            }

        }

        return attemptIds;
    }

    @AfterEach
    void cleanUP() throws SQLException {

        // Delete attempts first(child table off quizzes and users)
        if (!attemptIds.isEmpty()) {
            String deleteAttempts = "DELETE FROM UserQuizAttempts WHERE attempt_id = ?";
            try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(deleteAttempts)) {
                for (Integer id : attemptIds) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
        }


        // Delete quizzes (child table of users)
        if (!quizIds.isEmpty()) {
            String deleteQuizzes = "DELETE FROM Quizzes WHERE quiz_id = ?";
            try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(deleteQuizzes)) {
                for (Integer quizId : quizIds) {
                    ps.setInt(1, quizId);
                    ps.executeUpdate();
                }
            }
        }

        // Delete users (parent table)
        if (!userIds.isEmpty()) {
            String deleteUsers = "DELETE FROM Users WHERE user_id = ?";
            try (PreparedStatement ps = dbConnection.getConnection().prepareStatement(deleteUsers)) {
                for (Integer userId : userIds) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
            }
        }
        attemptIds.clear();
        userIds.clear();
        quizIds.clear();
    }

    @Test
    public void getQuizCreatedByUserTest() throws SQLException {
        assertEquals(0 ,achievementsDao.getCreatedCount(userIds.get(0)));
        assertEquals(1 ,achievementsDao.getCreatedCount(userIds.get(1)));
        assertEquals(5 ,achievementsDao.getCreatedCount(userIds.get(2)));
        assertEquals(10 ,achievementsDao.getCreatedCount(userIds.get(3)));
    }


    @Test
    public void getUserAttemptsTest() throws SQLException {
        assertEquals(3 ,achievementsDao.getAttemptCount(userIds.get(0)));
        assertEquals(0 ,achievementsDao.getAttemptCount(userIds.get(1)));
        assertEquals(16 ,achievementsDao.getAttemptCount(userIds.get(2)));
        assertEquals(0 ,achievementsDao.getAttemptCount(userIds.get(3)));
    }

    @Test
    public void insertAndHasAchievementTest() throws SQLException {
        Achievements achievement = new Achievements(1, "Amateur Author", "The user created a quiz.", "/iconUrl" );
        assertTrue(achievementsDao.giveAchievementToUser(userIds.get(0), achievement));

        assertTrue(achievementsDao.userHasAchievement(userIds.get(0),1));
        assertFalse(achievementsDao.userHasAchievement(userIds.get(0),2));
        assertFalse(achievementsDao.userHasAchievement(userIds.get(1),1));

    }

    @Test
    public void getAchievementByName() throws SQLException {
        Achievements achievement = achievementsDao.getAchievementByName("Amateur Author");
        assertTrue(achievement.getName().equals("Amateur Author"));
        assertTrue(achievement.getDescription().equals("The user created a quiz."));
    }

    @Test
    public void getAchievementListTest() throws SQLException {
        Achievements achievement1 =  achievementsDao.getAchievementByName("Amateur Author");
        assertTrue(achievementsDao.giveAchievementToUser(userIds.get(0), achievement1));
        Achievements achievement2 = achievementsDao.getAchievementByName("Quiz Machine");
        assertTrue(achievementsDao.giveAchievementToUser(userIds.get(0), achievement2));

        List<Achievements> achievements = achievementsDao.getUserAchievementList(userIds.get(0));
        assertTrue(achievements.contains(achievement1));
        assertTrue(achievements.contains(achievement2));
    }

}