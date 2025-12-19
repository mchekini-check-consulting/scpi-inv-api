package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.request.ScpiRevenuRequestDto;
import fr.checkconsulting.scpiinvapi.dto.request.SimulationSaveRequestDto;
import fr.checkconsulting.scpiinvapi.dto.request.UpdateScpiSharesRequestDto;
import fr.checkconsulting.scpiinvapi.dto.response.FiscaliteResponseDto;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationResponseDto;
import fr.checkconsulting.scpiinvapi.service.FiscaliteService;
import fr.checkconsulting.scpiinvapi.service.SimulationExportPDFService;
import fr.checkconsulting.scpiinvapi.service.SimulationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/scpi/simulations")
@RequiredArgsConstructor
@Tag(name = "simulation SCPI", description = "Endpoints pour gérer les simulations SCPI")

public class SimulationResource {

    private final SimulationService simulationService;
    private final FiscaliteService fiscaliteService;
    private final SimulationExportPDFService exportService;


    @PostMapping
    @Operation(
            summary = "Créer ou mettre à jour une simulation",
            description = "Si l'id est null → création, sinon mise à jour de la simulation du user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Simulation créée ou mise à jour avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SimulationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données de la simulation invalides"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Simulation introuvable lors de la mise à jour"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la sauvegarde de la simulation"
            )
    })
    public ResponseEntity<SimulationResponseDto> saveSimulation(
            @Parameter(description = "Données de la simulation à créer ou mettre à jour", required = true)
            @RequestBody SimulationSaveRequestDto simulationSaveRequestDto
    ) {
        return ResponseEntity.ok(simulationService.saveSimulation(simulationSaveRequestDto));
    }


    @GetMapping
    @Operation(
            summary = "Lister les simulations de l'utilisateur courant",
            description = "Retourne la liste de toutes les simulations SCPI associées à l'utilisateur connecté, triées par date de création décroissante"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des simulations récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = SimulationResponseDto.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la récupération des simulations"
            )
    })
    public ResponseEntity<List<SimulationResponseDto>> getAllSimulations() {
        List<SimulationResponseDto> simulations = simulationService.getAllSimulations();
        return ResponseEntity.ok(simulations);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une simulation",
            description = "Supprime une simulation avec toutes ses liens SCPI"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Simulation supprimée avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Simulation introuvable"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la suppression de la simulation"
            )
    })
    public ResponseEntity<Void> deleteSimulation(
            @Parameter(description = "Identifiant de la simulation", required = true)
            @PathVariable Long id) {
        simulationService.deleteSimulation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{simulationId}/scpis/{scpiId}")
    @Operation(
            summary = "Supprimer une SCPI d'une simulation",
            description = """
                    Supprime une SCPI spécifique d'une simulation existante et mis à jour des totaux.
                    
                    Si la simulation contient encore des SCPI après suppression :
                    - la simulation est mise à jour
                    
                    Si la simulation devient vide :
                    - la simulation est automatiquement supprimée
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "SCPI supprimée et simulation mise à jour",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SimulationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "SCPI supprimée et simulation automatiquement supprimée car vide"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Simulation ou SCPI introuvable"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la suppression de la SCPI"
            )
    })
    public ResponseEntity<SimulationResponseDto> deleteScpiFromSimulation(
            @Parameter(description = "Identifiant de la simulation", required = true)
            @PathVariable Long simulationId,
            @Parameter(description = "Identifiant de la SCPI à supprimer", required = true)
            @PathVariable Long scpiId) {

        return simulationService.deleteScpi(simulationId, scpiId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/{simulationId}/scpis/{scpiId}")
    @Operation(
            summary = "Modifier le nombre de parts d'une SCPI",
            description = """
                    Met à jour le nombre de parts  pour une SCPI donnée
                    dans une simulation existante.
                    
                    Les montants investis et revenus annuels sont recalculés automatiquement.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parts mises à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Payload invalide"),
            @ApiResponse(responseCode = "404", description = "Simulation ou SCPI introuvable")
    })

    public SimulationResponseDto updateScpiShares(
            @Parameter(description = "Identifiant de la simulation", required = true)
            @PathVariable Long simulationId,
            @Parameter(description = "Identifiant de la SCPI", required = true)
            @PathVariable Long scpiId,
            @Valid @RequestBody UpdateScpiSharesRequestDto payloadUpdateScpiSharesRequestDto
    ) {
        return simulationService.updateScpiShares(simulationId, scpiId, payloadUpdateScpiSharesRequestDto.getShares());
    }

    @GetMapping("/{simulationId}")
    @Operation(
            summary = "Consulter le détail d'une simulation",
            description = """
                    Retourne le détail complet d'une simulation SCPI :
                    - SCPI associées
                    - montants investis
                    - revenus annuels
                    - répartitions géographiques et sectorielles
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Détail de la simulation retourné avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SimulationResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Simulation introuvable"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la récupération de la simulation"
            )
    })
    public SimulationResponseDto getSimulation(
            @Parameter(description = "Identifiant unique de la simulation", required = true)
            @PathVariable Long simulationId) {
        return simulationService.getSimulationById(simulationId);
    }



    @PostMapping("/fiscalite")
    @Operation(
            summary = "Calculer la fiscalité d’un profil investisseur",
            description = """
                Calcule l’impact fiscal annuel d’un portefeuille SCPI en fonction :
                • du revenu SCPI brut
                • de la répartition géographique des investissements(France|Europe)
                
                Le calcul inclut :
                - l’impôt total estimé
                - la tranche marginale d’imposition (TMI) avant et après SCPI
                - les prélèvements sociaux (France / Europe)
                - le revenu net après fiscalité...
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fiscalité calculée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FiscaliteResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide (données manquantes ou incorrectes)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors du calcul de la fiscalité"
            )
    })
    public ResponseEntity<FiscaliteResponseDto> calculFiscalite(
            @Parameter(
                    description = """
            Paramètres d’entrée pour le calcul de la fiscalité :
            - revenu annuel brut issu des SCPI
            - répartition géographique des investissements  (pour le calcul des prélèvements sociaux (France / Europe))
            """,
                    required = true
            )

            @RequestBody ScpiRevenuRequestDto scpiRevenuRequestDTO
    ) {
        FiscaliteResponseDto fiscaliteResponseDto = fiscaliteService.calculerFiscalite(
                scpiRevenuRequestDTO.getRevenuScpiBrut(),
                scpiRevenuRequestDTO.getLocations()
        );
        return ResponseEntity.ok(fiscaliteResponseDto);
    }

    @GetMapping("/{id}/export-pdf")
    @Operation(
            summary = "Exporter une simulation SCPI en PDF",
            description = """
            Génère et télécharge un document PDF récapitulatif d’une simulation SCPI.

            Le PDF contient notamment :
            - les informations générales de la simulation
            - la liste des SCPI avec montants investis et revenus
            - la répartition géographique (France / Europe)
            - la synthèse fiscale détaillée (impôt, prélèvements sociaux, revenu net)
            - le rendement net après fiscalité

            Le document est généré dynamiquement à partir des données courantes
            de la simulation et de la fiscalité associée.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "PDF généré et téléchargé avec succès",
                    content = @Content(
                            mediaType = "application/pdf",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Simulation introuvable"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne lors de la génération du PDF"
            )
    })
    public ResponseEntity<byte[]> export(
            @Parameter(description = "Identifiant unique de la simulation à exporter",required = true)
            @PathVariable Long id) {


        byte[] pdf = exportService.exportSimulationPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=simulation-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}

