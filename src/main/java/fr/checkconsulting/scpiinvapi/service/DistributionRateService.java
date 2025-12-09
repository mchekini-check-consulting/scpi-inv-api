package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateChartResponse;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;
import fr.checkconsulting.scpiinvapi.mapper.DistributionRateMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.repository.DistributionRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DistributionRateService {

    private final DistributionRateRepository distributionRateRepository;
    private final DistributionRateMapper distributionRateMapper;

    public DistributionRateService(DistributionRateRepository distributionRateRepository,
                                   DistributionRateMapper distributionRateMapper) {
        this.distributionRateRepository = distributionRateRepository;
        this.distributionRateMapper = distributionRateMapper;
    }

    public DistributionRateChartResponse findAllDistributionRateByScpiId(Long scpiId) {
        log.info("Récupération des taux de distribution pour SCPI id={}", scpiId);

        List<DistributionRate> distributionRates =
                distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpiId);

        if (distributionRates.isEmpty()) {
            log.warn("Aucun taux de distribution trouvé pour SCPI id={}", scpiId);
        } else {
            log.debug("Nombre de taux de distribution trouvés pour SCPI id={} : {}", scpiId, distributionRates.size());
        }

        List<DistributionRateDTOResponse> distributionRateDTOResponses = distributionRateMapper.toDtoList(distributionRates);

        boolean insufficientHistory = distributionRateDTOResponses.size() < 2;
        double distribRateAvg3Years = calculateAverageLast3Years(distributionRateDTOResponses);

        log.debug("SCPI id={} - historique insuffisant={} - moyenne 3 ans={}",
                scpiId, insufficientHistory, distribRateAvg3Years);

        return DistributionRateChartResponse.builder()
                .rates(distributionRateDTOResponses)
                .avg3Years(distribRateAvg3Years)
                .insufficientHistory(insufficientHistory)
                .build();
    }

    private double calculateAverageLast3Years(List<DistributionRateDTOResponse> rates) {
        if (rates.size() < 3) {
            return 0.0;
        }

        List<BigDecimal> last3Rates = rates.subList(rates.size() - 3, rates.size()).stream()
                .map(DistributionRateDTOResponse::getRate)
                .filter(Objects::nonNull)
                .toList();

        return last3Rates.stream()
                .mapToDouble(BigDecimal::doubleValue)
                .average()
                .orElse(0.0);
    }
}
