package fr.checkconsulting.scpiinvapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScpiSummaryDto {
    
    private Long id;
    private String name;
    private String imageUrl;
    
    private Integer minimumSubscription;
    private Integer cashback;
    private String advertising;
    
    private BigDecimal distributionRate;
    private String country;
    
    
}