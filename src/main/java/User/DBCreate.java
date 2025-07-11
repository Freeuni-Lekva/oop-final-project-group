package User;
import java.io.BufferedReader;
import dao.DatabaseConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCreate {

    public void createDataBase() throws IOException, SQLException {
        Statement statement = DatabaseConnection.getConnection().createStatement();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("schema.sql");
        if (inputStream == null) {
            throw new IOException("Cannot find schema.sql in classpath");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        // String Builder to build the query line by line.
        StringBuilder query = new StringBuilder();
        String line;

        while((line = br.readLine()) != null) {

            if(line.trim().startsWith("-- ")) {
                continue;
            }

            // Append the line into the query string and add a space after that
            query.append(line).append(" ");

            if(line.trim().endsWith(";")) {
                // Execute the Query
                statement.execute(query.toString().trim());
                // Empty the Query string to add new query from the file
                query = new StringBuilder();
            }
        }
    }
}