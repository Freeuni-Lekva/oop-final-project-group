package com.quizwebsite.friendship;

import com.quizwebsite.friendship.FriendshipService;
import com.quizwebsite.friendship.ManageFriendRequestServlet;
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

class ManageFriendRequestServletTest {

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private ManageFriendRequestServlet servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        servlet = new ManageFriendRequestServlet(friendshipService);
    }

    @Test
    void testDoPost_Accept() throws Exception {
        // Arrange
        when(request.getParameter("action")).thenReturn("accept");
        when(request.getParameter("requestId")).thenReturn("2");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(friendshipService).acceptFriendRequest(2);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(captor.capture());
        assertTrue(captor.getValue().endsWith("/friend-requests"));
    }

    @Test
    void testDoPost_Reject() throws Exception {
        // Arrange
        when(request.getParameter("action")).thenReturn("reject");
        when(request.getParameter("requestId")).thenReturn("2");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(friendshipService).rejectFriendRequest(2);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).sendRedirect(captor.capture());
        assertTrue(captor.getValue().endsWith("/friend-requests"));
    }
}