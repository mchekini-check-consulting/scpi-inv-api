package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DistributionRateChartResponseDto {
    private List<DistributionRateResponseDto> rates;
    private Double avg3Years;
    private boolean insufficientHistory;
}
