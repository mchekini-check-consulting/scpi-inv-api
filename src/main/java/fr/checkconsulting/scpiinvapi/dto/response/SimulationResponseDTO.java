package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimulationResponseDTO {
    private Long id;
    private String name;
    private BigDecimal totalInvestment;
    private BigDecimal totalAnnualReturn;
    private List<SimulationScpiResponseDTO> items;
}
