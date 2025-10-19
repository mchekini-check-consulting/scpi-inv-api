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
public class DecoteDemembrement{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "duree_annee", nullable = false)
    private Integer dureeAnnee;

    @Column(name = "pourcentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal pourcentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scpi_id", nullable = false)
    private Scpi scpi;
}
