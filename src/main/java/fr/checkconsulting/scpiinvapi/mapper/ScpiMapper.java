package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiDetailDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiDismembrementDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiInvestmentDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiSummaryDto;
import fr.checkconsulting.scpiinvapi.model.entity.*;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                .max(java.util.Comparator.comparing(ScpiPartValues::getYear))
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
        if (source.getDistributionRates() == null)
            return;
        if (target.getDistributionRates() == null)
            target.setDistributionRates(new ArrayList<>());

        for (DistributionRate src : source.getDistributionRates()) {
            DistributionRate existing = target.getDistributionRates().stream()
                    .filter(dr -> Objects.equals(dr.getYear(), src.getYear()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setRate(src.getRate());
            } else {
                src.setScpi(target);
                target.getDistributionRates().add(src);
            }
        }
    }

    @AfterMapping
    default void mergeDismembermentDiscounts(@MappingTarget Scpi target, Scpi source) {
        if (source.getDismembermentDiscounts() == null)
            return;
        if (target.getDismembermentDiscounts() == null)
            target.setDismembermentDiscounts(new ArrayList<>());

        for (DismembermentDiscounts src : source.getDismembermentDiscounts()) {
            DismembermentDiscounts existing = target.getDismembermentDiscounts().stream()
                    .filter(d -> Objects.equals(d.getDurationYears(), src.getDurationYears()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setPercentage(src.getPercentage());
            } else {
                src.setScpi(target);
                target.getDismembermentDiscounts().add(src);
            }
        }
    }

    @AfterMapping
    default void mergeLocations(@MappingTarget Scpi target, Scpi source) {
        if (source.getLocations() == null)
            return;
        if (target.getLocations() == null)
            target.setLocations(new ArrayList<>());

        for (Location src : source.getLocations()) {
            Location existing = target.getLocations().stream()
                    .filter(l -> l.getCountry().equalsIgnoreCase(src.getCountry()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setPercentage(src.getPercentage());
            } else {
                src.setScpi(target);
                target.getLocations().add(src);
            }
        }
    }

    @AfterMapping
    default void mergeSectors(@MappingTarget Scpi target, Scpi source) {
        if (source.getSectors() == null)
            return;
        if (target.getSectors() == null)
            target.setSectors(new ArrayList<>());

        for (Sector src : source.getSectors()) {
            Sector existing = target.getSectors().stream()
                    .filter(s -> s.getName().equalsIgnoreCase(src.getName()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setPercentage(src.getPercentage());
            } else {
                src.setScpi(target);
                target.getSectors().add(src);
            }
        }
    }

    @AfterMapping
    default void mergeScpiPartValues(@MappingTarget Scpi target, Scpi source) {
        if (source.getScpiValues() == null)
            return;
        if (target.getScpiValues() == null)
            target.setScpiValues(new ArrayList<>());

        for (ScpiPartValues src : source.getScpiValues()) {
            ScpiPartValues existing = target.getScpiValues().stream()
                    .filter(v -> Objects.equals(v.getYear(), src.getYear()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.setSharePrice(src.getSharePrice());
                existing.setReconstitutionValue(src.getReconstitutionValue());
            } else {
                src.setScpi(target);
                target.getScpiValues().add(src);
            }
        }
    }


    ScpiDetailDto toScpiDetailDto(Scpi scpi);

}