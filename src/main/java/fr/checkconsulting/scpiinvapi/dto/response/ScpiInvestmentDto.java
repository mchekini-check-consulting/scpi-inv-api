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

public class ScpiInvestmentDto {
    private Long id;
    private String name;
    private Integer minimumSubscription;
    private BigDecimal sharePrice;
    private Boolean dismembermentActive;
    private BigDecimal distributionRate;
    private List<ScpiDismembrementDto> scpiDismembrement;
    private boolean hasInvested;
    private BigDecimal totalInvestedAmount;


}
