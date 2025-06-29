package com.quizwebsite.friendship;

import User.User;
import User.UserDao;
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
    private UserDao userDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private SendFriendRequestServlet servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new SendFriendRequestServlet(friendshipService, userDao);
        User currentUser = new User(1, "testuser", "password");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(currentUser);
    }

    @Test
    void testDoPost() throws Exception {
        // Arrange
        when(request.getParameter("recipientId")).thenReturn("2");
        User recipient = new User(2, "recipient", "password");
        when(userDao.getUserById(2)).thenReturn(recipient);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(friendshipService).sendFriendRequest(1, 2);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(captor.capture());
        assertTrue(captor.getValue().endsWith("/user-profile?username=recipient"));
    }
}