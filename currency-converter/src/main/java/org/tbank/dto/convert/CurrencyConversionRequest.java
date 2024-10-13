package org.tbank.dto.convert;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Currency rate conversion request's information")
public class CurrencyConversionRequest {

    @Schema(description = "From currency code", example = "USD", type = "string")
    @NotBlank(message = "Поле не должно быть пустым")
    private String fromCurrency;

    @Schema(description = "To currency code", example = "EUR", type = "string")
    @NotBlank(message = "Поле не должно быть пустым")
    private String toCurrency;

    @Schema(description = "Amount to convert", example = "27.2", type = "double")
    @Positive(message = "Amount must be greater than zero")
    @NotNull(message = "amount is required")
    private Double amount;
}
