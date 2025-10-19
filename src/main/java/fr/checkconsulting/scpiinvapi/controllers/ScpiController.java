package fr.checkconsulting.scpiinvapi.controllers;

import fr.checkconsulting.scpiinvapi.dto.ScpiDto;
import fr.checkconsulting.scpiinvapi.entities.Scpi;
import fr.checkconsulting.scpiinvapi.mappers.ScpiMapper;
import fr.checkconsulting.scpiinvapi.services.ScpiService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("scpi")
@Tag(name = "SCPI", description = "Opérations liées aux SCPI")
public class ScpiController {

    private final ScpiService service;
    private final ScpiMapper mapper;

    public ScpiController(ScpiService service, ScpiMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Récupère toutes les SCPI", responses = {@ApiResponse(responseCode = "200",
            description = "Liste des SCPI récupérée avec succès", content = @Content(array = @ArraySchema(schema =
    @Schema(implementation = ScpiDto.class)))), @ApiResponse(responseCode = "500", description = "Erreur serveur")})
    public ResponseEntity<List<ScpiDto>> getAll() {
        List<ScpiDto> dtos = mapper.toDtoList(service.getAll());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping
    @Operation(summary = "Crée une nouvelle SCPI", responses = {@ApiResponse(responseCode = "200",
            description = "Enregistrement d'une nouvelle SCPI", content = @Content(array = @ArraySchema(schema =
    @Schema(implementation = ScpiDto.class)))), @ApiResponse(responseCode = "500", description = "Erreur serveur")})
    public ResponseEntity<ScpiDto> create(@RequestBody ScpiDto dto) {
        Scpi saved = service.save(mapper.dtoToEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.entityToDto(saved));
    }
}
