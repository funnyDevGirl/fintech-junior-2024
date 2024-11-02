package org.tbank.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FixtureReader {

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", fileName)
                .toAbsolutePath().normalize();
    }

    public static String readFixture(String fileName) throws Exception {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath);
    }
}
