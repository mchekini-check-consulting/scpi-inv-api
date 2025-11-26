package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.request.InvestmentRequestDTO;

import fr.checkconsulting.scpiinvapi.dto.response.InvestmentResponseDto;
import fr.checkconsulting.scpiinvapi.dto.response.PortfolioSummaryDto;
import fr.checkconsulting.scpiinvapi.mapper.InvestmentMapper;
import fr.checkconsulting.scpiinvapi.model.entity.History;
import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import fr.checkconsulting.scpiinvapi.model.entity.Investor;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.enums.InvestmentStatus;
import fr.checkconsulting.scpiinvapi.repository.HistoryRepository;
import fr.checkconsulting.scpiinvapi.repository.InvestmentRepository;
import fr.checkconsulting.scpiinvapi.repository.InvestorRepository;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final HistoryRepository historyRepository;
    private final ScpiRepository scpiRepository;
    private final InvestorRepository investorRepository;
    private final InvestmentMapper investmentMapper;
    private final UserService userService;
    private final NotificationService notificationService;

    public InvestmentService(
            InvestmentRepository investmentRepository,
            HistoryRepository historyRepository,
            ScpiRepository scpiRepository,
            InvestorRepository investorRepository,
            InvestmentMapper investmentMapper, 
            UserService userService,
            NotificationService notificationService) {
        this.investmentRepository = investmentRepository;
        this.historyRepository = historyRepository;
        this.scpiRepository = scpiRepository;
        this.investorRepository = investorRepository;
        this.investmentMapper = investmentMapper;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public void createInvestment(InvestmentRequestDTO request, String userId) {

        Scpi scpi = scpiRepository.findById(request.getScpiId())
                .orElseThrow(() -> new IllegalArgumentException("SCPI non trouvée"));

        Investor investor = investorRepository.findById(userId)
                .orElseGet(() -> {
                    Investor newInvestor = Investor.builder()
                            .userId(userId)
                            .userEmail(userService.getEmail())
                            .firstName(userService.getFirstName())
                            .lastName(userService.getLastName())
                            .phoneNumber(userService.getPhoneNumber())
                            .build();
                    return investorRepository.save(newInvestor);
                });

        Investment investment = investmentMapper.toEntity(request);
        investment.setScpi(scpi);
        investment.setInvestor(investor);
        investment.setInvestmentType(request.getInvestmentType());
        investment.setNumberOfShares(request.getNumberOfShares());
        investment.setInvestmentAmount(request.getInvestmentAmount());
        investment.setDismembermentYears(request.getDismembermentYears());
        investment.setInvestmentDate(LocalDateTime.now());

        Investment saved = investmentRepository.save(investment);

        History history = History.builder()
                .investment(saved)
                .creationDate(ZonedDateTime.now(ZoneId.of("Europe/Paris")).toLocalDateTime())
                .modificationDate(ZonedDateTime.now(ZoneId.of("Europe/Paris")).toLocalDateTime())
                .status(InvestmentStatus.PENDING)
                .build();

        historyRepository.save(history);
        notificationService.sendEmailNotification(userService.getEmail(), investment);
    }

   
    public PortfolioSummaryDto getInvestorPortfolio(String userId, String sortBy) {
       
        List<Investment> investments;
        if ("amount".equalsIgnoreCase(sortBy)) {
            investments = investmentRepository.findByInvestorUserIdOrderByInvestmentAmountDesc(userId);
        } else {
       
            investments = investmentRepository.findByInvestorUserIdOrderByInvestmentDateDesc(userId);
        }
        

        BigDecimal totalAmount = investmentRepository.calculateTotalInvestedAmount(userId);
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
        
     
        Long distinctScpis = investmentRepository.countDistinctScpisByInvestorUserId(userId);
        
     
        List<InvestmentResponseDto> investmentDTOs = investmentMapper.toResponseDTOList(investments);
        
        return PortfolioSummaryDto.builder()
                .totalInvestedAmount(totalAmount)
                .totalInvestments(investments.size())
                .totalScpis(distinctScpis != null ? distinctScpis.intValue() : 0)
                .investments(investmentDTOs)
                .build();
    }
}