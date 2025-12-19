package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.InvestmentRequestDTO;
import fr.checkconsulting.scpiinvapi.dto.response.InvestmentResponseDto;
import fr.checkconsulting.scpiinvapi.dto.response.MonthlyRevenueDTO;
import fr.checkconsulting.scpiinvapi.dto.response.MonthlyRevenueHistoryDTO;
import fr.checkconsulting.scpiinvapi.dto.response.PortfolioSummaryDto;
import fr.checkconsulting.scpiinvapi.dto.response.RepartitionItemDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiRepartitionDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiRevenueDetailDTO;
import fr.checkconsulting.scpiinvapi.mapper.InvestmentMapper;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.History;
import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import fr.checkconsulting.scpiinvapi.model.entity.Location;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.Sector;
import fr.checkconsulting.scpiinvapi.model.enums.InvestmentStatus;
import fr.checkconsulting.scpiinvapi.model.enums.InvestmentType;
import fr.checkconsulting.scpiinvapi.repository.HistoryRepository;
import fr.checkconsulting.scpiinvapi.repository.InvestmentRepository;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final HistoryRepository historyRepository;
    private final ScpiRepository scpiRepository;
    private final InvestmentMapper investmentMapper;
    private final UserService userService;
    private final NotificationService notificationService;
    private final DocumentService documentService;

    public InvestmentService(
            InvestmentRepository investmentRepository,
            HistoryRepository historyRepository,
            ScpiRepository scpiRepository,
            InvestmentMapper investmentMapper,
            UserService userService,
            NotificationService notificationService, DocumentService documentService) {
        this.investmentRepository = investmentRepository;
        this.historyRepository = historyRepository;
        this.scpiRepository = scpiRepository;
        this.investmentMapper = investmentMapper;
        this.userService = userService;
        this.notificationService = notificationService;
        this.documentService = documentService;
    }

    public void createInvestment(InvestmentRequestDTO request) {

        String userEmail = userService.getEmail();


        if (!documentService.areAllDocumentsValidated(userEmail)) {
            log.warn("Tentative d'investissement bloquée : documents non validés pour {}", userEmail);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "REGULATORY_DOCUMENTS_REQUIRED"
            );
        }


        Scpi scpi = scpiRepository.findById(request.getScpiId())
                .orElseThrow(() -> {
                    log.error(
                            "SCPI introuvable | scpiId={} | user={}",
                            request.getScpiId(), userEmail
                    );
                    return new IllegalArgumentException("SCPI non trouvée");
                });

        Investment investment = investmentMapper.toEntity(request);
        investment.setScpi(scpi);
        investment.setUserEmail(userEmail);
        investment.setInvestmentType(request.getInvestmentType());
        investment.setNumberOfShares(request.getNumberOfShares());
        investment.setInvestmentAmount(request.getInvestmentAmount());
        investment.setDismembermentYears(request.getDismembermentYears());
        investment.setInvestmentDate(LocalDateTime.now());
        investment.setPaymentType(request.getPaymentType());
        investment.setScheduledPaymentDate(request.getScheduledPaymentDate());
        investment.setMonthlyAmount(request.getMonthlyAmount());

        Investment saved = investmentRepository.save(investment);
        log.info(
                "Investissement sauvegardé | investmentId={} | user={} | scpiId={}",
                saved.getId(), userEmail, scpi.getId()
        );

        History history = History.builder()
                .investment(saved)
                .creationDate(ZonedDateTime.now(ZoneId.of("Europe/Paris")).toLocalDateTime())
                .modificationDate(ZonedDateTime.now(ZoneId.of("Europe/Paris")).toLocalDateTime())
                .status(InvestmentStatus.PENDING)
                .build();

        historyRepository.save(history);
        notificationService.sendEmailNotification(userService.getEmail(), investment);
    }

    public PortfolioSummaryDto getInvestorPortfolio(String sortBy) {

        String userEmail = userService.getEmail();
        List<Investment> investments;
        if ("amount".equalsIgnoreCase(sortBy)) {
            investments = investmentRepository.findByUserEmailOrderByInvestmentAmountDesc(userEmail);
        } else {
            investments = investmentRepository.findByUserEmailOrderByInvestmentDateDesc(userEmail);
        }

        BigDecimal totalAmount = investmentRepository.calculateTotalInvestedAmount(userEmail);
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

        BigDecimal totalMonthRevenu = calculateTotalMonthlyRevenue(investments);

        BigDecimal totalCumulRevenu = calculateTotalCumulativeRevenue(investments);

        List<InvestmentResponseDto> investmentDTOs = investmentMapper.toResponseDTOList(investments);

        return PortfolioSummaryDto.builder()
                .totalInvestedAmount(totalAmount)
                .totalInvestments(investments.size())
                .totalMonthRevenu(totalMonthRevenu)
                .totalCumulRevenu(totalCumulRevenu)
                .investments(investmentDTOs)
                .build();
    }

    private BigDecimal calculateTotalMonthlyRevenue(List<Investment> investments) {
        BigDecimal totalRevenue = BigDecimal.ZERO;

        for (Investment investment : investments) {
            BigDecimal rate = getLatestDistributionRate(investment.getScpi());

            if (rate == null) {
                rate = BigDecimal.ZERO;
            }

            BigDecimal monthlyRevenue = calculateMonthlyRevenueForInvestment(
                    investment.getInvestmentAmount(),
                    rate);

            if (investment.getInvestmentType() != InvestmentType.BARE_OWNERSHIP) {
                totalRevenue = totalRevenue.add(monthlyRevenue);
            }
        }

        return totalRevenue;
    }

    public ScpiRepartitionDto getPortfolioDistribution() {

        String userEmail = userService.getEmail();
        List<Investment> investments = investmentRepository
                .findByUserEmailOrderByInvestmentDateDesc(userEmail);

        BigDecimal totalInvestedAmount = investments.stream()
                .map(Investment::getInvestmentAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalInvestedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return ScpiRepartitionDto.builder()
                    .totalInvestedAmount(BigDecimal.ZERO)
                    .sectoral(List.of())
                    .geographical(List.of())
                    .build();
        }

        List<RepartitionItemDto> sectoralDistribution = calculateSectoralDistribution(investments, totalInvestedAmount);
        List<RepartitionItemDto> geographicalDistribution = calculateGeographicalDistribution(investments,
                totalInvestedAmount);

        return ScpiRepartitionDto.builder()
                .totalInvestedAmount(totalInvestedAmount)
                .sectoral(sectoralDistribution)
                .geographical(geographicalDistribution)
                .build();
    }

    private List<RepartitionItemDto> calculateSectoralDistribution(
            List<Investment> investments,
            BigDecimal totalInvestedAmount) {

        Map<String, BigDecimal> sectorAmounts = new HashMap<>();

        for (Investment investment : investments) {
            Scpi scpi = investment.getScpi();

            if (scpi.getSectors() == null || scpi.getSectors().isEmpty()) {
                continue;
            }

            for (Sector sector : scpi.getSectors()) {
                String sectorName = sector.getName();

                BigDecimal sectorContribution = investment.getInvestmentAmount()
                        .multiply(sector.getPercentage())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                sectorAmounts.merge(sectorName, sectorContribution, BigDecimal::add);
            }
        }

        return sectorAmounts.entrySet().stream()
                .map(entry -> {
                    String sectorName = entry.getKey();
                    BigDecimal sectorAmount = entry.getValue();

                    BigDecimal percentage = sectorAmount
                            .multiply(BigDecimal.valueOf(100))
                            .divide(totalInvestedAmount, 2, RoundingMode.HALF_UP);

                    return RepartitionItemDto.builder()
                            .label(sectorName)
                            .percentage(percentage)
                            .build();
                })
                .sorted((r1, r2) -> r2.getPercentage().compareTo(r1.getPercentage()))
                .collect(Collectors.toList());
    }

    private List<RepartitionItemDto> calculateGeographicalDistribution(
            List<Investment> investments,
            BigDecimal totalInvestedAmount) {

        Map<String, BigDecimal> countryAmounts = new HashMap<>();

        for (Investment investment : investments) {
            Scpi scpi = investment.getScpi();

            if (scpi.getLocations() == null || scpi.getLocations().isEmpty()) {
                continue;
            }

            for (Location country : scpi.getLocations()) {
                String countryName = country.getCountry();

                BigDecimal countryContribution = investment.getInvestmentAmount()
                        .multiply(country.getPercentage())
                        .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);

                countryAmounts.merge(countryName, countryContribution, BigDecimal::add);
            }
        }

        return countryAmounts.entrySet().stream()
                .map(entry -> {
                    String countryName = entry.getKey();
                    BigDecimal countryAmount = entry.getValue();

                    BigDecimal percentage = countryAmount
                            .multiply(BigDecimal.valueOf(100))
                            .divide(totalInvestedAmount, 2, RoundingMode.HALF_UP);

                    return RepartitionItemDto.builder()
                            .label(countryName)
                            .percentage(percentage)
                            .amount(countryAmount)
                            .build();
                })
                .sorted((r1, r2) -> r2.getPercentage().compareTo(r1.getPercentage()))
                .collect(Collectors.toList());
    }

    public MonthlyRevenueDTO calculateMonthlyRevenue(
            int months,
            Integer year,
            Long scpiId) {

        String userEmail = userService.getEmail();

        List<Investment> investments = investmentRepository
                .findByUserEmailOrderByInvestmentDateDesc(userEmail);

        if (scpiId != null) {
            investments = investments.stream()
                    .filter(inv -> inv.getScpi().getId().equals(scpiId))
                    .collect(Collectors.toList());
        }

        BigDecimal totalCurrentRevenue = BigDecimal.ZERO;
        BigDecimal totalFutureRevenue = BigDecimal.ZERO;
        List<ScpiRevenueDetailDTO> details = new ArrayList<>();

        for (Investment investment : investments) {
            Scpi scpi = investment.getScpi();

            BigDecimal distributionRate = getLatestDistributionRate(scpi);

            if (distributionRate == null) {
                distributionRate = BigDecimal.ZERO;
            }

            BigDecimal monthlyRevenue = calculateMonthlyRevenueForInvestment(
                    investment.getInvestmentAmount(),
                    distributionRate);

            if (investment.getInvestmentType() == InvestmentType.BARE_OWNERSHIP) {
                totalFutureRevenue = totalFutureRevenue.add(monthlyRevenue);
            } else {
                totalCurrentRevenue = totalCurrentRevenue.add(monthlyRevenue);
            }

            details.add(ScpiRevenueDetailDTO.builder()
                    .scpiId(scpi.getId())
                    .scpiName(scpi.getName())
                    .monthlyRevenue(monthlyRevenue)
                    .investmentAmount(investment.getInvestmentAmount())
                    .distributionRate(distributionRate)
                    .investmentType(investment.getInvestmentType())
                    .build());
        }

        BigDecimal totalCumulRevenue = calculateTotalCumulativeRevenue(investments);

        List<MonthlyRevenueHistoryDTO> history = calculateRevenueHistory(
                months,
                year,
                scpiId);

        return MonthlyRevenueDTO.builder()
                .totalMonthlyRevenue(totalCurrentRevenue)
                .totalFutureMonthlyRevenue(totalFutureRevenue)
                .totalCumulRevenue(totalCumulRevenue)
                .details(details)
                .history(history)
                .build();
    }

    public int calculateMonthsSinceFirstInvestment() {

        String userEmail = userService.getEmail();
        List<Investment> investments = investmentRepository
                .findByUserEmailOrderByInvestmentDateAsc(userEmail);

        if (investments.isEmpty()) {
            return 6;
        }

        LocalDate firstInvestmentDate = investments.get(0).getInvestmentDate().toLocalDate();
        LocalDate today = LocalDate.now();

        long monthsSinceFirst = java.time.temporal.ChronoUnit.MONTHS.between(firstInvestmentDate, today);

        return (int) monthsSinceFirst + 1;
    }

    private List<MonthlyRevenueHistoryDTO> calculateRevenueHistory(
            int months,
            Integer year,
            Long scpiId) {

        String userEmail = userService.getEmail();

        List<Investment> allInvestments = investmentRepository
                .findByUserEmailOrderByInvestmentDateAsc(userEmail);

        if (scpiId != null) {
            allInvestments = allInvestments.stream()
                    .filter(inv -> inv.getScpi().getId().equals(scpiId))
                    .collect(Collectors.toList());
        }

        List<MonthlyRevenueHistoryDTO> history = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = months - 1; i >= 0; i--) {
            LocalDate targetMonth = today.minusMonths(i);

            // ✅ Filtrer par année si demandé
            if (year != null && targetMonth.getYear() != year) {
                continue; // Ignorer ce mois s'il n'est pas dans l'année demandée
            }

            List<Investment> activeInvestments = allInvestments.stream()
                    .filter(inv -> {
                        LocalDate investmentDate = inv.getInvestmentDate().toLocalDate();
                        return !investmentDate.isAfter(targetMonth);
                    })
                    .collect(Collectors.toList());

            BigDecimal monthlyRevenue = BigDecimal.ZERO;

            for (Investment investment : activeInvestments) {
                BigDecimal rate = getLatestDistributionRate(investment.getScpi());

                if (rate == null) {
                    rate = BigDecimal.ZERO;
                }

                BigDecimal revenue = calculateMonthlyRevenueForInvestment(
                        investment.getInvestmentAmount(),
                        rate);

                if (investment.getInvestmentType() != InvestmentType.BARE_OWNERSHIP) {
                    monthlyRevenue = monthlyRevenue.add(revenue);
                }
            }

            history.add(MonthlyRevenueHistoryDTO.builder()
                    .year(targetMonth.getYear())
                    .month(targetMonth.getMonthValue())
                    .revenue(monthlyRevenue)
                    .build());
        }

        return history;
    }

    private BigDecimal getLatestDistributionRate(Scpi scpi) {
        if (scpi.getDistributionRates() == null || scpi.getDistributionRates().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return scpi.getDistributionRates().stream()
                .max(java.util.Comparator.comparing(DistributionRate::getDistributionYear))
                .map(DistributionRate::getRate)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateMonthlyRevenueForInvestment(
            BigDecimal investmentAmount,
            BigDecimal distributionRate) {
        if (investmentAmount == null || distributionRate == null) {
            return BigDecimal.ZERO;
        }

        return investmentAmount
                .multiply(distributionRate)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCumulativeRevenue(List<Investment> investments) {
        BigDecimal totalCumul = BigDecimal.ZERO;

        for (Investment investment : investments) {

            BigDecimal rate = getLatestDistributionRate(investment.getScpi());

            if (rate == null) {
                rate = BigDecimal.ZERO;
            }

            LocalDate investmentDate = investment.getInvestmentDate().toLocalDate();
            LocalDate today = LocalDate.now();
            long monthsHeld = java.time.temporal.ChronoUnit.MONTHS.between(investmentDate, today);

            if (monthsHeld == 0) {
                monthsHeld = 1;
            }

            BigDecimal monthlyRevenue = calculateMonthlyRevenueForInvestment(
                    investment.getInvestmentAmount(),
                    rate);

            BigDecimal cumulForInvestment = monthlyRevenue
                    .multiply(BigDecimal.valueOf(monthsHeld));

            if (investment.getInvestmentType() != InvestmentType.BARE_OWNERSHIP) {
                totalCumul = totalCumul.add(cumulForInvestment);
            }
        }

        return totalCumul;
    }

    public boolean hasInvested(String userId, Long scpiId) {
        return investmentRepository.existsByUserEmailAndScpiId(userId, scpiId);
    }
}
