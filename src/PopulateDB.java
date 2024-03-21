package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PopulateDB {
    private static String filmsPath = "data/Films.csv";
    private static String actorsPath = "data/Actors2.csv";
    private static String directorsPath = "data/Directors2.csv";

    public static void main(String[] args) {
        String databaseUrl = "jdbc:sqlite:schemas/schema.db";

        try (Connection conn = DriverManager.getConnection(databaseUrl)) {
            // Read CSV file and insert data into Movies table
            // insertMovies(conn, filmsPath);
            // insertPeople(conn, actorsPath, "actor");
            // insertPeople(conn, directorsPath, "film_director");
            insertPeopleMovies(conn, actorsPath, filmsPath, "actor");
            System.out.println("Data inserted successfully!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void insertMovies(Connection conn, String csvFilePath) throws IOException, SQLException {
        String sql1 = "INSERT INTO Movies (title, release_year, running_time, genre_name, rating) VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 9)
                    continue; // Because we use just "," delimeter - we could devide our data into more
                              // columns than we need (for example, if we meet a comma in movie name - we
                              // would get 10 columns)
                String title = data[0];
                int releaseYear = Integer.parseInt(data[1]);
                String genre_name = data[2];
                int runningTime = Integer.parseInt(data[3]);
                double rating = Double.parseDouble(data[4]);

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

    private static void insertPeople(Connection conn, String csvFilePath, String status)
            throws IOException, SQLException {
        String queryString = "INSERT INTO Persons (person_id, status, name, birthday, gender) VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (!findStatus(data, status)) {
                    continue; // If person is not of a correct status - continue
                }
                
                try {
                    Integer id = Integer.parseInt(data[3]);
                
                String gender = extractGender(data);
                String birthday = data[2];
                if (birthday.length() != 10) {
                    continue; // TODO Check if birthday is right format
                }
                String name = data[0];

                try (PreparedStatement pstmt = conn.prepareStatement(queryString)) {
                    pstmt.setInt(1, id);
                    pstmt.setString(2, status);
                    pstmt.setString(3, name);
                    pstmt.setDate(4, Date.valueOf(birthday));
                    pstmt.setString(5, gender);
                    pstmt.executeUpdate();
                }
            }catch(NumberFormatException e){
                continue;
            }
            }
        }
    }

    private static void insertPeopleMovies(Connection conn, String actorsFile, String moviesFile, String status)
            throws IOException, SQLException {
        String queryString = "INSERT INTO Movies_Persons (person_id, movie_id) VALUES (?, ?)";

        try (BufferedReader reader = new BufferedReader(new FileReader(actorsFile))) {
            // Skip the header line
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length != 12){
                    continue;
                }
                if (!findStatus(data, status)) {
                    continue; // If person is not of a correct status - continue
                }
                if(!nameInMovies(moviesFile, data[0], data[10])){
                    continue; // Check if name is in movies table
                }
                String actorID = data[3];
                String movieID = data[10];
                
                
                try (PreparedStatement pstmt = conn.prepareStatement(queryString)) {
                    pstmt.setInt(1, Integer.parseInt(actorID));
                    pstmt.setInt(2, Integer.parseInt(movieID));
                    pstmt.executeUpdate();
                }
            }
        }
    }

    private static boolean nameInMovies(String csvFilePath, String name, String movie_id){
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))){
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if(data[8].equals(movie_id)){
                    return true;
                }
                    // if(data[7].contains("|")){
                    //     String[] x = data[7].split("|");
                    //     for (String actorName : x) {
                    //         if(actorName.equals(name) && data[8].equals(movie_id)){
                    //             return true;
                    //         }
                    //     }
                    // }else{
                    //     if(name.equals(data[7]) && data[8].equals(movie_id)){
                    //         return true;
                    //     }
                    // }
                
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    private static String extractGender(String[] csvRecord) {
        // Check if genderOrOccupations represents a gender
        for (String string : csvRecord) {
            if (string.equals("male") || string.equals("female")) {
                return string;
            }
        }
        return "other";
    }

    public static boolean findStatus(String[] data, String expectedStatus) {
        for (String i : data) {
            if (i.toLowerCase().contains(expectedStatus.toLowerCase())) {
                return true;
            }
        }
        return false; // Status not found
    }

}
