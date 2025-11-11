package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.mapper.HistoryMapper;
import fr.checkconsulting.scpiinvapi.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    private final HistoryRepository repository;
    private final HistoryMapper mapper;

    public HistoryService(HistoryRepository repository, HistoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HistoryDto> getHistory() {

        return repository.findLatestHistoryPerInvestment();
    }

    public List<HistoryDto> getHistoryByInvestmentId(Long id) {

        return mapper.entityToDto(repository.findByInvestmentId(id));
    }
}
