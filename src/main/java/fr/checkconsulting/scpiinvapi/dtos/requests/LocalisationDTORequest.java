package fr.checkconsulting.scpiinvapi.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocalisationDTORequest {
    private Long id;
    private String pays;
    private Double pourcentage;
    private Long scpiId;
}
