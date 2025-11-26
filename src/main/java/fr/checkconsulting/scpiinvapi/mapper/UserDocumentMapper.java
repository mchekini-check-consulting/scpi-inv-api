package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.request.UserDocumentDto;
import fr.checkconsulting.scpiinvapi.model.entity.UserDocument;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserDocumentMapper {
    UserDocumentDto toDto(UserDocument entity);
    UserDocument toEntity(UserDocumentDto dto);
    List<UserDocumentDto> toDtoList(List<UserDocument> entities);

}
