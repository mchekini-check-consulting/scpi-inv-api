package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.*;
import fr.checkconsulting.scpiinvapi.service.ScpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scpi")
@Slf4j
public class ScpiResource {

    private final ScpiService scpiService;

    public ScpiResource(ScpiService scpiService) {
        this.scpiService = scpiService;
    }

    @GetMapping
    public ResponseEntity<List<ScpiSummaryDto>> getAllScpi() {

        List<ScpiSummaryDto> scpiList = scpiService.getAllScpi();
        return ResponseEntity.ok(scpiList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScpiInvestmentDto> getScpiById(@PathVariable Long id) {
        ScpiInvestmentDto scpiDetails = scpiService.getScpiInvestmentById(id);
        return ResponseEntity.ok(scpiDetails);
    }

    @GetMapping("/{id}/repartition")
    public ResponseEntity<ScpiRepartitionDto> getScpiRepartition(@PathVariable Long id) {

        ScpiRepartitionDto repartition = scpiService.getScpiRepartitionById(id);
        return ResponseEntity.ok(repartition);
    }

    @GetMapping("/details/{slug}")
    public ScpiDetailDto getScpiDetails(@PathVariable String slug) {

        String[] parts = slug.split("-", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Le paramètre 'slug' doit être au format 'nom scpi - manager'.");
        }

        String scpiName = parts[0].trim();
        String managerName = parts[1].trim();

        return scpiService.getScpiDetails(scpiName, managerName);
    }

    @Operation(
            summary = "Liste des SCPI disponibles pour le comparateur",
            description = """
        Retourne toutes les SCPI 
        Utilisées par le comparateur des scpis.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des SCPI récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScpiWithRatesDTOResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Aucune SCPI disponible pour la comparaison",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la récupération des données du comparateur",
                    content = @Content
            )
    })
    @GetMapping("/comparator-scpis")
    public ResponseEntity<List<ScpiWithRatesDTOResponse>> getComparatorData() {
        return ResponseEntity.ok(scpiService.getAllForComparator());
    }


    @Operation(
            summary = "Liste des SCPI disponibles pour le simulateur",
            description = """
        Retourne toutes les SCPI 
        avec le dérnier rendement de distribution  (année la plus récente).
        Utilisées par le simulateur.
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des SCPI récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ScpiSimulatorDTOResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Aucune SCPI disponible pour le simulateur",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la récupération des SCPI du simulateur",
                    content = @Content
            )
    })
    @GetMapping("/scpis-full-ownership")
    public List<ScpiSimulatorDTOResponse> getScpiSimulatorData() {
        return scpiService.getScpiForSimulator();
    }


    @GetMapping("/scpiScheduledPayment")
    public ResponseEntity<List<ScpiSummaryDto>> getScpiScheduledPayment() {
        return ResponseEntity.ok(scpiService.getScpiShedultPayment());
    }

}