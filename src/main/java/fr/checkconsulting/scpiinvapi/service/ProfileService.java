package fr.checkconsulting.scpiinvapi.service;


import fr.checkconsulting.scpiinvapi.dto.request.ProfileRequest;
import fr.checkconsulting.scpiinvapi.dto.response.ProfileDtoResponse;
import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import fr.checkconsulting.scpiinvapi.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class ProfileService {

    private final ProfileRepository repo;
    private final UserService userService;

    public ProfileService(ProfileRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }

    public ProfileDtoResponse createProfile(ProfileRequest profilereq) {


        log.info("Création d'un profil avec status={} enfants={} revenuInvestisseur={} revenuConjoint={}",
                profilereq.getStatus(),
                profilereq.getChildren(),
                profilereq.getIncomeInvestor(),
                profilereq.getIncomeConjoint());

        validateProfileRequest(profilereq);

        Profile profile = new Profile();
        profile.setStatus(profilereq.getStatus());
        profile.setChildren(profilereq.getChildren());
        profile.setIncomeInvestor(profilereq.getIncomeInvestor());
        profile.setEmail(userService.getEmail());

        boolean hasConjoint = isConjointRequired(profilereq.getStatus());
        profile.setIncomeConjoint(hasConjoint ? profilereq.getIncomeConjoint() : null);

        Profile saved = repo.save(profile);
        log.info("Profil créé avec id={}", saved.getId());

        return toResponse(saved);
    }

    private void validateProfileRequest(ProfileRequest profilereq) {
        if (profilereq.getChildren() < 0) {
            log.error("Nombre d'enfants négatif fourni: {}", profilereq.getChildren());
            throw new IllegalArgumentException("Le nombre d’enfants ne peut pas être négatif.");
        }

        if (profilereq.getIncomeInvestor() == null || profilereq.getIncomeInvestor().compareTo(BigDecimal.ZERO) < 0) {
            log.error("Revenu investisseur invalide: {}", profilereq.getIncomeInvestor());
            throw new IllegalArgumentException("Le revenu de l’investisseur doit être positif.");
        }

        if (isConjointRequired(profilereq.getStatus())) {
            if (profilereq.getIncomeConjoint() == null) {
                log.error("Revenu conjoint manquant pour statut marital {}", profilereq.getStatus());
                throw new IllegalArgumentException("Le revenu du conjoint est obligatoire pour ce statut marital.");
            }
            if (profilereq.getIncomeConjoint().compareTo(BigDecimal.ZERO) < 0) {
                log.error("Revenu conjoint négatif: {}", profilereq.getIncomeConjoint());
                throw new IllegalArgumentException("Le revenu du conjoint doit être positif ou nul.");
            }
        }
    }

    private boolean isConjointRequired(MaritalStatus status) {
        return status == MaritalStatus.MARIE || status == MaritalStatus.PACSE;
    }

    private ProfileDtoResponse toResponse(Profile profile) {
        ProfileDtoResponse resp = new ProfileDtoResponse();
        resp.setId(profile.getId());
        resp.setStatus(profile.getStatus());
        resp.setChildren(profile.getChildren());
        resp.setIncomeInvestor(profile.getIncomeInvestor());
        resp.setIncomeConjoint(profile.getIncomeConjoint());
        return resp;
    }
}

