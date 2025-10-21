package fr.checkconsulting.scpiinvapi.batch.mappers;

import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScpiMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "distributionRates", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateScpi(@MappingTarget Scpi target, Scpi source);

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