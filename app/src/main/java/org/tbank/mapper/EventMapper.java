package org.tbank.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.tbank.dto.events.EventCreateDTO;
import org.tbank.dto.events.EventDTO;
import org.tbank.dto.events.EventUpdateDTO;
import org.tbank.exception.ResourceNotFoundException;
import org.tbank.model.Event;
import org.tbank.model.Location;
import org.tbank.repository.LocationJpaRepository;


@Mapper(
        uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EventMapper {

    @Autowired
    private LocationJpaRepository locationRepository;

    @Mapping(source = "placeId", target = "place", qualifiedByName = "idToPlace")
    public abstract Event map(EventCreateDTO eventCreateDTO);

    @Mapping(source = "place.id", target = "placeId")
    public abstract EventDTO map(Event event);

    @Mapping(source = "placeId", target = "place", qualifiedByName = "idToPlace")
    public abstract void update(EventUpdateDTO eventUpdateDTO, @MappingTarget Event event);


    @Named("idToPlace")
    public Location idToPlace(long placeId) {
        return locationRepository.findById(placeId).orElseThrow(
                () -> new ResourceNotFoundException("Place with id " + placeId + " not found"));
    }
}
