package fr.checkconsulting.scpiinvapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistributionRateDTORequest {
    private Long id;
    private Integer distributionYear;
    private BigDecimal rate;
    private Long scpiId;

}
