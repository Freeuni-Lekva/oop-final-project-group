package User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserDao uD = (UserDao) getServletContext().getAttribute("UserDao");

        String username = req.getParameter("user");
        String password = req.getParameter("pass");

        try {
            if(uD != null && uD.accountHasPass(username, password)) {
                req.setAttribute("username", username);
                req.getRequestDispatcher("/Welcome.jsp").forward(req, resp);
            }else{
                req.getRequestDispatcher("/TryAgain.html").forward(req, resp);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

