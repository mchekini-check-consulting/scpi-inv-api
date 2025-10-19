package fr.checkconsulting.scpiinvapi.services;

import fr.checkconsulting.scpiinvapi.entities.Scpi;
import fr.checkconsulting.scpiinvapi.repositories.ScpiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScpiService {
    private final ScpiRepository repository;

    public ScpiService(ScpiRepository repository) {
        this.repository = repository;
    }

    public List<Scpi> getAll() {
        return repository.findAll();
    }

    public Scpi save(Scpi scpi) {
        return repository.save(scpi);
    }
}
