import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InitialiseDB {

    private Connection connection;

    public InitialiseDB(String dbFilePath) {
        try {
            // Establish connection to SQLite database
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.err.println("Connection to SQLite failed: " + e.getMessage());
        }
    }

    public void createTablesFromDDL(String ddlFilePath) {
        try (Statement statement = connection.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(ddlFilePath))) {

            String line;
            StringBuilder ddlStatements = new StringBuilder();

            // Read DDL file line by line
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    ddlStatements.append(line);

                    // Check if line ends with semicolon, indicating end of SQL statement
                    if (line.trim().endsWith(";")) {
                        // Execute SQL statement
                        statement.execute(ddlStatements.toString().trim());
                        ddlStatements.setLength(0); // Reset StringBuilder
                    }
                }
            }

            System.out.println("Tables created successfully from DDL.");

        } catch (SQLException | IOException e) {
            System.err.println("Error creating tables from DDL: " + e.getMessage());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null)
                connection.close();
            System.out.println("Connection to SQLite closed.");

        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String dbFilePath = "schemas/schema.db";
        String ddlFilePath = "schemas/schema.ddl";

        InitialiseDB ddlReader = new InitialiseDB(dbFilePath);
        ddlReader.createTablesFromDDL(ddlFilePath);
        ddlReader.closeConnection();
    }
}
