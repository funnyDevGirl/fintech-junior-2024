package org.tbank.dto.currency;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CurrencyRateCreateDTO {

    @Schema(description = "From currency code", example = "EUR", type = "string")
    @NotBlank(message = "The field should not be empty")
    private String currency;

    @Schema(description = "Amount to convert", example = "102.4", type = "double")
    @NotNull
    private Double rate;
}
