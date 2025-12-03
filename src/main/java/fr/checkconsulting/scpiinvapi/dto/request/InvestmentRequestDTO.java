package fr.checkconsulting.scpiinvapi.dto.request;

import fr.checkconsulting.scpiinvapi.model.enums.InvestmentType;
import fr.checkconsulting.scpiinvapi.model.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @NotNull
    PaymentType paymentType;

    private LocalDate scheduledPaymentDate;

    private BigDecimal monthlyAmount;

}
