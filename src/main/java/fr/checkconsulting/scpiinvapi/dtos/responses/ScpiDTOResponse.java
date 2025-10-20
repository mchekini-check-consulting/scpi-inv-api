package fr.checkconsulting.scpiinvapi.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScpiDTOResponse {
    private Long id;

    private String nom;

    private String description;

    private Integer minimumSouscription;

    private Long capitalisation;

    private String frequenceLoyers;

    private BigDecimal fraisGestion;

    private BigDecimal fraisSouscription;

    private Integer delaiJouissance;

    private String iban;

    private String bic;

    private Boolean demembrement;

    private Integer cashback;

    private Boolean versementProgramme;

    private String publicite;

    private String urlImage;

    private SocieteGestionDTOResponse societeGestion;

    private List<LocalisationDTOResponse> localisations;

    private List<SecteurDTOResponse> secteurs;

    private List<TauxDistributionDTOResponse> tauxDistributions;

    private List<ValeursScpiDTOResponse> valeursScpi;

    private List<DecoteDemembrementDTOResponse> decotesDemembrement;

     private List<InvestisseurDTOResponse> investisseurs;
}
