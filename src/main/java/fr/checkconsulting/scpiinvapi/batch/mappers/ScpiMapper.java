package fr.checkconsulting.scpiinvapi.batch.mappers;

import fr.checkconsulting.scpiinvapi.model.entity.*;
import org.mapstruct.*;

import java.util.ArrayList;
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

    @AfterMapping
    default void mergeDistributionRates(@MappingTarget Scpi target, Scpi source) {
        if (source.getDistributionRates() == null) return;
        if (target.getDistributionRates() == null) target.setDistributionRates(new ArrayList<>());

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
        if (source.getDismembermentDiscounts() == null) return;
        if (target.getDismembermentDiscounts() == null) target.setDismembermentDiscounts(new ArrayList<>());

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
        if (source.getLocations() == null) return;
        if (target.getLocations() == null) target.setLocations(new ArrayList<>());

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
        if (source.getSectors() == null) return;
        if (target.getSectors() == null) target.setSectors(new ArrayList<>());

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
        if (source.getScpiValues() == null) return;
        if (target.getScpiValues() == null) target.setScpiValues(new ArrayList<>());

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

}