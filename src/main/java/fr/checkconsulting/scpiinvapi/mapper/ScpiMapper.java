package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiSummaryDto;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.Location;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ScpiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "distributionRates", ignore = true)
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

    @AfterMapping
    default void mergeDistributionRates(@MappingTarget Scpi target, Scpi source) {
        if (source.getDistributionRates() == null) {
            return;
        }

        List<DistributionRate> targetRates = target.getDistributionRates();

        if (targetRates == null || targetRates.isEmpty()) {
            target.setDistributionRates(source.getDistributionRates());
            source.getDistributionRates().forEach(rate -> rate.setScpi(target));
            return;
        }

        for (DistributionRate sourceRate : source.getDistributionRates()) {
            DistributionRate existingRate = targetRates.stream()
                    .filter(rate -> rate.getYear().equals(sourceRate.getYear()))
                    .findFirst()
                    .orElse(null);

            if (existingRate != null) {
                existingRate.setRate(sourceRate.getRate());
            } else {
                sourceRate.setScpi(target);
                targetRates.add(sourceRate);
            }
        }
        targetRates.removeIf(existing ->
                source.getDistributionRates().stream()
                        .noneMatch(sr -> sr.getYear().equals(existing.getYear()))
        );
    }
}