package src;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class UsefulOutput {
    public static void main(String[] args) {
        File inputFile = new File(args[0]);

        // Use Apache Commons CSV to create a parser
        try{
            CSVParser parser = CSVParser.parse(inputFile, Charset.forName("UTF-8"), CSVFormat.RFC4180);
            Stream<CSVRecord> stream = parser.stream();
            stream.limit(10).forEach(n -> System.out.println(n + "\t"));
        }catch(IOException e){
            System.err.println(e.getMessage());
        }

        // Create a stream from CSVParser
    }
}