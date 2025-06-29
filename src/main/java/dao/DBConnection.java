package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static final Properties properties = loadProperties();
    private static final String url = properties.getProperty("db.url");
    private static final String username = properties.getProperty("db.username");
    private static final String password = properties.getProperty("db.password");

    //loads properties needed for the database connection
    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties file");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading configuration: " + e.getMessage());
        }
        return properties;
    }



    /*
    * @return the connection
    * */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
