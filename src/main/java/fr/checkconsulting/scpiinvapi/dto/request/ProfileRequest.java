package fr.checkconsulting.scpiinvapi.dto.request;

import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProfileRequest {

    @NotNull(message = "Le statut marital est obligatoire.")
    private MaritalStatus status;

    @NotNull(message = "Le nombre d'enfants est obligatoire.")
    @Min(value = 0, message = "Le nombre d'enfants doit être ≥ 0.")
    private Integer children;

    @NotNull(message = "Les revenus de l'investisseur sont obligatoires.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Les revenus doivent être ≥ 0.")
    private BigDecimal incomeInvestor;

    @DecimalMin(value = "0.0", inclusive = true, message = "Les revenus doivent être ≥ 0.")
    private BigDecimal incomeConjoint;

    public MaritalStatus getStatus() { return status; }
    public void setStatus(MaritalStatus status) { this.status = status; }
    public Integer getChildren() { return children; }
    public void setChildren(Integer children) { this.children = children; }
    public BigDecimal getIncomeInvestor() { return incomeInvestor; }
    public void setIncomeInvestor(BigDecimal incomeInvestor) { this.incomeInvestor = incomeInvestor; }
    public BigDecimal getIncomeConjoint() { return incomeConjoint; }
    public void setIncomeConjoint(BigDecimal incomeConjoint) { this.incomeConjoint = incomeConjoint; }
}