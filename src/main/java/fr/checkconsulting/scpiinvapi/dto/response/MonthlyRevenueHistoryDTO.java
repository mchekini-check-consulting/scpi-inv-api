package fr.checkconsulting.scpiinvapi.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueHistoryDTO {
    private Integer year;      
    private Integer month;     
    private BigDecimal revenue; 
}