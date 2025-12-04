package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.request.ProfileRequest;
import fr.checkconsulting.scpiinvapi.dto.response.ProfileDtoResponse;
import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import fr.checkconsulting.scpiinvapi.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileResource {

    private final ProfileService service;

    public ProfileResource(ProfileService service) {
        this.service = service;
    }

    @PostMapping

    public ResponseEntity<ProfileDtoResponse> create(@Valid @RequestBody ProfileRequest req) {
        ProfileDtoResponse response = service.createProfile(req);
        return ResponseEntity.ok(response);

    }
}