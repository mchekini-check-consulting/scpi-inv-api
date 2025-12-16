package fr.checkconsulting.scpiinvapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = """
            Payload de création ou de mise à jour d'une simulation SCPI.
            
            - Si l'identifiant `id` est null → création d'une nouvelle simulation
            - Si l'identifiant `id` est renseigné → mise à jour d'une simulation existante
            
            Le contenu de la simulation est défini par la liste des SCPI et leurs nombres de parts.
            """
)
public class SimulationSaveRequestDto {
    @Schema(
            description = """
                Identifiant de la simulation.
                
                - null : création d'une nouvelle simulation
                - non null : mise à jour d'une simulation existante
                """
    )
    private Long id;

    @Schema(description = "Nom de la simulation défini par l'utilisateur")
    private String name;

    @Schema(description = """
                Liste des SCPI composant la simulation.
                
                Chaque élément contient :
                - l'identifiant de la SCPI
                - le nombre de parts associées
                
                Cette liste ne peut pas être vide lors de la sauvegarde.
                """)
    private List<SimulationScpiLineRequestDto> items;
}