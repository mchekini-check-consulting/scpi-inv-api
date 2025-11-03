package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateChartResponse;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;
import fr.checkconsulting.scpiinvapi.mapper.DistributionRateMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.repository.DistributionRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class DistributionRateService {

    private final DistributionRateRepository distributionRateRepository;
    private final DistributionRateMapper distributionRateMapper;
    
    public DistributionRateService(DistributionRateRepository distributionRateRepository, DistributionRateMapper distributionRateMapper) {
        this.distributionRateRepository = distributionRateRepository;
        this.distributionRateMapper = distributionRateMapper;
    }

    public DistributionRateChartResponse findAllDistributionRateByScpiId(Long scpiId) {
        List<DistributionRate> distributionRates= distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpiId);

        List<DistributionRateDTOResponse> distributionRateDTOResponses = distributionRateMapper.toDtoList(distributionRates);

        boolean insufficientHistory = distributionRateDTOResponses.size() < 2;
        double distribRateAvg3Years = 0.0;

        if (distributionRateDTOResponses.size() >= 3) {
            List<BigDecimal> last3distributionrates = distributionRateDTOResponses.subList(distributionRateDTOResponses.size() - 3, distributionRateDTOResponses.size())
                    .stream()
                    .map(DistributionRateDTOResponse::getRate)
                    .toList();
            distribRateAvg3Years = last3distributionrates.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .orElse(0.0);
        }

        return DistributionRateChartResponse.builder()
                .rates(distributionRateDTOResponses)
                .avg3Years(distribRateAvg3Years)
                .insufficientHistory(insufficientHistory)
                .build();
    }
    
}
