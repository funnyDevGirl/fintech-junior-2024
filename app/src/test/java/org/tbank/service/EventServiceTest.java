package org.tbank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventResponse;
import org.tbank.formatter.JsonParser;
import org.tbank.model.Location;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.tbank.util.FixtureReader.readFixture;


class EventServiceTest {

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient webClient;

    @Mock
    private JsonParser parser;

    @InjectMocks
    private EventService eventService;

    private static final String CONVERTER_URL = "http://example.com/convert";

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Настраиваю мок для WebClient
        doReturn(requestHeadersUriSpec).when(webClient).get();
        doReturn(requestBodyUriSpec).when(webClient).post();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestBodyUriSpec).uri(anyString());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Настройка для bodyToMono
        String mockResponse = readFixture("event_response.json");
        Location location = new Location("msk");
        EventDTO mockEventDTO = new EventDTO("проект VR Gallery", "vyistavka-vr-gallery", location, "от 950 до 1100 рублей");
        EventResponse mockEventResponse = new EventResponse(1, null, null, List.of(mockEventDTO));

        // Настраиваю парсер
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));
        when(parser.parseJson(mockResponse)).thenReturn(mockEventResponse);
    }

    @Test
    public void testFetchEvents_Success() {
        // Arrange
        String dateFrom = "01/01/2023";
        String dateTo = "01/01/2023";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String currency = "RUB";

        // Act
        Mono<List<EventDTO>> result = eventService.fetchEvents(dateFrom, dateTo, amount, currency);

        // Assert
        List<EventDTO> events = result.block();
        assert events != null;
        assertEquals(1, events.size());
        assertEquals("проект VR Gallery", events.getFirst().getTitle());
    }

    @Test
    void testFetchEvents_NoEventsFound() throws Exception {
        // Arrange
        String dateFrom = "01/01/2023";
        String dateTo = "01/01/2023";
        BigDecimal amount = BigDecimal.valueOf(500);
        String currency = "RUB";

        // Act
        Mono<List<EventDTO>> result = eventService.fetchEvents(dateFrom, dateTo, amount, currency);

        // Assert
        List<EventDTO> events = result.block();
        assert events != null;
        assertTrue(events.isEmpty(), "Expected no events to be found");
    }

    @Test
    public void testFetchEvents_ParsingError() throws Exception {
        // Arrange
        String dateFrom = "01/01/2023";
        String dateTo = "01/01/2023";
        BigDecimal amount = BigDecimal.valueOf(1000);
        String currency = "USD";
        String mockResponse = "invalid json";

        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(mockResponse));
        when(parser.parseJson(mockResponse)).thenThrow(new RuntimeException("Parsing error"));

        // Act
        Mono<List<EventDTO>> result = eventService.fetchEvents(dateFrom, dateTo, amount, currency);

        // Assert
        assertThrows(RuntimeException.class, result::block);
    }
}
