package User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CreateAccountServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() {
        userDao = new UserDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            if(userDao.containsUser(username)) {
                request.setAttribute("error", "Username is already taken.");
                request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            } else {
                User user = new User(username, email, password);
                userDao.registerUser(user);
                response.sendRedirect(request.getContextPath() + "/login");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
