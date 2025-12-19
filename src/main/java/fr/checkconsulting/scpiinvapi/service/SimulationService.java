package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.SimulationSaveRequestDto;
import fr.checkconsulting.scpiinvapi.dto.request.SimulationScpiLineRequestDto;
import fr.checkconsulting.scpiinvapi.dto.response.SimulationResponseDto;
import fr.checkconsulting.scpiinvapi.mapper.SimulationMapper;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final ScpiRepository scpiRepository;
    private final UserService userService;
    private final SimulationMapper simulationMapper;

    @Transactional
    public SimulationResponseDto saveSimulation(SimulationSaveRequestDto request) {

        String userEmail = userService.getEmail();
        log.info("Début sauvegarde simulation | user={} | simulationId={}",
                userEmail, request.getId());

        Simulation simulation;

        if (request.getId() == null) {
            simulation = Simulation.builder()
                    .userEmail(userEmail)
                    .name(request.getName())
                    .build();
        } else {
            simulation = simulationRepository
                    .findByIdAndUserEmail(request.getId(), userEmail)
                    .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));

            simulation.setName(request.getName());
        }

        if (simulation.getItems() == null) {
            simulation.setItems(new ArrayList<>());
        }

        for (SimulationScpiLineRequestDto line : request.getItems()) {
            boolean exists = simulation.getItems().stream()
                    .anyMatch(i -> i.getScpi().getId().equals(line.getScpiId()));

            if (exists) {
                log.debug("SCPI déjà existante, ignorée | scpiId={}", line.getScpiId());
                continue;
            }

            Scpi scpi = scpiRepository.findById(line.getScpiId())
                    .orElseThrow(() -> {
                        log.error("SCPI introuvable | scpiId={}", line.getScpiId());
                        return new EntityNotFoundException("SCPI introuvable");
                    });

            BigDecimal amount = scpi.getScpiValues().get(0).getSharePrice()
                    .multiply(BigDecimal.valueOf(line.getShares()));

            BigDecimal annualReturn = amount
                    .multiply(scpi.getDistributionRates().get(0).getRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            simulation.getItems().add(
                    SimulationScpi.builder()
                            .simulation(simulation)
                            .scpi(scpi)
                            .shares(line.getShares())
                            .amount(amount)
                            .annualReturn(annualReturn)
                            .build()
            );
        }

        simulation.setTotalInvestment(
                simulation.getItems().stream()
                        .map(SimulationScpi::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        simulation.setTotalAnnualReturn(
                simulation.getItems().stream()
                        .map(SimulationScpi::getAnnualReturn)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        Simulation saved = simulationRepository.save(simulation);
        log.info("Simulation sauvegardée | id={} | totalInvestment={} | totalAnnualReturn={}",
                saved.getId(),
                saved.getTotalInvestment(),
                saved.getTotalAnnualReturn()
        );
        return simulationMapper.toDto(saved);
    }

    @Transactional
    public Optional<SimulationResponseDto> deleteScpi(Long simulationId, Long scpiId) {

        String userEmail = userService.getEmail();
        log.info("Suppression SCPI de la simulation | simulationId={} | scpiId={} | user={}",
                simulationId, scpiId, userEmail);

        Simulation simulation = simulationRepository
                .findByIdAndUserEmail(simulationId, userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));

        boolean removed = simulation.getItems()
                .removeIf(item -> item.getScpi().getId().equals(scpiId));

        if (!removed) {
            log.warn("Aucune SCPI supprimée | simulationId={} | scpiId={}", simulationId, scpiId);
        }

        if (simulation.getItems().isEmpty()) {
            log.warn("Simulation supprimée car vide | simulationId={}", simulationId);
            simulationRepository.delete(simulation);
            return Optional.empty();
        }

        simulation.setTotalInvestment(
                simulation.getItems().stream()
                        .map(SimulationScpi::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        simulation.setTotalAnnualReturn(
                simulation.getItems().stream()
                        .map(SimulationScpi::getAnnualReturn)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        simulation.setUpdatedAt(LocalDateTime.now());

        Simulation saved = simulationRepository.save(simulation);

        return Optional.of(simulationMapper.toDto(saved));
    }

    @Transactional
    public SimulationResponseDto updateScpiShares(Long simulationId, Long scpiId, int shares) {

        String userEmail = userService.getEmail();
        log.info("Mise à jour parts SCPI | simulationId={} | scpiId={} | shares={} | user={}",
                simulationId, scpiId, shares, userEmail);

        Simulation simulation = simulationRepository
                .findByIdAndUserEmail(simulationId, userEmail)
                .orElseThrow(() -> {
                    log.error("Simulation introuvable | id={} | user={}",
                            simulationId, userEmail);
                    return new EntityNotFoundException("Simulation introuvable");
                });

        SimulationScpi item = simulation.getItems().stream()
                .filter(i -> i.getScpi().getId().equals(scpiId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Cette scpi ne figure pas dans la simulation | scpiId={} | simulationId={}",
                            scpiId, simulationId);
                    return new EntityNotFoundException("SCPI introuvable dans la simulation");
                });

        item.setShares(shares);

        BigDecimal sharePrice = item.getScpi()
                .getScpiValues().get(0).getSharePrice();

        BigDecimal amount = sharePrice.multiply(BigDecimal.valueOf(shares));

        BigDecimal annualReturn = amount
                .multiply(item.getScpi().getDistributionRates().get(0).getRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        item.setAmount(amount);
        item.setAnnualReturn(annualReturn);

        simulation.setTotalInvestment(
                simulation.getItems().stream()
                        .map(SimulationScpi::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        simulation.setTotalAnnualReturn(
                simulation.getItems().stream()
                        .map(SimulationScpi::getAnnualReturn)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        simulation.setUpdatedAt(LocalDateTime.now());

        Simulation savedSimulation = simulationRepository.save(simulation);

        log.info("Parts mises à jour | simulationId={} | totalInvestment={}",
                savedSimulation.getId(), savedSimulation.getTotalInvestment());
        return simulationMapper.toDto(savedSimulation);
    }


    @Transactional
    public SimulationResponseDto getSimulationById(Long simulationId) {
        String userEmail = userService.getEmail();
        log.info("Consultation simulation | id={} | user={}", simulationId, userEmail);

        Simulation simulation = simulationRepository
                .findByIdAndUserEmail(simulationId, userEmail)
                .orElseThrow(() -> {
                    log.error("Simulation introuvable | id={} | user={}",
                            simulationId, userEmail);
                    return new EntityNotFoundException("Simulation introuvable");
                });

        return simulationMapper.toDto(simulation);
    }


    public List<SimulationResponseDto> getAllSimulations() {
        String userEmail = userService.getEmail();
        log.info("Récupération simulations | user={}", userEmail);
        List<Simulation> simulations =
                simulationRepository.findAllByUserEmailOrderByCreatedAtDesc(userEmail);

        log.info("{} simulations trouvées | user={}", simulations.size(), userEmail);

        return simulationMapper.toDtoList(simulations);
    }

    @Transactional
    public void deleteSimulation(Long simulationId) {
        String userEmail = userService.getEmail();
        log.info("Suppression simulation | id={} | user={}", simulationId, userEmail);

        Simulation simulation = simulationRepository.findByIdAndUserEmail(simulationId, userEmail)
                .orElseThrow(() -> {
                    log.error("Simulation introuvable | id={} | user={}",
                            simulationId, userEmail);
                    return new EntityNotFoundException("Simulation introuvable");
                });
        simulationRepository.delete(simulation);
        log.info("Simulation supprimée | id={} | user={}", simulationId, userEmail);

    }

    @Transactional
    public Simulation getSimulationEntityById(Long simulationId) {

        String userEmail = userService.getEmail();

        return simulationRepository
                .findByIdAndUserEmail(simulationId, userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));
    }

}




