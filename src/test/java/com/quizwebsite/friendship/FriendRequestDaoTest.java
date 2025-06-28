package com.quizwebsite.friendship;

import User.DBConnection;
import User.User;
import User.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FriendRequestDaoTest {

    private Connection connection;
    private FriendRequestDao friendRequestDao;
    private UserDao userDao;
    private User testUser1;
    private User testUser2;

    @BeforeEach
    public void setUp() throws SQLException, ClassNotFoundException {
        connection = new DBConnection().getConnection();
        friendRequestDao = new FriendRequestDao();
        userDao = new UserDao();

        // Create and register test users
        testUser1 = new User(0, "testuser1", "password123");
        testUser2 = new User(0, "testuser2", "password123");

        userDao.registerUser(testUser1);
        userDao.registerUser(testUser2);

        // Retrieve users to get their generated IDs
        testUser1 = userDao.findUserByUsername("testuser1");
        testUser2 = userDao.findUserByUsername("testuser2");
    }

    @AfterEach
    public void tearDown() throws SQLException, ClassNotFoundException {
        // Clean up friend requests
        List<FriendRequest> requests = friendRequestDao.getPendingRequestsForUser(testUser2.getId());
        for (FriendRequest request : requests) {
            friendRequestDao.updateFriendRequestStatus(request.getRequestId(), "rejected"); // Or delete them if a method exists
        }

        // Clean up users
        userDao.removeUser(testUser1);
        userDao.removeUser(testUser2);

        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testSendFriendRequestAndGetPending() {
        // Send a friend request
        friendRequestDao.sendFriendRequest(testUser1.getId(), testUser2.getId());

        // Get pending requests for the recipient
        List<FriendRequest> pendingRequests = friendRequestDao.getPendingRequestsForUser(testUser2.getId());
        assertNotNull(pendingRequests, "Pending requests list should not be null.");
        assertEquals(1, pendingRequests.size(), "There should be one pending request.");

        FriendRequest request = pendingRequests.get(0);
        assertEquals(testUser1.getId(), request.getRequesterId(), "Requester ID should match.");
        assertEquals(testUser2.getId(), request.getRecipientId(), "Recipient ID should match.");
        assertEquals("pending", request.getStatus(), "Request status should be 'pending'.");
    }

    @Test
    public void testUpdateFriendRequestStatus() {
        // Send a friend request to update
        friendRequestDao.sendFriendRequest(testUser1.getId(), testUser2.getId());
        List<FriendRequest> pendingRequests = friendRequestDao.getPendingRequestsForUser(testUser2.getId());
        int requestId = pendingRequests.get(0).getRequestId();

        // Update the request status to "accepted"
        friendRequestDao.updateFriendRequestStatus(requestId, "accepted");

        // Verify the status update
        FriendRequest updatedRequest = friendRequestDao.getFriendRequestById(requestId);
        assertNotNull(updatedRequest, "The updated request should not be null.");
        assertEquals("accepted", updatedRequest.getStatus(), "Request status should be 'accepted'.");
    }

    @Test
    public void testGetFriendRequestById() {
        // Send a friend request to retrieve
        friendRequestDao.sendFriendRequest(testUser1.getId(), testUser2.getId());
        List<FriendRequest> pendingRequests = friendRequestDao.getPendingRequestsForUser(testUser2.getId());
        int requestId = pendingRequests.get(0).getRequestId();

        // Retrieve the request by its ID
        FriendRequest foundRequest = friendRequestDao.getFriendRequestById(requestId);
        assertNotNull(foundRequest, "Should find the friend request by ID.");
        assertEquals(requestId, foundRequest.getRequestId(), "Request ID should match.");
    }
}