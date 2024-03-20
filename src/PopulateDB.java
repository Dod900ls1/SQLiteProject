package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PopulateDB {
    public static void main(String[] args) {
        String csvFilePath = "data/Films.csv";
        String databaseUrl = "jdbc:sqlite:schemas/schema.db";

        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            // Read CSV file and insert data into Movies table
            insertMovies(conn, csvFilePath);

            System.out.println("Data inserted successfully!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }


    private static void insertMovies(Connection conn, String csvFilePath) throws IOException, SQLException {
        String sql1 = "INSERT INTO Movies (title, release_year, running_time, rating) VALUES (?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length != 8)
                    continue;
        
                String title = data[0];
                int releaseYear = Integer.parseInt(data[1]);
                int runningTime = Integer.parseInt(data[3]);
                double rating = Double.parseDouble(data[4]);

                try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setString(1, title);
                    pstmt.setInt(2, releaseYear);
                    pstmt.setInt(3, runningTime);
                    pstmt.setDouble(4, rating);
                    pstmt.executeUpdate();
                }
            }
        }
    }


    private static void insertPersons(Connection conn, String csvFilePath) throws IOException, SQLException {
        String sql1 = "INSERT INTO Movies (status, name, birthday, gender) VALUES (?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length != 8)
                    continue;
        
                String name = data[5];
                int releaseYear = Integer.parseInt(data[1]);
                int runningTime = Integer.parseInt(data[3]);
                double rating = Double.parseDouble(data[4]);

            }
        }
    }

}
