package fr.checkconsulting.scpiinvapi.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TauxDistributionDTORequest {
    private Long id;
    private Integer annee;
    private BigDecimal tauxDistribution;
    private Long scpiId;
}
