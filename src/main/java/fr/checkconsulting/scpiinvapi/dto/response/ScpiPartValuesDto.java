package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScpiPartValuesDto {

    private Integer valuationYear;
    private BigDecimal sharePrice;
    private BigDecimal reconstitutionValue;
}
