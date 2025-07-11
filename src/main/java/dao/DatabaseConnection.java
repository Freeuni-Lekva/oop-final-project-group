package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseConnection {
    private static final Properties properties = loadProperties();
    private static final String URL = properties.getProperty("db.url");
    private static final String USERNAME = properties.getProperty("db.username");
    private static final String PASSWORD = properties.getProperty("db.password");

    /*
     * loading database properties like URL, USERNAME, PASSWORD from config.properties file
     */
    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
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

    /**
     * Tries to establish connection with the database.
     * @return the connection
     * @throws RuntimeException if the connection could not be established or
     * the JDBC driver could not be loaded
     */
    public static Connection getConnection() {
        return getConnection(true);
    }

    public static Connection getConnection(boolean includeDbName) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load JDBC driver " + e.getMessage());
        }
        try {
            String url = includeDbName ? URL : URL.substring(0, URL.lastIndexOf('/'));
            return DriverManager.getConnection(url, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Could not establish connection with the database " + e.getMessage());
        }
    }

    /**
     * Tests if the connection is active
     * @return boolean indicating if connection is valid or not
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}