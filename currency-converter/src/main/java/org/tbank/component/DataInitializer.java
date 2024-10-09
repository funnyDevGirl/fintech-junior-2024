package org.tbank.component;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.tbank.service.CurrencyRateService;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CurrencyRateService currencyRateService;

    @Override
    public void run(ApplicationArguments args) {
        currencyRateService.fetchAndSaveToDBCurrencyRates();
    }
}
