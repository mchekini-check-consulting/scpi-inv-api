package fr.checkconsulting.scpiinvapi.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String name;
    private BigDecimal totalInvestment = BigDecimal.ZERO;
    private BigDecimal totalAnnualReturn = BigDecimal.ZERO;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    @JsonManagedReference
    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<SimulationScpi> items = new ArrayList<>();
}
