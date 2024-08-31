package org.tbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbank.model.City;
import com.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * The main entry point to the application.
     * This method analyzes JSON files:
     * You need to select one of the file names to check: 1 - correct, 2 - failed file, 3 - failed path.
     * For convenience, tests have been written for any of the scenarios in the MainTest class
     */
    public static void main(String[] args) {

        String fileName = "src/main/resources/city.json"; // 1
//        String fileName = "src/main/resources/city-error.json"; // 2
//        String fileName = "src/main/resources/city-er.json"; // 3

        // The path to the file to which the XML is written
        String filePathForSave = "src/main/resources/city-xml.xml";

        try {
            City city = Parser.parseJSON(fileName);

            logger.info("Successfully parsed JSON file '{}'.", fileName);
            logger.debug("The City object was created: {}", city);

            String cityXML = Parser.toXML(city);

            logger.info("The City object has been successfully serialized to XML format:\n{}", cityXML);

            Parser.writeXmlToFile(filePathForSave, city);

        } catch (JsonParseException e) {
            logger.error("An error occurred while parsing JSON. Check the {} file for invalid characters.\n{}",
                    fileName, e.getMessage());

        } catch (JsonProcessingException e) {
            logger.error("Error serializing City object to XML.\n{}", e.getMessage());

        } catch (NoSuchFileException e) {
            logger.error("The file '{}' does not exist. Check the file path.", fileName);

        } catch (IOException e) {
            logger.error("Error reading the file '{}'. Check if the file exists and the file path is correct.",
                    fileName);
        }
    }
}
