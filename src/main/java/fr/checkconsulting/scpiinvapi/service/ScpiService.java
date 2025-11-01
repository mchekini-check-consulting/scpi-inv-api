package fr.checkconsulting.scpiinvapi.service;

import fr.checkconsulting.scpiinvapi.dto.response.ScpiSummaryDto;
import fr.checkconsulting.scpiinvapi.mapper.ScpiMapper;
import fr.checkconsulting.scpiinvapi.repository.ScpiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ScpiService {

    private final ScpiRepository scpiRepository;
    private final ScpiMapper scpiMapper;

    public List<ScpiSummaryDto> getAllScpi() {

        return scpiMapper.toScpiSummaryDto(scpiRepository.findAll());


    }
}