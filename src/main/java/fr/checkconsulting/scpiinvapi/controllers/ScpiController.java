package fr.checkconsulting.scpiinvapi.controllers;

import fr.checkconsulting.scpiinvapi.dto.ScpiDto;
import fr.checkconsulting.scpiinvapi.entities.Scpi;
import fr.checkconsulting.scpiinvapi.mappers.ScpiMapper;
import fr.checkconsulting.scpiinvapi.services.ScpiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("scpi")
public class ScpiController {

    private final ScpiService service;
    private final ScpiMapper mapper;

    public ScpiController(ScpiService service, ScpiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping()
    public ResponseEntity<List<ScpiDto>> getAll() {
        List<ScpiDto> dtos = mapper.toDtoList(service.getAll());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping()
    public ResponseEntity<ScpiDto> create(@RequestBody ScpiDto dto) {
        Scpi saved = service.save(mapper.dtoToEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.entityToDto(saved));
    }
}
