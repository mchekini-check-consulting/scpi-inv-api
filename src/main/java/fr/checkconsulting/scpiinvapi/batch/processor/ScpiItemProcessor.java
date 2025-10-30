package fr.checkconsulting.scpiinvapi.batch.processor;

import fr.checkconsulting.scpiinvapi.batch.reporterrors.BatchErrorCollector;
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

    @Override
    public Scpi process(@NonNull ScpiDto scpiDto) {

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

        if ("Oui".equals(scpiDto.getDismemberment())) {
            scpi.setDismemberment(true);
        } else if ("Non".equals(scpiDto.getDismemberment())) {
            scpi.setDismemberment(false);
        }


        if ("Oui".equals(scpiDto.getScheduledPayment())) {
            scpi.setScheduledPayment(true);
        } else if ("Non".equals(scpiDto.getScheduledPayment())) {
            scpi.setScheduledPayment(false);
        }


        setLocations(scpiDto, scpi);
        setSectors(scpiDto, scpi);
        setDistributionRates(scpiDto, scpi);
        setDismembermentDiscounts(scpiDto, scpi);
        setScpiPartValues(scpiDto, scpi);

        if (!isValidPercentages(scpi)) {
            return null;
        }
        return scpi;
    }


    private void setSectors(ScpiDto dto, Scpi scpi) {
        List<Sector> sectors = new ArrayList<>();

        if (dto.getSectors() != null && !dto.getSectors().isBlank()) {
            String[] parts = dto.getSectors().split(",");

            for (int i = 0; i < parts.length; i += 2) {
                String name = parts[i].trim();
                BigDecimal percentage = new BigDecimal(parts[i + 1].trim());

                Sector sector = Sector.builder()
                        .name(name)
                        .percentage(percentage)
                        .scpi(scpi)
                        .build();

                sectors.add(sector);
            }
        }

        scpi.setSectors(sectors);
    }

    private void setLocations(ScpiDto scpiDto, Scpi scpi) {
        List<Location> locations = new ArrayList<>();

        if (scpiDto.getLocations() != null && !scpiDto.getLocations().isBlank()) {
            String[] parts = scpiDto.getLocations().split(",");

            for (int i = 0; i < parts.length; i += 2) {
                String country = parts[i].trim();
                BigDecimal percentage = new BigDecimal(parts[i + 1].trim());

                Location location = Location.builder()
                        .country(country)
                        .percentage(percentage)
                        .scpi(scpi)
                        .build();

                locations.add(location);
            }
        }

        scpi.setLocations(locations);
    }

    private void setDistributionRates(ScpiDto dto, Scpi scpi) {
        List<DistributionRate> rates = new ArrayList<>();

        if (dto.getDistributedRate() != null && !dto.getDistributedRate().isBlank()) {
            String[] parts = dto.getDistributedRate().split(",");

            int year = LocalDateTime.now().getYear() - 1;

            for (int i = 0; i < parts.length; i++) {
                BigDecimal rate = new BigDecimal(parts[i].trim());

                DistributionRate distributionRate = DistributionRate.builder()
                        .year(year)
                        .rate(rate)
                        .scpi(scpi)
                        .build();

                year--;

                rates.add(distributionRate);
            }
        }

        scpi.setDistributionRates(rates);
    }

    private void setDismembermentDiscounts(ScpiDto dto, Scpi scpi) {
        List<DismembermentDiscounts> discounts = new ArrayList<>();

        if (dto.getDismembermentDiscounts() != null && !dto.getDismembermentDiscounts().isBlank()) {
            String[] parts = dto.getDismembermentDiscounts().split(",");

            for (int i = 0; i < parts.length; i += 2) {
                int durationYears = Integer.parseInt(parts[i].trim());
                BigDecimal percentage = new BigDecimal(parts[i + 1].trim());

                DismembermentDiscounts discount = DismembermentDiscounts.builder()
                        .durationYears(durationYears)
                        .percentage(percentage)
                        .scpi(scpi)
                        .build();

                discounts.add(discount);
            }
        }

        scpi.setDismembermentDiscounts(discounts);
    }

    private void setScpiPartValues(ScpiDto dto, Scpi scpi) {
        List<ScpiPartValues> values = new ArrayList<>();

        if (dto.getSharePrice() != null && !dto.getSharePrice().isBlank()) {
            String[] sharePriceParts = dto.getSharePrice().split(",");
            String[] reconstitutionValueParts = dto.getReconstitutionValue().split(",");


            int year = LocalDateTime.now().getYear() - 1;

            for (int i = 0; i < sharePriceParts.length; i++) {


                BigDecimal sharePrice = new BigDecimal(sharePriceParts[i].trim());
                BigDecimal reconstitutionValue = new BigDecimal(reconstitutionValueParts[i].trim());

                ScpiPartValues value = ScpiPartValues.builder()
                        .year(year)
                        .sharePrice(sharePrice)
                        .reconstitutionValue(reconstitutionValue)
                        .scpi(scpi)
                        .build();

                year--;

                values.add(value);
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

        boolean validLocations = sumLocations.compareTo(LOWER_BOUND) >= 0
                && sumLocations.compareTo(UPPER_BOUND) <= 0;

        boolean validSectors = sumSectors.compareTo(LOWER_BOUND) >= 0
                && sumSectors.compareTo(UPPER_BOUND) <= 0;

        boolean valid = validLocations && validSectors;

        if (!valid) {
            log.info("SCPI '{}' ignorée : Localisations = {}%, Secteurs = {}%",
                    scpi.getName(), sumLocations, sumSectors);
            errorCollector.addError(0, "POURCENTAGE_INVALID",
                    String.format("SCPI '%s' : Localisations=%s%% (------tolérance [99.95-100.05]---------), Secteurs=%s%% (------tolérance [99.95-100.05]---------)",
                            scpi.getName(), sumLocations, sumSectors));
        }

        return valid;
    }
}

