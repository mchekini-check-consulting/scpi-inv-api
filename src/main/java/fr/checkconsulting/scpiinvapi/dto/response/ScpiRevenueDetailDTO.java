package fr.checkconsulting.scpiinvapi.dto.response;

import fr.checkconsulting.scpiinvapi.model.enums.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScpiRevenueDetailDTO {
    private Long scpiId;
    private String scpiName;                    
    private BigDecimal monthlyRevenue;        
    private BigDecimal investmentAmount;        
    private BigDecimal distributionRate;       
    private InvestmentType investmentType;     
}