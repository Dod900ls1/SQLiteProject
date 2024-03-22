package src;

import java.sql.*;

/**
 * A class for executing queries against a SQLite database.
 */
public class QueryDB {
    private static final String URL = "jdbc:sqlite:schemas/schema.db";

    /**
     * The main method establishes a connection to the database and executes a query based on the query number.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            int queryNumber = 5;
            executeQuery(conn, queryNumber);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Executes a specified query by its number.
     *
     * @param conn        The database connection.
     * @param queryNumber The number of the query to execute.
     */
    public static void executeQuery(Connection conn, int queryNumber) {
        switch (queryNumber) {
            case 1:
                query1(conn);
                break;
            case 2:
                query2(conn);
                break;
            case 3:
                query3(conn);
                break;
            case 4:
                query4(conn);
                break;
            case 5:
                query5(conn);
                break;
            default:
                System.out.println("Invalid query number.");
        }
    }

    /**
     * Executes the first query, retrieving movie titles from the Movies table.
     *
     * @param conn The database connection.
     */
    public static void query1(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT title FROM Movies";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    System.out.println("Movie Title: " + title);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes the second query, retrieving names of actors in 'Forever My Girl'.
     *
     * @param conn The database connection.
     */
    public static void query2(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = """
                SELECT p.name 
                FROM Persons p
                JOIN Movies_Persons mp ON p.person_id = mp.person_id
                JOIN Movies m ON mp.movie_id = m.movie_id
                WHERE m.title = "Forever My Girl" AND p.status = "actor";
                    """;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    System.out.println("Name: " + name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes the third query, retrieving distinct movie titles associated with specific individuals.
     *
     * @param conn The database connection.
     */
    public static void query3(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = """
                SELECT DISTINCT m.title
                FROM Movies m
                JOIN Movies_Persons mp ON m.movie_id = mp.movie_id
                JOIN Persons p ON mp.person_id = p.person_id
                WHERE (p.name = 'ian wright' AND p.status = 'actor')
                OR (p.name = 'dave stewart' AND p.status = 'film_director');
                    """;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    System.out.println("Title: " + title);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * SQL query retrieves information about people who have participated in movies 
     * released since the year 2000 and have a rating greater than 8.
     * @param conn The database connection.
     */
    public static void query4(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = """
                SELECT *
                FROM Persons
                JOIN Movies_Persons ON Persons.person_id = Movies_Persons.person_id
                JOIN Movies ON Movies.movie_id = Movies_Persons.movie_id
                LEFT JOIN Awards ON Movies.movie_id = Awards.FilmID
                WHERE Movies.release_year >= 2000 AND Movies.rating > 8;
                ORDER BY Movies.release_year DESC, Movies.rating DESC;
                    """;;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    System.out.println("Name: " + name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * list of people along with the count of movies they've participated
     * @param conn The database connection.
     */
    public static void query5(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            String sql = """
                SELECT Persons.name, COUNT(*) AS MovieCount
                FROM Persons
                JOIN Movies_Persons ON Persons.person_id = Movies_Persons.person_id
                GROUP BY Persons.name;
                    """;;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int count = rs.getInt("MovieCount");
                    System.out.println("Name: " + name + " Count: " + count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
