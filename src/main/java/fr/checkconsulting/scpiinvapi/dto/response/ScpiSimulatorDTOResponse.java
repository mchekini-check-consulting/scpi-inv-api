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
public class ScpiSimulatorDTOResponse {
    private Long id;
    private String name;
    private BigDecimal  yieldDistributionRate;
    private BigDecimal sharePrice;
    private Integer minimumSubscription;
    private List<RepartitionItemDto> sectors;
    private List<RepartitionItemDto> locations;
}
