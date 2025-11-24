package fr.checkconsulting.scpiinvapi.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryDto {
    private BigDecimal totalInvestedAmount;
    private Integer totalInvestments;       
    private Integer totalScpis;             
    private List<InvestmentResponseDto> investments; 
}