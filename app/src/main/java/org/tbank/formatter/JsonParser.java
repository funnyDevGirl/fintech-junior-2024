package org.tbank.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tbank.dto.events.EventResponse;

@Component
@Slf4j
public class JsonParser {

    public EventResponse parseJson(String content) throws Exception  {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, EventResponse.class);
    }
}
