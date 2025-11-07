package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRateDTOResponse {
    private Long id;
    private Integer distributionYear;
    private BigDecimal rate;
    private Long scpiId;
}
