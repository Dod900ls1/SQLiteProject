package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading and parsing CSV files.
 */
public class CSVUtils {

    /**
     * Reads a CSV file and returns a list of string arrays, each representing a row.
     *
     * @param filename  The path to the CSV file to be read.
     * @param delimiter The delimiter used to separate entries in the CSV file.
     * @return A List of String arrays, with each array representing a row from the CSV file. Returns {@code null} in case of an IOException.
     */
    public static List<String[]> readCSV(String filename, String delimiter) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split with limit -1 to include trailing empty strings
                String[] data = line.split(delimiter, -1);
                rows.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return rows;
    }

    /**
     * Parses a single line of a CSV file considering commas inside quotations.
     *
     * @param line The CSV line to be parsed.
     * @return A List of Strings, each representing a field in the CSV. Fields enclosed in double quotes are treated as a single field.
     */
    public static List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder buffer = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes; // Toggle the inQuotes flag on encountering a double quote
            } else if (c == ',' && !inQuotes) {
                // Add the content before the comma to fields list and reset the buffer
                fields.add(buffer.toString());
                buffer.setLength(0);
            } else {
                // Append the current character to the buffer
                buffer.append(c);
            }
        }
        // Add the last field to the fields list
        fields.add(buffer.toString());
        return fields;
    }
}
