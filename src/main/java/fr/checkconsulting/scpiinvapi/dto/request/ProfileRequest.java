package fr.checkconsulting.scpiinvapi.dto.request;

import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileRequest {

    @NotNull(message = "Le statut marital est obligatoire.")
    private MaritalStatus status;

    @NotNull(message = "Le nombre d'enfants est obligatoire.")
    @Min(value = 0, message = "Le nombre d'enfants doit être ≥ 0.")
    private Integer children;

    @NotNull(message = "Les revenus de l'investisseur sont obligatoires.")
    @Digits(integer = 19, fraction = 2)
    @DecimalMin(value = "0.0", inclusive = true, message = "Les revenus doivent être ≥ 0.")
    private BigDecimal incomeInvestor;

    @DecimalMin(value = "0.0", inclusive = true, message = "Les revenus doivent être ≥ 0.")
    @Digits(integer = 19, fraction = 2)
    private BigDecimal incomeConjoint;
}