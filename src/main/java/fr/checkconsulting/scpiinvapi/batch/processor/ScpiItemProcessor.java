package fr.checkconsulting.scpiinvapi.batch.processor;

import fr.checkconsulting.scpiinvapi.batch.report.BatchErrorCollector;
import fr.checkconsulting.scpiinvapi.dto.request.ScpiDto;
import fr.checkconsulting.scpiinvapi.model.entity.*;
import io.micrometer.common.lang.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScpiItemProcessor implements ItemProcessor<ScpiDto, Scpi> {

    private static final BigDecimal LOWER_BOUND = new BigDecimal("99.95");
    private static final BigDecimal UPPER_BOUND = new BigDecimal("100.05");

    private final BatchErrorCollector errorCollector;
    private int lineNum = 2;

    @Override
    public Scpi process(@NonNull ScpiDto scpiDto) {

        Scpi scpi = mapDtoToEntity(scpiDto);
        boolean valid = isValidPercentages(scpi);

        lineNum++;

        return valid ? scpi : null;
    }

    private Scpi mapDtoToEntity(ScpiDto scpiDto) {
        Scpi scpi = Scpi.builder()
                .name(scpiDto.getName())
                .minimumSubscription(scpiDto.getMinimumSubscription())
                .manager(scpiDto.getManager())
                .capitalization(scpiDto.getCapitalization())
                .subscriptionFees(scpiDto.getSubscriptionFees())
                .rentFrequency(scpiDto.getRentFrequency())
                .managementFees(scpiDto.getManagementFees())
                .enjoymentDelay(scpiDto.getEnjoymentDelay())
                .iban(scpiDto.getIban())
                .bic(scpiDto.getBic())
                .cashback(scpiDto.getCashback())
                .advertising(scpiDto.getAdvertising())
                .build();

        scpi.setDismemberment("Oui".equals(scpiDto.getDismemberment()));
        scpi.setScheduledPayment("Oui".equals(scpiDto.getScheduledPayment()));

        setLocations(scpiDto, scpi);
        setSectors(scpiDto, scpi);
        setDistributionRates(scpiDto, scpi);
        setDismembermentDiscounts(scpiDto, scpi);
        setScpiPartValues(scpiDto, scpi);

        return scpi;
    }

    private void setSectors(ScpiDto dto, Scpi scpi) {
        List<Sector> sectors = new ArrayList<>();
        if (dto.getSectors() != null && !dto.getSectors().isBlank()) {
            String[] parts = dto.getSectors().split(",");
            for (int i = 0; i < parts.length; i += 2) {
                sectors.add(Sector.builder()
                        .name(parts[i].trim())
                        .percentage(new BigDecimal(parts[i + 1].trim()))
                        .scpi(scpi)
                        .build());
            }
        }
        scpi.setSectors(sectors);
    }

    private void setLocations(ScpiDto dto, Scpi scpi) {
        List<Location> locations = new ArrayList<>();
        if (dto.getLocations() != null && !dto.getLocations().isBlank()) {
            String[] parts = dto.getLocations().split(",");
            for (int i = 0; i < parts.length; i += 2) {
                locations.add(Location.builder()
                        .country(parts[i].trim())
                        .percentage(new BigDecimal(parts[i + 1].trim()))
                        .scpi(scpi)
                        .build());
            }
        }
        scpi.setLocations(locations);
    }

    private void setDistributionRates(ScpiDto dto, Scpi scpi) {
        List<DistributionRate> rates = new ArrayList<>();
        if (dto.getDistributedRate() != null && !dto.getDistributedRate().isBlank()) {
            String[] parts = dto.getDistributedRate().split(",");
            int year = LocalDateTime.now().getYear() - 1;
            for (String part : parts) {
                rates.add(DistributionRate.builder()
                        .distributionYear(year--)
                        .rate(new BigDecimal(part.trim()))
                        .scpi(scpi)
                        .build());
            }
        }
        scpi.setDistributionRates(rates);
    }

    private void setDismembermentDiscounts(ScpiDto dto, Scpi scpi) {
        List<DismembermentDiscounts> discounts = new ArrayList<>();
        if (dto.getDismembermentDiscounts() != null && !dto.getDismembermentDiscounts().isBlank()) {
            String[] parts = dto.getDismembermentDiscounts().split(",");
            for (int i = 0; i < parts.length; i += 2) {
                discounts.add(DismembermentDiscounts.builder()
                        .durationYears(Integer.parseInt(parts[i].trim()))
                        .percentage(new BigDecimal(parts[i + 1].trim()))
                        .scpi(scpi)
                        .build());
            }
        }
        scpi.setDismembermentDiscounts(discounts);
    }

    private void setScpiPartValues(ScpiDto dto, Scpi scpi) {
        List<ScpiPartValues> values = new ArrayList<>();
        if (dto.getSharePrice() != null && !dto.getSharePrice().isBlank()) {
            String[] sharePrices = dto.getSharePrice().split(",");
            String[] reconstitutionValues = dto.getReconstitutionValue().split(",");
            int year = LocalDateTime.now().getYear() - 1;
            for (int i = 0; i < sharePrices.length; i++) {
                values.add(ScpiPartValues.builder()
                        .valuationYear(year--)
                        .sharePrice(new BigDecimal(sharePrices[i].trim()))
                        .reconstitutionValue(new BigDecimal(reconstitutionValues[i].trim()))
                        .scpi(scpi)
                        .build());
            }
        }
        scpi.setScpiValues(values);
    }

    private boolean isValidPercentages(Scpi scpi) {
        BigDecimal sumLocations = scpi.getLocations().stream()
                .map(Location::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sumSectors = scpi.getSectors().stream()
                .map(Sector::getPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean validLocations = sumLocations.compareTo(LOWER_BOUND) >= 0 && sumLocations.compareTo(UPPER_BOUND) <= 0;
        boolean validSectors = sumSectors.compareTo(LOWER_BOUND) >= 0 && sumSectors.compareTo(UPPER_BOUND) <= 0;
        boolean valid = validLocations && validSectors;

        if (!valid) {
            errorCollector.addError(
                    lineNum,
                    "POURCENTAGE_INVALID",
                    String.format(
                            "SCPI '%s' : Localisations=%s%% (tolérance [99.95-100.05]), Secteurs=%s%% (tolérance [99.95-100.05])",
                            scpi.getName(), sumLocations, sumSectors
                    )
            );
        }

        return valid;
    }
}
