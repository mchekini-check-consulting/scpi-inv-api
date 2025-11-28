package fr.checkconsulting.scpiinvapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddScpiToSimulationRequest {
    private Long simulationId;
    private Long scpiId;
    private Integer shares;
}
