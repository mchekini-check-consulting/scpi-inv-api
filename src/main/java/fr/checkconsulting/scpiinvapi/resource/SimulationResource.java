package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.request.SimulationSaveRequestDto;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationResponseDTO;
import fr.checkconsulting.scpiinvapi.model.entity.Simulation;
import fr.checkconsulting.scpiinvapi.repository.SimulationRepository;
import fr.checkconsulting.scpiinvapi.service.SimulationService;
import fr.checkconsulting.scpiinvapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scpi/simulations")
@RequiredArgsConstructor
@Tag(name = "Simulation SCPI", description = "Endpoints pour gérer les simulations SCPI")
public class SimulationResource {

    private final SimulationService simulationService;
    private final UserService userService;
    private final SimulationRepository simulationRepository;

    @PostMapping
    @Operation(
            summary = "Créer ou mettre à jour une simulation",
            description = "Si l'id est null → création, sinon mise à jour de la simulation du user"
    )
    public ResponseEntity<SimulationResponseDTO> saveSimulation(
            @RequestBody SimulationSaveRequestDto request
    ) {
        SimulationResponseDTO simulationDto = simulationService.saveSimulation(request);
        return ResponseEntity.ok(simulationDto);
    }


    @GetMapping
    @Operation(
            summary = "Lister les simulations du user courant",
            description = "Retourne toutes les simulations associées à l'utilisateur connecté"
    )
    public ResponseEntity<List<Simulation>> getAllSimulations() {
        String userId = userService.getUserId();
        List<Simulation> simulations = simulationRepository.findAllByUserId(userId);
        return ResponseEntity.ok(simulations);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une simulation",
            description = "Supprime une simulation du user avec tous ses liens SCPI"
    )
    public ResponseEntity<Void> deleteSimulation(@PathVariable Long id) {
        String userId = userService.getUserId();
        Simulation simulation = simulationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));

        simulationRepository.delete(simulation);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{simulationId}/scpis/{scpiId}")
    @Operation(
            summary = "Supprimer une SCPI d'une simulation",
            description = "Supprime une SCPI spécifique d'une simulation et met à jour les totaux"
    )
    public ResponseEntity<SimulationResponseDTO> deleteScpiFromSimulation(
            @PathVariable Long simulationId,
            @PathVariable Long scpiId) {

        SimulationResponseDTO updatedSimulation = simulationService.deleteScpi(simulationId, scpiId);
        return ResponseEntity.ok(updatedSimulation);
    }

    @PutMapping("/{simulationId}/scpis/{scpiId}")
    public SimulationResponseDTO updateScpiShares(
            @PathVariable Long simulationId,
            @PathVariable Long scpiId,
            @RequestBody Map<String, Integer> payload
    ) {
        int shares = payload.get("shares");
        return simulationService.updateScpiShares(simulationId, scpiId, shares);
    }

    @GetMapping("/{simulationId}")
    public SimulationResponseDTO getSimulation(@PathVariable Long simulationId) {
        return simulationService.getSimulationById(simulationId);
    }
}

