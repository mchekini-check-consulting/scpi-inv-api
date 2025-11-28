package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorPortfolioDistributionDTO {
    private BigDecimal totalInvestedAmount;        
    private List<RepartitionItemDto> sectoral;     
    private List<RepartitionItemDto> geographical;   
}