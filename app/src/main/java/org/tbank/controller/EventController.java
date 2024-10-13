package org.tbank.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventRequest;
import org.tbank.service.EventService;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public CompletableFuture<List<EventDTO>> getEvents(@Valid @PathVariable EventRequest request) {
        return eventService.fetchEvents(request.getDateFrom(), request.getDateTo(),
                request.getBudget(), request.getCurrency());
    }
}
