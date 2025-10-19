package fr.checkconsulting.scpiinvapi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity(name = "scpi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Scpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Min(0)
    @Column(name = "yield")
    private Double yield;
}
