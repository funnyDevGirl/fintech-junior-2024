package org.tbank.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventResponse;
import org.tbank.model.Location;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.tbank.util.FixtureReader.readFixture;

@ExtendWith(MockitoExtension.class)
public class JsonParserTest {

    @InjectMocks
    private JsonParser parser;

    @Mock
    private ObjectMapper objectMapper;


    @Test
    void testParseJson_Success() throws Exception {
        String jsonContent = readFixture("event_response.json");

        EventDTO result = new EventDTO("проект VR Gallery", "vyistavka-vr-gallery",
                new Location("msk"),"от 950 до 1100 рублей");

        EventResponse expectedResponse = new EventResponse();
        expectedResponse.setCount(1);
        expectedResponse.setNext("https://kudago.com/");
        expectedResponse.setPrevious(null);
        expectedResponse.setEvents(List.of(result));

        lenient().when(objectMapper.readValue(jsonContent, EventResponse.class)).thenReturn(expectedResponse);

        // Проверка фактического результата
        EventResponse actualResponse = parser.parseJson(jsonContent);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void testParseJson_Exception() {
        String invalidJson = "invalid json string"; // Некорректный JSON

        assertThrows(Exception.class, () -> {
            parser.parseJson(invalidJson);
        });
    }
}
