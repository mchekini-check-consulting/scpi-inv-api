package fr.checkconsulting.scpiinvapi.service.impl;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateChartResponse;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;
import fr.checkconsulting.scpiinvapi.mapper.DistributionRateMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.repository.DistributionRateRepository;
import fr.checkconsulting.scpiinvapi.service.DistributionRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class DistributionRateServiceImpl implements DistributionRateService {

    private final DistributionRateRepository distributionRateRepository;
    private final DistributionRateMapper distributionRateMapper;

    public DistributionRateServiceImpl(DistributionRateRepository distributionRateRepository, DistributionRateMapper distributionRateMapper) {
        this.distributionRateRepository = distributionRateRepository;
        this.distributionRateMapper = distributionRateMapper;
    }

    @Override
    public List<DistributionRateDTOResponse> findAllDistributionRateByScpiId(Long scpiId) {
        List<DistributionRate> rates = distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpiId);
        return distributionRateMapper.toDtoList(rates);
    }

    @Override
    public DistributionRateChartResponse getDistributionRatesChart(Long scpiId) {
        List<DistributionRate> entities = distributionRateRepository.findAllByScpi_IdOrderByDistributionYearAsc(scpiId);
        List<DistributionRateDTOResponse> dtos = distributionRateMapper.toDtoList(entities);

        boolean insufficientHistory = dtos.size() < 2;
        Double avg3Years = 0.0;

        if (dtos.size() >= 3) {
            List<BigDecimal> last3 = dtos.subList(dtos.size() - 3, dtos.size())
                    .stream()
                    .map(DistributionRateDTOResponse::getRate)
                    .toList();
            avg3Years = last3.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(BigDecimal::doubleValue)
                    .average()
                    .orElse(0.0);
        }

        return DistributionRateChartResponse.builder()
                .rates(dtos)
                .avg3Years(avg3Years)
                .insufficientHistory(insufficientHistory)
                .build();
    }

}
