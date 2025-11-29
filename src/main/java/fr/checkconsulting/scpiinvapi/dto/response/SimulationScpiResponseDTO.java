package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimulationScpiResponseDTO {
    private Long id;
    private Long scpiId;
    private String scpiName;
    private Integer shares;
    private BigDecimal amount;
    private BigDecimal annualReturn;
}
