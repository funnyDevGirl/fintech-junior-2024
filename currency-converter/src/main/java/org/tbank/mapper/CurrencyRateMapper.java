package org.tbank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.tbank.dto.currency.CurrencyRateCreateDTO;
import org.tbank.dto.currency.CurrencyRateDTO;
import org.tbank.model.CurrencyRate;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CurrencyRateMapper {

    public abstract CurrencyRate map(CurrencyRateCreateDTO locationCreateDTO);
    public abstract CurrencyRateDTO map(CurrencyRate location);
}
