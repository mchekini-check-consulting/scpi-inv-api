package fr.checkconsulting.scpiinvapi.model.entity;

import fr.checkconsulting.scpiinvapi.model.enums.InvestmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal investmentAmount;
    private BigDecimal numberOfShares;
    @Enumerated(EnumType.STRING)
    private InvestmentType investmentType;
    private Integer dismembermentYears;
    private LocalDateTime investmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scpi_id")
    private Scpi scpi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id")
    private Investor investor;

}
