package dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    @BeforeEach
    public void setUp() {
        DatabaseSetup.run();
    }

    @Test

    //Tests if the testConnectionMethod in DatabaseConnectionMethod
    public void testConnection() {
        Connection connection = DatabaseConnection.getConnection();
        assertTrue(DatabaseConnection.testConnection());
        try{
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}