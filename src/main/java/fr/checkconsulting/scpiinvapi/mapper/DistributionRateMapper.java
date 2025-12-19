package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.request.DistributionRateRequestDto;
import fr.checkconsulting.scpiinvapi.dto.response.DistributionRateResponseDto;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DistributionRateMapper {

    @Mapping(source = "scpi.id", target = "scpiId")
    DistributionRateResponseDto toDto(DistributionRate distributionRate);

    @Mapping(source = "scpiId", target = "scpi.id")
    DistributionRate toEntity(DistributionRateRequestDto distributionRateRequestDTO);

    List<DistributionRateResponseDto> toDtoList(List<DistributionRate> distributionRates);
}
