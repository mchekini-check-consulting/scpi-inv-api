package fr.checkconsulting.scpiinvapi.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PatrimoineImmobiliers extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investisseur_id", nullable = false)
    private Investisseur investisseur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scpi_id", nullable = false)
    private Scpi scpi;

}
