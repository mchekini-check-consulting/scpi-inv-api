package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.request.ProfileRequest;
import fr.checkconsulting.scpiinvapi.dto.response.ProfileDtoResponse;
import fr.checkconsulting.scpiinvapi.service.ProfileService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/v1/profile")
public class ProfileResource {

    private final ProfileService service;

    public ProfileResource(ProfileService service) {
        this.service = service;
    }

    @Operation(
            summary = "Créer un profil",
            description = "Cette API permet de créer un nouveau profil utilisateur. "
                    + "Tous les champs obligatoires doivent être fournis dans le corps de la requête."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil créé avec succès",
                    content = @Content(schema = @Schema(implementation = ProfileDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide, paramètres manquants ou invalides",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la création du profil",
                    content = @Content)
    })

    @PostMapping
    public ResponseEntity<ProfileDtoResponse> create(@Valid @RequestBody ProfileRequest req) {
        ProfileDtoResponse response = service.createProfile(req);
        return ResponseEntity.ok(response);

    }


    @Operation(
            summary = "Mettre à jour un profil",
            description = "Cette API permet de mettre à jour un profil existant à partir de son identifiant. "
                    + "Tous les champs doivent être fournis dans le corps de la requête."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ProfileDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide, paramètres manquants ou invalides",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Profil non trouvé pour l'identifiant fourni",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la mise à jour du profil",
                    content = @Content)
    })

    @PutMapping("/{id}")
    public ResponseEntity<ProfileDtoResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody ProfileRequest req) {

        ProfileDtoResponse updated = service.updateProfile(id, req);
        return ResponseEntity.ok(updated);
    }

}