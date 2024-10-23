package org.tbank.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tbank.dto.events.EventCreateDTO;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventFilterDTO;
import org.tbank.dto.events.EventUpdateDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.mapper.EventMapper;
import org.tbank.model.Event;
import org.tbank.repository.EventJpaRepository;
import org.tbank.repository.LocationJpaRepository;
import org.tbank.specification.EventSpecification;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final EventMapper eventMapper;
    private final EventJpaRepository eventRepository;
    private final EventSpecification eventSpecification;
    private final LocationJpaRepository locationRepository;


    public EventDTO create(EventCreateDTO eventCreateDTO) {
        if (!locationRepository.existsById(eventCreateDTO.getPlaceId())) {
            throw new NoSuchElementException("Place with id: " + eventCreateDTO.getPlaceId() + " not found");
        }

        Event event = eventMapper.map(eventCreateDTO);
        eventRepository.save(event);
        return eventMapper.map(event);
    }

    public List<EventDTO> getAll() {
        List<Event> events = eventRepository.findAll();
        log.info("{} events were extracted from the database", events.size());

        return events.stream()
                .map(eventMapper::map)
                .toList();
    }

    public List<EventDTO> search(EventFilterDTO eventFilterDTO) {
        log.info("Searching for events with filter: {}", eventFilterDTO);

        var filter = eventSpecification.build(eventFilterDTO);
        List<Event> events = eventRepository.findAll(filter);

        log.info("Events found: {}", events.size());

        return events.stream()
                .map(eventMapper::map)
                .toList();
    }

    public EventDTO findById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id: " + id + " not found"));

        log.info("Event with id: {} found in DB", id);

        return eventMapper.map(event);
    }

    public EventDTO update(EventUpdateDTO eventUpdateDTO, Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id: " + id + " not found"));

        Long placeId = eventUpdateDTO.getPlaceId().get();
        if (eventUpdateDTO.getPlaceId() != null && !locationRepository.existsById(eventUpdateDTO.getPlaceId().get())) {
            throw new NoSuchElementException("Place with id: " + placeId + " not found");
        }

        eventMapper.update(eventUpdateDTO, event);

        eventRepository.save(event);
        return eventMapper.map(event);
    }

    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event with id: " + id + " not found");
        }
        eventRepository.deleteById(id);
    }
}
