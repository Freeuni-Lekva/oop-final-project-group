package com.quizwebsite.friendship;

import User.User;
import java.util.List;

public class FriendshipService {

    private final FriendshipDao friendshipDao;
    private final FriendRequestDao friendRequestDao;

    public FriendshipService() {
        this.friendshipDao = new FriendshipDao();
        this.friendRequestDao = new FriendRequestDao();
    }

    public FriendshipService(FriendshipDao friendshipDao, FriendRequestDao friendRequestDao) {
        this.friendshipDao = friendshipDao;
        this.friendRequestDao = friendRequestDao;
    }

    public void sendFriendRequest(int requesterId, int recipientId) {
        if (!friendshipDao.areFriends(requesterId, recipientId)) {
            friendRequestDao.sendFriendRequest(requesterId, recipientId);
        }
    }

    public void acceptFriendRequest(int requestId) {
        FriendRequest request = friendRequestDao.getFriendRequestById(requestId);
        if (request != null && "pending".equals(request.getStatus())) {
            friendRequestDao.updateFriendRequestStatus(requestId, "accepted");
            friendshipDao.addFriendship(request.getRequesterId(), request.getRecipientId());
        }
    }

    public void rejectFriendRequest(int requestId) {
        friendRequestDao.updateFriendRequestStatus(requestId, "rejected");
    }

    public void removeFriend(int userId1, int userId2) {
        friendshipDao.removeFriendship(userId1, userId2);
    }

    public List<User> getFriendsForUser(int userId) {
        return friendshipDao.getFriendsForUser(userId);
    }

    public List<FriendRequest> getPendingRequestsForUser(int userId) {
        return friendRequestDao.getPendingRequestsForUser(userId);
    }

    public String getFriendshipStatus(int currentUser, int profileUser) {
        if (friendshipDao.areFriends(currentUser, profileUser)) {
            return "FRIENDS";
        }
        if (friendRequestDao.hasPendingRequest(currentUser, profileUser)) {
            return "PENDING_SENT";
        }
        if (friendRequestDao.hasPendingRequest(profileUser, currentUser)) {
            return "PENDING_RECEIVED";
        }
        return "NONE";
    }
}