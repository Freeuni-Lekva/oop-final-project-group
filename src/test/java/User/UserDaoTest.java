package User;

import dao.DatabaseSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {
    UserDao userDao;



    @BeforeEach
    public void setUp() {
        DatabaseSetup.run();
        this.userDao = new UserDao();
        cleanUp();
        try {
            userDao.registerUser(new User("Patrick", "1234", "patrick@example.com"));
            userDao.registerUser(new User("Molly", "1234", "molly@example.com"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void cleanUp() {
        try {
            userDao.removeUser(new User("Patrick", "1234", "patrick@example.com"));
            userDao.removeUser(new User("Molly", "1234", "molly@example.com"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testContainsUser() {

        try {
            assertTrue(userDao.containsUser("Patrick"));
            assertTrue(userDao.containsUser("Molly"));
            assertFalse(userDao.containsUser("Nick"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test(){
        try {
            assertTrue(userDao.accountHasPass("Patrick","1234"));
            assertTrue(userDao.accountHasPass("Molly", "1234"));
            assertFalse(userDao.accountHasPass("Molly", "123"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}