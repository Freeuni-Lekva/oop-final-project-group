package com.quizwebsite.friendship;

import User.User;
import User.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class UserSearchServletTest {

    @Mock
    private UserDao userDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher dispatcher;

    @InjectMocks
    private UserSearchServlet servlet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    @Test
    void testDoGet() throws Exception {
        // Arrange
        when(request.getParameter("query")).thenReturn("test");
        List<User> users = Arrays.asList(new User(1, "testuser1", "pass"), new User(2, "testuser2", "pass"));
        when(userDao.findUsersByUsername("test")).thenReturn(users);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(request).setAttribute("users", users);
        verify(dispatcher).forward(request, response);
    }
}