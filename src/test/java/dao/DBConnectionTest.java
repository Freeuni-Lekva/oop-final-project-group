package dao;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DBConnectionTest {
    @Test
    void test() throws IOException, SQLException {
        DBConnection connection = new DBConnection();
        assertNotNull(connection.getConnection());
    }
}