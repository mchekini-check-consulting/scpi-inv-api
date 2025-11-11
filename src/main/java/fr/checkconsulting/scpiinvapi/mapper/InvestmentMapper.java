package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.request.InvestmentRequestDTO;
import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvestmentMapper {

    Investment toEntity(InvestmentRequestDTO request);
}
