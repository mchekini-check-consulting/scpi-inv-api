package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiDetailDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiDismembrementDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiInvestmentDto;
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

    @Mapping(target = "sharePrice", source = "source", qualifiedByName = "extractLatestSharePrice")
    @Mapping(target = "distributionRate", source = "source", qualifiedByName = "extractRate")
    @Mapping(target = "dismembermentActive", source = "dismemberment")
    @Mapping(target = "scpiDismembrement", source = "source", qualifiedByName = "extractDismembermentBareme")
    ScpiInvestmentDto toScpiInvestmentDto(Scpi source);

    @Named("extractLatestSharePrice")
    default BigDecimal extractLatestSharePrice(Scpi scpi) {
        if (scpi.getScpiValues() == null || scpi.getScpiValues().isEmpty()) {
            return null;
        }

        return scpi.getScpiValues().stream()
                .max(java.util.Comparator.comparing(ScpiPartValues::getValuationYear))
                .map(ScpiPartValues::getSharePrice)
                .orElse(null);
    }

    @Named("extractDismembermentBareme")
    default List<ScpiDismembrementDto> extractDismembermentBareme(Scpi scpi) {
        if (scpi.getDismemberment() == null ||
                !scpi.getDismemberment() ||
                scpi.getDismembermentDiscounts() == null ||
                scpi.getDismembermentDiscounts().isEmpty()) {
            return List.of();
        }

        return scpi.getDismembermentDiscounts().stream()
                .sorted(java.util.Comparator.comparing(DismembermentDiscounts::getDurationYears))
                .map(discount -> {
                    BigDecimal nuePropriete = discount.getPercentage();
                    BigDecimal usufruit = BigDecimal.valueOf(100).subtract(nuePropriete);

                    return ScpiDismembrementDto.builder()
                            .durationYears(discount.getDurationYears())
                            .nueProprietePercentage(nuePropriete)
                            .usufruitPercentage(usufruit)
                            .build();
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Named("extractRate")
    default BigDecimal extractLastDistributionRate(Scpi scpi) {

        return scpi.getDistributionRates().stream()
                .max(java.util.Comparator.comparing(DistributionRate::getDistributionYear))
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

    ScpiDetailDto toScpiDetailDto(Scpi scpi);

}