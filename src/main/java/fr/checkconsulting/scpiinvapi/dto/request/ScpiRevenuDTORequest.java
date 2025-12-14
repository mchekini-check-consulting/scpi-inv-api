package fr.checkconsulting.scpiinvapi.dto.request;

import fr.checkconsulting.scpiinvapi.dto.response.RepartitionItemDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(
        name = "ScpiRevenuDTORequest",
        description = """
            Payload envoyé par le frontend pour calculer la fiscalité d’un portefeuille SCPI.
            
            Il contient :
            - le revenu annuel brut généré par les SCPI
            - la répartition géographique des investissements (France / Europe)
            """
)
public class ScpiRevenuDTORequest {
    @NotNull
    @Schema(
            description = "Revenu annuel brut issu des SCPI (avant toute fiscalité)"
    )
    private BigDecimal revenuScpiBrut;
    
    @Schema(name = "RepartitionItemDto",description = "Représente un pays et son pourcentage d’exposition dans le portefeuille SCPI")
    private List<RepartitionItemDto> locations;
}
