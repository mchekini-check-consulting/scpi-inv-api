package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/history")
public class HistoryResource {

    private final HistoryService service;

    @GetMapping
    public ResponseEntity<List<HistoryDto>> getHistory() {
        return ResponseEntity.ok(service.getHistory());
    }

    @GetMapping("/investment/{id}")
    public ResponseEntity<List<HistoryDto>> getHistoryByInvestment(@PathVariable Long id) {
        return ResponseEntity.ok(service.getHistoryByInvestmentId(id));
    }

}
