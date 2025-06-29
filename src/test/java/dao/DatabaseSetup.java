package dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseSetup {

    public static void run() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            InputStream in = DatabaseSetup.class.getClassLoader().getResourceAsStream("schema.sql");
            if (in == null) {
                throw new RuntimeException("Cannot find schema.sql");
            }
            String schemaSql = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

            // Split the script into individual statements.
            String[] statements = schemaSql.split(";");

            for (String statement : statements) {
                // Skip empty statements.
                if (statement.trim().isEmpty()) {
                    continue;
                }
                stmt.execute(statement);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up database", e);
        }
    }
}