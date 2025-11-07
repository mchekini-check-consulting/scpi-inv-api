package fr.checkconsulting.scpiinvapi.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ScpiDismembrementDto {

    private Integer durationYears;
    private BigDecimal nueProprietePercentage;
    private BigDecimal usufruitPercentage;
    
}
