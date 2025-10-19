package fr.checkconsulting.scpiinvapi.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScpiCSVDTORequest {
    @NotBlank
    private String nom;

    @NotNull
    private Integer minimumSouscription;

    @NotNull
    private BigDecimal capitalisation;


    @NotNull
    private BigDecimal fraisSouscription;

    @NotNull
    private BigDecimal fraisGestion;

    @NotNull
    private Integer delaiJouissance;

    @NotBlank
    private String frequenceLoyers;

    @NotBlank
    private String iban;

    @NotBlank
    private String bic;

    @NotNull
    private Boolean demembrement;

    @NotNull
    private Integer cashback;

    @NotNull
    private Boolean versementProgramme;

    @NotBlank
    private String publicite;

    private Integer lineNumber;

    private List<LocalisationDTORequest> localisations;

    private List<SecteurDTORequest> secteurs;

    private List<TauxDistributionDTORequest> tauxDistributions;

    private List<ValeursScpiDTORequest> valeursScpi;

    private List<DecoteDemembrementDTORequest> decotesDemembrement;
}
