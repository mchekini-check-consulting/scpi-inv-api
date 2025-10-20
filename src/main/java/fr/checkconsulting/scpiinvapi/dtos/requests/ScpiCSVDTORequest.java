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

    private List<TauxDistributionDTORequest> tauxDistributions;

    @NotNull
    private Integer minimumSouscription;

    private List<LocalisationDTORequest> localisations;

    private List<SecteurDTORequest> secteurs;

    @NotNull
    private BigDecimal prixPart;

    @NotNull
    private BigDecimal capitalisation;

    private String gerant;

    @NotNull
    private BigDecimal fraisSouscription;

    @NotNull
    private BigDecimal fraisGestion;

    @NotNull
    private Integer delaiJouissance;

    @NotBlank
    private String frequenceLoyers;

    @NotNull
    private BigDecimal valeurReconstitution;

    @NotBlank
    private String iban;

    @NotBlank
    private String bic;

    private List<DecoteDemembrementDTORequest> decotesDemembrement;

    @NotNull
    private Boolean demembrement;

    @NotNull
    private Integer cashback;

    @NotNull
    private Boolean versementProgramme;

    @NotBlank
    private String publicite;

    private Integer lineNumber;

    private List<ValeursScpiDTORequest> valeursScpi;

}
