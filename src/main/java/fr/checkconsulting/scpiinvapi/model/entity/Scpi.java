package fr.checkconsulting.scpiinvapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scpi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer minimumSubscription;
    private BigDecimal capitalization;
    private String rentFrequency;
    private BigDecimal managementFees;
    private BigDecimal subscriptionFees;
    private Integer enjoymentDelay;
    private String iban;
    private String bic;
    private Boolean dismemberment;
    private Integer cashback;
    private Boolean scheduledPayment;
    private String advertising;
    private String imageUrl;
    private String manager;


    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Location> locations;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Sector> sectors;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DistributionRate> distributionRates;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ScpiPartValues> scpiValues;

    @OneToMany(mappedBy = "scpi", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DismembermentDiscounts> dismembermentDiscounts;
}