package fr.checkconsulting.scpiinvapi.resource;

import fr.checkconsulting.scpiinvapi.dto.request.ProfileRequest;
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
    public ResponseEntity<Profile> create(@Valid @RequestBody ProfileRequest req) {
        boolean hasConjoint = req.getStatus() == MaritalStatus.MARIE || req.getStatus() == MaritalStatus.PACSE;

        Profile p = new Profile();
        p.setStatus(req.getStatus());
        p.setChildren(req.getChildren());
        p.setIncomeInvestor(req.getIncomeInvestor());
        p.setIncomeConjoint(hasConjoint ? req.getIncomeConjoint() : null);

        Profile saved = service.save(p);
        return ResponseEntity.ok(saved);
    }
}