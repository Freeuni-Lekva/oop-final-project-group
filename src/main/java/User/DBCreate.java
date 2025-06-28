package User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;

//This classes creates the database
public class DBCreate {

    public void createDataBase(DBConnection connection) throws IOException, SQLException {
        Statement statement = connection.getConnection().createStatement();
        try (InputStream inputStream = DBCreate.class.getClassLoader().getResourceAsStream("schema.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new IOException("File 'schema.sql' not found in classpath");
            }

            // String Builder to build the query line by line.
            StringBuilder query = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().startsWith("-- ")) {
                    continue;
                }

                // Append the line into the query string and add a space after that
                query.append(line).append(" ");

                if (line.trim().endsWith(";")) {
                    // Execute the Query
                    statement.execute(query.toString().trim());
                    // Empty the Query string to add new query from the file
                    query = new StringBuilder();
                }
            }
        }
    }
}
