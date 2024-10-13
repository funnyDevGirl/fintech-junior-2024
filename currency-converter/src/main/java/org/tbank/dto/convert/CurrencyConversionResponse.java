package org.tbank.dto.convert;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Currency conversion response's information")
public class CurrencyConversionResponse {

    @Schema(description = "From currency code", example = "USD", type = "string")
    private String fromCurrency;

    @Schema(description = "To currency code", example = "EUR", type = "string")
    private String toCurrency;

    @Schema(description = "Converted amount", example = "62.51", type = "double")
    private Double convertedAmount;
}
