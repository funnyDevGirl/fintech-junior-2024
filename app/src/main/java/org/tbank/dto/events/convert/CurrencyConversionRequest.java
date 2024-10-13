package org.tbank.dto.events.convert;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversionRequest {
    @NotBlank
    private String fromCurrency;

    @NotBlank
    private String toCurrency;

    @Positive
    @NotNull
    private Double amount;
}
