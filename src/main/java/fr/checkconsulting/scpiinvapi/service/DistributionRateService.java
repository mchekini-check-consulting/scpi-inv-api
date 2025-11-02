package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateChartResponse;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;

import java.util.List;

public interface DistributionRateService {

    List<DistributionRateDTOResponse> findAllDistributionRateByScpiId(Long scpiId);

    DistributionRateChartResponse getDistributionRatesChart(Long scpiId);
}
