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
public class DismembermentDiscounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer durationYears;
    private BigDecimal percentage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scpi_id", nullable = false)
    private Scpi scpi;
}
