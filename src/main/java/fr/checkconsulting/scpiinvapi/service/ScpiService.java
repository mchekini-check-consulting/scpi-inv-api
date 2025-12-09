package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiWithRatesDTOResponse;
import fr.checkconsulting.scpiinvapi.dto.response.*;
import fr.checkconsulting.scpiinvapi.mapper.ScpiMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.ScpiPartValues;
import fr.checkconsulting.scpiinvapi.repository.InvestmentRepository;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ScpiService {

    private final ScpiRepository scpiRepository;
    private final ScpiMapper scpiMapper;
    private final UserService userService;
    private final InvestmentRepository investmentRepository;

    public ScpiService(ScpiRepository scpiRepository,
                       ScpiMapper scpiMapper,
                       UserService userService,
                       InvestmentRepository investmentRepository) {
        this.scpiRepository = scpiRepository;
        this.scpiMapper = scpiMapper;
        this.userService = userService;
        this.investmentRepository = investmentRepository;
    }

    public List<ScpiSummaryDto> getAllScpi() {
        log.info("Récupération de toutes les SCPI");
        List<Scpi> scpis = scpiRepository.findAll();
        log.debug("Nombre de SCPI trouvées: {}", scpis.size());
        return scpiMapper.toScpiSummaryDto(scpis);
    }

    public ScpiDetailDto getScpiDetails(String name, String manager) {
        log.info("Récupération des détails SCPI pour name={} et manager={}", name, manager);

        Scpi scpi = scpiRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("Aucune SCPI trouvée avec le nom {}", name);
                    return new RuntimeException("Aucune SCPI trouvée avec le nom : " + name);
                });

        if (!scpi.getManager().equalsIgnoreCase(manager)) {
            log.error("Gestionnaire fourni ({}) ne correspond pas au gestionnaire réel ({})", manager, scpi.getManager());
            throw new RuntimeException(String.format(
                    "Le gestionnaire fourni (%s) ne correspond pas au gestionnaire de la SCPI (%s)",
                    manager, scpi.getManager()
            ));
        }

        BigDecimal sharePrice = extractLatestSharePrice(scpi);
        BigDecimal distributionRate = extractLatestDistributionRate(scpi);

        ScpiDetailDto scpiDto = scpiMapper.toScpiDetailDto(scpi);
        scpiDto.setSharePrice(sharePrice);
        scpiDto.setDistributionRate(distributionRate);

        log.debug("SCPI {} - prix part={} taux distribution={}", name, sharePrice, distributionRate);
        return scpiDto;
    }

    public ScpiInvestmentDto getScpiInvestmentById(Long id) {
        log.info("Récupération des investissements pour SCPI id={}", id);

        Scpi scpi = scpiRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("SCPI introuvable id={}", id);
                    return new IllegalArgumentException("SCPI non trouvée avec l'id: " + id);
                });

        String userId = userService.getUserId();
        List<Investment> investments = investmentRepository.findByInvestorUserIdAndScpiId(userId, id);

        BigDecimal totalInvestedAmount = investments.stream()
                .map(Investment::getInvestmentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasInvested = !investments.isEmpty();

        log.debug("User {} a investi={} montant total={}", userId, hasInvested, totalInvestedAmount);

        ScpiInvestmentDto dto = scpiMapper.toScpiInvestmentDto(scpi);
        dto.setHasInvested(hasInvested);
        dto.setTotalInvestedAmount(totalInvestedAmount);
        return dto;
    }

    public ScpiRepartitionDto getScpiRepartitionById(Long id) {
        log.info("Récupération de la répartition pour SCPI id={}", id);

        Scpi scpi = scpiRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("SCPI introuvable id={}", id);
                    return new IllegalArgumentException("SCPI non trouvée avec l'id: " + id);
                });

        return scpiMapper.toScpiRepartitionDto(scpi);
    }

    public List<ScpiWithRatesDTOResponse> getAllForComparator() {
        log.info("Récupération de toutes les SCPI pour comparateur");
        return scpiRepository.findAll().stream()
                .map(scpiMapper::toScpiWithRatesDTO)
                .toList();
    }

    public List<ScpiSimulatorDTOResponse> getScpiForSimulator() {
        log.info("Récupération des SCPI pour simulateur (hors démembrement)");

        return scpiRepository.findByDismembermentIsFalse().stream()
                .map(scpiMapper::toSimulatorDto)
                .sorted(Comparator
                        .<ScpiSimulatorDTOResponse, BigDecimal>comparing(
                                dto -> dto.getYieldDistributionRate() != null ? dto.getYieldDistributionRate() : BigDecimal.ZERO)
                        .reversed())
                .toList();
    }

    private BigDecimal extractLatestSharePrice(Scpi scpi) {
        return scpi.getScpiValues().stream()
                .max(Comparator.comparing(ScpiPartValues::getValuationYear))
                .map(ScpiPartValues::getSharePrice)
                .orElseThrow(() -> {
                    log.error("Aucune valeur de part trouvée pour SCPI {}", scpi.getName());
                    return new IllegalStateException("Aucune valeur de part trouvée");
                });
    }

    private BigDecimal extractLatestDistributionRate(Scpi scpi) {
        return scpi.getDistributionRates().stream()
                .max(Comparator.comparing(DistributionRate::getDistributionYear))
                .map(DistributionRate::getRate)
                .orElseThrow(() -> {
                    log.error("Aucun taux de distribution trouvé pour SCPI {}", scpi.getName());
                    return new IllegalStateException("Aucun taux de distribution trouvé");
                });
    }

    public List<ScpiSummaryDto> getScpiShedultPayment(){
        log.info("Récupération des versements programmés");
        return scpiRepository.findByScheduledPaymentTrue()
                .stream()
                .map(scpiMapper::toScpiSummaryDto)
                .toList();
    }
}