import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCreate {

    public void createDataBase(DBConnection connection )throws IOException, SQLException {
        Statement statement = connection.getConnection().createStatement();
        String filePath = "src/main/resources/schema.sql";
        BufferedReader br = new BufferedReader(new FileReader(filePath));

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