package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiInvestmentDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiDetailDto;
import fr.checkconsulting.scpiinvapi.dto.response.ScpiSummaryDto;
import fr.checkconsulting.scpiinvapi.service.ScpiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/scpi")
@RequiredArgsConstructor
@Slf4j
public class ScpiResource {
    
    private final ScpiService scpiService;
    
    @GetMapping
    public ResponseEntity<List<ScpiSummaryDto>> getAllScpi() {

        List<ScpiSummaryDto> scpiList = scpiService.getAllScpi();
        return ResponseEntity.ok(scpiList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScpiInvestmentDto> getScpiById(@PathVariable Long id) {
        ScpiInvestmentDto scpiDetails = scpiService.getScpiInvestmentById(id);
        return ResponseEntity.ok(scpiDetails);
    }


    @GetMapping("/details/{slug}")
    public ScpiDetailDto getScpiDetails(@PathVariable String slug) {

        String[] parts = slug.split("-", 2);
        if (parts.length < 2) {
            throw new IllegalArgumentException("Le paramètre 'slug' doit être au format 'nom scpi - manager'.");
        }

        String scpiName = parts[0].trim();
        String managerName = parts[1].trim();

        return scpiService.getScpiDetails(scpiName, managerName);
    }

}