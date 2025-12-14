package fr.checkconsulting.scpiinvapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Schema(description = "Payload de mise à jour du nombre de parts d'une SCPI")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateScpiSharesRequest {
    @NotNull
    @Min(1)
    @Schema(
            description = "Nombre de parts de la SCPI (doit être >= 1)")
    private Integer shares;
}
