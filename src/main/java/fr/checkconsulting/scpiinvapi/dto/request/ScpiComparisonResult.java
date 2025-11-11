package fr.checkconsulting.scpiinvapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScpiComparisonResult {
    private Long id;
    private String name;
    private BigDecimal capitalization;
    private String rentFrequency;
    private Integer enjoymentDelay;
    private Integer minimumSubscription;

    private BigDecimal subscriptionFees;
    private Integer cashback;

    private BigDecimal estimatedMonthlyRevenue;
    private BigDecimal annualYieldPercentage;
}
