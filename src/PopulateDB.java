package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PopulateDB {
    public static void main(String[] args) {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/oscars2.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        }catch(IOException e){
            System.err.println(e.getMessage());
        }

        int i = 0;
        for (List<String> list : records) {
            for (String str: list) {
                if (str.equals("ACTOR")) {
                    System.out.println(str);
                }
            }
            i++;
            if(i >= 10){
                break;
            }
        }
    }
}