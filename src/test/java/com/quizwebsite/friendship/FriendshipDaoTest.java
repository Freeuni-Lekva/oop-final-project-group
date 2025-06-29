package com.quizwebsite.friendship;

import dao.DatabaseConnection;
import dao.DatabaseSetup;
import User.User;
import User.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FriendshipDaoTest {

    private Connection connection;
    private FriendshipDao friendshipDao;
    private UserDao userDao;
    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    public void setUp() throws SQLException, ClassNotFoundException {
        DatabaseSetup.run();
        connection = DatabaseConnection.getConnection();
        friendshipDao = new FriendshipDao();
        userDao = new UserDao();

        // Create and register test users
        testUser1 = new User("testuser1", "password123", "testuser1@example.com");
        testUser2 = new User("testuser2", "password123", "testuser2@example.com");
        testUser3 = new User("testuser3", "password123", "testuser3@example.com");

        userDao.registerUser(testUser1);
        userDao.registerUser(testUser2);
        userDao.registerUser(testUser3);

        // Retrieve users to get their generated IDs
        testUser1 = userDao.findUserByUsername("testuser1");
        testUser2 = userDao.findUserByUsername("testuser2");
        testUser3 = userDao.findUserByUsername("testuser3");
    }

    @AfterEach
    public void tearDown() throws SQLException, ClassNotFoundException {
        // Clean up friendships
        friendshipDao.removeFriendship(testUser1.getId(), testUser2.getId());
        friendshipDao.removeFriendship(testUser1.getId(), testUser3.getId());
        friendshipDao.removeFriendship(testUser2.getId(), testUser3.getId());

        // Clean up users
        userDao.removeUser(testUser1);
        userDao.removeUser(testUser2);
        userDao.removeUser(testUser3);

        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testAddAndAreFriends() {
        // Test adding a friendship
        friendshipDao.addFriendship(testUser1.getId(), testUser2.getId());
        assertTrue(friendshipDao.areFriends(testUser1.getId(), testUser2.getId()), "Users should be friends after adding friendship.");
        assertTrue(friendshipDao.areFriends(testUser2.getId(), testUser1.getId()), "Friendship should be reciprocal.");
        assertFalse(friendshipDao.areFriends(testUser1.getId(), testUser3.getId()), "Users who are not friends should return false.");
    }

    @Test
    public void testRemoveFriendship() {
        // Add a friendship to remove
        friendshipDao.addFriendship(testUser1.getId(), testUser2.getId());
        assertTrue(friendshipDao.areFriends(testUser1.getId(), testUser2.getId()), "Pre-condition failed: Users should be friends.");

        // Test removing the friendship
        friendshipDao.removeFriendship(testUser1.getId(), testUser2.getId());
        assertFalse(friendshipDao.areFriends(testUser1.getId(), testUser2.getId()), "Users should not be friends after removing friendship.");
        assertFalse(friendshipDao.areFriends(testUser2.getId(), testUser1.getId()), "Friendship removal should be reciprocal.");
    }

    @Test
    public void testGetFriendsForUser() {
        // Add friendships
        friendshipDao.addFriendship(testUser1.getId(), testUser2.getId());
        friendshipDao.addFriendship(testUser1.getId(), testUser3.getId());

        // Test getting friends for a user
        List<User> friends = friendshipDao.getFriendsForUser(testUser1.getId());
        assertNotNull(friends, "The friends list should not be null.");
        assertEquals(2, friends.size(), "User should have two friends.");

        // Verify the friend IDs
        List<Integer> friendIds = friends.stream().map(User::getId).toList();
        assertTrue(friendIds.contains(testUser2.getId()), "Friend list should contain testUser2.");
        assertTrue(friendIds.contains(testUser3.getId()), "Friend list should contain testUser3.");
    }

    @Test
    public void testGetFriendsForUserWithNoFriends() {
        // Test getting friends for a user with no friends
        List<User> friends = friendshipDao.getFriendsForUser(testUser1.getId());
        assertNotNull(friends, "The friends list should not be null.");
        assertTrue(friends.isEmpty(), "User with no friends should have an empty list.");
    }
}