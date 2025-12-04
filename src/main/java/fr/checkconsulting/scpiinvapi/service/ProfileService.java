package fr.checkconsulting.scpiinvapi.service;


import fr.checkconsulting.scpiinvapi.dto.request.ProfileRequest;
import fr.checkconsulting.scpiinvapi.dto.response.ProfileDtoResponse;
import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import fr.checkconsulting.scpiinvapi.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProfileService {
    private final ProfileRepository repo;

    public ProfileService(ProfileRepository repo) {
        this.repo = repo;
    }

    public ProfileDtoResponse createProfile(ProfileRequest profilereq) {
        boolean hasConjoint = profilereq.getStatus() == MaritalStatus.MARIE
                || profilereq.getStatus() == MaritalStatus.PACSE;


                if (profilereq.getChildren() < 0) {
            throw new IllegalArgumentException("Le nombre d’enfants ne peut pas être négatif.");
        }


        if (profilereq.getIncomeInvestor() == null || profilereq.getIncomeInvestor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le revenu de l’investisseur doit être positif.");
        }

        if (hasConjoint) {
            if (profilereq.getIncomeConjoint() == null) {
                throw new IllegalArgumentException("Le revenu du conjoint est obligatoire pour ce statut marital.");
            }
            if (profilereq.getIncomeConjoint().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Le revenu du conjoint doit être positif ou nulle.");
            }
        }

        Profile profileInvest = new Profile();
        profileInvest.setStatus(profilereq.getStatus());
        profileInvest.setChildren(profilereq.getChildren());
        profileInvest.setIncomeInvestor(profilereq.getIncomeInvestor());
        profileInvest.setIncomeConjoint(hasConjoint ? profilereq.getIncomeConjoint() : null);
        Profile saved = repo.save(profileInvest);
        return toResponse(saved);
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

