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
public class MonthlyRevenueDTO {

    private BigDecimal totalMonthlyRevenue;
    private BigDecimal totalFutureMonthlyRevenue;
    private List<ScpiRevenueDetailDTO> details;
    private List<MonthlyRevenueHistoryDTO> history;

}
