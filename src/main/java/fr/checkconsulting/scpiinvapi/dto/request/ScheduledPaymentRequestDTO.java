package fr.checkconsulting.scpiinvapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScheduledPaymentRequestDTO {

    @NotNull
    private Long scpiId;

    private BigDecimal firstPaymentAmount;

    @NotNull
    private BigDecimal monthlyAmount;

    @NotNull
    private Integer monthlyShares;

    @NotNull
    private LocalDate firstDebitDate;
}

