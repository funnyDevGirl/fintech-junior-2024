package org.tbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbank.model.City;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);


    public static City parseJSON(String filePath) throws IOException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(readContent(filePath), City.class);
    }


    public static String toXML(City city) throws JsonProcessingException {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.writeValueAsString(city);
    }


    public static void writeXmlToFile(String filePath, City city) {
        try {
            File xmlOutput = new File(filePath);
            FileWriter fileWriter = new FileWriter(xmlOutput);
            fileWriter.write(toXML(city));
            fileWriter.close();

            logger.info("XML has been successfully saved to file: {}", filePath);

        } catch (IOException e) {
            logger.error("Failed to save the contents to file: '{}'.", filePath, e);
        }
    }


    // read file
    private static String readContent(String filePath) throws IOException {
        Path fullPath = Paths.get(filePath).toAbsolutePath().normalize();
        return Files.readString(fullPath);
    }
}
