package org.tbank.dto.currency;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRateDTO {

    @Schema(description = "ID", example = "1", type = "long")
    private Long id;

    @Schema(description = "From currency code", example = "EUR", type = "string")
    private String currency;

    @Schema(description = "Amount to convert", example = "102.4", type = "double")
    private Double rate;
}
