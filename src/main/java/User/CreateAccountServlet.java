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
    public void init() throws ServletException {
        super.init();
        this.userDao = (UserDao) getServletContext().getAttribute("userDao");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("user");
        String password = request.getParameter("pass");

        try {
            if(userDao.containsUser(username)) {
                request.setAttribute("error", "Username is already taken.");
                request.getRequestDispatcher("/CreateAccount.jsp").forward(request, response);
            }else{
                User user = new User(username, password);
                userDao.registerUser(user);
                response.sendRedirect(request.getContextPath() + "/login");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
