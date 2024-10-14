package org.tbank.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tbank.dto.events.EventResponse;
import org.tbank.dto.events.Price;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class Parser {

    public EventResponse parseJson(String content) throws Exception  {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, EventResponse.class);
    }

    public Price parsePrice(String input) {
        return new Price(getFirstNumber(input), "RUB");
    }

    private Double getFirstNumber(String input) {
        String regex = "\\b(?:\\w+\\s+)*\\s*([\\d\\s]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String numberWithSpaces = matcher.group(1);
            String cleanedNumber = numberWithSpaces.replaceAll("\\s+", "");
            return Double.parseDouble(cleanedNumber); // Возвращаю первое найденное число
        }
        return 0.0; // Если число не найдено
    }
}
