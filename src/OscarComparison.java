package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OscarComparison {
    // Assuming you have a Connection object conn to manage your database connection
    private static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:schemas/schema.db");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        List<String[]> oscarRows = readCSV("data/oscars.csv", "\t");
        List<String[]> filmsRows = readCSV("data/Films.csv", ",");

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
                    int movieId = getMovieIdByTitle(filmTitle);
                    if (movieId != -1) {
                        insertAward(movieId, category, filmTitle, winner);
                    }
                    break;
                }
            }
        }
        System.out.println("Awards table population complete.");
    }

    private static int getMovieIdByTitle(String title) {
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

    private static void insertAward(int movieId, String category, String filmName, String winner) {
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
}
