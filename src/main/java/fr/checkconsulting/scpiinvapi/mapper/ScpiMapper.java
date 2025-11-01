package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiSummaryDto;
import fr.checkconsulting.scpiinvapi.model.entity.*;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ScpiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "distributionRates", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "sectors", ignore = true)
    @Mapping(target = "scpiValues", ignore = true)
    @Mapping(target = "dismembermentDiscounts", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateScpi(@MappingTarget Scpi target, Scpi source);

    @Mapping(target = "distributionRate", source = "source", qualifiedByName = "extractRate")
    @Mapping(target = "country", source = "source", qualifiedByName = "extractCountry")
    ScpiSummaryDto toScpiSummaryDto(Scpi source);

    List<ScpiSummaryDto> toScpiSummaryDto(List<Scpi> source);

    @Named("extractRate")
    default BigDecimal extractLastDistributionRate(Scpi scpi) {

        return scpi.getDistributionRates().stream()
                .max(java.util.Comparator.comparing(DistributionRate::getYear))
                .map(DistributionRate::getRate).orElse(null);
    }

    @Named("extractCountry")
    default String extractMainCountry(Scpi scpi) {


        return scpi.getLocations().stream()
                .filter(location -> location.getPercentage() != null)
                .max(java.util.Comparator.comparing(Location::getPercentage))
                .map(Location::getCountry)
                .orElse(null);
    }

}