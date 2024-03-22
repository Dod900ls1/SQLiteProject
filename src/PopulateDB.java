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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PopulateDB {
    private static String filmsPath = "data/Films.csv";
    private static String actorsPath = "data/Actors2_cleaned.csv";
    private static String directorsPath = "data/Directors2_cleaned.csv";
    private static String oscarPath = "data/oscars.csv";
    private static String filmsCleanedPath = "data/Films_cleaned.csv";
    private static String databaseUrl = "jdbc:sqlite:schemas/schema.db";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            populateDatabase(conn);
            System.out.println("Data inserted successfully!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void populateDatabase(Connection conn) throws IOException, SQLException {
        insertMovies(conn, filmsPath);
        insertPeople(conn, actorsPath);
        insertPeople(conn, directorsPath);
        insertPeopleMovies(conn, actorsPath);
        insertPeopleMovies(conn, directorsPath);
        insertAwards(conn);
    }

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

    private static void insertMovies(Connection conn, String csvFilePath) throws IOException, SQLException {
        String sql1 = "INSERT INTO Movies (title, release_year, running_time, genre_name, rating) VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = CSVUtils.parseCSVLine(line);
                if (data.size() != 9)
                    continue; // Adjusted the check to match the expected number of fields after proper
                              // parsing

                String title = data.get(0).replace("\"", ""); 
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
            
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = CSVUtils.parseCSVLine(line);

                
                Integer id = !data.get(3).isEmpty() ? Integer.parseInt(data.get(3)) : null;
                String name = !data.get(0).isEmpty() ? data.get(0) : null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
                java.util.Date date = null; 
                try {
                    date = dateFormat.parse(data.get(2));
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                }
                String gender = !data.get(1).isEmpty() ? data.get(1) : null;
                String status = !data.get(5).isEmpty() ? data.get(5) : null;

                try (PreparedStatement pstmt = conn.prepareStatement(queryString)) {

                    pstmt.setInt(1, id);
                    pstmt.setString(2, status);
                    pstmt.setString(3, name);
                    if(date != null){
                        pstmt.setDate(4, new Date(date.getTime()));
                    }else{
                        pstmt.setDate(4, new Date(0));
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
            
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                List<String> data = CSVUtils.parseCSVLine(line);
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

}
