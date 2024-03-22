package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A class dedicated to populating a database with data from CSV files.
 */
public class PopulateDB {
    private static final String filmsPath = "data/Films.csv";
    private static final String actorsPath = "data/Actors2_cleaned.csv";
    private static final String directorsPath = "data/Directors2_cleaned.csv";
    private static final String oscarPath = "data/oscars.csv";
    private static final String filmsCleanedPath = "data/Films_cleaned.csv";
    private static final String databaseUrl = "jdbc:sqlite:schemas/schema.db";

    /**
     * The main method that establishes a connection to the database and initiates the population process.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            populateDatabase(conn);
            System.out.println("Data inserted successfully!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Coordinates the sequence of data insertion into the database.
     * @param conn The active database connection.
     * @throws IOException If an input or output operation is failed or interpreted.
     * @throws SQLException If a database access error occurs or the SQL statements are incorrect.
     */
    private static void populateDatabase(Connection conn) throws IOException, SQLException {
        insertMovies(conn, filmsPath);
        insertPeople(conn, actorsPath);
        insertPeople(conn, directorsPath);
        insertPeopleMovies(conn, actorsPath);
        insertPeopleMovies(conn, directorsPath);
        insertAwards(conn);
    }

    /**
     * Inserts award data into the database from a CSV file.
     * @param conn The active database connection.
     * @throws IOException If reading from the CSV file fails.
     */
    private static void insertAwards(Connection conn) throws IOException {
        List<String[]> oscarRows = CSVUtils.readCSV(oscarPath, "\t");
        List<String[]> filmsRows = CSVUtils.readCSV(filmsCleanedPath, ",");

        if (oscarRows == null || filmsRows == null) {
            System.out.println("Error reading CSV files.");
            return;
        }

        for (String[] oscar : oscarRows) {
            String filmTitle = oscar[6];
            String category = oscar[3];
            String winner = oscar[8].isEmpty() ? "No" : oscar[8];

            int movieId = getMovieIdByTitle(conn, filmTitle);
            if (movieId != -1) {
                insertAward(conn, movieId, category, filmTitle, winner);
            }
        }
        System.out.println("Awards table population complete.");
    }

    /**
     * Retrieves the movie ID by its title.
     * @param conn The active database connection.
     * @param title The title of the movie.
     * @return The movie ID or -1 if the movie is not found.
     */
    private static int getMovieIdByTitle(Connection conn, String title) {
        String query = "SELECT movie_id FROM Movies WHERE title = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, title);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("movie_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }

    /**
     * Inserts an award into the database.
     * @param conn The active database connection.
     * @param movieId The ID of the movie that received the award.
     * @param category The category of the award.
     * @param filmName The name of the film.
     * @param winner Indicates if the film was a winner or not.
     */
    private static void insertAward(Connection conn, int movieId, String category, String filmName, String winner) {
        String insertSQL = "INSERT INTO Awards (FilmID, Category, FilmName, Winner) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {
            ps.setInt(1, movieId);
            ps.setString(2, category);
            ps.setString(3, filmName);
            ps.setString(4, winner);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts movie data into the database from a CSV file.
     * @param conn The active database connection.
     * @param csvFilePath The path to the CSV file containing movie data.
     * @throws IOException If reading from the CSV file fails.
     * @throws SQLException If a database access error occurs or the SQL statements are incorrect.
     */
    private static void insertMovies(Connection conn, String csvFilePath) throws IOException, SQLException {
        String sql = "INSERT INTO Movies (title, release_year, running_time, genre_name, rating) VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            reader.readLine(); // Skip header line

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = CSVUtils.parseCSVLine(line);
                if (data.size() != 9)
                    continue;

                String title = data.get(0).replace("\"", ""); 
                int releaseYear = Integer.parseInt(data.get(1));
                String genreName = data.get(2);
                int runningTime = Integer.parseInt(data.get(3));
                double rating = Double.parseDouble(data.get(4));

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, title);
                    pstmt.setInt(2, releaseYear);
                    pstmt.setInt(3, runningTime);
                    pstmt.setString(4, genreName);
                    pstmt.setDouble(5, rating);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Inserts people data into the database from a CSV file.
     * @param conn The active database connection.
     * @param csvFilePath The path to the CSV file containing people data.
     * @throws IOException If reading from the CSV file fails.
     * @throws SQLException If a database access error occurs or the SQL statements are incorrect.
     */
    public static void insertPeople(Connection conn, String csvFilePath) throws IOException, SQLException {
        String queryString = "INSERT INTO Persons (person_id, status, name, birthday, gender) VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            reader.readLine(); // Skip header line

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = CSVUtils.parseCSVLine(line);

                Integer id = !data.get(3).isEmpty() ? Integer.parseInt(data.get(3)) : null;
                String name = data.get(0);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = new Date(dateFormat.parse(data.get(2)).getTime());
                } catch (ParseException e) {
                    System.err.println("Date parsing error forperson: " + name + ", " + e.getMessage());
                }
                String gender = data.get(1);
                String status = data.get(5);            try (PreparedStatement pstmt = conn.prepareStatement(queryString)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, status);
                    pstmt.setString(3, name);
                    if (date != null) {
                        pstmt.setDate(4, date);
                    } else {
                        // If the date is null, set it to a default value or leave it unset
                        pstmt.setNull(4, java.sql.Types.DATE);
                    }
                    pstmt.setString(5, gender);
                    pstmt.executeUpdate();
                }
            }
        }
    }
    
    /**
     * Inserts associations between people and movies into the database from a CSV file.
     * This method is used for linking actors and directors with their respective movies.
     * @param conn The active database connection.
     * @param csvFilePath The path to the CSV file containing associations between people and movies.
     * @throws IOException If reading from the CSV file fails.
     * @throws SQLException If a database access error occurs or the SQL statements are incorrect.
     */
    private static void insertPeopleMovies(Connection conn, String csvFilePath) throws IOException, SQLException {
        String queryString = "INSERT INTO Movies_Persons (person_id, movie_id) VALUES (?, ?)";
    
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            reader.readLine(); // Skip header line
    
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = CSVUtils.parseCSVLine(line);
                if (data.size() < 5) continue; // Ensure there are enough columns
    
                int personId = Integer.parseInt(data.get(3));
                int movieId = Integer.parseInt(data.get(4));
    
                try (PreparedStatement pstmt = conn.prepareStatement(queryString)) {
                    pstmt.setInt(1, personId);
                    pstmt.setInt(2, movieId);
                    pstmt.executeUpdate();
                }
            }
        }
    }
}    
