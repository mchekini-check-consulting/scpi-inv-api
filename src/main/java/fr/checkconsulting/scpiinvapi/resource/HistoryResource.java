package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.mapper.HistoryMapper;
import fr.checkconsulting.scpiinvapi.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/history")
public class HistoryResource {

    private final HistoryService service;
    private final HistoryMapper mapper;

    @GetMapping
    public ResponseEntity<List<HistoryDto>> getHistory(){
        return ResponseEntity.ok(service.getHistory().stream().map(mapper::entityToDto).collect(Collectors.toList()));
    }

}
