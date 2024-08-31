package org.tbank;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tbank.model.City;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class MainTest {

    @TempDir
    Path tempDir;

    @Test
    void parseJsonFromCorrectFile() throws Exception {
        String correctFile = getFixturePath("city.json").toString();
        // "src/test/resources/city.json"
        City city = Parser.parseJSON(correctFile);
        City expected = new City("spb", new City.Coordinates(59.939095, 30.315868));
        assertNotNull(city);
        assertEquals(expected, city);
    }

    @Test
    void parseJsonFromIncorrectFile() {
        String incorrectFile = getFixturePath("city-error.json").toString();
        //"src/test/resources/city-error.json"

        Assertions.assertThrows(JsonParseException.class, () -> {
                Parser.parseJSON(incorrectFile);
        });
    }

    @Test
    void parseJsonfromNonExistentFile() {
        String nonExistentFile = getFixturePath("non-existent.json").toString();

        Assertions.assertThrows(IOException.class, () -> {
            Parser.parseJSON(nonExistentFile);
        });
    }

    @Test
    void testToXML() throws JsonProcessingException {
        City city = new City("spb", new City.Coordinates(59.939095, 30.315868));
        String xml = Parser.toXML(city);
        Assertions.assertNotNull(xml);
        Assertions.assertTrue(xml.contains("<slug>spb</slug>"));
        Assertions.assertTrue(xml.contains("<lon>30.315868</lon>"));
    }

    @Test
    void testWriteXmlToFile() throws Exception {
        City city = new City("spb", new City.Coordinates(59.939095, 30.315868));
        String filePath = tempDir.resolve("city-xml.xml").toString();

        // Write XML to file
        Parser.writeXmlToFile(filePath, city);
        Assertions.assertTrue(Files.exists(Paths.get(filePath)));

        // Verify the content of the file
        String xml = Files.readString(Paths.get(filePath));
        Assertions.assertTrue(xml.contains("<slug>spb</slug>"));
        Assertions.assertTrue(xml.contains("<lat>59.939095</lat>"));
    }


    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", fileName)
                .toAbsolutePath().normalize();
    }
}
