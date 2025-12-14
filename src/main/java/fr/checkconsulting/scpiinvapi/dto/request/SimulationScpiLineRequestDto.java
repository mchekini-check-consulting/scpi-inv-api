package fr.checkconsulting.scpiinvapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        description = """
            Ligne représentant une SCPI incluse dans une simulation.
            
            Chaque ligne définit :
            - la SCPI sélectionnée
            - le nombre de parts associées
            """
)
public class SimulationScpiLineRequestDto {
    @Schema(description = "Identifiant unique de la SCPI")
    private Long scpiId;

    @Schema(description = "Nombre de parts de la SCPI")
    private Integer shares;
}