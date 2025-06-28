package com.quizwebsite.friendship;

import User.DBConnection;
import User.User;
import User.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDao {

    private final Connection connection;
    private final UserDao userDao;

    public FriendshipDao() {
        this.connection = new DBConnection().getConnection();
        this.userDao = new UserDao();
    }

    public void addFriendship(int userId1, int userId2) {
        String query = "INSERT INTO Friendships (user_id1, user_id2) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (userId1 < userId2) {
                statement.setInt(1, userId1);
                statement.setInt(2, userId2);
            } else {
                statement.setInt(1, userId2);
                statement.setInt(2, userId1);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFriendship(int userId1, int userId2) {
        String query = "DELETE FROM Friendships WHERE (user_id1 = ? AND user_id2 = ?) OR (user_id1 = ? AND user_id2 = ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId1);
            statement.setInt(2, userId2);
            statement.setInt(3, userId2);
            statement.setInt(4, userId1);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getFriendsForUser(int userId) {
        List<User> friends = new ArrayList<>();
        String query = "SELECT user_id1, user_id2 FROM Friendships WHERE user_id1 = ? OR user_id2 = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int friendId = resultSet.getInt("user_id1");
                if (friendId == userId) {
                    friendId = resultSet.getInt("user_id2");
                }
                friends.add(userDao.getUserById(friendId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    public boolean areFriends(int userId1, int userId2) {
        String query = "SELECT 1 FROM Friendships WHERE (user_id1 = ? AND user_id2 = ?) OR (user_id1 = ? AND user_id2 = ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId1);
            statement.setInt(2, userId2);
            statement.setInt(3, userId2);
            statement.setInt(4, userId1);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}