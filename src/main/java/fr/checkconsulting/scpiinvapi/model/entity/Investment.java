package fr.checkconsulting.scpiinvapi.model.entity;

import fr.checkconsulting.scpiinvapi.model.enums.InvestmentType;
import fr.checkconsulting.scpiinvapi.model.enums.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    private LocalDate scheduledPaymentDate;
    private BigDecimal monthlyAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scpi_id")
    private Scpi scpi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id")
    private Investor investor;

    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<History> history;

}
