package fr.checkconsulting.scpiinvapi.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Investisseur extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// // le uuid depuis keyclok
    @Column(name="id_utilisateur",nullable = false)
    private String idUtilisateur;

    @Column(name="adresse")
    private String adresse;

    @ManyToMany
    @JoinTable(
            name = "patrimoines_immobiliers",
            joinColumns = @JoinColumn(name = "investisseur_id"),
            inverseJoinColumns = @JoinColumn(name = "scpi_id")
    )
    private List<Scpi> scpis;

    @OneToMany(mappedBy = "investisseur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;
}
