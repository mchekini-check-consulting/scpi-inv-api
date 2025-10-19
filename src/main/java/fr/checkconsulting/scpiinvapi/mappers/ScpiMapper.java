package fr.checkconsulting.scpiinvapi.mappers;

import fr.checkconsulting.scpiinvapi.dto.ScpiDto;
import fr.checkconsulting.scpiinvapi.entities.Scpi;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScpiMapper {
    ScpiDto entityToDto(Scpi entity);

    Scpi dtoToEntity(ScpiDto dto);

    List<ScpiDto> toDtoList(List<Scpi> entities);
}
