package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiDetailDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiInvestmentDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiRepartitionDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiSummaryDto;
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

    public ScpiService(ScpiRepository scpiRepository, ScpiMapper scpiMapper, UserService userService, InvestmentRepository investmentRepository) {
        this.scpiRepository = scpiRepository;
        this.scpiMapper = scpiMapper;
        this.userService = userService;
        this.investmentRepository = investmentRepository;
    }

    public List<ScpiSummaryDto> getAllScpi() {

        return scpiMapper.toScpiSummaryDto(scpiRepository.findAll());
    }

    public ScpiDetailDto getScpiDetails(String name, String manager) {

        Scpi scpi = scpiRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Aucune SCPI trouvée avec le nom : " + name));

        if (!scpi.getManager().equalsIgnoreCase(manager)) {
            throw new RuntimeException(String.format(
                    "Le gestionnaire fourni (%s) ne correspond pas au gestionnaire de la SCPI (%s)",
                    manager, scpi.getManager()
            ));
        }

        BigDecimal sharePrice = scpi.getScpiValues().stream()
                .max(Comparator.comparing(ScpiPartValues::getValuationYear))
                .map(ScpiPartValues::getSharePrice)
                .orElseThrow(() -> new IllegalStateException("Aucune valeur de part trouvée"));

        BigDecimal distributionRate = scpi.getDistributionRates().stream()
                .max(Comparator.comparing(DistributionRate::getDistributionYear))
                .map(DistributionRate::getRate)
                .orElseThrow(() -> new IllegalStateException("Aucun taux de distribution trouvé"));

        ScpiDetailDto scpiDto = scpiMapper.toScpiDetailDto(scpi);
        scpiDto.setSharePrice(sharePrice);
        scpiDto.setDistributionRate(distributionRate);

        return scpiDto;
    }

    public ScpiInvestmentDto getScpiInvestmentById(Long id) {
        Scpi scpi = scpiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SCPI non trouvée avec l'id: " + id));

        String userId = userService.getUserId();

        List<Investment> investments = investmentRepository
                .findByInvestorUserIdAndScpiId(userId, id);

        BigDecimal totalInvestedAmount = investments.stream()
                .map(Investment::getInvestmentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasInvested = !investments.isEmpty();

        ScpiInvestmentDto dto = scpiMapper.toScpiInvestmentDto(scpi);
        dto.setHasInvested(hasInvested);
        dto.setTotalInvestedAmount(totalInvestedAmount);
        return dto;
    }

    public ScpiRepartitionDto getScpiRepartitionById(Long id) {


        Scpi scpi = scpiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SCPI non trouvée avec l'id: " + id));

        return scpiMapper.toScpiRepartitionDto(scpi);
    }
}