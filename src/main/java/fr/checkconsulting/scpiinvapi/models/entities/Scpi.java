package fr.checkconsulting.scpiinvapi.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Scpi extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "minimum_souscription")
    private Integer minimumSouscription;

    @Column(name = "capitalisation")
    private Long capitalisation;

    @Column(name = "frequence_loyers")
    private String frequenceLoyers;

    @Column(name = "frais_gestion")
    private BigDecimal fraisGestion;

    @Column(name = "frais_souscription")
    private BigDecimal fraisSouscription;

    @Column(name = "delai_jouissance")
    private Integer delaiJouissance;

    @Column(name = "iban")
    private String iban;

    @Column(name = "bic")
    private String bic;

    @Column(name = "demembrement")
    private Boolean demembrement;

    @Column(name = "cashback")
    private Integer cashback;

    @Column(name = "versement_programme")
    private Boolean versementProgramme;

    @Column(columnDefinition = "TEXT")
    private String publicite;

    @Column(name = "url_image")
    private String urlImage;

    @ManyToOne
    @JoinColumn(name = "societe_gestion_id")
    private SocieteGestion societeGestion;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL)
    private List<Localisation> localisations;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL)
    private List<Secteur> secteurs;

    @ManyToMany(mappedBy = "scpis")
    private List<Investisseur> investisseurs;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL)
    private List<TauxDistribution> tauxDistributions;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL)
    private List<ValeursScpi> valeursScpi;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL)
    private List<DecoteDemembrement> decotesDemembrement;
}
