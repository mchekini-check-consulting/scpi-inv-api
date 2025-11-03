package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.model.entity.History;
import fr.checkconsulting.scpiinvapi.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepository repository;

    public HistoryService(HistoryRepository repository) {
        this.repository = repository;
    }

    public List<History> getHistory(){
        return repository.findAll();
    }
}
