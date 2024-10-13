package org.tbank.dto.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
public class EventRequest {
    @NotNull
    private double budget;

    @NotBlank
    private String currency;

    private String dateFrom;
    private String dateTo;

    public EventRequest(double budget, String currency) {

        this.budget = budget;
        this.currency = currency;
        this.dateFrom = String.valueOf(Instant.now().getEpochSecond());
        this.dateTo = String.valueOf(Instant.now()
                .plus(7, ChronoUnit.DAYS).getEpochSecond());
    }

    public EventRequest(double budget, String currency, String dateFrom, String dateTo) {
        this.budget = budget;
        this.currency = currency;
        this.dateFrom = (dateFrom != null) ? dateFrom : String.valueOf(Instant.now().getEpochSecond());
        this.dateTo = (dateTo != null) ? dateTo
                : String.valueOf(Instant.now().plus(7, ChronoUnit.DAYS).getEpochSecond());
    }
}
