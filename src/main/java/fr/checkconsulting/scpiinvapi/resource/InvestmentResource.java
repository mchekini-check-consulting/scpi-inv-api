package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.request.InvestmentRequestDTO;
import fr.checkconsulting.scpiinvapi.dto.response.PortfolioSummaryDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiRepartitionDto;
import fr.checkconsulting.scpiinvapi.service.InvestmentService;
import fr.checkconsulting.scpiinvapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/investment")
public class InvestmentResource {

    private final InvestmentService investmentService;
    private final UserService userService;

    public InvestmentResource(InvestmentService investmentService, UserService userService) {
        this.investmentService = investmentService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody InvestmentRequestDTO request) {
        String userId = this.userService.getUserId();
        investmentService.createInvestment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // ← 201 sans corps
    }

    @GetMapping("/my-portfolio")
    public ResponseEntity<PortfolioSummaryDto> getMyPortfolio(
            @RequestParam(defaultValue = "date") String sortBy) {
        String userId = userService.getUserId();
        PortfolioSummaryDto portfolio = investmentService.getInvestorPortfolio(userId, sortBy);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/portfolio-distribution")
    public ResponseEntity<ScpiRepartitionDto> getPortfolioDistribution() {
        String userId = userService.getUserId();
        ScpiRepartitionDto distribution = investmentService.getPortfolioDistribution(userId);
        return ResponseEntity.ok(distribution);
    }

}
