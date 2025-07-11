package com.quizwebsite.friendship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import com.quizwebsite.friendship.FriendRequest;

public class FriendshipServiceTest {

    @Mock
    private FriendshipDao friendshipDao;

    @Mock
    private FriendRequestDao friendRequestDao;

    private FriendshipService friendshipService;

    @BeforeEach
    public void setUp() {
        friendshipDao = mock(FriendshipDao.class);
        friendRequestDao = mock(FriendRequestDao.class);
        friendshipService = new FriendshipService(friendshipDao, friendRequestDao);
    }

    @Test
    public void testSendFriendRequest_Success() {
        int requesterId = 1;
        int recipientId = 2;
        when(friendshipDao.areFriends(requesterId, recipientId)).thenReturn(false);

        friendshipService.sendFriendRequest(requesterId, recipientId);

        verify(friendRequestDao, times(1)).sendFriendRequest(requesterId, recipientId);
    }

    @Test
    public void testSendFriendRequest_AlreadyFriends() {
        int requesterId = 1;
        int recipientId = 2;
        when(friendshipDao.areFriends(requesterId, recipientId)).thenReturn(true);

        friendshipService.sendFriendRequest(requesterId, recipientId);

        verify(friendRequestDao, never()).sendFriendRequest(requesterId, recipientId);
    }

    @Test
    public void testAcceptFriendRequest() {
        int requestId = 1;
        int requesterId = 1;
        int recipientId = 2;
        FriendRequest request = new FriendRequest(requestId, requesterId, recipientId, "pending");
        when(friendRequestDao.getFriendRequestById(requestId)).thenReturn(request);

        friendshipService.acceptFriendRequest(requestId);

        verify(friendRequestDao, times(1)).updateFriendRequestStatus(requestId, "accepted");
        verify(friendshipDao, times(1)).addFriendship(requesterId, recipientId);
    }

    @Test
    public void testRejectFriendRequest() {
        int requestId = 1;

        friendshipService.rejectFriendRequest(requestId);

        verify(friendRequestDao, times(1)).updateFriendRequestStatus(requestId, "rejected");
    }

    @Test
    public void testRemoveFriend() {
        int userId1 = 1;
        int userId2 = 2;

        friendshipService.removeFriend(userId1, userId2);

        verify(friendshipDao, times(1)).removeFriendship(userId1, userId2);
    }
}