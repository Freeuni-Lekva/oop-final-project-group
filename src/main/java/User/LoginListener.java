package User;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.sql.SQLException;

@WebListener
public class LoginListener implements ServletContextListener{


    public void contextInitialized(ServletContextEvent sce) {
        try {
            UserDao uD = new UserDao();
            ServletContext sc = sce.getServletContext();
            sc.setAttribute("UserDao", uD);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize UserDao in context listener", e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        AbandonedConnectionCleanupThread.checkedShutdown();
    }

}