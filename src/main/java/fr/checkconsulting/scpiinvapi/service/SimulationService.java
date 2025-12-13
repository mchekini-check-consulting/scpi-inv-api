package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.SimulationSaveRequestDto;
import fr.checkconsulting.scpiinvapi.dto.request.SimulationScpiLineRequestDto;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationResponseDTO;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationScpiResponseDTO;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.Simulation;
import fr.checkconsulting.scpiinvapi.model.entity.SimulationScpi;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import fr.checkconsulting.scpiinvapi.repository.SimulationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final ScpiRepository scpiRepository;
    private final UserService userService;

    @Transactional
    public SimulationResponseDTO saveSimulation(SimulationSaveRequestDto request) {
        String userId = userService.getEmail();
        log.info("Sauvegarde simulation pour userId={} avec request={}", userId, request);

        if (request.getId() == null && (request.getItems() == null || request.getItems().isEmpty())) {
            log.error("Tentative de création d'une simulation sans SCPI");
            throw new IllegalArgumentException("Impossible de créer une simulation sans SCPI");
        }

        Simulation simulation = (request.getId() == null)
                ? Simulation.builder().userEmail(userId).name(request.getName()).build()
                : simulationRepository.findByIdAndUserEmail(request.getId(), userId)
                .orElseThrow(() -> {
                    log.error("Simulation introuvable pour id={} et userId={}", request.getId(), userId);
                    return new EntityNotFoundException("Simulation introuvable");
                });

        if (simulation.getItems() == null) {
            simulation.setItems(new ArrayList<>());
        }

        if (request.getItems() != null) {
            for (SimulationScpiLineRequestDto line : request.getItems()) {
                boolean exists = simulation.getItems().stream()
                        .anyMatch(i -> i.getScpi().getId().equals(line.getScpiId()));
                if (!exists) {
                    log.debug("Ajout SCPI id={} avec {} parts", line.getScpiId(), line.getShares());
                    Scpi scpi = scpiRepository.findById(line.getScpiId())
                            .orElseThrow(() -> {
                                log.error("SCPI introuvable id={}", line.getScpiId());
                                return new EntityNotFoundException("SCPI introuvable");
                            });

                    SimulationScpi item = buildSimulationScpi(simulation, scpi, line.getShares());
                    simulation.getItems().add(item);
                }
            }
        }

        updateTotals(simulation, request.getName());
        return saveAndBuildResponse(simulation);
    }

    @Transactional
    public SimulationResponseDTO deleteScpi(Long simulationId, Long scpiId) {
        String userId = userService.getUserId();
        log.info("Suppression SCPI id={} de simulation id={} pour userId={}", scpiId, simulationId, userId);

        Simulation simulation = simulationRepository.findByIdAndUserEmail(simulationId, userId)
                .orElseThrow(() -> {
                    log.error("Simulation introuvable id={} pour userId={}", simulationId, userId);
                    return new EntityNotFoundException("Simulation introuvable");
                });

        boolean removed = simulation.getItems().removeIf(item -> item.getScpi().getId().equals(scpiId));
        if (!removed) {
            log.warn("SCPI id={} non trouvée dans simulation id={}", scpiId, simulationId);
        }

        updateTotals(simulation, simulation.getName());
        return saveAndBuildResponse(simulation);
    }

    @Transactional
    public SimulationResponseDTO updateScpiShares(Long simulationId, Long scpiId, int shares) {
        String userId = userService.getUserId();
        log.info("Mise à jour des parts SCPI id={} dans simulation id={} pour userId={} avec shares={}",
                scpiId, simulationId, userId, shares);

        Simulation simulation = simulationRepository.findByIdAndUserEmail(simulationId, userId)
                .orElseThrow(() -> {
                    log.error("Simulation introuvable id={} pour userId={}", simulationId, userId);
                    return new EntityNotFoundException("Simulation introuvable");
                });

        SimulationScpi item = simulation.getItems().stream()
                .filter(i -> i.getScpi().getId().equals(scpiId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("SCPI id={} introuvable dans simulation id={}", scpiId, simulationId);
                    return new EntityNotFoundException("SCPI introuvable dans la simulation");
                });

        log.debug("Anciennes parts={} pour SCPI id={}", item.getShares(), scpiId);
        SimulationScpi updatedItem = buildSimulationScpi(simulation, item.getScpi(), shares);
        item.setShares(updatedItem.getShares());
        item.setAmount(updatedItem.getAmount());
        item.setAnnualReturn(updatedItem.getAnnualReturn());

        updateTotals(simulation, simulation.getName());
        return saveAndBuildResponse(simulation);
    }

    @Transactional
    public SimulationResponseDTO getSimulationById(Long simulationId) {
        String userId = userService.getUserId();
        log.info("Récupération simulation id={} pour userId={}", simulationId, userId);

        Simulation simulation = simulationRepository.findByIdAndUserEmail(simulationId, userId)
                .orElseThrow(() -> {
                    log.error("Simulation introuvable id={} pour userId={}", simulationId, userId);
                    return new EntityNotFoundException("Simulation introuvable");
                });

        return buildResponse(simulation);
    }

    private SimulationScpi buildSimulationScpi(Simulation simulation, Scpi scpi, int shares) {
        BigDecimal sharePrice = scpi.getScpiValues().get(0).getSharePrice();
        BigDecimal amount = sharePrice.multiply(BigDecimal.valueOf(shares));
        BigDecimal annualReturn = amount.multiply(scpi.getDistributionRates().get(0).getRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return SimulationScpi.builder()
                .simulation(simulation)
                .scpi(scpi)
                .shares(shares)
                .amount(amount)
                .annualReturn(annualReturn)
                .build();
    }

    private void updateTotals(Simulation simulation, String name) {
        BigDecimal totalInvestment = simulation.getItems().stream()
                .map(SimulationScpi::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAnnualReturn = simulation.getItems().stream()
                .map(SimulationScpi::getAnnualReturn)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        simulation.setTotalInvestment(totalInvestment);
        simulation.setTotalAnnualReturn(totalAnnualReturn);
        simulation.setName(name);
        simulation.setUpdatedAt(LocalDateTime.now());

        log.debug("Totaux recalculés: investissement={} rendementAnnuel={}", totalInvestment, totalAnnualReturn);
    }

    private SimulationResponseDTO saveAndBuildResponse(Simulation simulation) {
        Simulation savedSimulation = simulationRepository.save(simulation);
        log.info("Simulation id={} sauvegardée avec {} items", savedSimulation.getId(), savedSimulation.getItems().size());
        return buildResponse(savedSimulation);
    }

    private SimulationResponseDTO buildResponse(Simulation simulation) {
        List<SimulationScpiResponseDTO> itemsDTO = simulation.getItems().stream()
                .map(item -> SimulationScpiResponseDTO.builder()
                        .id(item.getId())
                        .scpiId(item.getScpi().getId())
                        .scpiName(item.getScpi().getName())
                        .shares(item.getShares())
                        .amount(item.getAmount())
                        .annualReturn(item.getAnnualReturn())
                        .build())
                .collect(Collectors.toList());

        return SimulationResponseDTO.builder()
                .id(simulation.getId())
                .name(simulation.getName())
                .totalInvestment(simulation.getTotalInvestment())
                .totalAnnualReturn(simulation.getTotalAnnualReturn())
                .items(itemsDTO)
                .build();
    }
}




