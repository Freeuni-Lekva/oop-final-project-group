package com.quizwebsite.friendship;

import User.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDao {

    private final Connection connection;

    public FriendRequestDao() {
        this.connection = new DBConnection().getConnection();
    }

    public void sendFriendRequest(int requesterId, int recipientId) {
        String query = "INSERT INTO FriendRequests (requester_id, recipient_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, requesterId);
            statement.setInt(2, recipientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FriendRequest> getPendingRequestsForUser(int userId) {
        List<FriendRequest> requests = new ArrayList<>();
        String query = "SELECT fr.*, u.username FROM FriendRequests fr JOIN Users u ON fr.requester_id = u.user_id WHERE fr.recipient_id = ? AND fr.status = 'pending'";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                FriendRequest request = new FriendRequest(
                        resultSet.getInt("request_id"),
                        resultSet.getInt("requester_id"),
                        resultSet.getInt("recipient_id"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("requested_at")
                );
                request.setRequesterUsername(resultSet.getString("username"));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    public void updateFriendRequestStatus(int requestId, String status) {
        String query = "UPDATE FriendRequests SET status = ? WHERE request_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, requestId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FriendRequest getFriendRequestById(int requestId) {
        String query = "SELECT * FROM FriendRequests WHERE request_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, requestId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new FriendRequest(
                        resultSet.getInt("request_id"),
                        resultSet.getInt("requester_id"),
                        resultSet.getInt("recipient_id"),
                        resultSet.getString("status"),
                        resultSet.getTimestamp("requested_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}