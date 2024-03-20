package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class PopulateDB {
    private final static String API_URL = "https://api.api-ninjas.com/v1/celebrity?name=";
    private static String csvFilePath = "data/Films.csv";
    public static void main(String[] args) {
        String databaseUrl = "jdbc:sqlite:schemas/schema.db";

        System.out.println(getName(csvFilePath, 2));
        getAPIInfo(csvFilePath);
        // try (Connection conn = DriverManager.getConnection(databaseUrl)) {
        //     // Read CSV file and insert data into Movies table
        //     insertMovies(conn, csvFilePath);
        //     insertPersons(conn, csvFilePath);
        //     System.out.println("Data inserted successfully!");
        // } catch (SQLException | IOException e) {
        //     e.printStackTrace();
        // }
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
        URL url = new URL(API_URL);

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            reader.readLine();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responcecode = connection.getResponseCode();
            if(responcecode != 200){
                throw new RuntimeException("HttpResponceCode: " + responcecode);
            }

            String inline = "";
            Scanner scanner = new Scanner(url.openStream());
            while(scanner.hasNext()){
                inline += scanner.nextLine();
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length != 8)
                    continue;
        
                String name = data[5];

            }
            scanner.close();
        }
    }

    private static String getName(String csvFilePath, int rowNumber){
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip the header line
            reader.readLine();

            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length != 9)
                    continue;
                String name = data[5];
                if(i == Integer.parseInt(data[8])){
                    return name;
                }
                i++;
            }
        }catch(IOException e){
            System.err.println(e.getMessage()); //TODO Handle
        }
        return null;
    }

    private static void getAPIInfo(String name){
        try {
            int rowNumber = 0;
            URL url = new URL(API_URL + getName(csvFilePath, rowNumber));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if( connection.getResponseCode() != 200 ){
                throw new RuntimeException("HttpResponseCode: " + connection.getResponseCode());
            }

            String inline = "";
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
             }
             System.out.println(inline);
             //Close the scanner
             scanner.close();
        } catch (IOException e) {
            System.err.println(e.getMessage()); //TODO Handle
        }
    }

}
