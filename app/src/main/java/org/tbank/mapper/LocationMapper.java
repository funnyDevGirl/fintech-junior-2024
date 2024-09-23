package org.tbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.tbank.dto.locations.LocationCreateDTO;
import org.tbank.dto.locations.LocationDTO;
import org.tbank.dto.locations.LocationUpdateDTO;
import org.tbank.model.Location;


@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LocationMapper {

    public abstract Location map(LocationCreateDTO locationCreateDTO);

    public abstract LocationDTO map(Location location);

    public abstract void update(LocationUpdateDTO locationUpdateDTO, @MappingTarget Location location);
}
