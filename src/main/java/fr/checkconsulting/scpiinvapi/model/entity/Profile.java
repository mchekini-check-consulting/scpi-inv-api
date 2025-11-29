package fr.checkconsulting.scpiinvapi.model.entity;

import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaritalStatus status;

    @Column(nullable = false)
    private Integer children;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal incomeInvestor;

    @Column(precision = 19, scale = 2)
    private BigDecimal incomeConjoint;

    public Long getId() { return id; }
    public MaritalStatus getStatus() { return status; }
    public void setStatus(MaritalStatus status) { this.status = status; }
    public Integer getChildren() { return children; }
    public void setChildren(Integer children) { this.children = children; }
    public BigDecimal getIncomeInvestor() { return incomeInvestor; }
    public void setIncomeInvestor(BigDecimal incomeInvestor) { this.incomeInvestor = incomeInvestor; }
    public BigDecimal getIncomeConjoint() { return incomeConjoint; }
    public void setIncomeConjoint(BigDecimal incomeConjoint) { this.incomeConjoint = incomeConjoint; }
}