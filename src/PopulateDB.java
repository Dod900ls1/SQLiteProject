package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PopulateDB {
    private static String filmsPath = "data/Films.csv";
    private static String actorsPath = "data/Actors2_cleaned.csv";
    private static String directorsPath = "data/Directors2_cleaned.csv";

    public static void main(String[] args) {
        String databaseUrl = "jdbc:sqlite:schemas/schema.db";

        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            // Read CSV file and insert data into Movies table
            insertMovies(conn, filmsPath);
            insertPeople(conn, actorsPath);
            insertPeople(conn, directorsPath);
            insertPeopleMovies(conn, actorsPath);
            insertPeopleMovies(conn, directorsPath);
            List<String[]> oscarRows = readCSV("data/oscars.csv", "\t");
            List<String[]> filmsRows = readCSV("data/Films_cleaned.csv", ",");
    
            if (oscarRows == null || filmsRows == null) {
                System.out.println("Error reading CSV files.");
                return;
            }
    
            for (String[] oscar : oscarRows) {
                String filmTitle = oscar[6]; // Film title is at index 6
                String category = oscar[3]; // Category is at index 3
                String winner = oscar[8].isEmpty() ? "No" : oscar[8]; // Winner flag at index 8
    
                for (String[] film : filmsRows) {
                    if (film[0].equals(filmTitle)) { // Film title is at index 0 in Films.csv
                        int movieId = getMovieIdByTitle(conn, filmTitle);
                        if (movieId != -1) {
                            insertAward(conn, movieId, category, filmTitle, winner);
                        }
                        break;
                    }
                }
            }
            System.out.println("Awards table population complete.");
            System.out.println("Data inserted successfully!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
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
        return -1; // Return -1 if movie is not found
    }

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

    public static List<String[]> readCSV(String filename, String delimiter) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(delimiter);
                rows.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return rows;
    }
    private static void insertMovies(Connection conn, String csvFilePath) throws IOException, SQLException {
        String sql1 = "INSERT INTO Movies (title, release_year, running_time, genre_name, rating) VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = parseCSVLine(line);
                if (data.size() != 9)
                    continue; // Adjusted the check to match the expected number of fields after proper
                              // parsing

                String title = data.get(0).replace("\"", ""); // Remove surrounding quotes if present
                int releaseYear = Integer.parseInt(data.get(1));
                String genre_name = data.get(2);
                int runningTime = Integer.parseInt(data.get(3));
                double rating = Double.parseDouble(data.get(4));

                try (PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setString(1, title);
                    pstmt.setInt(2, releaseYear);
                    pstmt.setInt(3, runningTime);
                    pstmt.setString(4, genre_name);
                    pstmt.setDouble(5, rating);
                    pstmt.executeUpdate();
                }
            }
        }
    }

    public static void insertPeople(Connection conn, String csvFilePath)
    throws IOException, SQLException {
String queryString = "INSERT INTO Persons (person_id, status, name, birthday, gender) VALUES (?, ?, ?, ?, ?)";

try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
    // Skip the header line
    reader.readLine();

    String line;
    while ((line = reader.readLine()) != null) {
        List<String> data = parseCSVLine(line); // Use the parseCSVLine for consistent parsing.

        // Adjust the indices as per your CSV structure.
        Integer id = !data.get(3).isEmpty() ? Integer.parseInt(data.get(3)) : null;
        String name = !data.get(0).isEmpty() ? data.get(0) : null;
        Date birthday = null;
        try {
            birthday = !data.get(2).isEmpty() ? Date.valueOf(data.get(2)) : null;
        } catch (IllegalArgumentException e) {
            System.out.println("Warning: Invalid date format for " + data.get(2) + ", setting birthday to null.");
        }
        String gender = !data.get(1).isEmpty() ? data.get(1) : null;
        String status = !data.get(5).isEmpty() ? data.get(5) : null;

        try (PreparedStatement pstmt = conn.prepareStatement(queryString)) {
            
            pstmt.setInt(1, id);
            pstmt.setString(2, status);
            pstmt.setString(3, name);
            if (birthday != null) {
                pstmt.setDate(4, birthday);
            } else {
                pstmt.setNull(4, java.sql.Types.DATE);
            }
            pstmt.setString(5, gender);
            pstmt.executeUpdate();
        }
    }
}
}

    private static void insertPeopleMovies(Connection conn, String csvFile)
            throws IOException, SQLException {
        String queryString = "INSERT INTO Movies_Persons (person_id, movie_id) VALUES (?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = parseCSVLine(line);
                System.out.println(data);
                String actorID = data.get(3);
                String movieID = data.get(4);

                try (PreparedStatement pstmt = conn.prepareStatement(queryString)) {
                    pstmt.setInt(1, Integer.parseInt(actorID));
                    pstmt.setInt(2, Integer.parseInt(movieID));
                    pstmt.executeUpdate();
                }
            }
        }
    }

    private static List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder buffer = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // Toggle the inQuotes flag on encountering a quote
            } else if (c == ',' && !inQuotes) {
                fields.add(buffer.toString()); // End of a field
                buffer.setLength(0); // Reset the buffer
            } else {
                buffer.append(c); // Build the field
            }
        }
        // Don't forget to add the last field
        fields.add(buffer.toString());
        return fields;
    }

}
