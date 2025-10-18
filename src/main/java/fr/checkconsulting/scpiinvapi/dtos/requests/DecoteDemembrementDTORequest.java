package fr.checkconsulting.scpiinvapi.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecoteDemembrementDTORequest {
    private Long id;
    private Integer dureeAnnee;
    private Double pourcentage;
    private Long scpiId;
}
