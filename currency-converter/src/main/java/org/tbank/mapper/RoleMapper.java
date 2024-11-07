package org.tbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.tbank.dto.roles.RoleCreateDTO;
import org.tbank.model.Role;
import org.tbank.dto.roles.RoleDTO;

@Mapper(
        uses = {ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class RoleMapper {

    public abstract Role toRole(RoleCreateDTO labelCreateDTO);

    public abstract RoleDTO toDto(Role label);
}
