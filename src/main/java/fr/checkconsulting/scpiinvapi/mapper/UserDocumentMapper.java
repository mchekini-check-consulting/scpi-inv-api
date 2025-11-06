package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.model.entity.UserDocument;
import fr.checkconsulting.scpiinvapi.model.enums.DocumentStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserDocumentMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUploadedFields(@MappingTarget UserDocument entity,
                              String originalFileName,
                              String storedFileName,
                              String bucketName);

    @AfterMapping
    default void setUploadedStatus(@MappingTarget UserDocument entity) {
        entity.setStatus(DocumentStatus.UPLOADED);
    }
}
