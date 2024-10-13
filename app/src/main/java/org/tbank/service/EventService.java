package org.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventResponse;
import org.tbank.dto.events.Price;
import org.tbank.dto.events.convert.CurrencyConversionRequest;
import org.tbank.dto.events.convert.CurrencyConversionResponse;
import org.tbank.enums.Currency;
import org.tbank.formatter.Parser;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    @Value("${events-url}")
    private String baseUrl;

    @Value("${converter-url}")
    private String converterUrl;

    private final RestTemplate restTemplate;
    private final Parser parser;

    public CompletableFuture<List<EventDTO>> fetchEvents(String dateFrom, String dateTo, double amount, String currency) {
        log.info("The beginning of receiving events from the Kudago service");

        // CompletableFuture для получения мероприятий
        CompletableFuture<List<EventDTO>> eventsFuture = CompletableFuture.supplyAsync(() -> {
            String eventsUrl = buildUrl(dateFrom, dateTo);
            log.info("Full url for request: {}", eventsUrl);

            String contentWithEventsFromApi = restTemplate.getForObject(eventsUrl, String.class);
            log.info("Response from API: {}", contentWithEventsFromApi);

            try {
                EventResponse eventsFromContent = parser.parseJson(contentWithEventsFromApi);
                log.info("Parsed response from API: {}", eventsFromContent);
                return eventsFromContent != null ? eventsFromContent.getEvents() : List.of();

            } catch (Exception e) {
                log.error("Failed to parse the content from Kudago", e);
                return List.of();
            }
        });

        // CompletableFuture для конвертации бюджета
        CompletableFuture<Double> budgetFuture = CompletableFuture.supplyAsync(() -> {
            log.info("Client's budget: {} {}", amount, currency);
            return defineBudgetAndConvert(amount, currency);
        });

        // Объединяю два CompletableFuture
        return eventsFuture.thenCombine(budgetFuture, (events, budget) -> {
            log.info("Budget in RUB: {}", budget);
            return filterSuitableEvents(events, budget);
        });
    }


    private String buildUrl(String dateFrom, String dateTo) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("&actual_since=").append(dateFrom).append("&actual_until=").append(dateTo);
        return urlBuilder.toString();
    }

    private List<EventDTO> filterSuitableEvents(List<EventDTO> events, double budget) {
        log.info("The filtering of the list of events begins, taking into account the client's budget");

        return events.stream()
                .map(event -> {
                    // Делаю парсинг цены и устанавливаю значение в поле price
                    Price price = parser.parsePrice(event.getPriceTextValue());
                    event.setPrice(price);
                    return event;
                })
                .filter(event -> event.getPrice().getAmount() == 0.0 || event.getPrice().getAmount() <= budget)
                .toList();
    }

    private double defineBudgetAndConvert(double amount, String currency) {
        log.info("The budget review begins");
        return currency.equals(Currency.RUB.getCode()) ? amount
                : convertCurrencyWithConverter(new CurrencyConversionRequest(currency, Currency.RUB.getCode(), amount));
    }

    private double convertCurrencyWithConverter(CurrencyConversionRequest request) {
        log.info("A request to the Currency-Converter service begins");
        CurrencyConversionResponse convertedCurrency = restTemplate.postForObject(converterUrl, request, CurrencyConversionResponse.class);
        return convertedCurrency.getConvertedAmount();
    }
}
