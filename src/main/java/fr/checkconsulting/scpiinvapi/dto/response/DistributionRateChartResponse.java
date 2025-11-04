package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DistributionRateChartResponse {
    private List<DistributionRateDTOResponse> rates;
    private Double avg3Years;
    private boolean insufficientHistory;
}
