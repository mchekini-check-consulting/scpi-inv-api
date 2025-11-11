package fr.checkconsulting.scpiinvapi.dto.response;

import fr.checkconsulting.scpiinvapi.dto.request.ScpiComparisonResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScpiComparisonDTOResponse {
    private BigDecimal amount;
    private List<ScpiComparisonResult> scpis;
}
