package fr.checkconsulting.scpiinvapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationSaveRequestDto {
    private Long id;
    private String name;
    private List<SimulationScpiLineRequestDto> items;
}