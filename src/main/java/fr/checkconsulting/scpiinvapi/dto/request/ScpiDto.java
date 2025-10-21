package fr.checkconsulting.scpiinvapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScpiDto {

    @NotBlank
    private String name;

    @NotNull
    private Integer minimumSubscription;

    @NotNull
    private String sharePrice;

    @NotNull
    private BigDecimal capitalization;

    private String manager;

    @NotNull
    private BigDecimal subscriptionFees;

    @NotNull
    private BigDecimal managementFees;

    @NotNull
    private Integer enjoymentDelay;

    @NotBlank
    private String rentFrequency;

    @NotNull
    private String reconstitutionValue;

    @NotBlank
    private String iban;

    @NotBlank
    private String bic;

    @NotNull
    private String dismemberment;

    @NotNull
    private Integer cashback;

    @NotNull
    private String scheduledPayment;

    @NotBlank
    private String advertising;
    private Integer lineNumber;
    private String distributedRate;
    private String locations;
    private String sectors;
    private String dismembermentDiscounts;

}
