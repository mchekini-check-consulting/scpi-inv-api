package fr.checkconsulting.scpiinvapi.dto.request;

import fr.checkconsulting.scpiinvapi.model.enums.InvestmentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentRequestDTO {
    @NotNull
    Long scpiId;

    @NotNull
    InvestmentType investmentType;

    @NotNull
    private BigDecimal numberOfShares;

    @NotNull
    BigDecimal investmentAmount;

    @PositiveOrZero
    Integer dismembermentYears;
}
