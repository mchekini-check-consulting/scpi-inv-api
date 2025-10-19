package fr.checkconsulting.scpiinvapi.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ValeursScpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "prix_part", precision = 10, scale = 2, nullable = false)
    private BigDecimal prixPart;

    @Column(name = "valeur_reconstitution", precision = 10, scale = 2, nullable = false)
    private BigDecimal valeurReconstitution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scpi_id", nullable = false)
    private Scpi scpi;
}
