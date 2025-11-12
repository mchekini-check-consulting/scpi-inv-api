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
    private final UserService userService;

    public HistoryService(HistoryRepository repository, HistoryMapper mapper, UserService userService) {
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
    }

    public List<HistoryDto> getHistory() {
        return repository.findLatestHistoryPerInvestment(userService.getUserId());
    }

    public List<HistoryDto> getHistoryByInvestmentId(Long id) {

        return mapper.entityToDto(repository.findByInvestmentId(id));
    }
}
