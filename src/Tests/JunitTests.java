package src.Tests;
import org.junit.Assert;
import org.junit.Test;
import src.PopulateDB;

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

    @Test
    public void findStatusTest(){
        String[] data = {"ian wright","male","1963-11-03", "presenter", "football_player", "radio_personality","60","1.75","gb","20000000","True"};
        Assert.assertFalse(PopulateDB.findStatus(data, "actor"));
    }
}
