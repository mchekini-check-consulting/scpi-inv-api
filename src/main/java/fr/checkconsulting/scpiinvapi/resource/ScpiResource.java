package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.request.ScpiSearchCriteriaDto;
import fr.checkconsulting.scpiinvapi.dto.response.*;
import fr.checkconsulting.scpiinvapi.service.ScpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scpi")
@Slf4j
public class ScpiResource {

    private final ScpiService scpiService;

    public ScpiResource(ScpiService scpiService) {
        this.scpiService = scpiService;
    }

    @GetMapping("/filters-options")
    public ResponseEntity<ScpiFiltersOptionsDto> getScpiFiltersOptions() {
        return ResponseEntity.ok(scpiService.getScpiFiltersOptions());
    }

    @GetMapping
    public ResponseEntity<Page<ScpiSummaryDto>> searchScpi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minimumSubscription,
            @RequestParam(required = false) Double yield,
            @RequestParam(required = false) List<String> countries,
            @RequestParam(required = false) List<String> sectors,
            @RequestParam(required = false) List<String> rentFrequencies) {

        ScpiSearchCriteriaDto criteria = new ScpiSearchCriteriaDto(
                name, type, minimumSubscription, yield, countries, sectors, rentFrequencies
        );

        Page<ScpiSummaryDto> scpiPage = scpiService.searchScpi(criteria, page, size);
        return ResponseEntity.ok(scpiPage);
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
                            schema = @Schema(implementation = ScpiWithRatesResponseDto.class)
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
    public ResponseEntity<List<ScpiWithRatesResponseDto>> getComparatorData() {
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
                            schema = @Schema(implementation = ScpiSimulatorResponseDto.class)
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
    public List<ScpiSimulatorResponseDto> getScpiSimulatorData() {
        return scpiService.getScpiForSimulator();
    }

    @GetMapping("/scpiScheduledPayment")
    public ResponseEntity<List<ScpiSummaryDto>> getScpiScheduledPayment() {
        return ResponseEntity.ok(scpiService.getScpiShedultPayment());
    }

}