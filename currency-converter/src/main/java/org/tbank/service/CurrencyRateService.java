package org.tbank.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tbank.dto.currency.CurrencyRateCreateDTO;
import org.tbank.dto.currency.CurrencyRateDTO;
import org.tbank.exception.CurrencyNotFoundException;
import org.tbank.exception.CurrencyServiceUnavailableException;
import org.tbank.mapper.CurrencyRateMapper;
import org.tbank.model.CurrencyRate;
import org.tbank.parser.XmlParser;
import org.tbank.repository.CurrencyRateRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyRateService {

    @Value("${cb-api-url}")
    private String currencyApiUrl;

    private final CurrencyRateMapper mapper;
    private final CurrencyRateRepository repository;
    private final XmlParser parser;


    @Cacheable(value = "currencyRatesCache", key = "#code") // включаю кэширование для запроса из репозитория
    public CurrencyRateDTO getCurrencyRate(String code) {

        CurrencyRate currencyRate = repository.findByCurrency(code).orElseThrow(
                () -> new CurrencyNotFoundException("CurrencyRate with code: '" + code + "' not found"));

        return mapper.map(currencyRate);
    }

    @CircuitBreaker(name = "CurrencyRateService",  fallbackMethod = "fallBackMethod")
    @Scheduled(cron = "0 0 12 * * ?", zone = "Europe/Moscow") // Каждый день в 12ч по Москве будут сохраняться данные о валютах в БД
    public void fetchAndSaveToDBCurrencyRates() {

        List<CurrencyRateCreateDTO> fetchingRates = parser.parseCurrency(currencyApiUrl + getFormattedDate());
        List<CurrencyRateDTO> currencyRates = new ArrayList<>();

        for (CurrencyRateCreateDTO rate : fetchingRates) {

            CurrencyRate currencyRate = mapper.map(rate);
            repository.save(currencyRate);

            currencyRates.add(mapper.map(currencyRate));
        }
        log.info("{} currency exchange rates have been saved to the database", currencyRates.size());
    }

    private String getFormattedDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void fallBackMethod(Throwable throwable) {
        log.error("Error receiving currency rates from the Central Bank service. {}", throwable.getMessage());

        throw new CurrencyServiceUnavailableException("The currency rate service is unavailable. Try again later.");
    }
}
