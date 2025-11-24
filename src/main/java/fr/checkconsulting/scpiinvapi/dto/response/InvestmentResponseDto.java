package fr.checkconsulting.scpiinvapi.dto.response;

import fr.checkconsulting.scpiinvapi.model.enums.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentResponseDto {
    private Long id;
    private BigDecimal investmentAmount;
    private BigDecimal numberOfShares;
    private InvestmentType investmentType;
    private Integer dismembermentYears;
    private LocalDateTime investmentDate;
    
  
    private Long scpiId;
    private String scpiName;
    private String scpiType; 

}