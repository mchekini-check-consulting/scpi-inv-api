package fr.checkconsulting.scpiinvapi.dto.response;

import fr.checkconsulting.scpiinvapi.dto.request.DistributionRateDTORequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScpiWithRatesDTOResponse {
    private Long id;
    private String name;
    private BigDecimal capitalization;
    private String rentFrequency;
    private Integer enjoymentDelay;
    private Integer minimumSubscription;
    private BigDecimal subscriptionFees;
    private Integer cashback;
    private List<DistributionRateDTORequest> distributionRates;
}
