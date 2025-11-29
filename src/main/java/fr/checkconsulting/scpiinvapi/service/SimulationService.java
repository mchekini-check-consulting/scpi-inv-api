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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;
    private final ScpiRepository scpiRepository;
    private final UserService userService;


    @Transactional
    public SimulationResponseDTO saveSimulation(SimulationSaveRequestDto request) {

        String userId = userService.getUserId();
        Simulation simulation;

        if (request.getId() == null && (request.getItems() == null || request.getItems().isEmpty())) {
            throw new IllegalArgumentException("Impossible de créer une simulation sans SCPI");
        }

        if (request.getId() == null) {
            simulation = Simulation.builder()
                    .userId(userId)
                    .name(request.getName())
                    .build();
        } else {
            simulation = simulationRepository.findByIdAndUserId(request.getId(), userId)
                    .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));
        }

        if (simulation.getItems() == null) {
            simulation.setItems(new ArrayList<>());
        }

        for (SimulationScpiLineRequestDto line : request.getItems()) {
            boolean exists = simulation.getItems().stream()
                    .anyMatch(i -> i.getScpi().getId().equals(line.getScpiId()));
            if (!exists) {
                Scpi scpi = scpiRepository.findById(line.getScpiId())
                        .orElseThrow(() -> new EntityNotFoundException("SCPI introuvable"));

                BigDecimal sharePrice = scpi.getScpiValues().get(0).getSharePrice();
                BigDecimal amount = sharePrice.multiply(BigDecimal.valueOf(line.getShares()));
                BigDecimal annualReturn = amount.multiply(
                        scpi.getDistributionRates().get(0).getRate()
                ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                SimulationScpi item = SimulationScpi.builder()
                        .simulation(simulation)
                        .scpi(scpi)
                        .shares(line.getShares())
                        .amount(amount)
                        .annualReturn(annualReturn)
                        .build();

                simulation.getItems().add(item);
            }
        }

        BigDecimal totalInvestment = simulation.getItems().stream()
                .map(SimulationScpi::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAnnualReturn = simulation.getItems().stream()
                .map(SimulationScpi::getAnnualReturn)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        simulation.setTotalInvestment(totalInvestment);
        simulation.setTotalAnnualReturn(totalAnnualReturn);
        simulation.setName(request.getName());
        simulation.setUpdatedAt(LocalDateTime.now());

        Simulation savedSimulation = simulationRepository.save(simulation);

        List<SimulationScpiResponseDTO> itemsDTO = savedSimulation.getItems().stream()
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
                .id(savedSimulation.getId())
                .name(savedSimulation.getName())
                .totalInvestment(savedSimulation.getTotalInvestment())
                .totalAnnualReturn(savedSimulation.getTotalAnnualReturn())
                .items(itemsDTO)
                .build();
    }

    @Transactional
    public SimulationResponseDTO deleteScpi(Long simulationId, Long scpiId) {

        String userId = userService.getUserId();

        Simulation simulation = simulationRepository.findByIdAndUserId(simulationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));

        simulation.getItems().removeIf(item -> item.getScpi().getId().equals(scpiId));

        BigDecimal totalInvestment = simulation.getItems().stream()
                .map(SimulationScpi::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAnnualReturn = simulation.getItems().stream()
                .map(SimulationScpi::getAnnualReturn)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        simulation.setTotalInvestment(totalInvestment);
        simulation.setTotalAnnualReturn(totalAnnualReturn);
        simulation.setUpdatedAt(LocalDateTime.now());

        Simulation savedSimulation = simulationRepository.save(simulation);

        List<SimulationScpiResponseDTO> itemsDTO = savedSimulation.getItems().stream()
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
                .id(savedSimulation.getId())
                .name(savedSimulation.getName())
                .totalInvestment(savedSimulation.getTotalInvestment())
                .totalAnnualReturn(savedSimulation.getTotalAnnualReturn())
                .items(itemsDTO)
                .build();
    }

    @Transactional
    public SimulationResponseDTO updateScpiShares(Long simulationId, Long scpiId, int shares) {

        String userId = userService.getUserId();

        Simulation simulation = simulationRepository.findByIdAndUserId(simulationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));

        SimulationScpi item = simulation.getItems().stream()
                .filter(i -> i.getScpi().getId().equals(scpiId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("SCPI introuvable dans la simulation"));

        item.setShares(shares);

        BigDecimal sharePrice = item.getScpi().getScpiValues().get(0).getSharePrice();
        BigDecimal amount = sharePrice.multiply(BigDecimal.valueOf(shares));
        BigDecimal annualReturn = amount.multiply(
                item.getScpi().getDistributionRates().get(0).getRate()
        ).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        item.setAmount(amount);
        item.setAnnualReturn(annualReturn);

        BigDecimal totalInvestment = simulation.getItems().stream()
                .map(SimulationScpi::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAnnualReturn = simulation.getItems().stream()
                .map(SimulationScpi::getAnnualReturn)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        simulation.setTotalInvestment(totalInvestment);
        simulation.setTotalAnnualReturn(totalAnnualReturn);
        simulation.setUpdatedAt(LocalDateTime.now());

        Simulation savedSimulation = simulationRepository.save(simulation);

        List<SimulationScpiResponseDTO> itemsDTO = savedSimulation.getItems().stream()
                .map(i -> SimulationScpiResponseDTO.builder()
                        .id(i.getId())
                        .scpiId(i.getScpi().getId())
                        .scpiName(i.getScpi().getName())
                        .shares(i.getShares())
                        .amount(i.getAmount())
                        .annualReturn(i.getAnnualReturn())
                        .build())
                .collect(Collectors.toList());

        return SimulationResponseDTO.builder()
                .id(savedSimulation.getId())
                .name(savedSimulation.getName())
                .totalInvestment(savedSimulation.getTotalInvestment())
                .totalAnnualReturn(savedSimulation.getTotalAnnualReturn())
                .items(itemsDTO)
                .build();
    }

    @Transactional
    public SimulationResponseDTO getSimulationById(Long simulationId) {
        String userId = userService.getUserId();

        Simulation simulation = simulationRepository.findByIdAndUserId(simulationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Simulation introuvable"));

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




