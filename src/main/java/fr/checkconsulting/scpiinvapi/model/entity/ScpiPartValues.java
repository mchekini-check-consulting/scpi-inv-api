package fr.checkconsulting.scpiinvapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScpiPartValues {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer valuationYear;
    private BigDecimal sharePrice;
    private BigDecimal reconstitutionValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scpi_id")
    private Scpi scpi;
}
