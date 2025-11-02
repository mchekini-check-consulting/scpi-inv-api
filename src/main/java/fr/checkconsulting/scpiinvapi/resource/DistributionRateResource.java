package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateChartResponse;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;
import fr.checkconsulting.scpiinvapi.service.DistributionRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scpi")
@RequiredArgsConstructor

public class DistributionRateResource {

    private final DistributionRateService service;

    @GetMapping("/{id}/distribution-rates")
    public ResponseEntity<List<DistributionRateDTOResponse>> getDistributionRates(@PathVariable Long id) {
        return ResponseEntity.ok(service.findAllDistributionRateByScpiId(id));
    }

    @GetMapping("/{id}/distribution-rates/chart")
    public ResponseEntity<DistributionRateChartResponse> getDistributionRatesChart(@PathVariable Long id) {
        DistributionRateChartResponse response = service.getDistributionRatesChart(id);
        return ResponseEntity.ok(response);
    }

}
