package org.tbank.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.dto.locations.LocationDTO;
import org.tbank.dto.locations.LocationUpdateDTO;
import org.tbank.model.Event;
import org.tbank.model.Location;
import org.tbank.repository.EventJpaRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(
        uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LocationMapper {

    @Autowired
    private EventJpaRepository eventRepository;

    @Mapping(source = "eventIds", target = "events", qualifiedByName = "eventIdsToEvents")
    public abstract Location map(LocationCreateDTO locationCreateDTO);

    @Mapping(target = "eventIds", source = "events", qualifiedByName = "eventsToEventIds")
    public abstract LocationDTO map(Location location);

    @Mapping(source = "eventIds", target = "events", qualifiedByName = "eventIdsToEvents")
    public abstract void update(LocationUpdateDTO locationUpdateDTO, @MappingTarget Location location);

    @Named("eventIdsToEvents")
    public Set<Event> eventIdsToEvents(Set<Long> eventIds) {
        return eventIds == null ? new HashSet<>()
                : eventRepository.findByIdIn(eventIds);
    }

    @Named("eventsToEventIds")
    public Set<Long> eventsToEventIds(Set<Event> events) {
        return events == null ? new HashSet<>()
                : events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());
    }
}
