package org.tbank.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tbank.dto.events.EventDTO;
import org.tbank.service.EventServiceWithMono;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventControllerWithMono {

    private final EventServiceWithMono eventService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Mono<List<EventDTO>> getEvents(
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo,
            @RequestParam(value = "budget") BigDecimal budget,
            @RequestParam(value = "currency") String currency) {

        return eventService.fetchEvents(dateFrom, dateTo, budget, currency);
    }
}
