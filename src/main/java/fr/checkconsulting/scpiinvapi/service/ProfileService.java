package fr.checkconsulting.scpiinvapi.service;


import fr.checkconsulting.scpiinvapi.dto.request.ProfileRequest;
import fr.checkconsulting.scpiinvapi.dto.response.ProfileDtoResponse;
import fr.checkconsulting.scpiinvapi.mapper.ProfileMapper;
import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import fr.checkconsulting.scpiinvapi.repository.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class ProfileService {

    private final ProfileRepository repo;
    private final UserService userService;
    private final ProfileMapper profileMapper;

    public ProfileService(ProfileRepository repo, UserService userService, ProfileMapper profileMapper) {
        this.repo = repo;
        this.userService = userService;
        this.profileMapper = profileMapper;
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

    public ProfileDtoResponse saveOrUpdateProfile(ProfileRequest req) {

        String email = userService.getEmail();

        Optional<Profile> existingOpt = repo.findByEmail(email);

        Profile profile;

        if (existingOpt.isPresent()) {
            profile = existingOpt.get();
            log.info("Mise à jour du profil existant id={}", profile.getId());
        } else {
            profile = new Profile();
            profile.setEmail(email);
            log.info("Création d’un nouveau profil pour {}", email);
        }

        validateProfileRequest(req);

        boolean hasConjoint = isConjointRequired(req.getStatus());

        profile.setStatus(req.getStatus());
        profile.setChildren(req.getChildren());
        profile.setIncomeInvestor(req.getIncomeInvestor());
        profile.setIncomeConjoint(hasConjoint ? req.getIncomeConjoint() : null);

        Profile saved = repo.save(profile);

        return profileMapper.toResponse(saved);
    }

    public ProfileDtoResponse getProfile() {
        Optional<Profile> profileOptional = repo.findByEmail(userService.getEmail());

        if(profileOptional.isPresent()){
            return profileMapper.toResponse(profileOptional.get());
        } else {
            return new ProfileDtoResponse();
        }
    }

}
