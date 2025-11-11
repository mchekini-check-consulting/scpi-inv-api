package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.response.HistoryDto;
import fr.checkconsulting.scpiinvapi.model.entity.History;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    @Mapping(target = "id", ignore = true)
    History dtoToEntity(HistoryDto historyDto);

    @Mapping(target = "investmentId", source = "investment.id")
    HistoryDto entityToDto(History history);
    List<HistoryDto> entityToDto(List<History> history);
}
