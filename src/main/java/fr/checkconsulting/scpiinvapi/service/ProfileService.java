package fr.checkconsulting.scpiinvapi.service;


import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import fr.checkconsulting.scpiinvapi.repository.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository repo;

    public ProfileService(ProfileRepository repo) {
        this.repo = repo;
    }

    public Profile save(Profile profile) {
        return repo.save(profile);
    }
}