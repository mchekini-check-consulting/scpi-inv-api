package fr.checkconsulting.scpiinvapi.dto.response;

import fr.checkconsulting.scpiinvapi.model.enums.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDtoResponse {
    private Long id;
    private String userEmail;
    private MaritalStatus status;
    private Integer children;
    private BigDecimal incomeInvestor;
    private BigDecimal incomeConjoint;


}
