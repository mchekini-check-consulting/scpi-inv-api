package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateChartResponse;
import fr.checkconsulting.scpiinvapi.service.DistributionRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scpi")
public class DistributionRateResource {

    private final DistributionRateService distributionRateService;

    public DistributionRateResource(DistributionRateService distributionRateService) {
        this.distributionRateService = distributionRateService;
    }

    @GetMapping("/{id}/distribution-rates")
    public ResponseEntity<DistributionRateChartResponse> getDistributionRatesChart(@PathVariable Long id) {
        DistributionRateChartResponse response = distributionRateService.findAllDistributionRateByScpiId(id);
        return ResponseEntity.ok(response);
    }

}
