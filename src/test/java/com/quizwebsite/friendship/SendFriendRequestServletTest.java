package com.quizwebsite.friendship;

import com.quizwebsite.friendship.FriendshipService;
import com.quizwebsite.friendship.SendFriendRequestServlet;
import User.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SendFriendRequestServletTest {

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private SendFriendRequestServlet servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoPost() throws Exception {
        // Arrange
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        User currentUser = new User(1, "testuser", "password");
        when(session.getAttribute("user")).thenReturn(currentUser);
        when(request.getSession()).thenReturn(session);
        when(request.getParameter("recipientId")).thenReturn("2");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(friendshipService).sendFriendRequest(1, 2);
        verify(response).sendRedirect(captor.capture());
        assertTrue(captor.getValue().endsWith("/home"));
    }
}