package User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginServlet extends HttpServlet {
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userDao = (UserDao) getServletContext().getAttribute("userDao");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/LoginPage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("user");
        String password = request.getParameter("pass");

        try {
            if(userDao.accountHasPass(username, password)) {
                User user = userDao.findUserByUsername(username);
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/home");
            }else{
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("/LoginPage.jsp").forward(request, response);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

