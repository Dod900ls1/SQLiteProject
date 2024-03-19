package src.Tests;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import src.InitialiseDB;

public class JunitTests {
    @Test
    public void testRegex(){
        String regexString = "--.*|/\\*(?:(?:(?!\\*/)[\\s\\S])*\\*/)?";

        String testString = """
            CREATE TABLE actors (
                /* PK actorkID */ actorID INTEGER AUTOINCREMENT, -- Author: Boiar Yehor
                name VARCHAR(255) NOT NULL
                /*
                 * 
                 */
                );
                """;
                
        String expectedString = """
            CREATE TABLE actors (
                actorID INTEGER AUTOINCREMENT, 
               name VARCHAR(255) NOT NULL
               
               );       
                """;
        String test = testString.replaceAll(regexString, "");
        Assert.assertEquals(expectedString, test);
    }
}
