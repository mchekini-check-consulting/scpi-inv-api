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
        ProfileDtoResponse response = service.saveOrUpdateProfile(req);
        return ResponseEntity.ok(response);

    }
    @Operation(
            summary = "Récupérer le profil de l'utilisateur",
            description = "Cette API permet de récupérer le profil de l'utilisateur. "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = ProfileDtoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération du profil",
                    content = @Content)
    })

    @GetMapping
    public ResponseEntity<ProfileDtoResponse> getProfile() {
        return ResponseEntity.ok(service.getProfile());

    }

}