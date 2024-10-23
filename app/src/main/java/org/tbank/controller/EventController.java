package org.tbank.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tbank.dto.events.EventCreateDTO;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventFilterDTO;
import org.tbank.dto.events.EventUpdateDTO;
import org.tbank.service.EventService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {

    private final EventService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDTO> getAll() {
        return service.getAll();
    }

    @PostMapping(path = "/search")
    @ResponseStatus(HttpStatus.OK)
    public List<EventDTO> search(@RequestBody EventFilterDTO eventFilterDTO) {
        return service.search(eventFilterDTO);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDTO show(@PathVariable long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO create(@Valid @RequestBody EventCreateDTO eventCreateDTO) {
        return service.create(eventCreateDTO);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDTO update(@Valid @RequestBody EventUpdateDTO eventUpdateDTO,
                              @PathVariable Long id) {
        return service.update(eventUpdateDTO, id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
