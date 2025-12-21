package fr.checkconsulting.scpiinvapi.repository;

import fr.checkconsulting.scpiinvapi.dto.request.ScpiSearchCriteriaDto;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import org.springframework.data.domain.Page;

interface ScpiRepositoryCustom {
    Page<Scpi> search(ScpiSearchCriteriaDto criteria, int page, int size);
}
