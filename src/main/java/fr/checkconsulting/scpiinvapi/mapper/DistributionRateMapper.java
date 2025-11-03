package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.request.DistributionRateDTORequest;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateDTOResponse;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DistributionRateMapper {

    @Mapping(source = "scpi.id", target = "scpiId")
    DistributionRateDTOResponse toDto(DistributionRate distributionRate);

    @Mapping(source = "scpiId", target = "scpi.id")
    DistributionRate toEntity(DistributionRateDTORequest distributionRateDTORequest);

    List<DistributionRateDTOResponse> toDtoList(List<DistributionRate> distributionRates);
}
