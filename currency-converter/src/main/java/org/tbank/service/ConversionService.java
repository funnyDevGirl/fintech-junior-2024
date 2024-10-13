package org.tbank.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tbank.dto.convert.CurrencyConversionRequest;
import org.tbank.dto.convert.CurrencyConversionResponse;
import org.tbank.dto.currency.CurrencyRateDTO;
import static org.tbank.util.CurrencyUtil.isCurrencyValid;

@Slf4j
@Service
@AllArgsConstructor
public class ConversionService {

    private final CurrencyRateService currencyRateService;

    public CurrencyConversionResponse convert(CurrencyConversionRequest request) {

        if (request.getAmount() <= 0) {

            log.error("Invalid request parameters");
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        String codeFrom = request.getFromCurrency();
        String codeTo = request.getToCurrency();

        if (!isCurrencyValid(codeFrom) || !isCurrencyValid(codeTo)) {

            log.error("No this currency");
            throw new IllegalArgumentException("Unsupported currency code");
        }

        CurrencyRateDTO currencyFrom = currencyRateService.getCurrencyRate(codeFrom); // будет извлекать из кэша, если код такой же
        CurrencyRateDTO currencyTo = currencyRateService.getCurrencyRate(codeTo);

        Double convertedAmount = getAmountToCurrency(
                request.getAmount(),
                currencyFrom.getRate(),
                currencyTo.getRate());

        return new CurrencyConversionResponse(codeFrom, codeTo, convertedAmount);
    }

    private Double getAmountToCurrency(Double amount, Double rateFrom, Double rateTo) {
        return (rateFrom / rateTo) * amount;
    }
}
