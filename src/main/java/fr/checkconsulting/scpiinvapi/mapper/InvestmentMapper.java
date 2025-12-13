package fr.checkconsulting.scpiinvapi.mapper;

import fr.checkconsulting.scpiinvapi.dto.request.InvestmentRequestDTO;
import fr.checkconsulting.scpiinvapi.dto.response.InvestmentResponseDto;
import fr.checkconsulting.scpiinvapi.model.entity.DistributionRate;
import fr.checkconsulting.scpiinvapi.model.entity.Investment;
import fr.checkconsulting.scpiinvapi.model.entity.Scpi;
import fr.checkconsulting.scpiinvapi.model.entity.Sector;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InvestmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scpi", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "investmentDate", ignore = true)
    Investment toEntity(InvestmentRequestDTO dto);

    @Mapping(source = "scpi.id", target = "scpiId")
    @Mapping(source = "scpi.name", target = "scpiName")
    @Mapping(source = "scpi.sectors", target = "scpiType", qualifiedByName = "mapSectorsToMainType")
    @Mapping(source = "scpi", target = "distributionRate", qualifiedByName = "extractRate")
    @Mapping(source = "scpi.manager", target = "scpiManagerName")
    @Mapping(source = "investmentDate", target = "investmentDurationMonths", qualifiedByName = "calculateDuration")
    InvestmentResponseDto toResponseDTO(Investment entity);

    List<InvestmentResponseDto> toResponseDTOList(List<Investment> entities);

    @Named("mapSectorsToMainType")
    default String mapSectorsToMainType(List<Sector> sectors) {
        if (sectors == null || sectors.isEmpty()) {
            return "Non défini";
        }

        return sectors.stream()
                .max((s1, s2) -> s1.getPercentage().compareTo(s2.getPercentage()))
                .map(Sector::getName)
                .orElse("Non défini");
    }

    @Named("extractRate")
    default BigDecimal extractRate(Scpi scpi) {
        if (scpi == null || scpi.getDistributionRates() == null || scpi.getDistributionRates().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return scpi.getDistributionRates().stream()
                .max(java.util.Comparator.comparing(DistributionRate::getDistributionYear))
                .map(DistributionRate::getRate)
                .orElse(BigDecimal.ZERO);
    }

    @Named("calculateDuration")
    default Long calculateDuration(LocalDateTime investmentDate) {
        if (investmentDate == null) {
            return 0L;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return java.time.temporal.ChronoUnit.MONTHS.between(investmentDate, now);
    }
}