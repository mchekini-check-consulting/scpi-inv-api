package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.request.ProfileRequest;
import fr.checkconsulting.scpiinvapi.dto.response.ProfileDtoResponse;
import fr.checkconsulting.scpiinvapi.model.entity.Profile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    Profile toEntity(ProfileRequest profileRequest);

    ProfileDtoResponse toResponse(Profile profile);
}
