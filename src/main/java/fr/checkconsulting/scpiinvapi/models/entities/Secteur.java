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
public class Secteur extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String secteur;

    @Column(name="pourcentage")
    private BigDecimal pourcentage;

    @ManyToOne
    @JoinColumn(name = "scpi_id")
    private Scpi scpi;
}
