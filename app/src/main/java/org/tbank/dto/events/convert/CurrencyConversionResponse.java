package org.tbank.dto.events.convert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CurrencyConversionResponse {
    private String fromCurrency;
    private String toCurrency;
    private Double convertedAmount;
}
