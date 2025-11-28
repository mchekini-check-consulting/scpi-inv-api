package fr.checkconsulting.scpiinvapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationScpiLineRequestDto {
    private Long scpiId;
    private Integer shares;
}