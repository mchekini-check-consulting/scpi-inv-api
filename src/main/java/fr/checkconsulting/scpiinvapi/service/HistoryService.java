package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.mapper.HistoryMapper;
import fr.checkconsulting.scpiinvapi.repository.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
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
        String userId = userService.getUserId();
        log.info("Récupération de l'historique de l'utilisateur: {}",userId);
        return repository.findLatestHistoryPerInvestment(userId);
    }

    public List<HistoryDto> getHistoryByInvestmentId(Long id) {
        log.info("Récupération de l'historique de l'investissement: {}",id);
        return mapper.entityToDto(repository.findByInvestmentId(id));
    }
}
