package org.tbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.tbank.dto.categories.CategoryCreateDTO;
import org.tbank.dto.categories.CategoryDTO;
import org.tbank.dto.categories.CategoryUpdateDTO;
import org.tbank.model.Category;


@Mapper(
        uses = JsonNullableMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CategoryMapper {

    public abstract Category map(CategoryCreateDTO categoryCreateDTO);

    public abstract CategoryDTO map(Category category);

    public abstract void update(CategoryUpdateDTO categoryUpdateDTO, @MappingTarget Category category);
}
