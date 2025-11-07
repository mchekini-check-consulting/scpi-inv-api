package fr.checkconsulting.scpiinvapi.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScpiDetailDto {
    private Long id; 
    private String name;
    private String manager;
    private BigDecimal capitalization;
    private BigDecimal sharePrice;
    private Integer minimumSubscription;
    private BigDecimal distributionRate;
    private BigDecimal subscriptionFees;
    private BigDecimal managementFees;
    private Integer enjoymentDelay;
    private String rentFrequency;
    private String advertising;


}
