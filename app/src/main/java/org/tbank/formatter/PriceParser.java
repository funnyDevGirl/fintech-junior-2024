package org.tbank.formatter;

import lombok.extern.slf4j.Slf4j;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigDecimal;

@Slf4j
public class PriceParser {

    public static BigDecimal parsePrice(String input) {
        log.info("Input String for parsing price - {}", input);

        BigDecimal result = BigDecimal.ZERO;

        if (input.isEmpty() || !input.matches(".*\\d.*")) {
            return result;
        }

        String regex = "(\\d[\\d\\s]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String numberWithSpaces = matcher.group(1);
            log.info("numberWithSpaces: {}", numberWithSpaces);

            // Удаляю пробелы в числе
            String cleanedNumber = numberWithSpaces.replaceAll("\\s+", "");
            log.info("cleanedNumber: {}", cleanedNumber);

            // Парсинг строки в BigDecimal
            result = new BigDecimal(cleanedNumber);
        }

        return result.setScale(2, RoundingMode.HALF_UP); // округление до 2 знаков после запятой
    }
}
