package org.tbank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventResponse;
import org.tbank.dto.events.convert.CurrencyConversionRequest;
import org.tbank.dto.events.convert.CurrencyConversionResponse;
import org.tbank.formatter.JsonParser;
import org.tbank.formatter.PriceParser;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceWithMono {

    @Value("${events-url}")
    private String baseUrl;

    @Value("${converter-url}")
    private String converterUrl;

    private final WebClient webClient;
    private final JsonParser parser;


    public Mono<List<EventDTO>> fetchEvents(String dateFrom, String dateTo, BigDecimal amount, String currency) {
        log.info("The beginning of receiving events from the Kudago service");

        // Получаем URL
        String eventsUrl = buildUrl(dateFrom, dateTo);
        log.info("Full url for request: {}", eventsUrl);

        // Mono для получения мероприятий
        Mono<List<EventDTO>> eventsMono = webClient.get()
                .uri(eventsUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(contentWithEventsFromApi -> {
                    log.info("Response from API: {}", contentWithEventsFromApi);
                    try {
                        EventResponse eventsFromContent = parser.parseJson(contentWithEventsFromApi);
                        log.info("Parsed response from API: {}", eventsFromContent);
                        return Mono.justOrEmpty(eventsFromContent != null ? eventsFromContent.getEvents() : List.of());
                    } catch (Exception e) {
                        log.error("Failed to parse the content from Kudago", e);
                        return Mono.error(e);
                    }
                });

        // Mono для конвертации бюджета
        Mono<BigDecimal> budgetMono = Mono.fromSupplier(() -> {
            log.info("Client's budget: {} {}", amount, currency);
            return defineBudgetAndConvert(amount, currency);
        });

        // Соединяем оба Mono, фильтруем подходящие мероприятия
        return Mono.zip(eventsMono, budgetMono)
                .flatMap(tuple -> {
                    List<EventDTO> events = tuple.getT1();
                    BigDecimal budget = tuple.getT2();
                    log.info("Budget in RUB: {}", budget);
                    List<EventDTO> suitableEvents = filterSuitableEvents(events, budget);
                    log.info("{} suitable events have been found", suitableEvents.size());
                    return Mono.just(suitableEvents);
                });
    }

    private String buildUrl(String dateFrom, String dateTo) {
        // Преобразую dateFrom и dateTo
        if (dateFrom == null) {
            dateFrom = String.valueOf(Instant.now().getEpochSecond()); // Текущая дата
        } else {
            dateFrom = String.valueOf(convertDateToEpoch(dateFrom));
        }
        log.info("Date From in the Double type: {}", dateFrom);

        if (dateTo == null) {
            dateTo = String.valueOf(Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond()); // Дата через 7 дней
        } else {
            dateTo = String.valueOf(convertDateToEpoch(dateTo));
        }
        log.info("Date To in the Double type: {}", dateTo);

        return baseUrl + "&actual_since=" + dateFrom + "&actual_until=" + dateTo;
    }

    private List<EventDTO> filterSuitableEvents(List<EventDTO> events, BigDecimal budget) {
        log.info("Filtering of the list of events begins, taking into account the client's budget");

        return events.stream()
                .filter(event -> {
                    String priceInText = event.getPrice();
                    BigDecimal price = PriceParser.parsePrice(priceInText);

                    log.debug("Price fot event: {}", price);

                    return budget.compareTo(price) >= 0;
                })
                .toList();
    }

    public BigDecimal defineBudgetAndConvert(BigDecimal amount, String currency) {
        log.info("The budget review begins");
        return currency.equals("RUB") ? amount
                : convertCurrencyWithConverter(new CurrencyConversionRequest(currency, "RUB", amount));
    }

    public BigDecimal convertCurrencyWithConverter(CurrencyConversionRequest request) {
        log.info("A request to the Currency-Converter service begins");
        CurrencyConversionResponse convertedCurrency = webClient.post()
                .uri(converterUrl)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CurrencyConversionResponse.class)
                .block();

        log.info("The Currency Conversion service sent the following response: {}", convertedCurrency);

        if (convertedCurrency != null) {
            return convertedCurrency.getConvertedAmount();
        } else {
            log.error("The Currency Conversion service did not return a response. Check your details or try again later.");
            throw new IllegalArgumentException("Incorrect data was transmitted for conversion.");
        }
    }

    private Long convertDateToEpoch(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond(); // Получаю timestamp
    }
}
