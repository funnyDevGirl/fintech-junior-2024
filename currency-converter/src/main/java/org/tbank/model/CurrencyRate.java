package org.tbank.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "currencyRates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyRate implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Currency code", example = "EUR")
    @NotBlank(message = "Поле не должно быть пустым")
    private String currency;

    @Schema(description = "Currency rate", example = "102.18")
    @NotNull
    private Double rate;
}
