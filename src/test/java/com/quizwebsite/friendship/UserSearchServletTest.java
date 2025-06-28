package com.quizwebsite.friendship;

import User.User;
import User.UserDao;
import com.google.gson.Gson;
import com.quizwebsite.friendship.UserSearchServlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserSearchServletTest {

    @Mock
    private UserDao userDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserSearchServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Manually inject a Gson instance to avoid calling servlet.init()
        Field gsonField = UserSearchServlet.class.getDeclaredField("gson");
        gsonField.setAccessible(true);
        gsonField.set(servlet, new Gson());
    }

    @Test
    void testDoGet() throws Exception {
        // Arrange
        when(request.getParameter("query")).thenReturn("test");

        List<User> users = Arrays.asList(new User(1, "testuser1", "pass"), new User(2, "testuser2", "pass"));
        when(userDao.findUsersByUsername("test")).thenReturn(users);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Act
        servlet.doGet(request, response);

        // Assert
        writer.flush();
        String expectedJson = new Gson().toJson(users);
        assertEquals(expectedJson, stringWriter.toString());
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }
}