package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationExportPDFDto {
    private Long simulationId;
    private String simulationName;
    private LocalDateTime generatedAt;

    private BigDecimal totalInvestment;
    private BigDecimal totalAnnualReturn;

    private BigDecimal netRevenueAfterTax;
    private BigDecimal netYieldPercentage;

    private FiscaliteResponseDto fiscalite;

    private List<String> scpiNames;
    private List<Integer> scpiShares;
    private List<BigDecimal> scpiInvestedAmounts;
    private List<BigDecimal> scpiAnnualReturns;
    private List<BigDecimal> scpiDistributionRates;

    private List<String> scpiLocalisations;

    private BigDecimal revenuAvantScpi;
    private BigDecimal revenuApresScpi;
    private BigDecimal gainNet;
}
