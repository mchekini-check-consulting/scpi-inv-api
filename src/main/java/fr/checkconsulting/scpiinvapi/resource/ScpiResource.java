package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiSummaryDto;
import fr.checkconsulting.scpiinvapi.service.ScpiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}