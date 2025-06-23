package dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {
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