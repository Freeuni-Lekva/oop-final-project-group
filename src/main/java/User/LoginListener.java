package User;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.quizwebsite.friendship.FriendshipService;

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
            DBCreate dbCreate = new DBCreate();
            dbCreate.createDataBase();
            UserDao userDao = new UserDao();
            sce.getServletContext().setAttribute("userDao", userDao);
            FriendshipService friendshipService = new FriendshipService();
            sce.getServletContext().setAttribute("friendshipService", friendshipService);

            // Seed the database with default users for testing
            try {
                if (!userDao.containsUser("lasha")) {
                    User lasha = new User("lasha", "gorgo1", "lasha@example.com");
                    userDao.registerUser(lasha);
                }
                if (!userDao.containsUser("gorgo")) {
                    User gorgo = new User("gorgo", "gorgo", "gorgo@example.com");
                    userDao.registerUser(gorgo);
                }
            } catch (Exception e) {
                // Log the error, but don't prevent the app from starting
                sce.getServletContext().log("Error seeding default users", e);
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        AbandonedConnectionCleanupThread.checkedShutdown();
    }

}